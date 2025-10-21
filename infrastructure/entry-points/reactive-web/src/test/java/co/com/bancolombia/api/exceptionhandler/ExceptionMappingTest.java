package co.com.bancolombia.api.exceptionhandler;

import co.com.bancolombia.model.exceptions.BranchNotFoundException;
import co.com.bancolombia.model.exceptions.FranchiseNotFoundException;
import co.com.bancolombia.model.exceptions.ProductNotFoundException;
import co.com.bancolombia.model.exceptions.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebInputException;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionMappingTest {

    @Test
    void from_ValidationException() {
        ValidationException ex = new ValidationException("Validation failed");
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.VALIDATION_BODY_ERROR);
        assertThat(mapping.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(mapping.getResponseCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void from_BranchNotFoundException() {
        BranchNotFoundException ex = new BranchNotFoundException();
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.BRANCH_NOT_FOUND);
        assertThat(mapping.getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void from_ProductNotFoundException() {
        ProductNotFoundException ex = new ProductNotFoundException();
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.PRODUCT_NOT_FOUND);
    }

    @Test
    void from_FranchiseNotFoundException() {
        FranchiseNotFoundException ex = new FranchiseNotFoundException();
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.FRANCHISE_NOT_FOUND);
    }

    @Test
    void from_ServerWebInputException() {
        ServerWebInputException ex = new ServerWebInputException("Bad input");
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.SERVER_WEB_INPUT_ERROR);
    }

    @Test
    void from_GeneralException() {
        RuntimeException ex = new RuntimeException("Unknown error");
        ExceptionMapping mapping = ExceptionMapping.from(ex);
        
        assertThat(mapping).isEqualTo(ExceptionMapping.GENERAL_ERROR);
        assertThat(mapping.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void getMessageFor_WithCustomMessage() {
        ValidationException ex = new ValidationException("Custom validation message");
        ExceptionMapping mapping = ExceptionMapping.VALIDATION_BODY_ERROR;
        
        String message = mapping.getMessageFor(ex);
        assertThat(message).isEqualTo("Custom validation message");
    }

    @Test
    void getMessageFor_WithNullMessage() {
        ValidationException ex = new ValidationException(null);
        ExceptionMapping mapping = ExceptionMapping.VALIDATION_BODY_ERROR;
        
        String message = mapping.getMessageFor(ex);
        assertThat(message).isEqualTo("Validation error");
    }

    @Test
    void getMessageFor_GeneralError() {
        RuntimeException ex = new RuntimeException("Some error");
        ExceptionMapping mapping = ExceptionMapping.GENERAL_ERROR;
        
        String message = mapping.getMessageFor(ex);
        assertThat(message).isEqualTo("Internal server error");
    }
}