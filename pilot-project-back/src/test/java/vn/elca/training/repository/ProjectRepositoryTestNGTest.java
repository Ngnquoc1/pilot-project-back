package vn.elca.training.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Project;

import java.util.List;

@SpringBootTest(classes = ApplicationWebConfig.class)
public class ProjectRepositoryTestNGTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testCount(){
        long count = projectRepository.count();
        Assert.assertTrue(count>=5, "Database has at least 5 projects");
    }

    @Test
    public void testFindProjectByNameContainsIgnoreCase() {
        List<Project> results=projectRepository.findProjectByNameContainsIgnoreCase("efv");
        List<Project> results1=projectRepository.findProjectByNameContainsIgnoreCase("EFV");
        Assert.assertFalse(results.isEmpty());
        Assert.assertEquals(results.size(), results1.size());
    }
}
