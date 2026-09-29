package vn.elca.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.dto.EmployeeDto;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.service.EmployeeService;
import vn.elca.training.util.ApplicationMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Employee operations.
 *
 * @author nnnq
 */
@Service
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final ApplicationMapper applicationMapper;

    @Autowired
    public EmployeeServiceImpl( EmployeeRepository employeeRepository, ApplicationMapper applicationMapper) {
        this.employeeRepository = employeeRepository;
        this.applicationMapper = applicationMapper;
    }

    @Override
    public List<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(applicationMapper::employeeToEmployeeDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EmployeeDto> searchEmployees(String term) {
        return employeeRepository.searchEmployee(term)
                .stream()
                .map(applicationMapper::employeeToEmployeeDto)
                .collect(Collectors.toList());
    }
}
