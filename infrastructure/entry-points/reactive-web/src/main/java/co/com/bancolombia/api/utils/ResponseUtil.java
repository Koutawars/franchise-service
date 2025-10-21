package co.com.bancolombia.api.utils;

import co.com.bancolombia.api.dto.StandardResponse;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@UtilityClass
public class ResponseUtil {
  public static <T> Mono<ServerResponse> processOk(T data) {
    HttpStatus status = HttpStatus.OK;
    StandardResponse<T> result = StandardResponse.<T>builder()
        .responseCode("OPERATION_SUCCESS")
        .message("Operation successful")
        .data(data)
        .timestamp(LocalDateTime.now().toString())
        .build();

    return ServerResponse.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .bodyValue(result);
  }
}