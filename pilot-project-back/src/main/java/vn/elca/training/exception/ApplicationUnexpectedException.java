package vn.elca.training.exception;

public class ApplicationUnexpectedException extends RuntimeException {
    public ApplicationUnexpectedException(Throwable e) {
        super("Unexpected exception", e);
    }
}
