package co.com.bancolombia.model.franchise.gateway;

import co.com.bancolombia.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
  Mono<Product> save(Product product);
  Mono<Product> findById(String id, String branchId, String franchiseId);
  Mono<Void> delete(Product product);
  Flux<Product> findTopProductsByFranchise(String franchiseId);
}
