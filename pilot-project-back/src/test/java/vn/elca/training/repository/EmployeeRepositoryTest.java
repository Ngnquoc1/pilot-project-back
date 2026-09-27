package vn.elca.training.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Employee;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@ExtendWith(SpringExtension.class)
@Transactional
@DisplayName("Unit Tests for EmployeeRepository")
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    @DisplayName("searchEmployee: Returns empty list when term is null")
    void testSearchEmployee_WhenTermIsNull_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee(null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("searchEmployee: Returns empty list when term is empty")
    void testSearchEmployee_WhenTermIsEmpty_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("searchEmployee: Returns empty list when term is whitespace")
    void testSearchEmployee_WhenTermIsWhitespace_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("   ");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("searchEmployee: Search by VISA exact and case-insensitive")
    void testSearchEmployee_ByVisa_ExactAndCaseInsensitive() {
        List<Employee> resultsLower = employeeRepository.searchEmployee("dth");
        assertFalse(resultsLower.isEmpty());
        assertTrue(resultsLower.stream().anyMatch(e -> "DTH".equalsIgnoreCase(e.getVisa())));

        List<Employee> resultsUpper = employeeRepository.searchEmployee("DTH");
        assertFalse(resultsUpper.isEmpty());
        assertTrue(resultsUpper.stream().anyMatch(e -> "DTH".equalsIgnoreCase(e.getVisa())));
    }

    @Test
    @DisplayName("searchEmployee: Search by VISA partial match")
    void testSearchEmployee_ByVisa_PartialMatch() {
        List<Employee> results = employeeRepository.searchEmployee("DT");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(e -> e.getVisa().contains("DT")));
    }

    @Test
    @DisplayName("searchEmployee: Search by first name case-insensitive")
    void testSearchEmployee_ByFirstName_CaseInsensitive() {
        List<Employee> results = employeeRepository.searchEmployee("duc thinh");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(e -> "Duc Thinh".equalsIgnoreCase(e.getFirstName())));
    }

    @Test
    @DisplayName("searchEmployee: Search by last name case-insensitive")
    void testSearchEmployee_ByLastName_CaseInsensitive() {
        List<Employee> results = employeeRepository.searchEmployee("nguyen");
        assertFalse(results.isEmpty());
        assertTrue(results.size() >= 3);
        assertTrue(results.stream().allMatch(e ->
                e.getLastName().toLowerCase().contains("nguyen") ||
                e.getFirstName().toLowerCase().contains("nguyen") ||
                e.getVisa().toLowerCase().contains("nguyen")
        ));
    }

    @Test
    @DisplayName("searchEmployee: Trim leading and trailing whitespace")
    void testSearchEmployee_WithLeadingAndTrailingWhitespace() {
        List<Employee> results = employeeRepository.searchEmployee("  PL1  ");
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(e -> "PL1".equalsIgnoreCase(e.getVisa())));
    }

    @Test
    @DisplayName("searchEmployee: Returns empty list when no match found")
    void testSearchEmployee_WhenNoMatch_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("NONEXISTENT_XYZ");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("searchEmployee: Respect MAX_RESULTS limit of 10")
    void testSearchEmployee_MaxResultsLimit() {
        // Save 12 employees with visa X01 to X12 and matching last name
        for (int i = 1; i <= 12; i++) {
            String visa = String.format("X%02d", i);
            Employee emp = new Employee(visa, "Limit", "MatchTarget", LocalDate.of(1990, 1, 1));
            employeeRepository.save(emp);
        }

        List<Employee> results = employeeRepository.searchEmployee("MatchTarget");
        assertNotNull(results);
        assertEquals(10, results.size());
    }

    @Test
    @DisplayName("findByVisaIn: Returns employees matching provided visas")
    void testFindByVisaIn() {
        List<Employee> employees = employeeRepository.findByVisaIn(Arrays.asList("DTH", "BHU", "NON"));
        assertEquals(2, employees.size());

        List<Employee> empty = employeeRepository.findByVisaIn(Collections.emptyList());
        assertTrue(empty.isEmpty());
    }
}
