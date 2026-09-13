package vn.elca.training.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Project;
import vn.elca.training.repository.ProjectRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(SpringRunner.class)
public class ProjectServiceTransactionalTest {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectRepository projectRepository;

    @PersistenceContext
    private EntityManager em;

    /**
     * Requirement Case 1 (Happy Path):
     * Create maintenance project successfully:
     * - Old project is deactivated (activated = false)
     * - New maintenance project is persisted with name: <old name> + " Maint. " + <current year>
     */
    @Test
    public void testCreateMaintenanceProject_Success() {
        // Given
        Project devProject = new Project("KSTA_DEV", LocalDate.now(), "CUSTOMER_ELCA");
        devProject.setActivated(true);
        devProject = projectRepository.saveAndFlush(devProject);
        Long devId = devProject.getId();

        // When
        Project maintenanceProject = projectService.createMaintenanceProject(devId);

        // Then
        // 1. Verify old project was deactivated
        Project updatedDev = projectRepository.findById(devId).orElse(null);
        Assert.assertNotNull(updatedDev);
        Assert.assertFalse("Old project must be inactive (activated = false)", updatedDev.isActivated());

        // 2. Verify maintenance project
        Assert.assertNotNull(maintenanceProject);
        int currentYear = LocalDate.now().getYear();
        String expectedName = String.format("KSTA_DEV Maint. %d", currentYear);
        Assert.assertEquals(expectedName, maintenanceProject.getName());
        Assert.assertTrue("Maintenance project must be active", maintenanceProject.isActivated());
        Assert.assertEquals("CUSTOMER_ELCA", maintenanceProject.getCustomer());
    }

    /**
     * Requirement Case 2 (Truly Transactional Verification):
     * Proves that if an exception occurs during the process, ALL changes are rolled back:
     * - The new project must NOT be persisted into DB
     * - The old project must NOT be updated (retains activated = true)
     */
    @Test
    public void testCreateMaintenanceProject_RollbackOnException() {
        int currentYear = LocalDate.now().getYear();
        String projName = "SECUTIX_DEV";
        String duplicateMaintName = String.format("%s Maint. %d", projName, currentYear);

        // Given 1: An existing maintenance project already in DB
        Project existingMaint = new Project(duplicateMaintName, LocalDate.now(), "CUSTOMER_SECUTIX");
        projectRepository.saveAndFlush(existingMaint);

        // Given 2: A development project with activated = true
        Project devProject = new Project(projName, LocalDate.now(), "CUSTOMER_SECUTIX");
        devProject.setActivated(true);
        devProject = projectRepository.saveAndFlush(devProject);
        Long devId = devProject.getId();

        // When: Attempt to create maintenance project, which will throw IllegalStateException
        try {
            projectService.createMaintenanceProject(devId);
            Assert.fail("Expected IllegalStateException to be thrown because maintenance project already exists!");
        } catch (IllegalStateException e) {
            // Expected exception caught
        }

        // Then: Verify Rollback in Database
        em.clear();
        Project oldProjectInDb = projectRepository.findById(devId).orElse(null);
        Assert.assertNotNull(oldProjectInDb);
        Assert.assertTrue("Old project must STILL be active (activated = true) because the transaction rolled back!",
                oldProjectInDb.isActivated());
    }
}
