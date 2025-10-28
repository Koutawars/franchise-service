package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.dynamodb.mapper.FranchiseMapper;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import java.util.Objects;

@Repository
public class FranchiseDynamoDB implements FranchiseRepository {
  public static final String TABLE_NAME_STRING = "tableName";
  public static final String FRANCHISE = "FRANCHISE#";
  private final String tableName;
  private final DynamoDbAsyncTable<FranchiseEntity> franchiseTable;
  private final Logger logger;

  public FranchiseDynamoDB(@Value("${aws.dynamodb.franchiseTable}") String tableName,
                           DynamoDbEnhancedAsyncClient connectionFactory,
                           Logger logger) {
    this.tableName = tableName;
    this.franchiseTable = connectionFactory.table(tableName, TableSchema.fromBean(FranchiseEntity.class));
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
}
