package vn.elca.training.repository;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Group;
import vn.elca.training.model.entity.User;

import java.util.Optional;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@RunWith(SpringRunner.class)
@Transactional
public class GroupRepositoryTest {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindGroup() {
        User leader = userRepository.save(new User("QMV", "Quoc Manh"));
        Group group = new Group(leader);
        Group savedGroup = groupRepository.save(group);

        Assert.assertNotNull(savedGroup.getId());
        Optional<Group> found = groupRepository.findById(savedGroup.getId());
        Assert.assertTrue(found.isPresent());
        Assert.assertEquals("QMV", found.get().getGroupLeader().getUsername());
    }
}
