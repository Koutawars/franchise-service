package co.com.bancolombia.model.franchise.gateway;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FranchiseRepository {
  public Mono<Franchise> save(Franchise franchise);
  public Mono<Franchise> findById(String id);
  public Mono<Branch> saveBranch(Branch branch);
  public Mono<Branch> findBranchById(String id, String franchiseId);
  public Mono<Product> saveProduct(Product product);
  public Mono<Product> findProductById(String id, String branchId, String franchiseId);
  public Mono<Void> deleteProduct(Product product);
  public Flux<Product> findProductsByFranchise(String franchiseId);
}
