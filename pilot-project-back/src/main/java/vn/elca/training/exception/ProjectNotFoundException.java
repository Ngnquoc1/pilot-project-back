package vn.elca.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProjectNotFoundException extends BaseBusinessException {

    private final Long projectId;

    public ProjectNotFoundException(Long id) {
        super(
                ErrorCode.PROJECT_NOT_FOUND,
                new Object[]{id},
                String.format("Project with ID %d not found", id)
        );
        this.projectId = id;
    }

    public Long getProjectId() {
        return projectId;
    }
}
