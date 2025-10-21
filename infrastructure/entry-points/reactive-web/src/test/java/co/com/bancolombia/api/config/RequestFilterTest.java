package co.com.bancolombia.api.config;

import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestFilterTest {

    @Mock
    private Logger logger;
    
    @Mock
    private LogBuilder logBuilder;
    
    @Mock
    private WebFilterChain chain;
    
    private RequestFilter requestFilter;

    @BeforeEach
    void setUp() {
        when(logger.with()).thenReturn(logBuilder);
        when(logBuilder.key(anyString(), any())).thenReturn(logBuilder);
        
        requestFilter = new RequestFilter(logger);
    }

    @Test
    void filter_Success() {
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.GET, URI.create("/test"))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        exchange.getResponse().setStatusCode(HttpStatus.OK);
        
        when(chain.filter(exchange)).thenReturn(Mono.empty());

        StepVerifier.create(requestFilter.filter(exchange, chain))
                .verifyComplete();
    }

    @Test
    void filter_WithError() {
        MockServerHttpRequest request = MockServerHttpRequest
                .method(HttpMethod.POST, URI.create("/test"))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        
        RuntimeException error = new RuntimeException("Test error");
        when(chain.filter(exchange)).thenReturn(Mono.error(error));

        StepVerifier.create(requestFilter.filter(exchange, chain))
                .expectError(RuntimeException.class)
                .verify();
    }
}