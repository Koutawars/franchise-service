package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateway.BranchRepository;
import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.franchise.gateway.ProductRepository;
import co.com.bancolombia.model.utils.Logger;
import co.com.bancolombia.usecase.franchise.BranchUseCase;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import co.com.bancolombia.usecase.franchise.ProductUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
  @Bean
  FranchiseUseCase getFranchiseUseCase(
      FranchiseRepository franchiseRepository,
      Logger logger
  ) {
    return new FranchiseUseCase(franchiseRepository, logger);
  }

  @Bean
  ProductUseCase getProductUseCase(
      FranchiseRepository franchiseRepository,
      BranchRepository branchRepository,
      ProductRepository productRepository,
      Logger logger
  ) {
    return new ProductUseCase(franchiseRepository, branchRepository, productRepository, logger);
  }

  @Bean
  BranchUseCase getBranchUseCase(
      BranchRepository branchRepository,
      FranchiseRepository franchiseRepository,
      Logger logger
  ) {
    return new BranchUseCase(branchRepository, franchiseRepository, logger);
  }
}
