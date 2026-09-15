package vn.elca.training.repository;

import java.time.LocalDate;
import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.querydsl.jpa.impl.JPAQuery;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.*;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(value = SpringRunner.class)
@Transactional
public class ProjectRepositoryTest {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCountAll() {
        projectRepository.save(new Project("KSTA", LocalDate.now()));
        projectRepository.save(new Project("LAGAPEO", LocalDate.now()));
        projectRepository.save(new Project("ZHQUEST", LocalDate.now()));
        projectRepository.save(new Project("SECUTIX", LocalDate.now()));
        Assert.assertEquals(9, projectRepository.count());
    }

    @Test
    public void testFindOneWithQueryDSL() {
        final String PROJECT_NAME = "KSTA_QUERYDSL";
        projectRepository.save(new Project(PROJECT_NAME, LocalDate.now()));
        Project project = new JPAQuery<Project>(em)
                .from(QProject.project)
                .where(QProject.project.name.eq(PROJECT_NAME))
                .fetchFirst();
        Assert.assertEquals(PROJECT_NAME, project.getName());
    }

    /**
     * Requirement 1: To verify the saving of one project via IProjectRepository
     */
    @Test
    public void testSaveOneProject() {
        Project project = new Project("TEST_SINGLE_PROJECT", LocalDate.now(), "ELCA");
        Project saved = projectRepository.save(project);
        Assert.assertNotNull(saved.getId());

        Optional<Project> found = projectRepository.findById(saved.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals("TEST_SINGLE_PROJECT", found.get().getName());
        Assert.assertEquals("ELCA", found.get().getCustomer());
    }

    /**
     * Requirement 2: To verify the saving of multiple projects to achieve the data represented by the tree
     * Tree:
     * - Group QMV:
     *   - EFV (PL: HTV, Devs: TQP, NQN, QA: HNH)
     *   - CXTRANET (PL: QKP, QA: PLH, Dev: HNL)
     *   - CRYSTAL BALL (PL: MKN, QA: TBH, Dev: TDN)
     * - Group HNH:
     *   - IOC CLIENT EXTRANET (PL: APL, Devs: HPN, BNN, PNH, QA: HUN)
     *   - KSTA MIGRATION (PL: XHP, QA: QMV, Dev: VVT)
     */
    @Test
    public void testSaveMultipleProjectsTree() {
        // 1. Create Users
        User qmv = userRepository.save(new User("QMV"));
        User htv = userRepository.save(new User("HTV"));
        User tqp = userRepository.save(new User("TQP"));
        User hnh = userRepository.save(new User("HNH"));
        User nqn = userRepository.save(new User("NQN"));
        User qkp = userRepository.save(new User("QKP"));
        User plh = userRepository.save(new User("PLH"));
        User hnl = userRepository.save(new User("HNL"));
        User mkn = userRepository.save(new User("MKN"));
        User tbh = userRepository.save(new User("TBH"));
        User tdn = userRepository.save(new User("TDN"));

        User apl = userRepository.save(new User("APL"));
        User hpn = userRepository.save(new User("HPN"));
        User hun = userRepository.save(new User("HUN"));
        User bnn = userRepository.save(new User("BNN"));
        User pnh = userRepository.save(new User("PNH"));
        User xhp = userRepository.save(new User("XHP"));
        User vvt = userRepository.save(new User("VVT"));

        // 2. Create Groups
        Group group1 = groupRepository.save(new Group(qmv));
        Group group2 = groupRepository.save(new Group(hnh));

        // 3. Create Projects for Group 1 (Leader QMV)
        Project efv = new Project("EFV_TREE", LocalDate.now(), "ELCA", group1);
        efv.setProjectMembers(new HashSet<>(Arrays.asList(htv, tqp, hnh, nqn)));

        Project cxtranet = new Project("CXTRANET_TREE", LocalDate.now(), "ELCA", group1);
        cxtranet.setProjectMembers(new HashSet<>(Arrays.asList(qkp, plh, hnl)));

        Project crystalBall = new Project("CRYSTAL_BALL_TREE", LocalDate.now(), "ELCA", group1);
        crystalBall.setProjectMembers(new HashSet<>(Arrays.asList(mkn, tbh, tdn)));

        // 4. Create Projects for Group 2 (Leader HNH)
        Project ioc = new Project("IOC_CLIENT_EXTRANET_TREE", LocalDate.now(), "IOC", group2);
        ioc.setProjectMembers(new HashSet<>(Arrays.asList(apl, hpn, hun, bnn, pnh)));

        Project ksta = new Project("KSTA_MIGRATION_TREE", LocalDate.now(), "KSTA", group2);
        ksta.setProjectMembers(new HashSet<>(Arrays.asList(xhp, qmv, vvt)));

        // 5. Save all projects
        projectRepository.saveAll(Arrays.asList(efv, cxtranet, crystalBall, ioc, ksta));
        em.flush();
        em.clear();

        // 6. Assertions to verify the tree
        // Verify Group 1 projects
        List<Project> group1Projects = new JPAQuery<Project>(em)
                .from(QProject.project)
                .innerJoin(QProject.project.group, QGroup.group)
                .innerJoin(QGroup.group.groupLeader, QUser.user)
                .where(QUser.user.username.eq("QMV"))
                .fetch();
        Assert.assertEquals(3, group1Projects.size());

        // Verify Group 2 projects
        List<Project> group2Projects = new JPAQuery<Project>(em)
                .from(QProject.project)
                .innerJoin(QProject.project.group, QGroup.group)
                .innerJoin(QGroup.group.groupLeader, QUser.user)
                .where(QUser.user.username.eq("HNH"))
                .fetch();
        Assert.assertEquals(2, group2Projects.size());

        // Verify members in EFV
        Project foundEfv = projectRepository.findById(efv.getId()).orElse(null);
        Assert.assertNotNull(foundEfv);
        Assert.assertEquals(4, foundEfv.getProjectMembers().size());

        // Verify QMV is both leader of Group 1 and member in KSTA MIGRATION
        Project foundKsta = projectRepository.findById(ksta.getId()).orElse(null);
        Assert.assertNotNull(foundKsta);
        boolean qmvIsMember = foundKsta.getProjectMembers().stream()
                .anyMatch(u -> "QMV".equals(u.getUsername()));
        Assert.assertTrue(qmvIsMember);
    }

    /**
     * Requirement 3: To verify the deletion of a project via IProjectRepository
     */
    @Test
    public void testDeleteProject() {
        User user = userRepository.save(new User("DELETE_MEMBER"));
        Project project = new Project("TO_DELETE", LocalDate.now(), "ELCA");
        project.setProjectMembers(new HashSet<>(Collections.singletonList(user)));
        Project saved = projectRepository.save(project);
        Long projectId = saved.getId();
        Assert.assertNotNull(projectId);

        // Delete the project
        projectRepository.delete(saved);
        em.flush();

        // Verify project is deleted
        Optional<Project> found = projectRepository.findById(projectId);
        Assert.assertFalse(found.isPresent());

        // Verify member user still exists (not cascade-deleted)
        Optional<User> memberStillExists = userRepository.findById(user.getId());
        Assert.assertTrue(memberStillExists.isPresent());
    }

    /**
     * Requirement 4: To verify a simple query written in QueryDSL to query projects based on their own attributes
     */
    @Test
    public void testSimpleQueryWithQueryDSL() {
        projectRepository.save(new Project("SIMPLE_QUERY_PROJ", LocalDate.of(2026, 12, 31), "CUSTOMER_A"));
        em.flush();

        QProject qProject = QProject.project;
        List<Project> result = new JPAQuery<Project>(em)
                .from(qProject)
                .where(qProject.name.eq("SIMPLE_QUERY_PROJ")
                        .and(qProject.customer.eq("CUSTOMER_A")))
                .fetch();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("SIMPLE_QUERY_PROJ", result.get(0).getName());
    }

    /**
     * Requirement 5: To verify a complex query written in QueryDSL to query projects based on their own and relations' attributes
     * (i.e. name + group's and customer's attributes)
     */
    @Test
    public void testComplexQueryWithQueryDSL() {
        User leader = userRepository.save(new User("COMPLEX_LEADER"));
        Group group = groupRepository.save(new Group(leader));

        Project p1 = new Project("COMPLEX_P1", LocalDate.now(), "TARGET_CUSTOMER", group);
        Project p2 = new Project("COMPLEX_P2", LocalDate.now(), "OTHER_CUSTOMER", group);
        projectRepository.saveAll(Arrays.asList(p1, p2));
        em.flush();

        QProject qProject = QProject.project;
        QGroup qGroup = QGroup.group;
        QUser qUser = QUser.user;

        List<Project> result = new JPAQuery<Project>(em)
                .from(qProject)
                .innerJoin(qProject.group, qGroup)
                .innerJoin(qGroup.groupLeader, qUser)
                .where(qUser.username.eq("COMPLEX_LEADER")
                        .and(qProject.customer.eq("TARGET_CUSTOMER"))
                        .and(qProject.name.startsWith("COMPLEX")))
                .fetch();

        Assert.assertEquals(1, result.size());
        Assert.assertEquals("COMPLEX_P1", result.get(0).getName());
    }
}
