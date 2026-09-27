package vn.elca.training.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.model.dto.response.ProjectSearchResultDto;
import vn.elca.training.model.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@ExtendWith(SpringExtension.class)
@Transactional
@DisplayName("Unit Tests for ProjectRepository")
public class ProjectRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Group defaultGroup;
    private Employee leader;

    @BeforeEach
    public void setUp() {
        leader = employeeRepository.save(new Employee("LEA", "Leader", "Test", LocalDate.of(1985, 1, 1)));
        defaultGroup = groupRepository.save(new Group(leader));
    }

    @Test
    @DisplayName("findById: Eagerly fetches group, groupLeader, and members via @EntityGraph")
    void testFindById_WithEntityGraph_FetchesRelationsEagerly() {
        // Project ID 1 (number 1001) is seeded in data.sql with group 1 and members
        Optional<Project> found = projectRepository.findById(1L);
        assertTrue(found.isPresent(), "Expected project with ID 1 to exist");

        // Detach entities from Hibernate session to verify @EntityGraph eager fetch
        em.flush();
        em.clear();

        Project project = found.get();
        assertNotNull(project.getGroup(), "Group should be eagerly fetched by @EntityGraph");
        assertNotNull(project.getGroup().getGroupLeader(), "Group leader should be eagerly fetched by @EntityGraph");
        assertNotNull(project.getGroup().getGroupLeader().getVisa(), "Leader visa should be accessible after detach");
        assertNotNull(project.getMembers(), "Members should be eagerly fetched by @EntityGraph");
        assertFalse(project.getMembers().isEmpty(), "Project members should not be empty");
    }

    @Test
    @DisplayName("existsByProjectNumber: Returns true when project number exists")
    void testExistsByProjectNumber_WhenExists_ReturnsTrue() {
        // Project number 1001 is seeded in data.sql
        boolean exists = projectRepository.existsByProjectNumber(1001);
        assertTrue(exists, "Expected project number 1001 to exist");
    }

    @Test
    @DisplayName("existsByProjectNumber: Returns false when project number does not exist")
    void testExistsByProjectNumber_WhenNotExists_ReturnsFalse() {
        boolean exists = projectRepository.existsByProjectNumber(99999);
        assertFalse(exists, "Expected project number 99999 to not exist");
    }

    @Test
    @DisplayName("members ManyToMany: Successfully manage member relationships")
    void testProjectMembersManyToMany() {
        Employee emp1 = employeeRepository.save(new Employee("EM1", "Emp", "One", LocalDate.of(1992, 2, 2)));
        Employee emp2 = employeeRepository.save(new Employee("EM2", "Emp", "Two", LocalDate.of(1993, 3, 3)));

        Project project = new Project(
                2003,
                "Team Project",
                "Client B",
                ProjectStatus.INP,
                LocalDate.now(),
                LocalDate.now().plusMonths(6),
                defaultGroup
        );
        project.getMembers().add(emp1);
        project.getMembers().add(emp2);
        Project saved = projectRepository.save(project);

        em.flush();
        em.clear();

        Project found = projectRepository.findById(saved.getId()).orElse(null);
        assertNotNull(found);
        assertEquals(2, found.getMembers().size());
    }

    @Test
    @DisplayName("searchProjects: With default criteria and pagination")
    void testSearchProjects_WithCriteriaAndPagination() {
        Page<ProjectSearchResultDto> page = projectRepository.searchProjects(
                new ProjectSearchCriteriaDto(),
                PageRequest.of(0, 10)
        );
        assertNotNull(page);
        assertEquals(10, page.getContent().size());
        ProjectSearchResultDto firstItem = page.getContent().get(0);
        assertNotNull(firstItem.getId());
        assertNotNull(firstItem.getProjectNumber());
        assertNotNull(firstItem.getName());
        assertNotNull(firstItem.getCustomer());
        assertNotNull(firstItem.getStatus());
        assertNotNull(firstItem.getStartDate());
    }

    @Test
    @DisplayName("searchProjects: Filter by text keyword and project status")
    void testSearchProjects_ByKeywordTextAndStatus() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        criteria.setKeyword("EFV");
        criteria.setStatus(ProjectStatus.NEW);

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(Integer.valueOf(1001), page.getContent().get(0).getProjectNumber());
        assertEquals(ProjectStatus.NEW, page.getContent().get(0).getStatus());
    }

    @Test
    @DisplayName("searchProjects: Filter by numeric project number keyword")
    void testSearchProjects_ByProjectNumberKeyword() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        criteria.setKeyword("1004");

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(1, page.getTotalElements());
        assertEquals(Integer.valueOf(1004), page.getContent().get(0).getProjectNumber());
    }

    @Test
    @DisplayName("searchProjects: Returns empty page when no match found")
    void testSearchProjects_WhenNoMatch_ReturnsEmptyPage() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        criteria.setKeyword("NONEXISTENT_PROJECT_KEYWORD");

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(0, page.getTotalElements());
        assertTrue(page.getContent().isEmpty());
    }

    @Test
    @DisplayName("searchProjects: Safely handles null criteria without exception")
    void testSearchProjects_WhenCriteriaIsNull() {
        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(null, PageRequest.of(0, 10));

        assertNotNull(page);
        assertEquals(10, page.getContent().size());
        assertTrue(page.getTotalElements() >= 15);
    }

    @Test
    @DisplayName("searchProjects: By group leader VISA")
    void testSearchProjects_ByGroupLeaderVisa() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        criteria.setGroupLeaderVisa("PL1");

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    @DisplayName("searchProjects: By member VISAs including blank entries")
    void testSearchProjects_ByMemberVisas() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        Set<String> visas = new HashSet<>();
        visas.add("DTH");
        visas.add("   ");
        visas.add("");
        criteria.setMemberVisas(visas);

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
        assertTrue(page.getTotalElements() > 0);
    }

    @Test
    @DisplayName("searchProjects: By date ranges")
    void testSearchProjects_ByDateRanges() {
        ProjectSearchCriteriaDto criteria = new ProjectSearchCriteriaDto();
        criteria.setStartDateFrom(LocalDate.of(2010, 1, 1));
        criteria.setStartDateTo(LocalDate.of(2030, 12, 31));
        criteria.setEndDateFrom(LocalDate.of(2010, 1, 1));
        criteria.setEndDateTo(LocalDate.of(2030, 12, 31));

        Page<ProjectSearchResultDto> page =
                projectRepository.searchProjects(criteria, PageRequest.of(0, 10));

        assertNotNull(page);
    }

    @Test
    @DisplayName("searchProjects: With sorting variants covering ASC, DESC and Unsorted")
    void testSearchProjects_WithSortingVariants() {
        Sort[] sorts = new Sort[]{
                Sort.by(Sort.Direction.ASC, "name"),
                Sort.by(Sort.Direction.DESC, "name"),
                Sort.by(Sort.Direction.ASC, "customer"),
                Sort.by(Sort.Direction.DESC, "customer"),
                Sort.by(Sort.Direction.ASC, "status"),
                Sort.by(Sort.Direction.DESC, "status"),
                Sort.by(Sort.Direction.ASC, "startDate"),
                Sort.by(Sort.Direction.DESC, "startDate"),
                Sort.by(Sort.Direction.ASC, "projectNumber"),
                Sort.by(Sort.Direction.DESC, "projectNumber"),
                Sort.unsorted()
        };

        for (Sort sort : sorts) {
            Page<ProjectSearchResultDto> page =
                    projectRepository.searchProjects(
                            new ProjectSearchCriteriaDto(),
                            PageRequest.of(0, 5, sort)
                    );
            assertNotNull(page);
            assertEquals(5, page.getContent().size());
        }
    }
}
