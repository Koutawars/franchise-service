package co.com.bancolombia.logger;

import co.com.bancolombia.model.utils.LogBuilder;
import co.com.bancolombia.model.utils.Logger;
import net.logstash.logback.argument.StructuredArguments;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.util.context.ContextView;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class StructuredLogger implements Logger {
  private final org.slf4j.Logger log;

  public StructuredLogger() {
    this.log = LoggerFactory.getLogger("StructuredLogger");
  }

  @Override
  public LogBuilder with() {
    return new LogBuilderImpl(log);
  }

  @Override
  public LogBuilder with(ContextView ctx) {
    return new LogBuilderImpl(log)
        .key("traceId", ctx.getOrDefault("traceId", "unknown"));
  }

  private static class LogBuilderImpl implements LogBuilder {
    private final org.slf4j.Logger log;
    private final Map<String, Object> keyValues = new LinkedHashMap<>();

    LogBuilderImpl(org.slf4j.Logger log) {
      this.log = log;
    }

    @Override
    public LogBuilder key(String key, Object value) {
      if (key != null && value != null) {
        keyValues.put(key, value);
      }
      return this;
    }

    @Override
    public void info(String message) {
      log.info(message, StructuredArguments.entries(keyValues));
    }

    @Override
    public void warn(String message) {
      log.warn(message, StructuredArguments.entries(keyValues));
    }

    @Override
    public void debug(String message) {
      log.debug(message, StructuredArguments.entries(keyValues));
    }

    @Override
    public void error(String message) {
      log.error(message, StructuredArguments.entries(keyValues));
    }

    @Override
    public void error(String message, Throwable t) {
      log.error(message, StructuredArguments.entries(keyValues), t);
    }
  }
}