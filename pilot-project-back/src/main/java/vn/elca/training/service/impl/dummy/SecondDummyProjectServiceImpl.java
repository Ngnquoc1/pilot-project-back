package vn.elca.training.service.impl.dummy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.service.ProjectService;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

/**
 * @author gtn
 *
 */
@Service
@Profile("dummy")
public class SecondDummyProjectServiceImpl extends AbstractDummyProjectService implements ProjectService {

    @Override
    public List<Project> findAll() {
        throw new UnsupportedOperationException("This is second dummy service");
    }

    @Override
    public List<Project> findByName(String keyword) {
        throw new UnsupportedOperationException("This is second dummy service");
    }

    @Override
    public Project findById(Long id) {
        return null;
    }

    @Override
    public Project update(ProjectDto projectDto, Long id) {
        return null;
    }

    @Override
    public long count() {
        printCurrentActiveProfiles();
        throw new UnsupportedOperationException("This is second dummy service");
    }

    @Override
    public Project createMaintenanceProject(Long oldProjectId) {
        throw new UnsupportedOperationException("This is second dummy service");
    }
}
