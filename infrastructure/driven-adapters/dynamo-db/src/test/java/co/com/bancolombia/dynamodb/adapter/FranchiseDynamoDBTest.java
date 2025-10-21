package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.dynamodb.entity.ProductEntity;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FranchiseDynamoDBTest {

    @Mock
    private DynamoDbEnhancedAsyncClient connectionFactory;
    @Mock
    private DynamoDbAsyncTable<FranchiseEntity> franchiseTable;
    @Mock
    private DynamoDbAsyncTable<BranchEntity> branchTable;
    @Mock
    private DynamoDbAsyncTable<ProductEntity> productTable;
    @Mock
    private Logger logger;
    @Mock
    private LogBuilder logBuilder;

    private FranchiseDynamoDB repository;
    private final String tableName = "test-table";

    @BeforeEach
    void setUp() {
        when(connectionFactory.table(eq(tableName), eq(TableSchema.fromBean(FranchiseEntity.class))))
                .thenReturn(franchiseTable);
        when(connectionFactory.table(eq(tableName), eq(TableSchema.fromBean(BranchEntity.class))))
                .thenReturn(branchTable);
        when(connectionFactory.table(eq(tableName), eq(TableSchema.fromBean(ProductEntity.class))))
                .thenReturn(productTable);
        
        when(logger.with(any(Context.class))).thenReturn(logBuilder);
        when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
        doNothing().when(logBuilder).info(anyString());

        repository = new FranchiseDynamoDB(tableName, connectionFactory, logger);
    }

    @Test
    void shouldSaveFranchise() {
        Franchise franchise = Franchise.builder()
                .id("1")
                .name("Test Franchise")
                .build();

        when(franchiseTable.putItem(any(FranchiseEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.save(franchise))
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

        StepVerifier.create(repository.findById("1"))
                .expectNextMatches(franchise -> 
                    franchise.getId().equals("1") && 
                    franchise.getName().equals("Test Franchise"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenFranchiseNotFound() {
        when(franchiseTable.getItem(any(FranchiseEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.findById("1"))
                .verifyComplete();
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

        StepVerifier.create(repository.saveBranch(branch))
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

        StepVerifier.create(repository.findBranchById("1", "1"))
                .expectNextMatches(branch -> 
                    branch.getId().equals("1") && 
                    branch.getName().equals("Test Branch"))
                .verifyComplete();
    }

    @Test
    void shouldSaveProduct() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .stock(10)
                .franchiseId("1")
                .branchId("1")
                .build();

        when(productTable.putItem(any(ProductEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.saveProduct(product))
                .expectNextMatches(result -> 
                    result.getId().equals("1") && 
                    result.getName().equals("Test Product") &&
                    result.getStock().equals(10) &&
                    result.getFranchiseId().equals("1") &&
                    result.getBranchId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldFindProductById() {
        ProductEntity entity = ProductEntity.builder()
                .pk("FRANCHISE#1")
                .sk("BRANCH#1#PRODUCT#1")
                .name("Test Product")
                .stock(10)
                .build();

        when(productTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(entity));

        StepVerifier.create(repository.findProductById("1", "1", "1"))
                .expectNextMatches(product -> 
                    product.getId().equals("1") && 
                    product.getName().equals("Test Product") &&
                    product.getStock().equals(10))
                .verifyComplete();
    }

    @Test
    void shouldDeleteProduct() {
        Product product = Product.builder()
                .id("1")
                .name("Test Product")
                .stock(10)
                .franchiseId("1")
                .branchId("1")
                .build();

        when(productTable.deleteItem(any(ProductEntity.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.deleteProduct(product))
                .verifyComplete();
    }

    @Test
    void shouldFindProductsByFranchise() {
        ProductEntity product1 = ProductEntity.builder()
                .pk("FRANCHISE#1")
                .sk("BRANCH#1#PRODUCT#1")
                .name("Product 1")
                .stock(10)
                .build();

        Page<ProductEntity> page = mock(Page.class);
        when(page.items()).thenReturn(Arrays.asList(product1));

        PagePublisher<ProductEntity> pagePublisher = mock(PagePublisher.class);
        doAnswer(invocation -> {
            org.reactivestreams.Subscriber<Page<ProductEntity>> subscriber = invocation.getArgument(0);
            subscriber.onSubscribe(mock(org.reactivestreams.Subscription.class));
            subscriber.onNext(page);
            subscriber.onComplete();
            return null;
        }).when(pagePublisher).subscribe(any(org.reactivestreams.Subscriber.class));
        
        when(productTable.query(any(QueryConditional.class)))
                .thenReturn(pagePublisher);

        StepVerifier.create(repository.findProductsByFranchise("1"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenBranchNotFound() {
        when(branchTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.findBranchById("1", "1"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        when(productTable.getItem(any(Key.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(repository.findProductById("1", "1", "1"))
                .verifyComplete();
    }

    @Test
    void shouldHandleEmptyProductsList() {
        Page<ProductEntity> page = mock(Page.class);
        when(page.items()).thenReturn(Arrays.asList());

        PagePublisher<ProductEntity> pagePublisher = mock(PagePublisher.class);
        doAnswer(invocation -> {
            org.reactivestreams.Subscriber<Page<ProductEntity>> subscriber = invocation.getArgument(0);
            subscriber.onSubscribe(mock(org.reactivestreams.Subscription.class));
            subscriber.onNext(page);
            subscriber.onComplete();
            return null;
        }).when(pagePublisher).subscribe(any(org.reactivestreams.Subscriber.class));
        
        when(productTable.query(any(QueryConditional.class)))
                .thenReturn(pagePublisher);

        StepVerifier.create(repository.findProductsByFranchise("1"))
                .verifyComplete();
    }
}