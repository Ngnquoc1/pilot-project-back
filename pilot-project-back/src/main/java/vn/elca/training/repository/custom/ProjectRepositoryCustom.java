package vn.elca.training.repository.custom;

import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;

import java.util.List;

public interface ProjectRepositoryCustom {
    List<Project> searchProjects(String keyword, ProjectStatus status);
}
