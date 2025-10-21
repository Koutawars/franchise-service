package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.*;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, RouterRestTest.TestConfig.class})
@WebFluxTest
class RouterRestTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  private FranchiseUseCase franchiseUseCase;

  @Autowired
  private RequestValidator validator;

  @TestConfiguration
  static class TestConfig {
    @Bean
    public FranchiseUseCase franchiseUseCase() {
      return mock(FranchiseUseCase.class);
    }

    @Bean
    public RequestValidator requestValidator() {
      return mock(RequestValidator.class);
    }
  }

  @Test
  void testCreateFranchise() {
    CreateFranchise request = CreateFranchise.builder().name("Test Franchise").build();
    Franchise franchise = Franchise.builder().id("1").name("Test Franchise").build();
    
    when(validator.validate(any(CreateFranchise.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.addFranchise(any(Franchise.class))).thenReturn(Mono.just(franchise));

    webTestClient.post()
        .uri("/api/v1/franchises")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testCreateBranch() {
    CreateBranch request = CreateBranch.builder().name("Test Branch").build();
    Branch branch = Branch.builder().id("1").name("Test Branch").franchiseId("franchise-1").build();
    
    when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.addBranch(any(Branch.class))).thenReturn(Mono.just(branch));

    webTestClient.post()
        .uri("/api/v1/franchises/franchise-1/branches")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testCreateProduct() {
    CreateProduct request = CreateProduct.builder().name("Test Product").stock(10).build();
    Product product = Product.builder().id("1").name("Test Product").stock(10).build();
    
    when(validator.validate(any(CreateProduct.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.addProduct(any(Product.class))).thenReturn(Mono.just(product));

    webTestClient.post()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/products")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testDeleteProduct() {
    Product product = Product.builder().id("product-1").name("Test Product").build();
    
    when(franchiseUseCase.deleteProduct(anyString(), anyString(), anyString()))
        .thenReturn(Mono.just(product));

    webTestClient.delete()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/products/product-1")
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testUpdateStock() {
    UpdateStock request = UpdateStock.builder().stock(20).build();
    Product product = Product.builder().id("product-1").stock(20).build();
    
    when(validator.validate(any(UpdateStock.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.modifyStock(anyString(), anyString(), anyString(), any(Integer.class)))
        .thenReturn(Mono.just(product));

    webTestClient.patch()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/products/product-1/stock")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testGetTopProducts() {
    Product product = Product.builder().id("product-1").name("Top Product").stock(100).build();
    
    when(franchiseUseCase.getTopProductsPerBranch(anyString()))
        .thenReturn(Flux.fromIterable(List.of(product)));

    webTestClient.get()
        .uri("/api/v1/franchises/franchise-1/top-products")
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testUpdateFranchiseName() {
    UpdateName request = UpdateName.builder().name("Updated Franchise").build();
    Franchise franchise = Franchise.builder().id("franchise-1").name("Updated Franchise").build();
    
    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.updateNameFranchise(anyString(), anyString()))
        .thenReturn(Mono.just(franchise));

    webTestClient.patch()
        .uri("/api/v1/franchises/franchise-1/name")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testUpdateBranchName() {
    UpdateName request = UpdateName.builder().name("Updated Branch").build();
    Branch branch = Branch.builder().id("branch-1").name("Updated Branch").build();
    
    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.updateNameBranch(anyString(), anyString(), anyString()))
        .thenReturn(Mono.just(branch));

    webTestClient.patch()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/name")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testUpdateProductName() {
    UpdateName request = UpdateName.builder().name("Updated Product").build();
    Product product = Product.builder().id("product-1").name("Updated Product").build();
    
    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.updateNameProduct(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Mono.just(product));

    webTestClient.patch()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/products/product-1/name")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk()
        .expectBody(StandardResponse.class);
  }

  @Test
  void testCreateFranchiseValidationError() {
    CreateFranchise request = CreateFranchise.builder().name("").build();
    
    when(validator.validate(any(CreateFranchise.class)))
        .thenReturn(Mono.error(new RuntimeException("Validation error")));

    webTestClient.post()
        .uri("/api/v1/franchises")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  void testCreateBranchUseCaseError() {
    CreateBranch request = CreateBranch.builder().name("Test Branch").build();
    
    when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.addBranch(any(Branch.class)))
        .thenReturn(Mono.error(new RuntimeException("UseCase error")));

    webTestClient.post()
        .uri("/api/v1/franchises/franchise-1/branches")
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(request)
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  void testDeleteProductError() {
    when(franchiseUseCase.deleteProduct(anyString(), anyString(), anyString()))
        .thenReturn(Mono.error(new RuntimeException("Delete error")));

    webTestClient.delete()
        .uri("/api/v1/franchises/franchise-1/branches/branch-1/products/product-1")
        .exchange()
        .expectStatus().is5xxServerError();
  }
}
