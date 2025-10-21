package co.com.bancolombia.model.utils;

public interface LogBuilder {
  LogBuilder key(String key, Object value);
  void info(String message);
  void warn(String message);
  void debug(String message);
  void error(String message);
  void error(String message, Throwable t);
}
