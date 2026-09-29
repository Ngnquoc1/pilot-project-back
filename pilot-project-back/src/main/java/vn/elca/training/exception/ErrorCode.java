package vn.elca.training.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "project.not.found"),
    PROJECT_NUMBER_ALREADY_EXISTED(HttpStatus.BAD_REQUEST,  "project.number.already.exists"),
    EMPLOYEE_VISA_NOT_FOUND(HttpStatus.BAD_REQUEST, "employee.visa.not.found"),
    INVALID_PROJECT_STATUS_FOR_DELETION(HttpStatus.BAD_REQUEST, "project.status.invalid.delete"),
    CONCURRENT_UPDATE_CONFLICT(HttpStatus.CONFLICT, "project.concurrent.conflict"),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "validation.failed"),
    INVALID_ARGUMENT(HttpStatus.BAD_REQUEST,  null),
    ILLEGAL_STATE(HttpStatus.BAD_REQUEST,  null),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,  null);

    private final HttpStatus httpStatus;

    private final String messageKey;

    ErrorCode(HttpStatus httpStatus,  String messageKey) {
        this.httpStatus = httpStatus;
        this.messageKey = messageKey;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
