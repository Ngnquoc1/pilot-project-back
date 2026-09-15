package vn.elca.training.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.service.ProjectService;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author gtn
 *
 */
@RestController
@RequestMapping("/projects")
public class ProjectController extends AbstractApplicationController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController( @Qualifier("projectServiceImpl") ProjectService projectService){
        this.projectService=projectService;
    }

    @GetMapping("/search")
    public List<ProjectDto> search(@RequestParam(value="keyword", defaultValue = "") String keyword) {
        return projectService.findByName(keyword)
                .stream()
                .map(mapper::projectToProjectDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectDto searchById(@PathVariable Long id){
            return mapper.projectToProjectDto(projectService.findById(id));
    }

    @PutMapping("/{id}")
    public  ProjectDto update(@RequestBody ProjectDto projectDto, @PathVariable Long id) {
        return mapper.projectToProjectDto(projectService.update(projectDto, id));
    }

    @PostMapping("/{id}/maintenance")
    public ProjectDto createMaintenanceProject(@PathVariable Long id) {
        return mapper.projectToProjectDto(projectService.createMaintenanceProject(id));
    }
}
