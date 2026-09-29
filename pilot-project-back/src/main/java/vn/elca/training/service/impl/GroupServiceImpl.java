package vn.elca.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.dto.GroupDto;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.service.GroupService;
import vn.elca.training.util.ApplicationMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Group operations.
 *
 * @author nnnq
 */
@Service
@Transactional(readOnly = true)
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final ApplicationMapper applicationMapper;

    @Autowired
    public GroupServiceImpl(GroupRepository groupRepository, ApplicationMapper applicationMapper) {
        this.groupRepository = groupRepository;
        this.applicationMapper = applicationMapper;
    }

    @Override
    public List<GroupDto> findAll() {
        return groupRepository.findAll()
                .stream()
                .map(applicationMapper::groupToGroupDto)
                .collect(Collectors.toList());
    }
}
