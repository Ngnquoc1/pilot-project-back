package vn.elca.training.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Group;

import java.time.LocalDate;
import java.util.Optional;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(SpringRunner.class)
@Transactional
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    public void testSaveAndFindGroup() {
        Employee leader = employeeRepository.save(new Employee("QMV", "Quoc", "Manh", LocalDate.of(1995, 4, 11)));
        Group group = new Group(leader);
        Group savedGroup = groupRepository.save(group);

        Assert.assertNotNull(savedGroup.getId());
        Assert.assertNotNull(savedGroup.getVersion());
        Optional<Group> found = groupRepository.findById(savedGroup.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals("QMV", found.get().getGroupLeader().getVisa());
    }
}
