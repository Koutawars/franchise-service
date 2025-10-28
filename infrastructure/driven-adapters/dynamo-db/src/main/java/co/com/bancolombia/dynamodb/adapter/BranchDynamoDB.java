package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.dynamodb.mapper.BranchMapper;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.gateway.BranchRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Objects;

@Repository
public class BranchDynamoDB implements BranchRepository {
  public static final String TABLE_NAME_STRING = "tableName";
  public static final String BRANCH = "BRANCH#";
  public static final String FRANCHISE = "FRANCHISE#";
  private final String tableName;
  private final Logger logger;
  private final DynamoDbAsyncTable<BranchEntity> branchTable;

  public BranchDynamoDB(@Value("${aws.dynamodb.franchiseTable}") String tableName,
                           DynamoDbEnhancedAsyncClient connectionFactory,
                           Logger logger) {
    this.tableName = tableName;
    this.branchTable = connectionFactory.table(tableName, TableSchema.fromBean(BranchEntity.class));
    this.logger = logger;
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
}
