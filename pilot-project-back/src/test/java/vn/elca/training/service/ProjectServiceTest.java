package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import vn.elca.training.model.dto.request.ProjectRequestDto;
import vn.elca.training.model.dto.response.ProjectResponseDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Group;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.exception.InvalidProjectStatusForDeletionException;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.model.exception.ProjectNumberAlreadyException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.impl.ProjectServiceImpl;
import vn.elca.training.validator.ProjectValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import org.mockito.Spy;
import vn.elca.training.util.ApplicationMapper;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for ProjectService")
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ProjectValidator projectValidator;

    @Spy
    private ApplicationMapper applicationMapper = new ApplicationMapper();

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("Case 1: Search projects by keyword and status (US02 searchProjects)")
    void testSearchProjects_WithKeywordAndStatus_ShouldReturnMatchingProjects() {
        String keyword = "EFV";
        ProjectStatus status = ProjectStatus.NEW;
        Project project = new Project(1001, "EFV Core", "EFV", ProjectStatus.NEW, LocalDate.now(), null, null);

        when(projectRepository.searchProjects(keyword, status)).thenReturn(List.of(project));

        List<ProjectResponseDto> actualResult = projectService.searchProjects(keyword, status);

        assertNotNull(actualResult);
        assertEquals(1, actualResult.size());
        assertEquals("EFV Core", actualResult.get(0).getName());
        assertEquals(ProjectStatus.NEW, actualResult.get(0).getStatus());
        verify(projectRepository).searchProjects(keyword, status);
    }

    @Test
    @DisplayName("Case 2: Find project by ID successfully")
    void testFindById_Success() {
        Long id = 1L;
        Project project = new Project(1001, "EFV Core", "EFV", ProjectStatus.NEW, LocalDate.now(), null, null);
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        ProjectResponseDto found = projectService.findById(id);

        assertNotNull(found);
        assertEquals("EFV Core", found.getName());
    }

    @Test
    @DisplayName("Case 3: Throw ProjectNotFoundException when ID does not exist")
    void testFindById_NotFound() {
        Long invalidId = 999L;
        when(projectRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.findById(invalidId));
    }

    @Test
    @DisplayName("Case 4: Create project successfully when data is valid")
    void testCreateProject_Success() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectNumber(1005);
        dto.setName("New Project");
        dto.setCustomer("Customer A");
        dto.setGroupId(1L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));
        dto.setEndDate(LocalDate.of(2021, 12, 31));
        dto.setMemberVisas(Set.of("ABC"));

        Group mockGroup = new Group();
        mockGroup.setId(1L);

        Employee mockEmployee = new Employee();
        mockEmployee.setId(10L);
        mockEmployee.setVisa("ABC");

        doNothing().when(projectValidator).validateForCreate(dto);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));
        when(employeeRepository.findByVisaIn(Set.of("ABC"))).thenReturn(List.of(mockEmployee));
        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponseDto created = projectService.create(dto);

        assertNotNull(created);
        assertEquals(1005, created.getProjectNumber());
        assertEquals("New Project", created.getName());
        assertEquals("Customer A", created.getCustomer());
        assertEquals(ProjectStatus.NEW, created.getStatus());
        assertEquals(1L, created.getGroupId());
        assertEquals(1, created.getMemberVisas().size());

        verify(projectValidator).validateForCreate(dto);
        verify(projectRepository).saveAndFlush(any(Project.class));
    }

    @Test
    @DisplayName("Case 5: Throw ProjectNumberAlreadyException when project number already exists")
    void testCreateProject_WhenProjectNumberAlreadyExists_ShouldThrowException() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectNumber(1001);

        doThrow(new ProjectNumberAlreadyException(1001)).when(projectValidator).validateForCreate(dto);

        assertThrows(ProjectNumberAlreadyException.class, () -> projectService.create(dto));
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("Case 6: Update project successfully")
    void testUpdateProject_Success() {
        Long id = 1L;
        Project existing = new Project(1001, "Old Name", "Old Customer", ProjectStatus.NEW, LocalDate.of(2021, 1, 1), null, null);
        existing.setId(id);
        existing.setVersion(0L);

        Group mockGroup = new Group();
        mockGroup.setId(2L);

        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setName("Updated Name");
        dto.setCustomer("New Customer");
        dto.setStatus(ProjectStatus.INP);
        dto.setStartDate(LocalDate.of(2021, 2, 1));
        dto.setEndDate(LocalDate.of(2021, 12, 31));
        dto.setGroupId(2L);
        dto.setMemberVisas(Collections.emptySet());

        when(projectRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(projectValidator).validateForUpdate(existing, dto);
        when(groupRepository.findById(2L)).thenReturn(Optional.of(mockGroup));
        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponseDto updated = projectService.update(dto, id);

        assertEquals("Updated Name", updated.getName());
        assertEquals("New Customer", updated.getCustomer());
        assertEquals(ProjectStatus.INP, updated.getStatus());
        assertEquals(LocalDate.of(2021, 2, 1), updated.getStartDate());
        assertEquals(LocalDate.of(2021, 12, 31), updated.getEndDate());
        assertEquals(2L, updated.getGroupId());
        assertTrue(updated.getMemberVisas().isEmpty());

        verify(projectValidator).validateForUpdate(existing, dto);
        verify(projectRepository).saveAndFlush(existing);
    }

    @Test
    @DisplayName("Case 7: Throw ProjectNotFoundException when updating non-existent project")
    void testUpdateProject_WhenProjectNotFound_ShouldThrowException() {
        Long invalidId = 999L;
        ProjectRequestDto dto = new ProjectRequestDto();

        when(projectRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.update(dto, invalidId));
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Case 8: Throw ObjectOptimisticLockingFailureException when concurrent update conflict occurs")
    void testUpdateProject_WhenConcurrentUpdateConflict_ShouldThrowException() {
        Long id = 1L;
        Project existing = new Project(1001, "Old Name", "Old Customer", ProjectStatus.NEW, LocalDate.of(2021, 1, 1), null, null);
        existing.setId(id);
        existing.setVersion(1L);

        Group mockGroup = new Group();
        mockGroup.setId(1L);

        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setName("Concurrent Name");
        dto.setCustomer("Concurrent Customer");
        dto.setGroupId(1L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));
        dto.setVersion(0L);

        when(projectRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(projectValidator).validateForUpdate(existing, dto);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));

        when(projectRepository.saveAndFlush(any(Project.class)))
                .thenThrow(new ObjectOptimisticLockingFailureException(Project.class, id));

        assertThrows(ObjectOptimisticLockingFailureException.class, () -> projectService.update(dto, id));
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("Case 9: Delete projects with status NEW successfully")
    void testDeleteProjects_WhenStatusIsNew_ShouldDeleteSuccessfully() {
        Project p1 = new Project(1001, "Project 1", "Cust 1", ProjectStatus.NEW, LocalDate.now(), null, null);
        Project p2 = new Project(1002, "Project 2", "Cust 2", ProjectStatus.NEW, LocalDate.now(), null, null);
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllById(ids)).thenReturn(List.of(p1, p2));

        projectService.delete(ids);

        verify(projectRepository).deleteAll(List.of(p1, p2));
    }

    @Test
    @DisplayName("Case 10: Throw InvalidProjectStatusForDeletionException when deleting projects with status other than NEW")
    void testDeleteProjects_WhenStatusIsNotNew_ShouldThrowException() {
        Project p1 = new Project(1001, "Project 1", "Cust 1", ProjectStatus.NEW, LocalDate.now(), null, null);
        Project p2 = new Project(1002, "Project 2", "Cust 2", ProjectStatus.INP, LocalDate.now(), null, null);
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllById(ids)).thenReturn(List.of(p1, p2));

        assertThrows(InvalidProjectStatusForDeletionException.class, () -> projectService.delete(ids));
        verify(projectRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Case 11: Do nothing when deleting empty or null project ID list")
    void testDeleteProjects_WhenListIsNullOrEmpty_ShouldDoNothing() {
        projectService.delete(null);
        projectService.delete(Collections.emptyList());

        verify(projectRepository, never()).findAllById(any());
        verify(projectRepository, never()).deleteAll(any());
    }
}
