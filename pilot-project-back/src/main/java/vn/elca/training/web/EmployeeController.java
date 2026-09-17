package vn.elca.training.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.training.model.dto.EmployeeDto;
import vn.elca.training.service.EmployeeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Employee operations.
 *
 * @author nnnq
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController extends AbstractApplicationController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<EmployeeDto> findAll() {
        return employeeService.findAll();
    }
}
