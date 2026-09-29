package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.dto.response.PageResponseDto;
import vn.elca.training.dto.response.ProjectDeleteResponseDto;
import vn.elca.training.dto.response.ProjectResponseDto;
import vn.elca.training.entity.Employee;
import vn.elca.training.entity.Group;
import vn.elca.training.entity.Project;
import vn.elca.training.entity.ProjectStatus;
import org.springframework.dao.DataIntegrityViolationException;
import vn.elca.training.exception.InvalidProjectStatusForDeletionException;
import vn.elca.training.exception.ProjectNotFoundException;
import vn.elca.training.exception.ProjectNumberAlreadyException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.impl.ProjectServiceImpl;
import vn.elca.training.validator.ProjectValidator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.mockito.Spy;
import vn.elca.training.dto.response.ProjectSearchResultDto;
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
    @DisplayName("Case 1: Search projects with criteria and pagination returning ProjectSearchResultDto")
    void testSearchProjects_WithCriteriaAndPagination_ShouldReturnPageOfSearchResultDto() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto("EFV", ProjectStatus.NEW);
        Pageable pageable = PageRequest.of(0, 5);
        ProjectSearchResultDto dto = new ProjectSearchResultDto(10L, 1001, "EFV Core", "EFV", ProjectStatus.NEW, LocalDate.of(2021, 1, 1), 0L);

        Page<ProjectSearchResultDto> mockPage = new PageImpl<>(List.of(dto), pageable, 1);
        when(projectRepository.searchProjects(criteria, pageable)).thenReturn(mockPage);

        PageResponseDto<ProjectSearchResultDto> result = projectService.searchProjects(criteria, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        ProjectSearchResultDto item = result.getContent().get(0);
        assertEquals(10L, item.getId());
        assertEquals(1001, item.getProjectNumber());
        assertEquals("EFV Core", item.getName());
        assertEquals("EFV", item.getCustomer());
        assertEquals(ProjectStatus.NEW, item.getStatus());
        assertEquals(LocalDate.of(2021, 1, 1), item.getStartDate());
        assertEquals(0L, item.getVersion());

        verify(projectRepository).searchProjects(criteria, pageable);
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
        assertEquals(1, created.getMembers().size());
        assertEquals("ABC", created.getMembers().iterator().next().getVisa());

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
        assertTrue(updated.getMembers().isEmpty());

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
        p1.setId(1L);
        Project p2 = new Project(1002, "Project 2", "Cust 2", ProjectStatus.NEW, LocalDate.now(), null, null);
        p2.setId(2L);
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllById(any())).thenReturn(List.of(p1, p2));

        ProjectDeleteResponseDto response = projectService.delete(ids);

        assertNotNull(response);
        assertEquals(2, response.getRequestedCount());
        assertEquals(2, response.getDeletedCount());
        assertTrue(response.getNotFoundIds().isEmpty());
        assertTrue(response.getMessage().contains("Successfully deleted 2"));
        verify(projectRepository).deleteAll(List.of(p1, p2));
    }

    @Test
    @DisplayName("Case 9b: Delete projects when some IDs are not found in DB")
    void testDeleteProjects_WhenSomeIdsNotFound_ShouldDeleteExistingAndReturnNotFoundIds() {
        Project p1 = new Project(1001, "Project 1", "Cust 1", ProjectStatus.NEW, LocalDate.now(), null, null);
        p1.setId(1L);
        List<Long> ids = List.of(1L, 999L);

        when(projectRepository.findAllById(any())).thenReturn(List.of(p1));

        ProjectDeleteResponseDto response = projectService.delete(ids);

        assertNotNull(response);
        assertEquals(2, response.getRequestedCount());
        assertEquals(1, response.getDeletedCount());
        assertEquals(List.of(999L), response.getNotFoundIds());
        assertTrue(response.getMessage().contains("999"));
        verify(projectRepository).deleteAll(List.of(p1));
    }

    @Test
    @DisplayName("Case 10: Throw InvalidProjectStatusForDeletionException when deleting projects with status other than NEW")
    void testDeleteProjects_WhenStatusIsNotNew_ShouldThrowException() {
        Project p1 = new Project(1001, "Project 1", "Cust 1", ProjectStatus.NEW, LocalDate.now(), null, null);
        Project p2 = new Project(1002, "Project 2", "Cust 2", ProjectStatus.INP, LocalDate.now(), null, null);
        List<Long> ids = List.of(1L, 2L);

        when(projectRepository.findAllById(any())).thenReturn(List.of(p1, p2));

        assertThrows(InvalidProjectStatusForDeletionException.class, () -> projectService.delete(ids));
        verify(projectRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Case 11: Do nothing when deleting empty or null project ID list")
    void testDeleteProjects_WhenListIsNullOrEmpty_ShouldDoNothing() {
        ProjectDeleteResponseDto r1 = projectService.delete(null);
        ProjectDeleteResponseDto r2 = projectService.delete(Collections.emptyList());

        assertNotNull(r1);
        assertEquals(0, r1.getDeletedCount());
        assertNotNull(r2);
        assertEquals(0, r2.getDeletedCount());

        verify(projectRepository, never()).findAllById(any());
        verify(projectRepository, never()).deleteAll(any());
    }

    @Test
    @DisplayName("Case 4b: Create project successfully when member visas is null")
    void testCreateProject_WhenMemberVisasIsNull_ShouldSucceedWithEmptyMembers() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectNumber(1006);
        dto.setName("Project Without Members");
        dto.setCustomer("Customer B");
        dto.setGroupId(1L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));
        dto.setMemberVisas(null);

        Group mockGroup = new Group();
        mockGroup.setId(1L);

        doNothing().when(projectValidator).validateForCreate(dto);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));
        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponseDto created = projectService.create(dto);

        assertNotNull(created);
        assertEquals(1006, created.getProjectNumber());
        assertTrue(created.getMembers().isEmpty());
        verify(employeeRepository, never()).findByVisaIn(any());
        verify(projectRepository).saveAndFlush(any(Project.class));
    }

    @Test
    @DisplayName("Case 5b: Throw ProjectNumberAlreadyException when database constraint violation occurs during save")
    void testCreateProject_WhenDatabaseThrowsDataIntegrityViolationException_ShouldThrowProjectNumberAlreadyException() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectNumber(1005);
        dto.setName("New Project");
        dto.setCustomer("Customer A");
        dto.setGroupId(1L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));

        Group mockGroup = new Group();
        mockGroup.setId(1L);

        doNothing().when(projectValidator).validateForCreate(dto);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));
        when(projectRepository.saveAndFlush(any(Project.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate key project_number"));

        assertThrows(ProjectNumberAlreadyException.class, () -> projectService.create(dto));
        verify(projectRepository).saveAndFlush(any(Project.class));
    }

    @Test
    @DisplayName("Case 5c: Throw IllegalArgumentException when group is not found")
    void testCreateProject_WhenGroupNotFound_ShouldThrowIllegalArgumentException() {
        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setProjectNumber(1005);
        dto.setName("New Project");
        dto.setCustomer("Customer A");
        dto.setGroupId(99L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));

        doNothing().when(projectValidator).validateForCreate(dto);
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> projectService.create(dto));
        assertTrue(ex.getMessage().contains("Group not found with id: 99"));
        verify(projectRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Case 6b: Update project when existing members is null and input member visas is null")
    void testUpdateProject_WhenExistingMembersIsNullAndInputVisasNull_ShouldInitializeEmptyMembers() {
        Long id = 1L;
        Project existing = new Project(1001, "Old Name", "Old Customer", ProjectStatus.NEW, LocalDate.of(2021, 1, 1), null, null);
        existing.setId(id);
        existing.setMembers(null);

        Group mockGroup = new Group();
        mockGroup.setId(1L);

        ProjectRequestDto dto = new ProjectRequestDto();
        dto.setName("Updated Name");
        dto.setCustomer("Customer");
        dto.setGroupId(1L);
        dto.setStartDate(LocalDate.of(2021, 1, 1));
        dto.setMemberVisas(null);

        when(projectRepository.findById(id)).thenReturn(Optional.of(existing));
        doNothing().when(projectValidator).validateForUpdate(existing, dto);
        when(groupRepository.findById(1L)).thenReturn(Optional.of(mockGroup));
        when(projectRepository.saveAndFlush(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponseDto updated = projectService.update(dto, id);

        assertNotNull(updated);
        assertTrue(updated.getMembers().isEmpty());
    }

    @Test
    @DisplayName("Case 9c: Delete projects when all IDs are not found in DB")
    void testDeleteProjects_WhenAllIdsNotFound_ShouldNotCallDeleteAllAndReturnAllNotFoundIds() {
        List<Long> ids = List.of(998L, 999L);
        when(projectRepository.findAllById(any())).thenReturn(Collections.emptyList());

        ProjectDeleteResponseDto response = projectService.delete(ids);

        assertNotNull(response);
        assertEquals(2, response.getRequestedCount());
        assertEquals(0, response.getDeletedCount());
        assertEquals(2, response.getNotFoundIds().size());
        assertTrue(response.getNotFoundIds().containsAll(List.of(998L, 999L)));
        assertTrue(response.getMessage().contains("Deleted 0 project(s)"));
        verify(projectRepository, never()).deleteAll(any());
    }
}
