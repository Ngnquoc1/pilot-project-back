package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.model.dto.EmployeeDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.service.impl.EmployeeServiceImpl;
import vn.elca.training.util.ApplicationMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for EmployeeService")
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Spy
    private ApplicationMapper applicationMapper = new ApplicationMapper();

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    @DisplayName("findAll: Returns list of EmployeeDto")
    void testFindAll_Success() {
        Employee e1 = new Employee("ABC", "An", "Bui", LocalDate.of(1990, 1, 1));
        e1.setId(1L);
        Employee e2 = new Employee("DEF", "Dung", "Em", LocalDate.of(1992, 2, 2));
        e2.setId(2L);

        when(employeeRepository.findAll()).thenReturn(List.of(e1, e2));

        List<EmployeeDto> result = employeeService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("ABC", result.get(0).getVisa());
        assertEquals("An Bui", result.get(0).getFullName());
        assertEquals("DEF", result.get(1).getVisa());
        verify(employeeRepository).findAll();
    }

    @Test
    @DisplayName("searchEmployees: Returns matched EmployeeDto list")
    void testSearchEmployees_Success() {
        Employee e1 = new Employee("ABC", "An", "Bui", LocalDate.of(1990, 1, 1));
        e1.setId(1L);

        when(employeeRepository.searchEmployee("ABC")).thenReturn(List.of(e1));

        List<EmployeeDto> result = employeeService.searchEmployees("ABC");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ABC", result.get(0).getVisa());
        verify(employeeRepository).searchEmployee("ABC");
    }
}
