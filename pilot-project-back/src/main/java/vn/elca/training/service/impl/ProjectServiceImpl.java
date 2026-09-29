package vn.elca.training.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.dto.request.ProjectSearchCriteriaDto;
import vn.elca.training.dto.response.PageResponseDto;
import vn.elca.training.dto.response.ProjectDeleteResponseDto;
import vn.elca.training.dto.response.ProjectResponseDto;
import vn.elca.training.dto.response.ProjectSearchResultDto;
import vn.elca.training.entity.Employee;
import vn.elca.training.entity.Group;
import vn.elca.training.entity.Project;
import vn.elca.training.entity.ProjectStatus;
import vn.elca.training.exception.InvalidProjectStatusForDeletionException;
import vn.elca.training.exception.ProjectNotFoundException;
import vn.elca.training.exception.ProjectNumberAlreadyException;
import org.springframework.dao.DataIntegrityViolationException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;
import vn.elca.training.service.ProjectService;
import vn.elca.training.util.ApplicationMapper;
import vn.elca.training.validator.ProjectValidator;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
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
    public PageResponseDto<ProjectSearchResultDto> searchProjects(ProjectSearchCriteriaDto criteria, Pageable pageable) {
        Page<ProjectSearchResultDto> pageResult = projectRepository.searchProjects(criteria, pageable);

        return new PageResponseDto<>(
                pageResult.getContent(),
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

        try {
            Project savedProject = projectRepository.saveAndFlush(newProject);
            return applicationMapper.projectToProjectResponseDto(savedProject);
        } catch (DataIntegrityViolationException ex) {
            throw new ProjectNumberAlreadyException(projectDto.getProjectNumber(), ex);
        }
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
    @Transactional
    public ProjectDeleteResponseDto delete(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return new ProjectDeleteResponseDto(0, 0, Collections.emptyList(), "No project IDs provided.");
        }

        Set<Long> uniqueIds = new LinkedHashSet<>(projectIds);
        List<Project> projects = projectRepository.findAllById(uniqueIds);

        // Kiểm tra ràng buộc nghiệp vụ: Chỉ cho phép xóa project trạng thái NEW (Rollback nếu vi phạm)
        for (Project project : projects) {
            if (project.getStatus() != ProjectStatus.NEW) {
                throw new InvalidProjectStatusForDeletionException("Only projects with status 'NEW' can be deleted.");
            }
        }

        // Xác định danh sách ID đã tìm thấy và các ID không tồn tại (đã bị xóa trước đó)
        Set<Long> foundIds = projects.stream()
                .map(Project::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        List<Long> notFoundIds = uniqueIds.stream()
                .filter(id -> !foundIds.contains(id))
                .collect(Collectors.toList());

        // Xóa các entity hợp lệ tìm thấy
        if (!projects.isEmpty()) {
            projectRepository.deleteAll(projects);
        }

        // Tạo thông điệp minh bạch
        String message;
        if (notFoundIds.isEmpty()) {
            message = String.format("Successfully deleted %d project(s).", projects.size());
        } else {
            message = String.format("Deleted %d project(s). Project ID(s) %s not found or already deleted.",
                    projects.size(), notFoundIds);
        }

        return new ProjectDeleteResponseDto(uniqueIds.size(), projects.size(), notFoundIds, message);
    }

    private void mapDtoToEntity(Project project, ProjectRequestDto dto) {
        project.setName(dto.getName().trim());
        project.setCustomer(dto.getCustomer().trim());
        project.setStartDate(dto.getStartDate());
        project.setEndDate(dto.getEndDate());
        project.setStatus(dto.getStatus() != null ? dto.getStatus() : ProjectStatus.NEW);

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
