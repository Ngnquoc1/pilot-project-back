package vn.elca.training.model.dto.response;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Response DTO for project bulk deletion operations.
 * Provides transparency regarding which projects were successfully deleted
 * and which IDs were not found in the database.
 *
 * @author nnnq
 */
public class ProjectDeleteResponseDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private int requestedCount;
    private int deletedCount;
    private List<Long> notFoundIds = Collections.emptyList();
    private String message;

    public ProjectDeleteResponseDto() {
    }

    public ProjectDeleteResponseDto(int requestedCount, int deletedCount, List<Long> notFoundIds, String message) {
        this.requestedCount = requestedCount;
        this.deletedCount = deletedCount;
        this.notFoundIds = notFoundIds != null ? notFoundIds : Collections.emptyList();
        this.message = message;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    public void setRequestedCount(int requestedCount) {
        this.requestedCount = requestedCount;
    }

    public int getDeletedCount() {
        return deletedCount;
    }

    public void setDeletedCount(int deletedCount) {
        this.deletedCount = deletedCount;
    }

    public List<Long> getNotFoundIds() {
        return notFoundIds;
    }

    public void setNotFoundIds(List<Long> notFoundIds) {
        this.notFoundIds = notFoundIds;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
