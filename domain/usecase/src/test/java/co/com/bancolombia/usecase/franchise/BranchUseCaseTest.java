package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BranchUseCaseTest {

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

  private BranchUseCase branchUseCase;

  private Franchise franchise;
  private Branch branch;

  @BeforeEach
  void setUp() {
    branchUseCase = new BranchUseCase(branchRepository, franchiseRepository, logger);

    franchise = Franchise.builder()
        .id("franchise-1")
        .name("Test Franchise")
        .build();

    branch = Branch.builder()
        .id("branch-1")
        .name("Test Branch")
        .franchiseId("franchise-1")
        .build();

    when(logger.with(any(Context.class))).thenReturn(logBuilder);
    when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
  }


  @Test
  void addBranch_Success() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.saveBranch(branch)).thenReturn(Mono.just(branch));

    StepVerifier.create(branchUseCase.addBranch(branch))
        .expectNext(branch)
        .verifyComplete();
  }

  @Test
  void addBranch_FranchiseNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());
    when(branchRepository.saveBranch(branch)).thenReturn(Mono.just(branch));

    StepVerifier.create(branchUseCase.addBranch(branch))
        .expectError(FranchiseNotFoundException.class)
        .verify();
  }
  @Test
  void updateNameBranch_Success() {
    Branch updatedBranch = Branch.builder()
        .id("branch-1")
        .name("Updated Branch")
        .franchiseId("franchise-1")
        .build();

    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.just(franchise));
    when(branchRepository.findBranchById("branch-1", "franchise-1")).thenReturn(Mono.just(branch));
    when(branchRepository.saveBranch(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

    StepVerifier.create(branchUseCase.updateNameBranch("branch-1", "franchise-1", "Updated Branch"))
        .expectNext(updatedBranch)
        .verifyComplete();
  }

  @Test
  void updateNameBranch_FranchiseNotFound() {
    when(franchiseRepository.findById("franchise-1")).thenReturn(Mono.empty());

    StepVerifier.create(branchUseCase.updateNameBranch("branch-1", "franchise-1", "Updated Branch"))
        .expectError(FranchiseNotFoundException.class)
        .verify();
  }
}
