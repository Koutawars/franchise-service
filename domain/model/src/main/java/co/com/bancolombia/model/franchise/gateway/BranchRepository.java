package co.com.bancolombia.model.franchise.gateway;

import co.com.bancolombia.model.franchise.Branch;
import reactor.core.publisher.Mono;

public interface BranchRepository {
  Mono<Branch> saveBranch(Branch branch);
  Mono<Branch> findBranchById(String id, String franchiseId);
}
