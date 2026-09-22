package vn.elca.training.service;

import vn.elca.training.model.dto.EmployeeDto;
import vn.elca.training.model.entity.Employee;

import java.util.List;

/**
 * Service interface for Employee operations.
 *
 * @author nnnq
 */
public interface EmployeeService {

    List<EmployeeDto> findAll();

    List<EmployeeDto> searchEmployees(String term);
}
