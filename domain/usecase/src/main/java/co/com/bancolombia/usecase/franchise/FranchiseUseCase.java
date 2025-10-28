package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {
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
}
