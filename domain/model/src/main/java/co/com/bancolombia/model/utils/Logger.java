package co.com.bancolombia.model.utils;

import reactor.util.context.ContextView;

public interface Logger {
  LogBuilder with();
  LogBuilder with(ContextView ctx);
}
