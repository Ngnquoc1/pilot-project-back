package vn.elca.training.service;

import vn.elca.training.model.dto.request.ProjectRequestDto;
import vn.elca.training.model.dto.response.ProjectResponseDto;
import vn.elca.training.model.entity.ProjectStatus;

import java.util.List;

/**
 * Service interface for Project operations.
 * Uses ProjectRequestDto for creation/updates and ProjectResponseDto for queries.
 *
 * @author vlp, nnnq
 */
public interface ProjectService {

    ProjectResponseDto findById(Long id);

    List<ProjectResponseDto> searchProjects(String keyword, ProjectStatus status);

    ProjectResponseDto create(ProjectRequestDto projectDto);

    ProjectResponseDto update(ProjectRequestDto projectDto, Long id);

    void delete(List<Long> projectIds);

    long count();
}
