package vn.elca.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.model.dto.request.ProjectRequestDto;
import vn.elca.training.model.dto.response.PageResponseDto;
import vn.elca.training.model.dto.response.ProjectResponseDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Group;
import vn.elca.training.model.entity.Project;
import vn.elca.training.model.entity.ProjectStatus;
import vn.elca.training.model.exception.InvalidProjectStatusForDeletionException;
import vn.elca.training.model.exception.ProjectNotFoundException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.ProjectService;
import vn.elca.training.util.ApplicationMapper;
import vn.elca.training.validator.ProjectValidator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service implementation for Project operations.
 *
 * @author vlp, nnnq
 */
@Service
@Profile("!dummy | dev")
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final ProjectValidator projectValidator;
    private final EmployeeRepository employeeRepository;
    private final ApplicationMapper applicationMapper;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectValidator projectValidator,
                              GroupRepository groupRepository,
                              EmployeeRepository employeeRepository,
                              ApplicationMapper applicationMapper) {
        this.projectRepository = projectRepository;
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
        this.projectValidator = projectValidator;
        this.applicationMapper = applicationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponseDto findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return applicationMapper.projectToProjectResponseDto(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectResponseDto> searchProjects(String keyword, ProjectStatus status) {
        return projectRepository.searchProjects(keyword, status)
                .stream()
                .map(applicationMapper::projectToProjectResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponseDto<ProjectResponseDto> searchProjects(String keyword, ProjectStatus status, Pageable pageable) {
        Page<Project> pageResult = projectRepository.searchProjects(keyword, status, pageable);
        List<ProjectResponseDto> dtoList = pageResult.getContent().stream()
                .map(applicationMapper::projectToProjectResponseDto)
                .collect(Collectors.toList());

        return new PageResponseDto<>(
                dtoList,
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        );
    }

    @Override
    @Transactional
    public ProjectResponseDto create(ProjectRequestDto projectDto) throws IllegalArgumentException {
        projectValidator.validateForCreate(projectDto);

        Project newProject = new Project();
        newProject.setProjectNumber(projectDto.getProjectNumber());

        mapDtoToEntity(newProject, projectDto);

        Project savedProject = projectRepository.saveAndFlush(newProject);
        return applicationMapper.projectToProjectResponseDto(savedProject);
    }

    @Override
    @Transactional
    public ProjectResponseDto update(ProjectRequestDto projectDto, Long id) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));

        projectValidator.validateForUpdate(existingProject, projectDto);

        mapDtoToEntity(existingProject, projectDto);

        Project savedProject = projectRepository.saveAndFlush(existingProject);
        return applicationMapper.projectToProjectResponseDto(savedProject);
    }

    @Override
    @Transactional()
    public void delete(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return;
        }
        List<Project> projects = projectRepository.findAllById(projectIds);
        for (Project project : projects) {
            if (project.getStatus() != ProjectStatus.NEW) {
                throw new InvalidProjectStatusForDeletionException("Only projects with status 'NEW' can be deleted.");
            }
        }
        projectRepository.deleteAll(projects);
    }

    @Override
    public long count() {
        return projectRepository.count();
    }

    private void mapDtoToEntity(Project project, ProjectRequestDto dto) {
        project.setName(dto.getName().trim());
        project.setCustomer(dto.getCustomer().trim());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.NEW);
        project.setVersion(dto.getVersion());
        Group group = groupRepository.findById(dto.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + dto.getGroupId()));
        project.setGroup(group);

        Set<String> memberVisas = dto.getMemberVisas();
        if (memberVisas != null && !memberVisas.isEmpty()) {
            List<Employee> employees = employeeRepository.findByVisaIn(memberVisas);
            project.setMembers(new HashSet<>(employees));
        } else {
            if (project.getMembers() != null) {
                project.getMembers().clear();
            } else {
                project.setMembers(new HashSet<>());
            }
        }
    }
}
