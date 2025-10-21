package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HandlerTest {

    @Mock
    private FranchiseUseCase franchiseUseCase;
    
    @Mock
    private RequestValidator validator;
    
    private Handler handler;
    
    private Franchise franchise;
    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        handler = new Handler(franchiseUseCase, validator);
        
        franchise = Franchise.builder().id("franchise-1").name("Test Franchise").build();
        branch = Branch.builder().id("branch-1").name("Test Branch").franchiseId("franchise-1").build();
        product = Product.builder().id("product-1").name("Test Product").stock(10).build();
    }

    @Test
    void listenPOSTCreateFranchise_Success() {
        CreateFranchise request = CreateFranchise.builder().name("Test Franchise").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));
        
        when(validator.validate(any(CreateFranchise.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.addFranchise(any(Franchise.class))).thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.listenPOSTCreateFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPOSTCreateBranch_Success() {
        CreateBranch request = CreateBranch.builder().name("Test Branch").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.addBranch(any(Branch.class))).thenReturn(Mono.just(branch));

        StepVerifier.create(handler.listenPOSTCreateBranch(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPOSTCreateProduct_Success() {
        CreateProduct request = CreateProduct.builder().name("Test Product").stock(10).build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(CreateProduct.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.addProduct(any(Product.class))).thenReturn(Mono.just(product));

        StepVerifier.create(handler.listenPOSTCreateProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenDELETEProduct_Success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .pathVariable("productId", "product-1")
                .build();
        
        when(franchiseUseCase.deleteProduct(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(handler.listenDELETEProduct(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPATCHUpdateStock_Success() {
        UpdateStock request = UpdateStock.builder().stock(20).build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .pathVariable("productId", "product-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(UpdateStock.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.modifyStock(anyString(), anyString(), anyString(), any(Integer.class)))
                .thenReturn(Mono.just(product));

        StepVerifier.create(handler.listenPATCHUpdateStock(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenGETTopProducts_Success() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .build();
        
        when(franchiseUseCase.getTopProductsPerBranch(anyString()))
                .thenReturn(Flux.fromIterable(List.of(product)));

        StepVerifier.create(handler.listenGETTopProducts(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPATCHUpdateFranchiseName_Success() {
        UpdateName request = UpdateName.builder().name("Updated Franchise").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.updateNameFranchise(anyString(), anyString()))
                .thenReturn(Mono.just(franchise));

        StepVerifier.create(handler.listenPATCHUpdateFranchiseName(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPATCHUpdateBranchName_Success() {
        UpdateName request = UpdateName.builder().name("Updated Branch").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.updateNameBranch(anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(branch));

        StepVerifier.create(handler.listenPATCHUpdateBranchName(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPATCHUpdateProductName_Success() {
        UpdateName request = UpdateName.builder().name("Updated Product").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .pathVariable("productId", "product-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.updateNameProduct(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Mono.just(product));

        StepVerifier.create(handler.listenPATCHUpdateProductName(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void listenPOSTCreateFranchise_ValidationError() {
        CreateFranchise request = CreateFranchise.builder().name("").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));
        
        when(validator.validate(any(CreateFranchise.class)))
                .thenReturn(Mono.error(new RuntimeException("Validation failed")));

        StepVerifier.create(handler.listenPOSTCreateFranchise(serverRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void listenPOSTCreateBranch_UseCaseError() {
        CreateBranch request = CreateBranch.builder().name("Test Branch").build();
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .body(Mono.just(request));
        
        when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
        when(franchiseUseCase.addBranch(any(Branch.class)))
                .thenReturn(Mono.error(new RuntimeException("UseCase error")));

        StepVerifier.create(handler.listenPOSTCreateBranch(serverRequest))
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    void listenDELETEProduct_Error() {
        ServerRequest serverRequest = MockServerRequest.builder()
                .pathVariable("franchiseId", "franchise-1")
                .pathVariable("branchId", "branch-1")
                .pathVariable("productId", "product-1")
                .build();
        
        when(franchiseUseCase.deleteProduct(anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new RuntimeException("Delete failed")));

        StepVerifier.create(handler.listenDELETEProduct(serverRequest))
                .expectError(RuntimeException.class)
                .verify();
    }
}