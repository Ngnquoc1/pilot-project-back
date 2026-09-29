package vn.elca.training.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.elca.training.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.dto.response.ProjectSearchResultDto;
public interface ProjectRepositoryCustom {
    Page<ProjectSearchResultDto> searchProjects(ProjectSearchCriteriaDto criteriaDto, Pageable pageable);
}
