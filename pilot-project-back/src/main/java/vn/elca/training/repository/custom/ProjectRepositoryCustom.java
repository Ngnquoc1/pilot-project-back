package vn.elca.training.repository.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.elca.training.model.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.model.dto.response.ProjectSearchResultDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> searchProjects(String keyword, ProjectStatus status);
    Page<ProjectSearchResultDto> searchProjects(ProjectSearchCriteriaDto criteriaDto, Pageable pageable);
}
