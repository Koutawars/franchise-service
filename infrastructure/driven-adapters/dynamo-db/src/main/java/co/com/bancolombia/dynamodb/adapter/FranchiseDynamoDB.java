package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.dynamodb.mapper.BranchMapper;
import co.com.bancolombia.dynamodb.mapper.FranchiseMapper;
import co.com.bancolombia.dynamodb.mapper.ProductMapper;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import java.util.Objects;

@Repository
public class FranchiseDynamoDB implements FranchiseRepository {
  public static final String TABLE_NAME_STRING = "tableName";
  public static final String BRANCH = "BRANCH#";
  public static final String FRANCHISE = "FRANCHISE#";
  private final String tableName;
  private final DynamoDbAsyncTable<FranchiseEntity> franchiseTable;
  private final DynamoDbAsyncTable<BranchEntity> branchTable;
  private final DynamoDbAsyncTable<ProductEntity> productTable;
  private final Logger logger;

  public FranchiseDynamoDB(@Value("${aws.dynamodb.franchiseTable}") String tableName,
                           DynamoDbEnhancedAsyncClient connectionFactory,
                           Logger logger) {
    this.tableName = tableName;
    this.franchiseTable = connectionFactory.table(tableName, TableSchema.fromBean(FranchiseEntity.class));
    this.branchTable = connectionFactory.table(tableName, TableSchema.fromBean(BranchEntity.class));
    this.productTable = connectionFactory.table(tableName, TableSchema.fromBean(ProductEntity.class));
    this.logger = logger;
  }

  @Override
  public Mono<Franchise> save(Franchise franchise) {
    FranchiseEntity franchiseEntity = FranchiseMapper.toEntity(franchise);
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("franchise", franchiseEntity);
      logBuilder.info("Saving franchise");
      return Mono.fromFuture(franchiseTable.putItem(franchiseEntity))
          .doOnSuccess(unused -> logBuilder.info("Franchise saved"))
          .doOnError(error -> logBuilder.error("Error saving franchise", error))
          .then(Mono.fromCallable(() -> FranchiseMapper.toDomain(franchiseEntity)));
    });
  }

  @Override
  public Mono<Franchise> findById(String id) {
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("pk", FRANCHISE + id)
          .key("sk", "METADATA");
      logBuilder.info("Finding franchise");
      return Mono.fromFuture(franchiseTable.getItem(FranchiseEntity.builder().pk(FRANCHISE + id).sk("METADATA").build()))
          .filter(Objects::nonNull)
          .doOnSuccess(unused -> logBuilder.info("Franchise found"))
          .doOnError(error -> logBuilder.error("Error finding franchise", error))
          .map(FranchiseMapper::toDomain);
    });
  }

  @Override
  public Mono<Branch> saveBranch(Branch branch) {
    BranchEntity branchEntity = BranchMapper.toEntity(branch);
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("branch", branch);
      logBuilder.info("add branch");
      return Mono.fromFuture(branchTable.putItem(branchEntity))
          .doOnSuccess(unused -> logBuilder.info("branch saved"))
          .doOnError(error -> logBuilder.error("Error saving branch", error))
          .then(Mono.fromCallable(() -> BranchMapper.toDomain(branchEntity)));
    });
  }

  @Override
  public Mono<Branch> findBranchById(String id, String franchiseId) {
    return Mono.deferContextual(ctx -> {
      Key key = Key.builder()
          .partitionValue(FRANCHISE + franchiseId)
          .sortValue(BRANCH + id)
          .build();
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("partitionValue", FRANCHISE + franchiseId)
          .key("sortKey", BRANCH + id);
      logBuilder.info("finding branch");
      return Mono.fromFuture(branchTable.getItem(key))
          .filter(Objects::nonNull)
          .doOnSuccess(unused -> logBuilder.info("branch found"))
          .doOnError(error -> logBuilder.error("Error finding branch", error))
          .map(BranchMapper::toDomain);
    });
  }

  @Override
  public Mono<Product> saveProduct(Product product) {
    ProductEntity productEntity = ProductMapper.toEntity(product);
    return Mono.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("product", product);
      logBuilder.info("add product");
      return Mono.fromFuture(productTable.putItem(productEntity))
          .doOnSuccess(unused -> logBuilder.info("product saved"))
          .doOnError(error -> logBuilder.error("Error saving product", error))
          .then(Mono.fromCallable(() -> ProductMapper.toDomain(productEntity)));
    });
  }

  @Override
  public Mono<Product> findProductById(String id, String branchId, String franchiseId) {
    return Mono.deferContextual(ctx -> {
      Key key = Key.builder()
          .partitionValue(FRANCHISE + franchiseId)
          .sortValue(BRANCH + branchId + "#PRODUCT#" + id)
          .build();
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("partitionValue", FRANCHISE + franchiseId)
          .key("sortValue", BRANCH + branchId + "#PRODUCT#" + id);
      logBuilder.info("find product");
      return Mono.fromFuture(productTable.getItem(key))
          .filter(Objects::nonNull)
          .doOnSuccess(unused -> logBuilder.info("product found"))
          .doOnError(error -> logBuilder.error("Error founding product", error))
          .map(ProductMapper::toDomain);
    });
  }

  @Override
  public Mono<Void> deleteProduct(Product product) {
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


  @Override
  public Flux<Product> findProductsByFranchise(String franchiseId) {
    return Flux.deferContextual(ctx -> {
      LogBuilder logBuilder = logger.with(ctx)
          .key(TABLE_NAME_STRING, tableName)
          .key("franchiseId", franchiseId);
      logBuilder.info("Getting products by franchise");
      
      QueryConditional queryConditional = QueryConditional.keyEqualTo(
          Key.builder().partitionValue(FRANCHISE + franchiseId).build());
      
      return Flux.from(productTable.query(queryConditional))
          .flatMap(page -> Flux.fromIterable(page.items()))
          .filter(entity -> entity.getSk().contains("#PRODUCT#"))
          .map(ProductMapper::toDomain)
          .doOnComplete(() -> logBuilder.info("products by franchise retrieved"))
          .doOnError(error -> logBuilder.error("Error getting products by franchise", error));
    });
  }
}
