package vn.elca.training.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Centralized catalog of all API error codes across the system.
 * Acts as the Single Source of Truth (SSOT) defining error code strings,
 * associated HTTP status codes, and i18n message lookup keys.
 */
public enum ErrorCode {

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "RESOURCE_NOT_FOUND", "project.not.found"),
    PROJECT_NUMBER_ALREADY_EXISTED(HttpStatus.BAD_REQUEST, "PROJECT_NUMBER_ALREADY_EXISTED", "project.number.already.exists"),
    EMPLOYEE_VISA_NOT_FOUND(HttpStatus.BAD_REQUEST, "EMPLOYEE_VISA_NOT_FOUND", "employee.visa.not.found"),
    INVALID_PROJECT_STATUS_FOR_DELETION(HttpStatus.BAD_REQUEST, "INVALID_PROJECT_STATUS_FOR_DELETION", "project.status.invalid.delete"),
    CONCURRENT_UPDATE_CONFLICT(HttpStatus.CONFLICT, "CONCURRENT_UPDATE_CONFLICT", "project.concurrent.conflict"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "validation.failed"),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "INVALID_ARGUMENT", null),
    ILLEGAL_STATE(HttpStatus.BAD_REQUEST, "ILLEGAL_STATE", null),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", null);

    private final HttpStatus httpStatus;
    private final String code;
    private final String messageKey;

    ErrorCode(HttpStatus httpStatus, String code, String messageKey) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.messageKey = messageKey;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
