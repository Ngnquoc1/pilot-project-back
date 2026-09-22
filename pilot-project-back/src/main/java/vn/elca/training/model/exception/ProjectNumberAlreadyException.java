package vn.elca.training.model.exception;

public class ProjectNumberAlreadyException extends RuntimeException {
    private final Integer projectNumber;
    public ProjectNumberAlreadyException(Integer pNumber) {
        super(String.format("The project number: %d already existed. Please select a different project number", pNumber));
        this.projectNumber=pNumber;
    }
    public Integer getProjectNumber() {
        return projectNumber;
    }
}
