package vn.elca.training.model.exception;

import java.util.Set;

public class EmployeeVisaNotFoundException extends RuntimeException {
    private final Set<String> visas;

    public EmployeeVisaNotFoundException(Set<String> visas) {
        super("The following visas do not exist: " + visas);
        this.visas = visas;
    }

    public Set<String> getVisas() {
        return visas;
    }
}
