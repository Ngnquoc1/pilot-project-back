package vn.elca.training.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.entity.Employee;
import vn.elca.training.entity.Project;
import vn.elca.training.entity.ProjectStatus;
import vn.elca.training.exception.EmployeeVisaNotFoundException;
import vn.elca.training.exception.ProjectNumberAlreadyException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for ProjectValidator")
public class ProjectValidatorTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProjectValidator projectValidator;

    private ProjectRequestDto validDto;

    @BeforeEach
    void setUp() {
        validDto = new ProjectRequestDto();
        validDto.setProjectNumber(1001);
        validDto.setName("Valid Project Name");
        validDto.setCustomer("Valid Customer");
        validDto.setGroupId(1L);
        validDto.setStartDate(LocalDate.of(2021, 1, 1));
        validDto.setEndDate(LocalDate.of(2021, 12, 31));
        validDto.setStatus(ProjectStatus.NEW);
        validDto.setMemberVisas(Set.of("ABC"));
    }

    @Test
    @DisplayName("Create: Valid DTO passes validation")
    void testValidateForCreate_Success() {
        when(projectRepository.existsByProjectNumber(1001)).thenReturn(false);
        when(groupRepository.existsGroupById(1L)).thenReturn(true);
        Employee emp = new Employee("ABC", "First", "Last", LocalDate.of(1990, 1, 1));
        when(employeeRepository.findByVisaIn(Set.of("ABC"))).thenReturn(List.of(emp));

        assertDoesNotThrow(() -> projectValidator.validateForCreate(validDto));
    }

    @Test
    @DisplayName("Create: Missing project number throws IllegalArgumentException")
    void testValidateForCreate_MissingProjectNumber_ThrowsException() {
        validDto.setProjectNumber(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Project number is mandatory", ex.getMessage());
    }

    @Test
    @DisplayName("Create: Duplicate project number throws ProjectNumberAlreadyException")
    void testValidateForCreate_DuplicateProjectNumber_ThrowsException() {
        when(projectRepository.existsByProjectNumber(1001)).thenReturn(true);
        assertThrows(ProjectNumberAlreadyException.class,
                () -> projectValidator.validateForCreate(validDto));
    }

    @Test
    @DisplayName("Create: Missing or blank name throws IllegalArgumentException")
    void testValidateForCreate_BlankName_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);

        validDto.setName(null);
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Project Name is mandatory", ex1.getMessage());

        validDto.setName("   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Project Name is mandatory", ex2.getMessage());
    }

    @Test
    @DisplayName("Create: Name exceeding 50 characters throws IllegalArgumentException")
    void testValidateForCreate_NameExceeds50_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        validDto.setName("A".repeat(51));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Project Name must not exceed 50 characters", ex.getMessage());
    }

    @Test
    @DisplayName("Create: Missing or blank customer throws IllegalArgumentException")
    void testValidateForCreate_BlankCustomer_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);

        validDto.setCustomer(null);
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Customer is mandatory", ex1.getMessage());

        validDto.setCustomer("   ");
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Customer is mandatory", ex2.getMessage());
    }

    @Test
    @DisplayName("Create: Customer exceeding 50 characters throws IllegalArgumentException")
    void testValidateForCreate_CustomerExceeds50_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        validDto.setCustomer("C".repeat(51));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Customer must not exceed 50 characters", ex.getMessage());
    }

    @Test
    @DisplayName("Create: Missing start date throws IllegalArgumentException")
    void testValidateForCreate_MissingStartDate_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        validDto.setStartDate(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Start Date is mandatory", ex.getMessage());
    }

    @Test
    @DisplayName("Create: Missing groupId throws IllegalArgumentException")
    void testValidateForCreate_MissingGroupId_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        validDto.setGroupId(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("Group Id is mandatory", ex.getMessage());
    }

    @Test
    @DisplayName("Create: End date before start date throws IllegalArgumentException")
    void testValidateForCreate_EndDateBeforeStartDate_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        validDto.setStartDate(LocalDate.of(2021, 6, 1));
        validDto.setEndDate(LocalDate.of(2021, 5, 31));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertEquals("End date must be after or equal to Start date", ex.getMessage());
    }

    @Test
    @DisplayName("Create: Non-existent group throws IllegalArgumentException")
    void testValidateForCreate_GroupNotFound_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        when(groupRepository.existsGroupById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForCreate(validDto));
        assertTrue(ex.getMessage().contains("Group not found with id: 1"));
    }

    @Test
    @DisplayName("Create: Non-existent member visa throws EmployeeVisaNotFoundException")
    void testValidateForCreate_VisaNotFound_ThrowsException() {
        when(projectRepository.existsByProjectNumber(anyInt())).thenReturn(false);
        when(groupRepository.existsGroupById(1L)).thenReturn(true);
        when(employeeRepository.findByVisaIn(Set.of("ABC"))).thenReturn(Collections.emptyList());

        assertThrows(EmployeeVisaNotFoundException.class,
                () -> projectValidator.validateForCreate(validDto));
    }

    @Test
    @DisplayName("Update: Changing projectNumber throws IllegalArgumentException")
    void testValidateForUpdate_ChangedProjectNumber_ThrowsException() {
        Project existing = new Project();
        existing.setProjectNumber(1001);
        existing.setVersion(0L);

        validDto.setProjectNumber(2002);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> projectValidator.validateForUpdate(existing, validDto));
        assertEquals("Project number cannot be changed in edit mode", ex.getMessage());
    }

    @Test
    @DisplayName("Update: Version mismatch throws ObjectOptimisticLockingFailureException")
    void testValidateForUpdate_VersionMismatch_ThrowsException() {
        Project existing = new Project();
        existing.setId(5L);
        existing.setProjectNumber(1001);
        existing.setVersion(2L);

        validDto.setProjectNumber(1001);
        validDto.setVersion(1L);

        assertThrows(ObjectOptimisticLockingFailureException.class,
                () -> projectValidator.validateForUpdate(existing, validDto));
    }

    @Test
    @DisplayName("Update: Valid update passes cleanly")
    void testValidateForUpdate_Success() {
        Project existing = new Project();
        existing.setId(5L);
        existing.setProjectNumber(1001);
        existing.setVersion(1L);

        validDto.setProjectNumber(1001);
        validDto.setVersion(1L);
        validDto.setMemberVisas(null); // null visas should be skipped without query

        when(groupRepository.existsGroupById(1L)).thenReturn(true);

        assertDoesNotThrow(() -> projectValidator.validateForUpdate(existing, validDto));
        verify(employeeRepository, never()).findByVisaIn(any());
    }
}
