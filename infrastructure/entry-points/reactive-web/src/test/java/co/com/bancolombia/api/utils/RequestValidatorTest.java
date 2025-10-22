package co.com.bancolombia.api.utils;

import co.com.bancolombia.model.exceptions.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestValidatorTest {

    @Mock
    private Validator validator;
    
    private RequestValidator requestValidator;
    
    @BeforeEach
    void setUp() {
        requestValidator = new RequestValidator(validator);
    }

    @Test
    void validate_Success() {
        String testObject = "valid-object";
        when(validator.validate(testObject)).thenReturn(Set.of());

        StepVerifier.create(requestValidator.validate(testObject))
                .expectNext(testObject)
                .verifyComplete();
    }

    @Test
    void validate_WithViolations() {
        String testObject = "invalid-object";
        
        @SuppressWarnings("unchecked")
        ConstraintViolation<String> violation = mock(ConstraintViolation.class);
        when(violation.getPropertyPath()).thenReturn(mock(jakarta.validation.Path.class));
        when(violation.getPropertyPath().toString()).thenReturn("field");
        when(violation.getMessage()).thenReturn("must not be null");
        
        @SuppressWarnings("unchecked")
        Set<ConstraintViolation<String>> violations = Set.of(violation);
        when(validator.validate(testObject)).thenReturn(violations);

        StepVerifier.create(requestValidator.validate(testObject))
                .expectErrorMatches(throwable -> 
                    throwable instanceof ValidationException &&
                    throwable.getMessage().contains("field: must not be null"))
                .verify();
    }
}