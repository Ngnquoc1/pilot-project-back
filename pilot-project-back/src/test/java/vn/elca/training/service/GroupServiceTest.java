package vn.elca.training.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.model.dto.GroupDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Group;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.service.impl.GroupServiceImpl;
import vn.elca.training.util.ApplicationMapper;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests for GroupService")
public class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @Spy
    private ApplicationMapper applicationMapper = new ApplicationMapper();

    @InjectMocks
    private GroupServiceImpl groupService;

    @Test
    @DisplayName("findAll: Returns list of GroupDto")
    void testFindAll_Success() {
        Employee leader = new Employee("GL1", "Group", "Leader", LocalDate.of(1985, 5, 5));
        leader.setId(10L);

        Group g1 = new Group(leader);
        g1.setId(1L);

        when(groupRepository.findAll()).thenReturn(List.of(g1));

        List<GroupDto> result = groupService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(10L, result.get(0).getGroupLeaderId());
        assertEquals("GL1", result.get(0).getGroupLeaderVisa());
        verify(groupRepository).findAll();
    }
}
