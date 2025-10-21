package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
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
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;
    
    @Mock
    private Logger logger;
    
    @Mock
    private LogBuilder logBuilder;
    
    private FranchiseUseCase franchiseUseCase;
    
    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        franchiseUseCase = new FranchiseUseCase(franchiseRepository, logger);
        
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
    void addFranchise_Success() {
        when(franchiseRepository.save(franchise)).thenReturn(Mono.just(franchise));

        StepVerifier.create(franchiseUseCase.addFranchise(franchise))
                .expectNext(franchise)
                .verifyComplete();
    }

    @Test
    void addBranch_Success() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.saveBranch(branch)).thenReturn(Mono.just(branch));

        StepVerifier.create(franchiseUseCase.addBranch(branch))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void addBranch_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());
        when(franchiseRepository.saveBranch(branch)).thenReturn(Mono.just(branch));

        StepVerifier.create(franchiseUseCase.addBranch(branch))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void addProduct_Success() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.saveProduct(product)).thenReturn(Mono.just(product));

        StepVerifier.create(franchiseUseCase.addProduct(product))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void addProduct_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.saveProduct(product)).thenReturn(Mono.just(product));

        StepVerifier.create(franchiseUseCase.addProduct(product))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void addProduct_BranchNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.empty());
        when(franchiseRepository.saveProduct(product)).thenReturn(Mono.just(product));

        StepVerifier.create(franchiseUseCase.addProduct(product))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void deleteProduct_Success() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
        when(franchiseRepository.deleteProduct(product)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.deleteProduct("product-1", "branch-1", "franchise-1"))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void deleteProduct_ProductNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.deleteProduct("product-1", "branch-1", "franchise-1"))
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
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
        when(franchiseRepository.saveProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(franchiseUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
                .expectNext(updatedProduct)
                .verifyComplete();
    }

    @Test
    void getTopProductsPerBranch_Success() {
        Product product1 = Product.builder().id("p1").stock(10).branchId("branch-1").build();
        Product product2 = Product.builder().id("p2").stock(20).branchId("branch-1").build();
        Product product3 = Product.builder().id("p3").stock(15).branchId("branch-2").build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findProductsByFranchise("franchise-1"))
                .thenReturn(Flux.fromIterable(List.of(product1, product2, product3)));

        StepVerifier.create(franchiseUseCase.getTopProductsPerBranch("franchise-1"))
                .expectNext(product2) // highest stock in branch-1
                .expectNext(product3) // highest stock in branch-2
                .verifyComplete();
    }

    @Test
    void getTopProductsPerBranch_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.getTopProductsPerBranch("franchise-1"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void updateNameFranchise_Success() {
        Franchise updatedFranchise = Franchise.builder()
                .id("franchise-1")
                .name("Updated Name")
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updatedFranchise));

        StepVerifier.create(franchiseUseCase.updateNameFranchise("franchise-1", "Updated Name"))
                .expectNext(updatedFranchise)
                .verifyComplete();
    }

    @Test
    void updateNameBranch_Success() {
        Branch updatedBranch = Branch.builder()
                .id("branch-1")
                .name("Updated Branch")
                .franchiseId("franchise-1")
                .build();

        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.saveBranch(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        StepVerifier.create(franchiseUseCase.updateNameBranch("branch-1", "franchise-1", "Updated Branch"))
                .expectNext(updatedBranch)
                .verifyComplete();
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
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.just(product));
        when(franchiseRepository.saveProduct(any(Product.class))).thenReturn(Mono.just(updatedProduct));

        StepVerifier.create(franchiseUseCase.updateNameProduct("product-1", "branch-1", "franchise-1", "Updated Product"))
                .expectNext(updatedProduct)
                .verifyComplete();
    }

    @Test
    void updateNameFranchise_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateNameFranchise("franchise-1", "Updated Name"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void updateNameBranch_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateNameBranch("branch-1", "franchise-1", "Updated Branch"))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void modifyStock_ProductNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
        when(franchiseRepository.findProductById("product-1", "branch-1", "franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    void modifyStock_FranchiseNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
                .expectError(FranchiseNotFoundException.class)
                .verify();
    }

    @Test
    void modifyStock_BranchNotFound() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.modifyStock("product-1", "branch-1", "franchise-1", 20))
                .expectError(BranchNotFoundException.class)
                .verify();
    }

    @Test
    void getTopProductsPerBranch_EmptyProducts() {
        when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
        when(franchiseRepository.findProductsByFranchise("franchise-1"))
                .thenReturn(Flux.empty());

        StepVerifier.create(franchiseUseCase.getTopProductsPerBranch("franchise-1"))
                .verifyComplete();
    }
}