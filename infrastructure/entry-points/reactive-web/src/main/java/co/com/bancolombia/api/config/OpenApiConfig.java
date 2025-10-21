package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI franchiseServiceOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Franchise Service API")
            .description("RESTful API for managing franchises, branches, and products. " +
                "This service provides endpoints to create, update, delete, and retrieve " +
                "franchise-related entities following Clean Architecture principles.")
            .version("1.0.0")
            .contact(new Contact()
                .name("API Support")
                .email("support@franchise-service.com"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("Local Development Server"),
            new Server().url("https://api.franchise-service.com").description("Production Server")
        ));
  }
}