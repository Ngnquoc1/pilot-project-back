package vn.elca.training.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.dto.response.PageResponseDto;
import vn.elca.training.dto.response.ProjectDeleteResponseDto;
import vn.elca.training.dto.response.ProjectResponseDto;
import vn.elca.training.dto.response.ProjectSearchResultDto;
import vn.elca.training.service.ProjectService;

import java.util.List;

@RestController
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService projectService;

    @Autowired
    public ProjectController(@Qualifier("projectServiceImpl") ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/search")
    public PageResponseDto<ProjectSearchResultDto> search(
            ProjectSearchCriteriaDto criteria,
            @PageableDefault(page = 0, size = 5, sort = "projectNumber", direction = Sort.Direction.ASC) Pageable pageable) {
        return projectService.searchProjects(criteria, pageable);
    }

    @GetMapping("/{id}")
    public ProjectResponseDto searchById(@PathVariable Long id) {
        return projectService.findById(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ProjectResponseDto update(@RequestBody ProjectRequestDto projectDto, @PathVariable Long id) {
        return projectService.update(projectDto, id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDto create(@RequestBody ProjectRequestDto projectDto) {
        return projectService.create(projectDto);
    }

    @DeleteMapping()
    public ResponseEntity<ProjectDeleteResponseDto> delete(@RequestBody List<Long> projectIds) {
        ProjectDeleteResponseDto response = projectService.delete(projectIds);
        return ResponseEntity.ok(response);
    }
}
