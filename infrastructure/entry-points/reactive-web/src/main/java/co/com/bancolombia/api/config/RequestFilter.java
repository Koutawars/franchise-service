package co.com.bancolombia.api.config;

import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import lombok.AllArgsConstructor;
import org.jboss.logging.MDC;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class RequestFilter implements WebFilter {
  private static final String TRACE_ID_KEY = "traceId";
  private final Logger log;

  @NonNull
  @Override
  public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
    String traceId = UUID.randomUUID().toString();

    LogBuilder logBuilder = log.with();

    logBuilder.key("method", exchange.getRequest().getMethod().toString())
        .key("path", exchange.getRequest().getURI().getPath())
        .key("traceId", traceId);

    logBuilder.info("Incoming request");

    exchange.getResponse().getHeaders().add(TRACE_ID_KEY, traceId);

    long startTime = System.currentTimeMillis();

    return chain.filter(exchange)
        .doOnSuccess(aVoid -> {
          long elapsed = System.currentTimeMillis() - startTime;
          logBuilder.key("status", exchange.getResponse().getStatusCode())
              .key("elapsed", elapsed)
              .info("Completed request");
        })
        .doOnError(error -> {
          long elapsed = System.currentTimeMillis() - startTime;
          logBuilder.key("elapsed", elapsed)
              .key("error", error.getClass().toString())
              .error("Completed request with error");
        })
        .contextWrite(ctx -> {
          MDC.put(TRACE_ID_KEY, traceId);
          return ctx.put(TRACE_ID_KEY, traceId);
        })
        .doFinally(signal -> MDC.remove(TRACE_ID_KEY));
  }
}