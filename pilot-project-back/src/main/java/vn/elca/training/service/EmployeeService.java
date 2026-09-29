package vn.elca.training.service;

import vn.elca.training.dto.EmployeeDto;

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
