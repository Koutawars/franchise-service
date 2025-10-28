package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.gateway.BranchRepository;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {
  private final BranchRepository branchRepository;
  private final FranchiseRepository franchiseRepository;
  private final Logger logger;
  public static final String BRANCH_ID = "branchId";
  public static final String FRANCHISE_ID = "franchiseId";

  public Mono<Branch> addBranch(Branch branch) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("branch", branch);

      return franchiseRepository.findById(branch.getFranchiseId())
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(branchRepository.save(branch))
          .doOnSubscribe(unused -> logBuilder.info("Adding branch"))
          .doOnSuccess(unused -> logBuilder.info("Branch added"))
          .doOnError(error -> logBuilder.error("Error adding branch"));
    });
  }
  public Mono<Branch> updateNameBranch(String branchId, String franchiseId, String name) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(BRANCH_ID, branchId)
          .key(FRANCHISE_ID, franchiseId)
          .key("name", name);

      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMap(franchise -> branchRepository.findById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .flatMap(branch -> {
            branch.setName(name);
            return branchRepository.save(branch);
          })
          .doOnSubscribe(unused -> logBuilder.info("Updating branch name"))
          .doOnSuccess(unused -> logBuilder.info("Branch name updated"))
          .doOnError(error -> logBuilder.error("Error updating branch name"));
    });
  }
}
