package vn.elca.training.service;

import org.springframework.data.domain.Pageable;
import vn.elca.training.model.dto.request.ProjectRequestDto;
import vn.elca.training.model.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.model.dto.response.PageResponseDto;
import vn.elca.training.model.dto.response.ProjectDeleteResponseDto;
import vn.elca.training.model.dto.response.ProjectResponseDto;
import vn.elca.training.model.dto.response.ProjectSearchResultDto;
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

    PageResponseDto<ProjectSearchResultDto> searchProjects(ProjectSearchCriteriaDto criteria, Pageable pageable);

    ProjectResponseDto create(ProjectRequestDto projectDto);

    ProjectResponseDto update(ProjectRequestDto projectDto, Long id);

    ProjectDeleteResponseDto delete(List<Long> projectIds);

    long count();
}
