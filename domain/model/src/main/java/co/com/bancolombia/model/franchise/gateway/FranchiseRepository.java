package co.com.bancolombia.model.franchise.gateway;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
  Mono<Franchise> save(Franchise franchise);
  Mono<Franchise> findById(String id);
  Mono<Branch> saveBranch(Branch branch);
  Mono<Branch> findBranchById(String id, String franchiseId);
  Mono<Product> saveProduct(Product product);
  Mono<Product> findProductById(String id, String branchId, String franchiseId);
  Mono<Void> deleteProduct(Product product);
  Flux<Product> findProductsByFranchise(String franchiseId);
}
