package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.cache.ProductCache;
import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.dynamodb.mapper.ProductMapper;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.ProductRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.List;
import java.util.Objects;

@Repository
public class ProductDynamoDB implements ProductRepository {
  public static final String TABLE_NAME_STRING = "tableName";
  public static final String BRANCH = "BRANCH#";
  public static final String FRANCHISE = "FRANCHISE#";
  public static final String PRODUCT = "#PRODUCT#";
  private final DynamoDbAsyncTable<ProductEntity> productTable;
  private final DynamoDbAsyncIndex<ProductEntity> branchProductsByStockIndex;
  private final DynamoDbAsyncTable<BranchEntity> branchTable;
  private final String tableName;
  private final Logger logger;
  private final ProductCache productCache;

  public ProductDynamoDB(@Value("${aws.dynamodb.franchiseTable}") String tableName,
                           DynamoDbEnhancedAsyncClient connectionFactory,
                           Logger logger, ProductCache productCache) {
    this.tableName = tableName;
    this.productTable = connectionFactory.table(tableName, TableSchema.fromBean(ProductEntity.class));
    this.branchTable = connectionFactory.table(tableName, TableSchema.fromBean(BranchEntity.class));
    this.branchProductsByStockIndex = productTable.index("BranchProductsByStock");
    this.logger = logger;
    this.productCache = productCache;
  }

  @Override
  public Mono<Product> save(Product product) {
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("product", product);
      logBuilder.info("save product");

      Mono<Product> existingProductMono = product.getId() != null
          ? findById(product.getId(), product.getBranchId(), product.getFranchiseId())
          : Mono.empty();

      return existingProductMono
          .flatMap(existingProduct -> {
            ProductEntity oldEntity = ProductMapper.toEntity(existingProduct);
            return Mono.fromFuture(productTable.deleteItem(oldEntity))
                .doOnSuccess(unused -> logBuilder.info("old product deleted for update"));
          })
          .then(Mono.defer(() -> {
            ProductEntity newEntity = ProductMapper.toEntity(product);
            return Mono.fromFuture(productTable.putItem(newEntity))
                .doOnSuccess(unused -> logBuilder.info("product saved"))
                .doOnError(error -> logBuilder.error("Error saving product", error))
                .thenReturn(ProductMapper.toDomain(newEntity));
          }));
    });
  }

  @Override
  public Mono<Product> findById(String id, String branchId, String franchiseId) {
    return Mono.deferContextual(ctx -> {
      Key key = Key.builder()
          .partitionValue(FRANCHISE + franchiseId)
          .sortValue(BRANCH + branchId + PRODUCT + id)
          .build();
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("partitionValue", FRANCHISE + franchiseId)
          .key("sortValue", BRANCH + branchId + PRODUCT + id);
      logBuilder.info("find product");
      return Mono.fromFuture(productTable.getItem(key))
          .filter(Objects::nonNull)
          .doOnSuccess(unused -> logBuilder.info("product found"))
          .doOnError(error -> logBuilder.error("Error founding product", error))
          .map(ProductMapper::toDomain);
    });
  }

  @Override
  public Mono<Void> delete(Product product) {
    ProductEntity productEntity = ProductMapper.toEntity(product);
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("productEntity", productEntity);
      logBuilder.info("delete product");
      return Mono.fromFuture(productTable.deleteItem(productEntity))
          .doOnSuccess(unused -> logBuilder.info("product deleted"))
          .doOnError(error -> logBuilder.error("Error deleting product", error))
          .then();
    });
  }

  @CircuitBreaker(name = "dynamoFindTopProductsByFranchise", fallbackMethod = "fallbackTop")
  @Override
  public Flux<Product> findTopProductsByFranchise(String franchiseId) {
    return Flux.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("franchiseId", franchiseId);
      logBuilder.info("Getting top products by franchise using GSI");

      return findBranchesByFranchise(franchiseId)
          .flatMap(branchId -> {
            QueryConditional queryConditional = QueryConditional.keyEqualTo(
                Key.builder()
                    .partitionValue(FRANCHISE + franchiseId + "#" + BRANCH + branchId)
                    .build());

            QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(1)
                .build();

            return Flux.from(branchProductsByStockIndex.query(request))
                .flatMap(page -> Flux.fromIterable(page.items()))
                .next()
                .map(ProductMapper::toDomain);
          })
          .collectList()
          .doOnNext(list -> productCache.putTop(franchiseId, list))
          .flatMapMany(Flux::fromIterable)
          .doOnComplete(() -> logBuilder.info("top products by franchise retrieved"))
          .doOnError(error -> logBuilder.error("Error getting top products by franchise", error));
    });
  }

  public Flux<Product> fallbackTop(String franchiseId, Throwable ex) {
    return Flux.deferContextual(ctx -> {
      List<Product> cached = productCache.getTop(franchiseId);
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("franchiseId", franchiseId);
      if (cached != null && !cached.isEmpty()) {
        logBuilder.info("Returning cached products");
        return Flux.fromIterable(cached);
      }
      logBuilder.error("No cache available for franchiseId, returning empty", ex);
      return Flux.empty();
    });
  }

  private Flux<String> findBranchesByFranchise(String franchiseId) {
    QueryConditional queryConditional = QueryConditional.sortBeginsWith(
        Key.builder()
            .partitionValue(FRANCHISE + franchiseId)
            .sortValue(BRANCH)
            .build());

    return Flux.from(branchTable.query(queryConditional))
        .flatMap(page -> Flux.fromIterable(page.items()))
        .map(branch -> branch.getSk().replace(BRANCH, ""));
  }
}
