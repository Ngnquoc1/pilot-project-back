package vn.elca.training.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.service.ProjectService;

import vn.elca.training.model.entity.ProjectStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for Project operations.
 *
 * @author gtn
 */
@RestController
@RequestMapping("/projects")
public class ProjectController extends AbstractApplicationController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(@Qualifier("projectServiceImpl") ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/search")
    public List<ProjectDto> search(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "status", required = false) ProjectStatus status) {
        return projectService.searchProjects(keyword, status);
    }

    @GetMapping("/{id}")
    public ProjectDto searchById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectDto update(@RequestBody ProjectDto projectDto, @PathVariable Long id) {
        return projectService.update(projectDto, id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto create(@RequestBody ProjectDto projectDto) {
        return projectService.create(projectDto);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody List<Long> projectIds) {
        projectService.delete(projectIds);
    }

}
