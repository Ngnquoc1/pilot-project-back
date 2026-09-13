package vn.elca.training.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.elca.training.model.dto.ErrorResponseDto;
import vn.elca.training.model.exception.ApplicationUnexpectedException;
import vn.elca.training.model.exception.DeadlineAfterFinishingDateException;
import vn.elca.training.model.exception.ProjectNotFoundException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Log logger = LogFactory.getLog(getClass());

    /**
     * 1. Project NOT FOUND_HTTP 404 Not Found
     */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleProjectNotFound(ProjectNotFoundException ex) {
        logger.warn("Resource not found: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.NOT_FOUND.value(),
                "RESOURCE_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    /**
     * 2.Deadline After_ HTTP 400 Bad Request
     */
    @ExceptionHandler(DeadlineAfterFinishingDateException.class)
    public ResponseEntity<ErrorResponseDto> handleDeadlineAfterFinishingDate(DeadlineAfterFinishingDateException ex) {
        logger.warn("Business validation error: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "DEADLINE_AFTER_FINISHING_DATE",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. Illegal Argument_HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument in request: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_ARGUMENT",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 4. Bean Validation @Valid (HTTP 400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        logger.warn("Validation failed for request: " + fieldErrors);
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_FAILED",
                "Request validation failed",
                fieldErrors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 5. HTTP 500 Internal Server Error
     */
    @ExceptionHandler(ApplicationUnexpectedException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationUnexpected(ApplicationUnexpectedException ex) {
        String errorId = UUID.randomUUID().toString();
        logger.error(String.format("Application unexpected error [ErrorId: %s]", errorId), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                String.format("An unexpected system error occurred (Error ID: %s).", errorId)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 6. Others Unexpected Error_HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        logger.error(String.format("Unhandled server error [ErrorId: %s]", errorId), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_SERVER_ERROR",
                String.format("An internal server error occurred (Error ID: %s). Please contact administrator.", errorId)
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
