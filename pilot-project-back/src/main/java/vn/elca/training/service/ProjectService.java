package vn.elca.training.service;

import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;

import java.util.List;

/**
 * Service interface for Project operations.
 *
 * @author vlp
 */
public interface ProjectService {

    ProjectDto findById(Long id);

    List<ProjectDto> searchProjects(String keyword, ProjectStatus status);

    ProjectDto create(ProjectDto projectDto);

    ProjectDto update(ProjectDto projectDto, Long id);

    void delete(List<Long> projectIds);

    long count();
}
