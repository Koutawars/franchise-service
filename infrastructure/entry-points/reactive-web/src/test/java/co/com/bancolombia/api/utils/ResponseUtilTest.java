package co.com.bancolombia.api.utils;

import co.com.bancolombia.api.dto.StandardResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseUtilTest {

    @Test
    void processOk_Success() {
        String testData = "test-data";

        StepVerifier.create(ResponseUtil.processOk(testData))
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.headers().getContentType().toString())
                            .contains("application/json");
                })
                .verifyComplete();
    }

    @Test
    void processOk_WithNullData() {
        StepVerifier.create(ResponseUtil.processOk(null))
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                })
                .verifyComplete();
    }

    @Test
    void processOk_WithComplexObject() {
        StandardResponse<String> complexData = StandardResponse.<String>builder()
                .responseCode("TEST")
                .message("Test message")
                .data("test")
                .build();

        StepVerifier.create(ResponseUtil.processOk(complexData))
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                })
                .verifyComplete();
    }
}