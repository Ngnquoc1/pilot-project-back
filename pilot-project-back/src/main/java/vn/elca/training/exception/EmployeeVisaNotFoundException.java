package vn.elca.training.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmployeeVisaNotFoundException extends BaseBusinessException {

    private final Set<String> visas;

    public EmployeeVisaNotFoundException(Set<String> visas) {
        super(
                ErrorCode.EMPLOYEE_VISA_NOT_FOUND,
                new Object[]{visas != null ? String.join(", ", visas) : ""},
                "The following visas do not exist: " + visas
        );
        this.visas = visas;
    }

    public Set<String> getVisas() {
        return visas;
    }
}
