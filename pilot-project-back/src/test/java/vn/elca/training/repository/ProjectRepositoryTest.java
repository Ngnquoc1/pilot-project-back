package vn.elca.training.repository;

import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(SpringRunner.class)
@Transactional
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

    @Before
    public void setUp() {
        leader = employeeRepository.save(new Employee("LEA", "Leader", "Test", LocalDate.of(1985, 1, 1)));
        defaultGroup = groupRepository.save(new Group(leader));

    }

    @Test
    public void testSaveAndFindProject() {
        Project project = new Project(
                2001,
                "New Project Test",
                "ELCA Client",
                ProjectStatus.NEW,
                LocalDate.of(2021, 1, 1),
                LocalDate.of(2021, 12, 31),
                defaultGroup
        );
        Project saved = projectRepository.save(project);

        Assert.assertNotNull(saved.getId());
        Assert.assertNotNull(saved.getVersion());
        Assert.assertEquals(Integer.valueOf(2001), saved.getProjectNumber());

        Optional<Project> found = projectRepository.findById(saved.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals("New Project Test", found.get().getName());
        Assert.assertEquals(ProjectStatus.NEW, found.get().getStatus());
    }

    @Test
    public void testFindWithQueryDSL() {
        final String projectName = "QUERYDSL_PIM_PROJECT";
        Project project = new Project(
                2002,
                projectName,
                "Customer A",
                ProjectStatus.PLA,
                LocalDate.now(),
                null,
                defaultGroup
        );
        projectRepository.save(project);

        Project found = new JPAQuery<Project>(em)
                .from(QProject.project)
                .where(QProject.project.name.eq(projectName))
                .fetchFirst();

        Assert.assertNotNull(found);
        Assert.assertEquals(projectName, found.getName());
        Assert.assertEquals(Integer.valueOf(2002), found.getProjectNumber());
    }

    @Test
    public void testProjectMembersManyToMany() {
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
        Assert.assertNotNull(found);
        Assert.assertEquals(2, found.getMembers().size());
    }

    @Test
    public void testSearchProjects_ByKeywordName() {

        List<Project> results = projectRepository.searchProjects("EFV", null);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(Integer.valueOf(1001), results.get(0).getProjectNumber());
    }

    @Test
    public void testSearchProjects_ByKeywordCustomer() {

        List<Project> results = projectRepository.searchProjects("Secutix", null);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals("CRYSTAL BALL Analytics", results.get(0).getName());
    }

    @Test
    public void testSearchProjects_ByProjectNumber() {
        // Tìm theo số dự án "1004"
        List<Project> results = projectRepository.searchProjects("1004", null);
        Assert.assertEquals(1, results.size());
        Assert.assertEquals(Integer.valueOf(1004), results.get(0).getProjectNumber());
    }

    @Test
    public void testSearchProjects_ByStatusOnly() {

        List<Project> results = projectRepository.searchProjects(null, ProjectStatus.NEW);
        Assert.assertEquals(2, results.size());
        Assert.assertTrue(results.stream().allMatch(p -> p.getStatus() == ProjectStatus.NEW));

        Assert.assertEquals(Integer.valueOf(1001), results.get(0).getProjectNumber());
        Assert.assertEquals(Integer.valueOf(1005), results.get(1).getProjectNumber());
    }

    @Test
    public void testSearchProjects_EmptyCriteria_ShouldReturnAllSortedAsc() {
        // Không truyền tiêu chí nào -> Trả về toàn bộ danh sách sắp xếp tăng dần
        List<Project> results = projectRepository.searchProjects("", null);
        Assert.assertEquals(5, results.size());
        Assert.assertEquals(Integer.valueOf(1001), results.get(0).getProjectNumber());
        Assert.assertEquals(Integer.valueOf(1005), results.get(4).getProjectNumber());
    }
}
