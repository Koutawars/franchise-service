package co.com.bancolombia.api.exceptionhandler;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {
  @Override
  public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
    Throwable error = getError(request);

    ExceptionMapping mapping = ExceptionMapping.from(error);

    Map<String, Object> response = new HashMap<>();
    response.put("status", mapping.getStatus().value());
    response.put("responseCode", mapping.getResponseCode());
    response.put("message", mapping.getMessageFor(error));
    response.put("timestamp", LocalDateTime.now().toString());

    return response;
  }
}