package co.com.bancolombia.api.exceptionhandler;

import co.com.bancolombia.model.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CustomErrorAttributesTest {

    private CustomErrorAttributes customErrorAttributes;

    @BeforeEach
    void setUp() {
        customErrorAttributes = new CustomErrorAttributes();
    }

    @Test
    void getErrorAttributes_WithValidationException() {
        ValidationException exception = new ValidationException("Validation failed");
        ServerRequest request = MockServerRequest.builder()
                .attribute("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR", exception)
                .build();

        Map<String, Object> attributes = customErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attributes.get("status")).isEqualTo(400);
        assertThat(attributes.get("responseCode")).isEqualTo("VALIDATION_ERROR");
        assertThat(attributes.get("message")).isEqualTo("Validation failed");
        assertThat(attributes.get("timestamp")).isNotNull();
    }

    @Test
    void getErrorAttributes_WithGeneralException() {
        RuntimeException exception = new RuntimeException("Unknown error");
        ServerRequest request = MockServerRequest.builder()
                .attribute("org.springframework.boot.web.reactive.error.DefaultErrorAttributes.ERROR", exception)
                .build();

        Map<String, Object> attributes = customErrorAttributes.getErrorAttributes(request, ErrorAttributeOptions.defaults());

        assertThat(attributes.get("status")).isEqualTo(500);
        assertThat(attributes.get("responseCode")).isEqualTo("INTERNAL_SERVER_ERROR");
        assertThat(attributes.get("message")).isEqualTo("Internal server error");
        assertThat(attributes.get("timestamp")).isNotNull();
    }
}