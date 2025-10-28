package co.com.bancolombia.api;

import co.com.bancolombia.api.dto.CreateBranch;
import co.com.bancolombia.api.dto.UpdateName;
import co.com.bancolombia.api.handler.BranchHandler;
import co.com.bancolombia.api.utils.RequestValidator;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
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
class BranchHandlerTest {

  @Mock
  private BranchUseCase branchUseCase;

  @Mock
  private RequestValidator validator;

  private BranchHandler branchHandler;

  @BeforeEach
  void setUp() {
    branchHandler = new BranchHandler(branchUseCase, validator);
  }

  @Test
  void listenPOSTCreateBranch_Success() {
    CreateBranch request = CreateBranch.builder().name("Test Branch").build();
    Branch branch = Branch.builder().id("branch-1").name("Test Branch").franchiseId("franchise-1").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .body(Mono.just(request));

    when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
    when(branchUseCase.addBranch(any(Branch.class))).thenReturn(Mono.just(branch));

    StepVerifier.create(branchHandler.listenPOSTCreateBranch(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPATCHUpdateBranchName_Success() {
    UpdateName request = UpdateName.builder().name("Updated Branch").build();
    Branch branch = Branch.builder().id("branch-1").name("Updated Branch").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .pathVariable("branchId", "branch-1")
        .body(Mono.just(request));

    when(validator.validate(any(UpdateName.class))).thenReturn(Mono.just(request));
    when(branchUseCase.updateNameBranch(anyString(), anyString(), anyString())).thenReturn(Mono.just(branch));

    StepVerifier.create(branchHandler.listenPATCHUpdateBranchName(serverRequest))
        .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
        .verifyComplete();
  }

  @Test
  void listenPOSTCreateBranch_UseCaseError() {
    CreateBranch request = CreateBranch.builder().name("Test Branch").build();
    ServerRequest serverRequest = MockServerRequest.builder()
        .pathVariable("franchiseId", "franchise-1")
        .body(Mono.just(request));

    when(validator.validate(any(CreateBranch.class))).thenReturn(Mono.just(request));
    when(branchUseCase.addBranch(any(Branch.class)))
        .thenReturn(Mono.error(new RuntimeException("UseCase error")));

    StepVerifier.create(branchHandler.listenPOSTCreateBranch(serverRequest))
        .expectError(RuntimeException.class)
        .verify();
  }
}
