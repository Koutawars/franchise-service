package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateProduct;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.dto.UpdateStock;
import co.com.bancolombia.api.handler.ProductHandler;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
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
class ProductHandlerTest {

  @Mock
  private ProductUseCase productUseCase;

  @Mock
  private RequestValidator validator;

  private ProductHandler productHandler;

  @BeforeEach
  void setUp() {
    productHandler = new ProductHandler(productUseCase, validator);
  }

  @Test
  void listenPOSTCreateProduct_Success() {
    CreateProduct request = CreateProduct.builder().name("Test Product").stock(10).build();
    Product product = Product.builder().id("product-1").name("Test Product").stock(10).build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .pathVariable("branchId", "branch-1")
        .body(Mono.just(request));

    when(validator.validate(any(CreateProduct.class))).thenReturn(Mono.just(request));
    when(productUseCase.addProduct(any(Product.class))).thenReturn(Mono.just(product));

    StepVerifier.create(productHandler.listenPOSTCreateProduct(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenDELETEProduct_Success() {
    Product product = Product.builder().id("product-1").name("Test Product").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .pathVariable("branchId", "branch-1")
        .pathVariable("productId", "product-1")
        .build();

    when(productUseCase.deleteProduct(anyString(), anyString(), anyString())).thenReturn(Mono.just(product));

    StepVerifier.create(productHandler.listenDELETEProduct(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPATCHUpdateStock_Success() {
    UpdateStock request = UpdateStock.builder().stock(20).build();
    Product product = Product.builder().id("product-1").stock(20).build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .pathVariable("branchId", "branch-1")
        .pathVariable("productId", "product-1")
        .body(Mono.just(request));

    when(validator.validate(any(UpdateStock.class))).thenReturn(Mono.just(request));
    when(productUseCase.modifyStock(anyString(), anyString(), anyString(), any(Integer.class)))
        .thenReturn(Mono.just(product));

    StepVerifier.create(productHandler.listenPATCHUpdateStock(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenGETTopProducts_Success() {
    Product product = Product.builder().id("product-1").name("Test Product").stock(100).build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .build();

    when(productUseCase.getTopProductsPerBranch(anyString()))
        .thenReturn(Flux.fromIterable(List.of(product)));

    StepVerifier.create(productHandler.listenGETTopProducts(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPATCHUpdateProductName_Success() {
    UpdateName request = UpdateName.builder().name("Updated Product").build();
    Product product = Product.builder().id("product-1").name("Updated Product").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .pathVariable("branchId", "branch-1")
        .pathVariable("productId", "product-1")
        .body(Mono.just(request));

    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(productUseCase.updateNameProduct(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(Mono.just(product));

    StepVerifier.create(productHandler.listenPATCHUpdateProductName(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }
}
