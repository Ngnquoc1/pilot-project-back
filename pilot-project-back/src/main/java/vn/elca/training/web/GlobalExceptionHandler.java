package vn.elca.training.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
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
import vn.elca.training.model.exception.BaseBusinessException;
import vn.elca.training.model.exception.ErrorCode;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for REST controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Log logger = LogFactory.getLog(getClass());
    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Unified handler for all domain business exceptions extending BaseBusinessException.
     * (ProjectNotFoundException, ProjectNumberAlreadyException, EmployeeVisaNotFoundException,
     *  InvalidProjectStatusForDeletionException, etc.)
     */
    @ExceptionHandler(BaseBusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBaseBusinessException(BaseBusinessException ex, Locale locale) {
        logger.warn(String.format("Business exception [%s]: %s", ex.getErrorKey(), ex.getMessage()));

        String localizedMessage = ex.getMessage();
        if (ex.getMessageKey() != null) {
            localizedMessage = messageSource.getMessage(
                    ex.getMessageKey(),
                    ex.getMessageArgs(),
                    ex.getMessage(), // Fallback if key not found
                    locale
            );
        }

        ErrorResponseDto error = new ErrorResponseDto(
                ex.getHttpStatus().value(),
                ex.getErrorKey(),
                localizedMessage
        );
        return new ResponseEntity<>(error, ex.getHttpStatus());
    }

    /**
     * 2. Optimistic Locking / Concurrent Modification Conflict -> HTTP 409 Conflict
     */
    @ExceptionHandler({OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class})
    public ResponseEntity<ErrorResponseDto> handleOptimisticLockingFailure(Exception ex, Locale locale) {
        logger.warn("Optimistic locking conflict detected: " + ex.getMessage());
        String message = messageSource.getMessage(
                "project.concurrent.conflict",
                null,
                "The project was updated or deleted by another transaction. Please refresh the page and try again.",
                locale
        );
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.CONCURRENT_UPDATE_CONFLICT.getHttpStatus().value(),
                ErrorCode.CONCURRENT_UPDATE_CONFLICT.getCode(),
                message
        );
        return new ResponseEntity<>(error, ErrorCode.CONCURRENT_UPDATE_CONFLICT.getHttpStatus());
    }

    /**
     * 3. Illegal Argument (Business rule validation) -> HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Illegal argument in request: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.INVALID_ARGUMENT.getHttpStatus().value(),
                ErrorCode.INVALID_ARGUMENT.getCode(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, ErrorCode.INVALID_ARGUMENT.getHttpStatus());
    }

    /**
     * 4. Illegal State -> HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalState(IllegalStateException ex) {
        logger.warn("Illegal state in request: " + ex.getMessage());
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.ILLEGAL_STATE.getHttpStatus().value(),
                ErrorCode.ILLEGAL_STATE.getCode(),
                ex.getMessage()
        );
        return new ResponseEntity<>(error, ErrorCode.ILLEGAL_STATE.getHttpStatus());
    }

    /**
     * 5. Bean Validation @Valid -> HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        logger.warn("Validation failed for request: " + fieldErrors);
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.VALIDATION_FAILED.getHttpStatus().value(),
                ErrorCode.VALIDATION_FAILED.getCode(),
                "Request validation failed",
                fieldErrors
        );
        return new ResponseEntity<>(error, ErrorCode.VALIDATION_FAILED.getHttpStatus());
    }

    /**
     * 6. Application Unexpected Error -> HTTP 500 Internal Server Error
     */
    @ExceptionHandler(ApplicationUnexpectedException.class)
    public ResponseEntity<ErrorResponseDto> handleApplicationUnexpected(ApplicationUnexpectedException ex) {
        String errorId = UUID.randomUUID().toString();
        logger.error(String.format("Application unexpected error [ErrorId: %s]", errorId), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                String.format("An unexpected system error occurred (Error ID: %s).", errorId)
        );
        return new ResponseEntity<>(error, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }

    /**
     * 7. Fallback for All Other Unhandled Errors -> HTTP 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(Exception ex) {
        String errorId = UUID.randomUUID().toString();
        logger.error(String.format("Unhandled server error [ErrorId: %s]", errorId), ex);
        ErrorResponseDto error = new ErrorResponseDto(
                ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus().value(),
                ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                String.format("An internal server error occurred (Error ID: %s). Please contact administrator.", errorId)
        );
        return new ResponseEntity<>(error, ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus());
    }
}
