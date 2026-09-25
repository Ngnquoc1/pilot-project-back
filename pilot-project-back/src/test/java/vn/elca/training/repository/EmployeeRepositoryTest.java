package vn.elca.training.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Employee;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(SpringRunner.class)
@Transactional
public class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testSearchEmployee_WhenTermIsNull_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee(null);
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchEmployee_WhenTermIsEmpty_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchEmployee_WhenTermIsWhitespace_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("   ");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchEmployee_ByVisa_ExactAndCaseInsensitive() {
        List<Employee> resultsLower = employeeRepository.searchEmployee("dth");
        Assert.assertFalse(resultsLower.isEmpty());
        Assert.assertTrue(resultsLower.stream().anyMatch(e -> "DTH".equalsIgnoreCase(e.getVisa())));

        List<Employee> resultsUpper = employeeRepository.searchEmployee("DTH");
        Assert.assertFalse(resultsUpper.isEmpty());
        Assert.assertTrue(resultsUpper.stream().anyMatch(e -> "DTH".equalsIgnoreCase(e.getVisa())));
    }

    @Test
    public void testSearchEmployee_ByVisa_PartialMatch() {
        List<Employee> results = employeeRepository.searchEmployee("DT");
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.stream().anyMatch(e -> e.getVisa().contains("DT")));
    }

    @Test
    public void testSearchEmployee_ByFirstName_CaseInsensitive() {
        List<Employee> results = employeeRepository.searchEmployee("duc thinh");
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.stream().anyMatch(e -> "Duc Thinh".equalsIgnoreCase(e.getFirstName())));
    }

    @Test
    public void testSearchEmployee_ByLastName_CaseInsensitive() {
        List<Employee> results = employeeRepository.searchEmployee("nguyen");
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.size() >= 3);
        Assert.assertTrue(results.stream().allMatch(e ->
                e.getLastName().toLowerCase().contains("nguyen") ||
                e.getFirstName().toLowerCase().contains("nguyen") ||
                e.getVisa().toLowerCase().contains("nguyen")
        ));
    }

    @Test
    public void testSearchEmployee_WithLeadingAndTrailingWhitespace() {
        List<Employee> results = employeeRepository.searchEmployee("  PL1  ");
        Assert.assertFalse(results.isEmpty());
        Assert.assertTrue(results.stream().anyMatch(e -> "PL1".equalsIgnoreCase(e.getVisa())));
    }

    @Test
    public void testSearchEmployee_WhenNoMatch_ReturnsEmptyList() {
        List<Employee> results = employeeRepository.searchEmployee("NONEXISTENT_XYZ");
        Assert.assertNotNull(results);
        Assert.assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchEmployee_MaxResultsLimit() {
        // Save 12 employees with visa X01 to X12 and matching last name
        for (int i = 1; i <= 12; i++) {
            String visa = String.format("X%02d", i);
            Employee emp = new Employee(visa, "Limit", "MatchTarget", LocalDate.of(1990, 1, 1));
            employeeRepository.save(emp);
        }

        List<Employee> results = employeeRepository.searchEmployee("MatchTarget");
        Assert.assertNotNull(results);
        Assert.assertEquals(10, results.size());
    }

    @Test
    public void testFindByVisa() {
        Optional<Employee> found = employeeRepository.findByVisa("DTH");
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals("DTH", found.get().getVisa());

        Optional<Employee> notFound = employeeRepository.findByVisa("NON");
        Assert.assertFalse(notFound.isPresent());
    }

    @Test
    public void testFindByVisaIn() {
        List<Employee> employees = employeeRepository.findByVisaIn(Arrays.asList("DTH", "BHU", "NON"));
        Assert.assertEquals(2, employees.size());

        List<Employee> empty = employeeRepository.findByVisaIn(Collections.emptyList());
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testExistsByVisa() {
        Assert.assertTrue(employeeRepository.existsByVisa("DTH"));
        Assert.assertFalse(employeeRepository.existsByVisa("NON"));
    }
}
