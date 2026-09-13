package vn.elca.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.ProjectService;

import java.util.List;

/**
 * @author vlp
 *
 */
@Service
@Profile("!dummy | dev")
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    //Error3: No autowire
    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository= projectRepository;
    }

    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

//    @Override
//    public List<Project> findByName(String keyword) {
//        return projectRepository.findAll().stream()
//                .filter(p-> p.getName().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<Project> findByName(String keyword) {
        return projectRepository.findProjectByNameContainsIgnoreCase(keyword);
    }

    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException(id));
    }

    @Override
    public Project update(ProjectDto projectDto, Long id) {

        Project existingProject=projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException(id));

        if (projectDto.getName()!=null) {
            existingProject.setName(projectDto.getName());
        }

        if (projectDto.getFinishingDate() != null) {
            existingProject.setFinishingDate(projectDto.getFinishingDate());
        }

        if(projectDto.getCustomer() !=null) {
            existingProject.setCustomer(projectDto.getCustomer());
        }
        return projectRepository.save(existingProject);
    }

    @Override
    public long count() {
        return projectRepository.count();
    }
}
