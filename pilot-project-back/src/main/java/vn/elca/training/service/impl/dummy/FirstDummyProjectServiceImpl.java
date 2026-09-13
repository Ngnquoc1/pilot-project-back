package vn.elca.training.service.impl.dummy;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.service.ProjectService;

import java.util.List;
import java.util.Optional;

/**
 * @author gtn
 *
 */
@Component
@Profile("dummy")
public class FirstDummyProjectServiceImpl extends AbstractDummyProjectService implements ProjectService {

    @Override
    public List<Project> findAll() {
        throw new UnsupportedOperationException("This is first dummy service");
    }

    @Override
    public List<Project> findByName(String keyword) {
        throw new UnsupportedOperationException("This is first dummy service");
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
        throw new UnsupportedOperationException("This is first dummy service");
    }
}
