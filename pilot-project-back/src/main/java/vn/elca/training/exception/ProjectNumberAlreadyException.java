package vn.elca.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProjectNumberAlreadyException extends BaseBusinessException {

    private final Integer projectNumber;

    public ProjectNumberAlreadyException(Integer pNumber) {
        super(
                ErrorCode.PROJECT_NUMBER_ALREADY_EXISTED,
                new Object[]{pNumber},
                String.format("The project number: %d already existed. Please select a different project number", pNumber)
        );
        this.projectNumber = pNumber;
    }

    public ProjectNumberAlreadyException(Integer pNumber, Throwable cause) {
        super(
                ErrorCode.PROJECT_NUMBER_ALREADY_EXISTED,
                new Object[]{pNumber},
                String.format("The project number: %d already existed. Please select a different project number", pNumber),
                cause
        );
        this.projectNumber = pNumber;
    }

    public Integer getProjectNumber() {
        return projectNumber;
    }
}
