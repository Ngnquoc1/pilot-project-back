package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.impl.ProjectServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test cho ProjectService")
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("Case 1: Tìm thấy danh sách dự án khi từ khóa khớp")
    void testFindByName_WhenKeywordMatches_ShouldReturnProjectList() {
        String keyword = "EFV";
        Project project1 = new Project(1001, "EFV Core", "EFV", ProjectStatus.NEW, LocalDate.now(), null, null);
        Project project2 = new Project(1002, "EFV Integration", "EFV", ProjectStatus.PLA, LocalDate.now(), null, null);
        List<Project> mockList = List.of(project1, project2);

        when(projectRepository.findAll()).thenReturn(mockList);

        List<Project> actualResult = projectService.findByName(keyword);

        assertNotNull(actualResult);
        assertEquals(2, actualResult.size());
        assertEquals("EFV Core", actualResult.get(0).getName());
        assertEquals("EFV Integration", actualResult.get(1).getName());
    }

    @Test
    @DisplayName("Case 2: Tìm theo ID thành công")
    void testFindById_Success() {
        Long id = 1L;
        Project project = new Project(1001, "EFV Core", "EFV", ProjectStatus.NEW, LocalDate.now(), null, null);
        when(projectRepository.findById(id)).thenReturn(Optional.of(project));

        Project found = projectService.findById(id);

        assertNotNull(found);
        assertEquals("EFV Core", found.getName());
    }

    @Test
    @DisplayName("Case 3: Ném ProjectNotFoundException khi ID không tồn tại")
    void testFindById_NotFound() {
        Long invalidId = 999L;
        when(projectRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.findById(invalidId));
    }

    @Test
    @DisplayName("Case 4: Cập nhật thông tin dự án thành công")
    void testUpdateProject_Success() {
        Long id = 1L;
        Project existing = new Project(1001, "Old Name", "Old Customer", ProjectStatus.NEW, LocalDate.of(2021, 1, 1), null, null);
        when(projectRepository.findById(id)).thenReturn(Optional.of(existing));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectDto dto = new ProjectDto();
        dto.setName("Updated Name");
        dto.setCustomer("New Customer");
        dto.setStatus(ProjectStatus.INP);
        dto.setStartDate(LocalDate.of(2021, 2, 1));
        dto.setEndDate(LocalDate.of(2021, 12, 31));

        Project updated = projectService.update(dto, id);

        assertEquals("Updated Name", updated.getName());
        assertEquals("New Customer", updated.getCustomer());
        assertEquals(ProjectStatus.INP, updated.getStatus());
        assertEquals(LocalDate.of(2021, 2, 1), updated.getStartDate());
        assertEquals(LocalDate.of(2021, 12, 31), updated.getEndDate());
    }
}
