package vn.elca.training.model.exception;

import org.springframework.http.HttpStatus;

/**
 * Base abstract exception for all domain business exceptions.
 * Encapsulates the ErrorCode and optional message arguments for i18n interpolation.
 *
 * @author nnnq
 */
public abstract class BaseBusinessException extends RuntimeException {

    private final ErrorCode errorCode;
    private final Object[] messageArgs;

    public BaseBusinessException(ErrorCode errorCode, Object[] messageArgs, String defaultMessage) {
        super(defaultMessage);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    public BaseBusinessException(ErrorCode errorCode, Object[] messageArgs, String defaultMessage, Throwable cause) {
        super(defaultMessage, cause);
        this.errorCode = errorCode;
        this.messageArgs = messageArgs;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }

    public String getErrorKey() {
        return errorCode.getCode();
    }

    public String getMessageKey() {
        return errorCode.getMessageKey();
    }

    public Object[] getMessageArgs() {
        return messageArgs;
    }
}
