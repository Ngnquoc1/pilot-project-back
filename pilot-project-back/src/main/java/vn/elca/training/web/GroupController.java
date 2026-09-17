package vn.elca.training.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.elca.training.model.dto.GroupDto;
import vn.elca.training.service.GroupService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Group operations.
 *
 * @author nnnq
 */
@RestController
@RequestMapping("/groups")
public class GroupController extends AbstractApplicationController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<GroupDto> findAll() {
        return groupService.findAll();
    }
}
