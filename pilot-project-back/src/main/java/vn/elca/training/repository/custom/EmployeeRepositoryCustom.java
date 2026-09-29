package vn.elca.training.repository.custom;

import vn.elca.training.entity.Employee;

import java.util.List;

public interface EmployeeRepositoryCustom {
    List<Employee> searchEmployee(String term);
}
