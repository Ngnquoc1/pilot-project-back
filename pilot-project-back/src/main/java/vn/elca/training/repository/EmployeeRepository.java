package vn.elca.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.elca.training.entity.Employee;
import vn.elca.training.repository.custom.EmployeeRepositoryCustom;

import java.util.Collection;
import java.util.List;

/**
 * Repository interface for Employee entity.
 *
 * @author nnnq
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, QuerydslPredicateExecutor<Employee>, EmployeeRepositoryCustom {
    List<Employee> findByVisaIn(Collection<String> visas);
}
