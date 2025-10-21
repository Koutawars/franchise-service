package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OpenApiConfigTest {

    @Test
    void franchiseServiceOpenAPI_ShouldCreateOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        
        OpenAPI openAPI = config.franchiseServiceOpenAPI();
        
        assertThat(openAPI).isNotNull();
        assertThat(openAPI.getInfo().getTitle()).isEqualTo("Franchise Service API");
        assertThat(openAPI.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(openAPI.getServers()).hasSize(2);
    }
}