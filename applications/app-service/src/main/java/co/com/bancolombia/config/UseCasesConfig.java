package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateway.FranchiseRepository;
import co.com.bancolombia.model.utils.Logger;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
public class UseCasesConfig {
  @Bean
  FranchiseUseCase getFranchiseUseCase(
      FranchiseRepository franchiseRepository,
      Logger logger
  ) {
    return new FranchiseUseCase(franchiseRepository, logger);
  }
}
