package vn.elca.training.service;

import org.springframework.data.domain.Pageable;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.dto.response.PageResponseDto;
import vn.elca.training.dto.response.ProjectDeleteResponseDto;
import vn.elca.training.dto.response.ProjectResponseDto;
import vn.elca.training.dto.response.ProjectSearchResultDto;

import java.util.List;

/**
 * Service interface for Project operations.
 * Uses ProjectRequestDto for creation/updates and ProjectResponseDto for queries.
 *
 * @author vlp, nnnq
 */
public interface ProjectService {

    ProjectResponseDto findById(Long id);

    PageResponseDto<ProjectSearchResultDto> searchProjects(ProjectSearchCriteriaDto criteria, Pageable pageable);

    ProjectResponseDto create(ProjectRequestDto projectDto);

    ProjectResponseDto update(ProjectRequestDto projectDto, Long id);

    ProjectDeleteResponseDto delete(List<Long> projectIds);
}
