package co.com.bancolombia.logger;

import co.com.bancolombia.model.utils.LogBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.util.context.Context;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StructuredLoggerTest {

  private StructuredLogger structuredLogger;

  @BeforeEach
  void setUp() {
    structuredLogger = new StructuredLogger();
  }

  @Test
  void shouldCreateLogBuilderWithContextValues() {
    Context ctx = Context.of(
        "currentCompany", "TestCompany",
        "role", "Admin",
        "userEmail", "test@example.com",
        "traceId", "trace-123"
    );

    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertNotNull(logBuilder);
  }

  @Test
  void shouldCreateLogBuilderWithDefaultTraceId() {
    Context ctx = Context.of(
        "currentCompany", "TestCompany",
        "role", "Admin"
    );

    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertNotNull(logBuilder);
  }

  @Test
  void shouldCreateLogBuilderWithEmptyContext() {
    Context ctx = Context.empty();

    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertNotNull(logBuilder);
  }

  @Test
  void shouldAddKeyValuePairs() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    LogBuilder result = logBuilder.key("testKey", "testValue");

    assertNotNull(result);
    assertSame(logBuilder, result);
  }

  @Test
  void shouldIgnoreNullKey() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    LogBuilder result = logBuilder.key(null, "testValue");

    assertNotNull(result);
  }

  @Test
  void shouldIgnoreNullValue() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    LogBuilder result = logBuilder.key("testKey", null);

    assertNotNull(result);
  }

  @Test
  void shouldLogInfo() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertDoesNotThrow(() -> logBuilder.info("Test info message"));
  }

  @Test
  void shouldLogWarn() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertDoesNotThrow(() -> logBuilder.warn("Test warn message"));
  }

  @Test
  void shouldLogDebug() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertDoesNotThrow(() -> logBuilder.debug("Test debug message"));
  }

  @Test
  void shouldLogError() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    assertDoesNotThrow(() -> logBuilder.error("Test error message"));
  }

  @Test
  void shouldLogErrorWithThrowable() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);
    Throwable throwable = new RuntimeException("Test exception");

    assertDoesNotThrow(() -> logBuilder.error("Test error message", throwable));
  }

  @Test
  void shouldChainMultipleKeys() {
    Context ctx = Context.of("traceId", "trace-123");
    LogBuilder logBuilder = structuredLogger.with(ctx);

    LogBuilder result = logBuilder
        .key("key1", "value1")
        .key("key2", "value2")
        .key("key3", "value3");

    assertNotNull(result);
    assertDoesNotThrow(() -> result.info("Chained keys test"));
  }
}
