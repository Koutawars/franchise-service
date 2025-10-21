package co.com.bancolombia.api.utils;

import co.com.bancolombia.model.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RequestValidator {
  private final Validator validator;
  @SneakyThrows
  public <T> Mono<T> validate(T object) {
    Set<ConstraintViolation<T>> violations = validator.validate(object);
    if (!violations.isEmpty()) {
      String constraintsViolationsMessages = violations.stream()
          .map(v -> v.getPropertyPath() + ": " + v.getMessage())
          .collect(Collectors.joining(", "));
      throw new ValidationException(constraintsViolationsMessages);
    }
    return Mono.just(object);
  }
}