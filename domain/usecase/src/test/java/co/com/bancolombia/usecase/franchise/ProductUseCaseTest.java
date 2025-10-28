package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.BranchRepository;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.franchise.gateway.ProductRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

  @Mock
  private FranchiseRepository franchiseRepository;

  @Mock
  private ProductRepository productRepository;

  @Mock
  private BranchRepository branchRepository;

  @Mock
  private Logger logger;

  @Mock
  private LogBuilder logBuilder;

  private ProductUseCase productUseCase;

  private Franchise franchise;
  private Branch branch;
  private Product product;

  @BeforeEach
  void setUp() {
    productUseCase = new ProductUseCase(franchiseRepository, branchRepository, productRepository, logger);

    franchise = Franchise.builder()
        .id("franchise-1")
        .name("Test Franchise")
        .build();

    branch = Branch.builder()
        .id("branch-1")
        .name("Test Branch")
        .franchiseId("franchise-1")
        .build();

    product = Product.builder()
        .id("product-1")
        .name("Test Product")
        .stock(10)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    when(logger.with(any(Context.class))).thenReturn(logBuilder);
    when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
  }

  @Test
  void addProduct_Success() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.saveProduct(product)).thenReturn(Mono.just(product));

    StepVerifier.create(productUseCase.addProduct(product))
        .expectNext(product)
        .verifyComplete();
  }

  @Test
  void addProduct_FranchiseNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.saveProduct(product)).thenReturn(Mono.just(product));

    StepVerifier.create(productUseCase.addProduct(product))
        .expectError(FranchiseNotFoundException.class)
        .verify();
  }

  @Test
  void addProduct_BranchNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.empty());
    when(productRepository.saveProduct(product)).thenReturn(Mono.just(product));

    StepVerifier.create(productUseCase.addProduct(product))
        .expectError(BranchNotFoundException.class)
        .verify();
  }

  @Test
  void deleteProduct_Success() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
    when(productRepository.deleteProduct(product)).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.deleteProduct("product-1", "branch-1", "franchise-1"))
        .expectNext(product)
        .verifyComplete();
  }

  @Test
  void deleteProduct_ProductNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.deleteProduct("product-1", "branch-1", "franchise-1"))
        .expectError(ProductNotFoundException.class)
        .verify();
  }

  @Test
  void modifyStock_Success() {
    Product updatedProduct = Product.builder()
        .id("product-1")
        .name("Test Product")
        .stock(20)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
    when(productRepository.saveProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

    StepVerifier.create(productUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
        .expectNext(updatedProduct)
        .verifyComplete();
  }

  @Test
  void getTopProductsPerBranch_Success() {
    Product product1 = Product.builder().id("p1").stock(20).branchId("branch-1").build();
    Product product2 = Product.builder().id("p2").stock(15).branchId("branch-2").build();

    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(productRepository.findTopProductsByFranchise("franchise-1"))
        .thenReturn(Flux.fromIterable(List.of(product1, product2)));

    StepVerifier.create(productUseCase.getTopProductsPerBranch("franchise-1"))
        .expectNext(product1)
        .expectNext(product2)
        .verifyComplete();
  }

  @Test
  void getTopProductsPerBranch_FranchiseNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.getTopProductsPerBranch("franchise-1"))
        .expectError(FranchiseNotFoundException.class)
        .verify();
  }

  @Test
  void updateNameProduct_Success() {
    Product updatedProduct = Product.builder()
        .id("product-1")
        .name("Updated Product")
        .stock(10)
        .franchiseId("franchise-1")
        .branchId("branch-1")
        .build();

    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
    when(productRepository.saveProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

    StepVerifier.create(productUseCase.updateNameProduct("product-1", "branch-1", "franchise-1", "Updated Product"))
        .expectNext(updatedProduct)
        .verifyComplete();
  }
  @Test
  void modifyStock_ProductNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(productRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
        .expectError(ProductNotFoundException.class)
        .verify();
  }

  @Test
  void modifyStock_FranchiseNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
        .expectError(FranchiseNotFoundException.class)
        .verify();
  }

  @Test
  void modifyStock_BranchNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(productUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
        .expectError(BranchNotFoundException.class)
        .verify();
  }

  @Test
  void getTopProductsPerBranch_EmptyProducts() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(productRepository.findTopProductsByFranchise("franchise-1"))
        .thenReturn(Flux.empty());

    StepVerifier.create(productUseCase.getTopProductsPerBranch("franchise-1"))
        .verifyComplete();
  }
}
