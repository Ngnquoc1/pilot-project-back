package vn.elca.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.elca.training.model.entity.Employee;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Employee entity.
 *
 * @author nnnq
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, QuerydslPredicateExecutor<Employee> {
    Optional<Employee> findByVisa(String visa);
    List<Employee> findByVisaIn(Collection<String> visas);
    boolean existsByVisa(String visa);
}
