package co.com.bancolombia.api.exceptionhandler;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.exceptions.ValidationException;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;

@Getter
public enum ExceptionMapping {

  VALIDATION_BODY_ERROR(ValidationException.class, HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Validation error"),
  BRANCH_NOT_FOUND(BranchNotFoundException.class, HttpStatus.NOT_FOUND, "BRANCH_NOT_FOUND", "Branch not found"),
  PRODUCT_NOT_FOUND(ProductNotFoundException.class, HttpStatus.NOT_FOUND, "PRODUCT_NOT_FOUND", "Product not found"),
  FRANCHISE_NOT_FOUND(FranchiseNotFoundException.class, HttpStatus.NOT_FOUND, "FRANCHISE_NOT_FOUND", "Franchise not found"),
  SERVER_WEB_INPUT_ERROR(ServerWebInputException.class, HttpStatus.BAD_REQUEST, "MALFORMED_REQUEST", "Malformed request body"),
  GENERAL_ERROR(Exception.class, HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Internal server error");

  private final Class<? extends Throwable> exceptionClass;
  private final HttpStatus status;
  private final String responseCode;
  private final String message;

  ExceptionMapping(Class<? extends Throwable> exceptionClass, HttpStatus status, String responseCode, String message) {
    this.exceptionClass = exceptionClass;
    this.status = status;
    this.responseCode = responseCode;
    this.message = message;
  }

  public static ExceptionMapping from(Throwable ex) {
    for (ExceptionMapping mapping : values()) {
      if (mapping.exceptionClass.isAssignableFrom(ex.getClass())) {
        return mapping;
      }
    }
    return GENERAL_ERROR;
  }

  public String getMessageFor(Throwable ex) {
    if (this == GENERAL_ERROR) {
      return this.message;
    }
    return (ex.getMessage() != null && !ex.getMessage().isEmpty()) ? ex.getMessage() : this.message;
  }
}