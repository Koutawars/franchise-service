package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.model.franchise.Franchise;
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
import software.amazon.awssdk.enhanced.dynamodb.model.*;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class FranchiseDynamoDBTest {

  @Mock
  private DynamoDbEnhancedAsyncClient connectionFactory;
  @Mock
  private DynamoDbAsyncTable<FranchiseEntity> franchiseTable;
  @Mock
  private Logger logger;
  @Mock
  private LogBuilder logBuilder;
  @Mock
  private co.com.bancolombia.dynamodb.cache.FranchiseCache franchiseCache;

  private FranchiseDynamoDB franchiseDynamoDB;
  private final String tableName = "test-table";

  @BeforeEach
  void setUp() {
    when(connectionFactory.table(tableName, TableSchema.fromBean(FranchiseEntity.class)))
        .thenReturn(franchiseTable);

    lenient().when(logger.with(any(Context.class))).thenReturn(logBuilder);
    lenient().when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
    lenient().doNothing().when(logBuilder).info(anyString());
    lenient().doNothing().when(logBuilder).error(anyString(), any(Throwable.class));

    franchiseDynamoDB = new FranchiseDynamoDB(tableName, connectionFactory, logger, franchiseCache);
  }

  @Test
  void shouldSaveFranchise() {
    Franchise franchise = Franchise.builder()
        .id("1")
        .name("Test Franchise")
        .build();

    when(franchiseTable.putItem(any(FranchiseEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(franchiseDynamoDB.save(franchise))
        .expectNextMatches(result ->
            result.getId().equals("1") &&
                result.getName().equals("Test Franchise"))
        .verifyComplete();
  }

  @Test
  void shouldFindFranchiseById() {
    FranchiseEntity entity = FranchiseEntity.builder()
        .pk("FRANCHISE#1")
        .sk("METADATA")
        .name("Test Franchise")
        .build();

    when(franchiseTable.getItem(any(FranchiseEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(entity));

    StepVerifier.create(franchiseDynamoDB.findById("1"))
        .expectNextMatches(franchise ->
            franchise.getId().equals("1") &&
                franchise.getName().equals("Test Franchise"))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenFranchiseNotFound() {
    when(franchiseTable.getItem(any(FranchiseEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(franchiseDynamoDB.findById("1"))
        .verifyComplete();
  }

  @Test
  void shouldReturnCachedFranchiseOnFallback() {
    Franchise franchise = Franchise.builder()
        .id("1")
        .name("Cached Franchise")
        .build();

    when(franchiseCache.get("1")).thenReturn(franchise);

    StepVerifier.create(franchiseDynamoDB.fallbackFindById("1", new RuntimeException("DynamoDB error")))
        .expectNextMatches(f -> f.getId().equals("1") && f.getName().equals("Cached Franchise"))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyOnFallbackWhenNoCacheAvailable() {
    when(franchiseCache.get("1")).thenReturn(null);

    StepVerifier.create(franchiseDynamoDB.fallbackFindById("1", new RuntimeException("DynamoDB error")))
        .verifyComplete();
  }
}