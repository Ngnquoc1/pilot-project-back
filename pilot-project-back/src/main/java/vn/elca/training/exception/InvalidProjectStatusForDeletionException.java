package vn.elca.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidProjectStatusForDeletionException extends BaseBusinessException {

    public InvalidProjectStatusForDeletionException(String message) {
        super(
                ErrorCode.INVALID_PROJECT_STATUS_FOR_DELETION,
                null,
                message
        );
    }
}
