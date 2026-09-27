package vn.elca.training.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.ApplicationWebConfig;
import vn.elca.training.model.entity.Group;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ContextConfiguration(classes = {ApplicationWebConfig.class})
@ExtendWith(SpringExtension.class)
@Transactional
@DisplayName("Unit Tests for GroupRepository")
public class GroupRepositoryTest {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GroupRepository groupRepository;

    @Test
    @DisplayName("existsGroupById: Returns true when group ID exists")
    void testExistsGroupById_WhenExists_ReturnsTrue() {
        // Group ID 1 is seeded in data.sql
        boolean exists = groupRepository.existsGroupById(1L);
        assertTrue(exists, "Expected group with ID 1 to exist");
    }

    @Test
    @DisplayName("existsGroupById: Returns false when group ID does not exist")
    void testExistsGroupById_WhenNotExists_ReturnsFalse() {
        boolean exists = groupRepository.existsGroupById(99999L);
        assertFalse(exists, "Expected group with ID 99999 to not exist");
    }

    @Test
    @DisplayName("findAll: Eagerly fetches groupLeader via @EntityGraph without LazyInitializationException")
    void testFindAll_WithEntityGraph_FetchesGroupLeaderEagerly() {
        List<Group> groups = groupRepository.findAll();

        assertNotNull(groups);
        assertFalse(groups.isEmpty(), "Groups list should not be empty");
        assertTrue(groups.size() >= 2, "Expected at least 2 groups from seed data");

        // Clear persistence context to detach entities
        // If @EntityGraph worked properly, groupLeader is already in memory
        em.flush();
        em.clear();

        for (Group group : groups) {
            assertNotNull(group.getId());
            assertNotNull(group.getGroupLeader(), "Group leader should be eagerly fetched by @EntityGraph");
            assertNotNull(group.getGroupLeader().getVisa(), "Leader visa should be accessible after session detach");
        }
    }

    @Test
    @DisplayName("save: Throws DataIntegrityViolationException when groupLeader is null")
    void testSaveGroup_WithoutLeader_ThrowsException() {
        Group groupWithoutLeader = new Group(null);

        assertThrows(DataIntegrityViolationException.class, () -> {
            groupRepository.saveAndFlush(groupWithoutLeader);
        }, "Saving group without leader must violate nullable = false constraint");
    }
}
