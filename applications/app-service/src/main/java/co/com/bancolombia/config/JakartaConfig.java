package co.com.bancolombia.config;

import jakarta.validation.Validator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;

@Configuration
public class JakartaConfig {
  @Bean
  public Validator getValidatorJakarta() {
    return buildDefaultValidatorFactory().getValidator();
  }
}