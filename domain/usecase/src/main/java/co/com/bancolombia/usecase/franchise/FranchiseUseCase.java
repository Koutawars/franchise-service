package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class FranchiseUseCase {
  public static final String PRODUCT_ID = "productId";
  public static final String BRANCH_ID = "branchId";
  public static final String FRANCHISE_ID = "franchiseId";
  private final FranchiseRepository franchiseRepository;
  private final Logger logger;

  public Mono<Franchise> addFranchise(Franchise franchise) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("franchise", franchise);
      return franchiseRepository.save(franchise)
          .doOnSubscribe(unused -> logBuilder.info("Saving franchise"))
          .doOnSuccess(unused -> logBuilder.info("Franchise saved"))
          .doOnError(error -> logBuilder.error("Error saving franchise"));
    });
  }

  public Mono<Branch> addBranch(Branch branch) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("branch", branch);

      return franchiseRepository.findById(branch.getFranchiseId())
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(franchiseRepository.saveBranch(branch))
          .doOnSubscribe(unused -> logBuilder.info("Adding branch"))
          .doOnSuccess(unused -> logBuilder.info("Branch added"))
          .doOnError(error -> logBuilder.error("Error adding branch"));
    });
  }

  public Mono<Product> addProduct(Product product) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key("product", product);

      return franchiseRepository.findById(product.getFranchiseId())
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(franchiseRepository.findBranchById(product.getBranchId(), product.getFranchiseId()))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(franchiseRepository.saveProduct(product))
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
          .then(franchiseRepository.findBranchById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(franchiseRepository.findProductById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new ProductNotFoundException()))
          .flatMap(product -> franchiseRepository.deleteProduct(product)
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
          .then(franchiseRepository.findBranchById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new BranchNotFoundException()))
          .then(franchiseRepository.findProductById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new ProductNotFoundException()))
          .flatMap(product -> {
            product.setStock(stock);
            return franchiseRepository.saveProduct(product);
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
          .flatMapMany(franchise -> franchiseRepository.findProductsByFranchise(franchiseId))
          .collectList()
          .flatMapMany(products -> Flux.fromIterable(
              products.stream()
                  .collect(Collectors.groupingBy(Product::getBranchId))
                  .values()
                  .stream()
                  .map(branchProducts -> branchProducts.stream()
                      .max(Comparator.comparing(Product::getStock))
                      .orElse(null))
                  .filter(Objects::nonNull)
                  .toList()
          ))
          .doOnSubscribe(unused -> logBuilder.info("Top products per branch retrieved"))
          .doOnError(error -> logBuilder.error("Error getting top products per branch"))
          .doOnComplete(() -> logBuilder.info("Top products per branch retrieved"));
    });
  }

  public Mono<Franchise> updateNameFranchise(String franchiseId, String name) {
    return Mono.deferContextual(context -> {
      LogBuilder logBuilder = logger.with(context)
          .key(FRANCHISE_ID, franchiseId)
          .key("name", name);

      return franchiseRepository.findById(franchiseId)
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMap(franchise -> {
            franchise.setName(name);
            return franchiseRepository.save(franchise);
          })
          .doOnSubscribe(unused -> logBuilder.info("Updating franchise name"))
          .doOnSuccess(unused -> logBuilder.info("Franchise name updated"))
          .doOnError(error -> logBuilder.error("Error updating franchise name"));
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
          .then(franchiseRepository.findBranchById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMap(branch -> {
            branch.setName(name);
            return franchiseRepository.saveBranch(branch);
          })
          .doOnSubscribe(unused -> logBuilder.info("Updating branch name"))
          .doOnSuccess(unused -> logBuilder.info("Branch name updated"))
          .doOnError(error -> logBuilder.error("Error updating branch name"));
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
          .then(franchiseRepository.findBranchById(branchId, franchiseId))
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .then(franchiseRepository.findProductById(productId, branchId, franchiseId))
          .switchIfEmpty(Mono.error(new FranchiseNotFoundException()))
          .flatMap(product -> {
            product.setName(name);
            return franchiseRepository.saveProduct(product);
          })
          .doOnSubscribe(unused -> logBuilder.info("Updating branch name"))
          .doOnSuccess(unused -> logBuilder.info("Product name updated"))
          .doOnError(error -> logBuilder.error("Error updating product name"));
    });
  }
}
