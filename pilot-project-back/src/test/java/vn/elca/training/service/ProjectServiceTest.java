package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.model.entity.Project;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.impl.ProjectServiceImpl;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test cho ProjectService - Method findByName")
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    @Test
    @DisplayName("Case 1: Tìm thấy danh sách dự án khi từ khóa khớp")
    void testFindByName_WhenKeywordMatches_ShouldReturnProjectList() {

        String keyword = "EFV";
        Project project1 = new Project("EFV Core", LocalDate.now());
        Project project2 = new Project("EFV Integration", LocalDate.now());
        List<Project> mockList = List.of(project1, project2);

        // Config Mock Repository: Khi được gọi với từ khóa "EFV" thì trả về mockList
        when(projectRepository.findProjectByNameContainsIgnoreCase("EFV"))
                .thenReturn(mockList);

        // [When]: Gọi method cần test của Service
        List<Project> actualResult = projectService.findByName(keyword);

        // [Then]: Kiểm tra kết quả trả về
        assertNotNull(actualResult, "Kết quả trả về không được null");
        assertEquals(2, actualResult.size(), "Số lượng phần tử trả về phải là 2");
        assertEquals("EFV Core", actualResult.get(0).getName());
        assertEquals("EFV Integration", actualResult.get(1).getName());

        // [Verify]: Xác minh Service có thực sự gọi xuống Repository đúng 1 lần với đúng từ khóa "EFV"
        verify(projectRepository, times(1)).findProjectByNameContainsIgnoreCase("EFV");
        verifyNoMoreInteractions(projectRepository);
    }

    @Test
    @DisplayName("Case 2: Trả về danh sách rỗng khi không có dự án nào khớp từ khóa")
    void testFindByName_WhenNoMatchFound_ShouldReturnEmptyList() {
        // [Given]
        String keyword = "NOT_EXISTING_KEYWORD";
        when(projectRepository.findProjectByNameContainsIgnoreCase(keyword))
                .thenReturn(Collections.emptyList());

        // [When]
        List<Project> actualResult = projectService.findByName(keyword);

        // [Then]
        assertNotNull(actualResult, "Kết quả trả về không được null");
        assertTrue(actualResult.isEmpty(), "Danh sách kết quả phải rỗng");

        // [Verify]
        verify(projectRepository, times(1)).findProjectByNameContainsIgnoreCase(keyword);
    }

    @ParameterizedTest(name = "Case 3: Test với từ khóa ''{0}''")
    @ValueSource(strings = { "efv", "EFV", "Efv", "   " })
    @DisplayName("Case 3: Data-Driven Test với nhiều định dạng từ khóa khác nhau")
    void testFindByName_Parameterized(String keyword) {
        // [Given]
        Project sampleProject = new Project("EFV Project", LocalDate.now());
        when(projectRepository.findProjectByNameContainsIgnoreCase(keyword))
                .thenReturn(List.of(sampleProject));

        // [When]
        List<Project> actualResult = projectService.findByName(keyword);

        // [Then]
        assertEquals(1, actualResult.size());
        assertEquals("EFV Project", actualResult.get(0).getName());

        // [Verify]
        verify(projectRepository).findProjectByNameContainsIgnoreCase(keyword);
    }

    @Test
    @DisplayName("Case 4: Tạo dự án bảo trì thành công - Cũ inactive, Mới active với tên đúng chuẩn")
    void testCreateMaintenanceProject_Success() {
        Long oldId = 1L;
        Project oldProject = new Project("EFV", LocalDate.now(), "ELCA");
        oldProject.setId(oldId);
        oldProject.setActivated(true);

        when(projectRepository.findById(oldId)).thenReturn(java.util.Optional.of(oldProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Project maintenanceProject = projectService.createMaintenanceProject(oldId);

        // Verify old project deactivated
        assertFalse(oldProject.isActivated(), "Dự án cũ phải bị chuyển thành inactive (activated = false)");

        // Verify maintenance project attributes
        assertNotNull(maintenanceProject, "Dự án bảo trì mới không được null");
        int currentYear = LocalDate.now().getYear();
        assertEquals(String.format("EFV Maint. %d", currentYear), maintenanceProject.getName(), "Tên dự án mới phải có đuôi Maint. <năm>");
        assertTrue(maintenanceProject.isActivated(), "Dự án bảo trì mới phải ở trạng thái active (activated = true)");
        assertEquals("ELCA", maintenanceProject.getCustomer());

        // Verify repository interactions: save called twice (1 update old, 1 insert new)
        verify(projectRepository, times(2)).save(any(Project.class));
    }

    @Test
    @DisplayName("Case 5: Báo lỗi ProjectNotFoundException khi ID dự án cũ không tồn tại")
    void testCreateMaintenanceProject_NotFound() {
        Long invalidId = 999L;
        when(projectRepository.findById(invalidId)).thenReturn(java.util.Optional.empty());

        assertThrows(vn.elca.training.model.exception.ProjectNotFoundException.class, () -> {
            projectService.createMaintenanceProject(invalidId);
        });

        verify(projectRepository, never()).save(any(Project.class));
    }
}
