package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.BranchRepository;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.franchise.gateway.ProductRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class ProductUseCase {
  private final FranchiseRepository franchiseRepository;
  private final BranchRepository branchRepository;
  private final ProductRepository productRepository;
  private final Logger logger;

  public static final String PRODUCT_ID = "productId";
  public static final String BRANCH_ID = "branchId";
  public static final String FRANCHISE_ID = "franchiseId";

  public Mono<Product> addProduct(Product product) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("product", product);

      return franchiseRepository.findById(product.getFranchiseId())
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(branchRepository.findById(product.getBranchId(), product.getFranchiseId()))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(productRepository.save(product))
          .doOnSubscribe(unused -> logBuilder.info("Adding product"))
          .doOnSuccess(unused -> logBuilder.info("Product added"))
          .doOnError(error -> logBuilder.error("Error adding product"));
    });
  }

  public Mono<Product> deleteProduct(String productId, String branchId, String franchiseId) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(PRODUCT_ID, productId)
          .key(BRANCH_ID, branchId)
          .key(FRANCHISE_ID, franchiseId);

      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(branchRepository.findById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(productRepository.findById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new ProductNotFoundException()))
          .flatMap(product -> productRepository.delete(product)
              .then(Mono.just(product)))
          .doOnSubscribe(unused -> logBuilder.info("Deleting product"))
          .doOnSuccess(unused -> logBuilder.info("Product deleted"))
          .doOnError(error -> logBuilder.error("Error deleting product"));
    });
  }


  public Mono<Product> modifyStock(String productId, String branchId, String franchiseId, Integer stock) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(PRODUCT_ID, productId)
          .key(BRANCH_ID, branchId)
          .key(FRANCHISE_ID, franchiseId)
          .key("stock", stock);

      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMap(franchise -> branchRepository.findById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .flatMap(branch -> productRepository.findById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new ProductNotFoundException()))
          .flatMap(product -> {
            product.setStock(stock);
            return productRepository.save(product);
          })
          .doOnSubscribe(unused -> logBuilder.info("Modifying product stock"))
          .doOnSuccess(unused -> logBuilder.info("Product stock modified"))
          .doOnError(error -> logBuilder.error("Error modifying product stock"));
    });
  }


  public Flux<Product> getTopProductsPerBranch(String franchiseId) {
    return Flux.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(FRANCHISE_ID, franchiseId);
      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMapMany(franchise -> productRepository.findTopProductsByFranchise(franchiseId))
          .doOnSubscribe(unused -> logBuilder.info("Getting top products per branch"))
          .doOnError(error -> logBuilder.error("Error getting top products per branch"))
          .doOnComplete(() -> logBuilder.info("Top products per branch retrieved"));
    });
  }


  public Mono<Product> updateNameProduct(String productId, String branchId, String franchiseId, String name) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(PRODUCT_ID, productId)
          .key(BRANCH_ID, branchId)
          .key(FRANCHISE_ID, franchiseId)
          .key("name", name);
      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(branchRepository.findById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(productRepository.findById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new ProductNotFoundException()))
          .flatMap(product -> {
            product.setName(name);
            return productRepository.save(product);
          })
          .doOnSubscribe(unused -> logBuilder.info("Updating branch name"))
          .doOnSuccess(unused -> logBuilder.info("Product name updated"))
          .doOnError(error -> logBuilder.error("Error updating product name"));
    });
  }
}
