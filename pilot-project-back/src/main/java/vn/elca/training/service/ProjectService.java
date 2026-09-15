package vn.elca.training.service;

import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;

import java.util.List;

/**
 * Service interface for Project operations.
 *
 * @author vlp
 */
public interface ProjectService {
    List<Project> findAll();

    List<Project> findByName(String keyword);

    Project findById(Long id);

    Project update(ProjectDto projectDto, Long id);

    long count();
}
