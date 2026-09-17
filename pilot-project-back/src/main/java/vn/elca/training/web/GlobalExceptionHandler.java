package vn.elca.training.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.elca.training.model.dto.ErrorResponseDto;
import vn.elca.training.model.exception.ApplicationUnexpectedException;
import vn.elca.training.model.exception.EmployeeVisaNotFoundException;
import vn.elca.training.model.exception.InvalidProjectStatusForDeletionException;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.model.exception.ProjectNumberAlreadyException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Log logger = LogFactory.getLog(getClass());

    /**
     * 1. Project Not Found -> HTTP 404 Not Found
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
     * 2. Project Number Already Exists -> HTTP 400 Bad Request
     */
    @ExceptionHandler(ProjectNumberAlreadyException.class)
    public ResponseEntity<ErrorResponseDto> handleProjectNumberAlready(ProjectNumberAlreadyException ex) {
        logger.warn("Project number already exists: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "PROJECT_NUMBER_ALREADY_EXISTED",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. Employee Visa Not Found -> HTTP 400 Bad Request
     */
    @ExceptionHandler(EmployeeVisaNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEmployeeVisaNotFound(EmployeeVisaNotFoundException ex) {
        logger.warn("Employee VISA not found: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "EMPLOYEE_VISA_NOT_FOUND",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 4. Invalid Project Status For Deletion -> HTTP 400 Bad Request
     */
    @ExceptionHandler(InvalidProjectStatusForDeletionException.class)
    public ResponseEntity<ErrorResponseDto> handleInvalidProjectStatusForDeletion(InvalidProjectStatusForDeletionException ex) {
        logger.warn("Invalid project status for deletion: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_PROJECT_STATUS_FOR_DELETION",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 5. Optimistic Locking / Concurrent Modification Conflict -> HTTP 409 Conflict
     */
    @ExceptionHandler({OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponseDto> handleOptimisticLockingFailure(Exception ex) {
        logger.warn("Optimistic locking conflict detected: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.CONFLICT.value(),
                "CONCURRENT_UPDATE_CONFLICT",
                "The project was updated or deleted by another transaction. Please refresh the page and try again."
        );
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    /**
     * 6. Illegal Argument (Business rule validation) -> HTTP 400 Bad Request
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
     * 7. Illegal State -> HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException ex) {
        logger.warn("Illegal state in request: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_STATE",
                ex.getMessage()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    /**
     * 8. Bean Validation @Valid -> HTTP 400 Bad Request
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
     * 9. Application Unexpected Error -> HTTP 500 Internal Server Error
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
     * 10. Fallback for All Other Unhandled Errors -> HTTP 500 Internal Server Error
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
