package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import software.amazon.awssdk.enhanced.dynamodb.*;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchDynamoDBTest {
  @Mock
  private DynamoDbEnhancedAsyncClient connectionFactory;
  @Mock
  private DynamoDbAsyncTable<BranchEntity> branchTable;
  @Mock
  private Logger logger;
  @Mock
  private LogBuilder logBuilder;

  private BranchDynamoDB branchDynamoDB;
  private final String tableName = "test-table";

  @BeforeEach
  void setUp() {
    when(connectionFactory.table(tableName, TableSchema.fromBean(BranchEntity.class)))
        .thenReturn(branchTable);

    when(logger.with(any(Context.class))).thenReturn(logBuilder);
    when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
    doNothing().when(logBuilder).info(anyString());

    branchDynamoDB = new BranchDynamoDB(tableName, connectionFactory, logger);
  }

  @Test
  void shouldSaveBranch() {
    Branch branch = Branch.builder()
        .id("1")
        .name("Test Branch")
        .franchiseId("1")
        .build();

    when(branchTable.putItem(any(BranchEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(branchDynamoDB.saveBranch(branch))
        .expectNextMatches(result ->
            result.getId().equals("1") &&
                result.getName().equals("Test Branch") &&
                result.getFranchiseId().equals("1"))
        .verifyComplete();
  }

  @Test
  void shouldFindBranchById() {
    BranchEntity entity = BranchEntity.builder()
        .pk("FRANCHISE#1")
        .sk("BRANCH#1")
        .name("Test Branch")
        .build();

    when(branchTable.getItem(any(Key.class)))
        .thenReturn(CompletableFuture.completedFuture(entity));

    StepVerifier.create(branchDynamoDB.findBranchById("1", "1"))
        .expectNextMatches(branch ->
            branch.getId().equals("1") &&
                branch.getName().equals("Test Branch"))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenBranchNotFound() {
    when(branchTable.getItem(any(Key.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(branchDynamoDB.findBranchById("1", "1"))
        .verifyComplete();
  }
}
