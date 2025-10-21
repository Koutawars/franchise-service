package co.com.bancolombia.api.exceptionhandler;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    @Test
    void globalExceptionHandler_ClassExists() {
        assertThat(GlobalExceptionHandler.class).isNotNull();
    }
}