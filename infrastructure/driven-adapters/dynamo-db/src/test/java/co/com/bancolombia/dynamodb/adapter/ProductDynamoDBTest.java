package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entity.BranchEntity;
import co.com.bancolombia.dynamodb.entity.FranchiseEntity;
import co.com.bancolombia.dynamodb.entity.ProductEntity;
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
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PagePublisher;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ProductDynamoDBTest {
  @Mock
  private DynamoDbEnhancedAsyncClient connectionFactory;
  @Mock
  private DynamoDbAsyncTable<BranchEntity> branchTable;
  @Mock
  private DynamoDbAsyncTable<ProductEntity> productTable;
  @Mock
  private DynamoDbAsyncIndex<ProductEntity> branchProductsByStockIndex;
  @Mock
  private Logger logger;
  @Mock
  private LogBuilder logBuilder;

  private ProductDynamoDB productDynamoDB;
  private final String tableName = "test-table";

  @BeforeEach
  void setUp() {
    when(connectionFactory.table(tableName, TableSchema.fromBean(BranchEntity.class)))
        .thenReturn(branchTable);
    when(connectionFactory.table(tableName, TableSchema.fromBean(ProductEntity.class)))
        .thenReturn(productTable);
    when(productTable.index("BranchProductsByStock"))
        .thenReturn(branchProductsByStockIndex);

    when(logger.with(any(Context.class))).thenReturn(logBuilder);
    when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
    doNothing().when(logBuilder).info(anyString());

    productDynamoDB = new ProductDynamoDB(tableName, connectionFactory, logger);
  }


  @Test
  void shouldSaveProduct() {
    Product product = Product.builder()
        .name("Test Product")
        .stock(10)
        .franchiseId("1")
        .branchId("1")
        .build();

    when(productTable.putItem(any(ProductEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(productDynamoDB.saveProduct(product))
        .expectNextMatches(result ->
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

    StepVerifier.create(productDynamoDB.findProductById("1", "1", "1"))
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

    StepVerifier.create(productDynamoDB.deleteProduct(product))
        .verifyComplete();
  }


  @Test
  void shouldReturnEmptyWhenProductNotFound() {
    when(productTable.getItem(any(Key.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(productDynamoDB.findProductById("1", "1", "1"))
        .verifyComplete();
  }


  @Test
  void shouldUpdateProductWithDeleteAndInsert() {
    Product existingProduct = Product.builder()
        .id("1")
        .name("Test Product")
        .stock(50)
        .franchiseId("1")
        .branchId("1")
        .build();

    Product updatedProduct = Product.builder()
        .id("1")
        .name("Test Product")
        .stock(100)
        .franchiseId("1")
        .branchId("1")
        .build();

    ProductEntity existingEntity = ProductEntity.builder()
        .pk("FRANCHISE#1")
        .sk("BRANCH#1#PRODUCT#1")
        .name("Test Product")
        .stock(50)
        .build();

    when(productTable.getItem(any(Key.class)))
        .thenReturn(CompletableFuture.completedFuture(existingEntity));
    when(productTable.deleteItem(any(ProductEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));
    when(productTable.putItem(any(ProductEntity.class)))
        .thenReturn(CompletableFuture.completedFuture(null));

    StepVerifier.create(productDynamoDB.saveProduct(updatedProduct))
        .expectNextMatches(result -> result.getStock().equals(100))
        .verifyComplete();

    verify(productTable, times(1)).deleteItem(any(ProductEntity.class));
    verify(productTable, times(1)).putItem(any(ProductEntity.class));
  }


  @Test
  void shouldFindTopProductsByFranchise() {
    BranchEntity branch1 = BranchEntity.builder()
        .pk("FRANCHISE#1")
        .sk("BRANCH#1")
        .name("Branch 1")
        .build();

    ProductEntity product1 = ProductEntity.builder()
        .pk("FRANCHISE#1")
        .sk("BRANCH#1#PRODUCT#1")
        .name("Product 1")
        .stock(100)
        .branchProductsKey("FRANCHISE#1#BRANCH#1")
        .stockSortKey("0000000000")
        .build();

    Page<BranchEntity> branchPage = mock(Page.class);
    when(branchPage.items()).thenReturn(Arrays.asList(branch1));

    PagePublisher<BranchEntity> branchPagePublisher = mock(PagePublisher.class);
    doAnswer(invocation -> {
      org.reactivestreams.Subscriber<Page<BranchEntity>> subscriber = invocation.getArgument(0);
      subscriber.onSubscribe(mock(org.reactivestreams.Subscription.class));
      subscriber.onNext(branchPage);
      subscriber.onComplete();
      return null;
    }).when(branchPagePublisher).subscribe(any(org.reactivestreams.Subscriber.class));

    Page<ProductEntity> productPage = mock(Page.class);
    when(productPage.items()).thenReturn(Arrays.asList(product1));

    PagePublisher<ProductEntity> productPagePublisher = mock(PagePublisher.class);
    doAnswer(invocation -> {
      org.reactivestreams.Subscriber<Page<ProductEntity>> subscriber = invocation.getArgument(0);
      subscriber.onSubscribe(mock(org.reactivestreams.Subscription.class));
      subscriber.onNext(productPage);
      subscriber.onComplete();
      return null;
    }).when(productPagePublisher).subscribe(any(org.reactivestreams.Subscriber.class));

    when(branchTable.query(any(QueryConditional.class)))
        .thenReturn(branchPagePublisher);
    when(branchProductsByStockIndex.query(any(QueryEnhancedRequest.class)))
        .thenReturn(productPagePublisher);

    StepVerifier.create(productDynamoDB.findTopProductsByFranchise("1"))
        .expectNextMatches(product -> product.getStock().equals(100))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenNoTopProducts() {
    Page<BranchEntity> page = mock(Page.class);
    when(page.items()).thenReturn(Arrays.asList());

    PagePublisher<BranchEntity> pagePublisher = mock(PagePublisher.class);
    doAnswer(invocation -> {
      org.reactivestreams.Subscriber<Page<BranchEntity>> subscriber = invocation.getArgument(0);
      subscriber.onSubscribe(mock(org.reactivestreams.Subscription.class));
      subscriber.onNext(page);
      subscriber.onComplete();
      return null;
    }).when(pagePublisher).subscribe(any(org.reactivestreams.Subscriber.class));

    when(branchTable.query(any(QueryConditional.class)))
        .thenReturn(pagePublisher);

    StepVerifier.create(productDynamoDB.findTopProductsByFranchise("1"))
        .verifyComplete();
  }
}
