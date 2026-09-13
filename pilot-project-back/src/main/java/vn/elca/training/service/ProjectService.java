package vn.elca.training.service;

import java.util.List;
import java.util.Optional;

import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;

/**
 * @author vlp
 *
 */
public interface ProjectService {
    List<Project> findAll();

    List<Project> findByName(String keyword);

    Project findById(Long id);

    Project update(ProjectDto projectDto, Long id);

    long count();

    Project createMaintenanceProject(Long oldProjectId);
}
