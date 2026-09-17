package vn.elca.training.model.exception;

import java.util.Set;

public class EmployeeVisaNotFoundException extends RuntimeException {
    public EmployeeVisaNotFoundException(Set<String> visa) {
        super("The following visas do not exist: " + visa);
    }
}
