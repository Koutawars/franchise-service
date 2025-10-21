package co.com.bancolombia.model.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

  @Test
  void shouldCreateFranchiseNotFoundException() {
    FranchiseNotFoundException exception = new FranchiseNotFoundException();
    
    assertNotNull(exception);
    assertNull(exception.getMessage());
  }

  @Test
  void shouldCreateBranchNotFoundException() {
    BranchNotFoundException exception = new BranchNotFoundException();
    
    assertNotNull(exception);
    assertNull(exception.getMessage());
  }

  @Test
  void shouldCreateProductNotFoundException() {
    ProductNotFoundException exception = new ProductNotFoundException();
    
    assertNotNull(exception);
    assertNull(exception.getMessage());
  }

  @Test
  void shouldCreateValidationExceptionWithoutMessage() {
    ValidationException exception = new ValidationException();
    
    assertNotNull(exception);
    assertNull(exception.getMessage());
  }

  @Test
  void shouldCreateValidationExceptionWithMessage() {
    String message = "Validation failed";
    ValidationException exception = new ValidationException(message);
    
    assertNotNull(exception);
    assertEquals(message, exception.getMessage());
  }
}