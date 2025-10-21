package co.com.bancolombia.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.cors.reactive.CorsWebFilter;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void corsWebFilter_ShouldCreateFilter() {
        CorsConfig config = new CorsConfig();
        String origins = "http://localhost:3000,http://localhost:8080";
        
        CorsWebFilter filter = config.corsWebFilter(origins);
        
        assertThat(filter).isNotNull();
    }

    @Test
    void corsWebFilter_WithSingleOrigin() {
        CorsConfig config = new CorsConfig();
        String origins = "http://localhost:3000";
        
        CorsWebFilter filter = config.corsWebFilter(origins);
        
        assertThat(filter).isNotNull();
    }
}