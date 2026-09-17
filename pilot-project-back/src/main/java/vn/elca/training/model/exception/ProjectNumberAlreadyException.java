package vn.elca.training.model.exception;

public class ProjectNumberAlreadyException extends RuntimeException {
    public ProjectNumberAlreadyException(Integer pNumber) {
        super(String.format("The project number: %d already existed. Please select a different project number", pNumber));
    }
}
