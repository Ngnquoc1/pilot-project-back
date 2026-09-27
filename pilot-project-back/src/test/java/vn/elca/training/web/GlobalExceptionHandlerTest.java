package vn.elca.training.web;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import vn.elca.training.model.dto.ErrorResponseDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.exception.ApplicationUnexpectedException;
import vn.elca.training.model.exception.BaseBusinessException;
import vn.elca.training.model.exception.ErrorCode;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.model.exception.ProjectNumberAlreadyException;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for GlobalExceptionHandler")
public class GlobalExceptionHandlerTest {

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("handleBaseBusinessException: Returns formatted error with status")
    void testHandleBaseBusinessException() {
        // Business exceptions with a messageKey should resolve localized text via MessageSource based on client Locale
        ProjectNotFoundException ex = new ProjectNotFoundException(999L);
        when(messageSource.getMessage(eq(ex.getMessageKey()), any(), any(), eq(Locale.ENGLISH)))
                .thenReturn("Project with id '999' not found");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleBaseBusinessException(ex, Locale.ENGLISH);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().getStatus());
        assertEquals("Project with id '999' not found", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleOptimisticLockingFailure: Returns 409 Conflict")
    void testHandleOptimisticLockingFailure() {
        // Concurrent update conflict must return HTTP 409 Conflict with standard localized user guidance
        ObjectOptimisticLockingFailureException ex = new ObjectOptimisticLockingFailureException(Project.class, 1L);
        when(messageSource.getMessage(eq("project.concurrent.conflict"), any(), any(), eq(Locale.ENGLISH)))
                .thenReturn("The project was updated by another transaction");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleOptimisticLockingFailure(ex, Locale.ENGLISH);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().getStatus());
    }

    @Test
    @DisplayName("handleIllegalArgument: Returns 400 Bad Request")
    void testHandleIllegalArgument() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument value");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleIllegalArgument(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid argument value", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleIllegalState: Returns 400 Bad Request")
    void testHandleIllegalState() {
        IllegalStateException ex = new IllegalStateException("Invalid state");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleIllegalState(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Invalid state", response.getBody().getMessage());
    }

    @Test
    @DisplayName("handleApplicationUnexpected: Returns 500 Internal Server Error")
    void testHandleApplicationUnexpected() {
        // Generates a random UUID ErrorId for server-side log correlation while keeping the client message sanitized
        ApplicationUnexpectedException ex = new ApplicationUnexpectedException(new RuntimeException("System failure"));

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleApplicationUnexpected(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("An unexpected system error occurred"));
        assertTrue(response.getBody().getMessage().contains("Error ID:"));
    }

    @Test
    @DisplayName("handleGeneralException: Returns 500 Internal Server Error")
    void testHandleGeneralException() {
        // Fallback for unhandled exceptions: masks internal details and provides a trackable ErrorId
        RuntimeException ex = new RuntimeException("Fatal error");

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleGeneralException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().getStatus());
        assertTrue(response.getBody().getMessage().contains("An internal server error occurred"));
        assertTrue(response.getBody().getMessage().contains("Error ID:"));
    }

    @Test
    @DisplayName("handleBaseBusinessException: Fallbacks to default message when messageKey is null")
    void testHandleBaseBusinessException_WhenMessageKeyIsNull_ShouldFallbackToDefaultMessage() {
        // When messageKey is null, it should directly use ex.getMessage() and avoid unnecessary MessageSource lookup
        BaseBusinessException ex = new BaseBusinessException(ErrorCode.INVALID_ARGUMENT, null, "Direct fallback error") {};

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleBaseBusinessException(ex, Locale.ENGLISH);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Direct fallback error", response.getBody().getMessage());
        verifyNoInteractions(messageSource);
    }

    @Test
    @DisplayName("handleValidationExceptions: Returns 400 Bad Request with field errors")
    void testHandleValidationExceptions() {
        // Decomposes Spring Bean Validation FieldErrors into a Map<fieldName, message> for client-side form highlighting
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError1 = new FieldError("projectRequestDto", "name", "Project Name is mandatory");
        FieldError fieldError2 = new FieldError("projectRequestDto", "customer", "Customer is mandatory");

        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<ErrorResponseDto> response = exceptionHandler.handleValidationExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().getStatus());
        assertEquals("Request validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getFieldErrors());
        assertEquals(2, response.getBody().getFieldErrors().size());
        assertEquals("Project Name is mandatory", response.getBody().getFieldErrors().get("name"));
        assertEquals("Customer is mandatory", response.getBody().getFieldErrors().get("customer"));
    }
}
