package vn.elca.training.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import vn.elca.training.model.dto.EmployeeDto;
import vn.elca.training.model.dto.GroupDto;
import vn.elca.training.model.dto.response.ProjectResponseDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Group;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.util.ApplicationMapper;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationMapperTest {

    private ApplicationMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ApplicationMapper();
    }

    @Test
    @DisplayName("Test mapping Project Entity to ProjectResponseDto")
    void testProjectToProjectResponseDto() {
        Employee leader = new Employee("LEA", "Leader", "One", LocalDate.of(1985, 1, 1));
        leader.setId(10L);

        Group group = new Group(leader);
        group.setId(1L);

        Employee member1 = new Employee("DTH", "Duc Thinh", "Ha", LocalDate.of(1990, 1, 1));
        member1.setId(2L);

        Project project = new Project(
                1001,
                "EFV Project",
                "Customer A",
                ProjectStatus.NEW,
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31),
                group
        );
        project.setId(100L);
        project.setVersion(0L);
        project.setMembers(Set.of(member1));

        ProjectResponseDto dto = mapper.projectToProjectResponseDto(project);

        assertNotNull(dto);
        assertEquals(100L, dto.getId());
        assertEquals(1001, dto.getProjectNumber());
        assertEquals("EFV Project", dto.getName());
        assertEquals("Customer A", dto.getCustomer());
        assertEquals(ProjectStatus.NEW, dto.getStatus());
        assertEquals(1L, dto.getGroupId());
        assertEquals("LEA", dto.getGroupLeaderVisa());
        assertEquals(1, dto.getMembers().size());
        assertEquals("DTH", dto.getMembers().iterator().next().getVisa());
        assertEquals("Duc Thinh Ha", dto.getMembers().iterator().next().getFullName());
    }

    @Test
    @DisplayName("Test mapping Employee Entity to EmployeeDto")
    void testEmployeeToEmployeeDto() {
        Employee emp = new Employee("NNQ", "Nhu Quoc", "Nguyen", LocalDate.of(1995, 4, 11));
        emp.setId(5L);

        EmployeeDto dto = mapper.employeeToEmployeeDto(emp);

        assertNotNull(dto);
        assertEquals(5L, dto.getId());
        assertEquals("NNQ", dto.getVisa());
        assertEquals("Nhu Quoc", dto.getFirstName());
        assertEquals("Nguyen", dto.getLastName());
        assertEquals("Nhu Quoc Nguyen", dto.getFullName());
    }

    @Test
    @DisplayName("Test mapping Group Entity to GroupDto")
    void testGroupToGroupDto() {
        Employee leader = new Employee("PL1", "Project", "Leader", LocalDate.of(1987, 5, 5));
        leader.setId(8L);

        Group group = new Group(leader);
        group.setId(2L);

        GroupDto dto = mapper.groupToGroupDto(group);

        assertNotNull(dto);
        assertEquals(2L, dto.getId());
        assertEquals(8L, dto.getGroupLeaderId());
        assertEquals("PL1", dto.getGroupLeaderVisa());
    }
}