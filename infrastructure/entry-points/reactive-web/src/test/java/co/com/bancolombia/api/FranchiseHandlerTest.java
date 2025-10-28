package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateFranchise;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.handler.FranchiseHandler;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

  @Mock
  private FranchiseUseCase franchiseUseCase;

  @Mock
  private RequestValidator validator;

  private FranchiseHandler franchiseHandler;

  @BeforeEach
  void setUp() {
    franchiseHandler = new FranchiseHandler(franchiseUseCase, validator);
  }

  @Test
  void listenPOSTCreateFranchise_Success() {
    CreateFranchise request = CreateFranchise.builder().name("Test Franchise").build();
    Franchise franchise = Franchise.builder().id("franchise-1").name("Test Franchise").build();
    ServerRequest serverRequest = MockServerRequest.builder().body(Mono.just(request));

    when(validator.validate(any(CreateFranchise.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.addFranchise(any(Franchise.class))).thenReturn(Mono.just(franchise));

    StepVerifier.create(franchiseHandler.listenPOSTCreateFranchise(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPATCHUpdateFranchiseName_Success() {
    UpdateName request = UpdateName.builder().name("Updated Franchise").build();
    Franchise franchise = Franchise.builder().id("franchise-1").name("Updated Franchise").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .body(Mono.just(request));

    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(franchiseUseCase.updateNameFranchise(anyString(), anyString())).thenReturn(Mono.just(franchise));

    StepVerifier.create(franchiseHandler.listenPATCHUpdateFranchiseName(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPOSTCreateFranchise_ValidationError() {
    CreateFranchise request = CreateFranchise.builder().name("").build();
    ServerRequest serverRequest = MockServerRequest.builder().body(Mono.just(request));

    when(validator.validate(any(CreateFranchise.class)))
        .thenReturn(Mono.error(new RuntimeException("Validation failed")));

    StepVerifier.create(franchiseHandler.listenPOSTCreateFranchise(serverRequest))
        .expectError(RuntimeException.class)
        .verify();
  }
}
