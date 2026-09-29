package vn.elca.training.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import vn.elca.training.dto.request.ProjectRequestDto;
import vn.elca.training.entity.Employee;
import vn.elca.training.entity.Project;
import vn.elca.training.exception.EmployeeVisaNotFoundException;
import vn.elca.training.exception.ProjectNumberAlreadyException;
import vn.elca.training.repository.EmployeeRepository;
import vn.elca.training.repository.GroupRepository;
import vn.elca.training.repository.ProjectRepository;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProjectValidator {
    private final ProjectRepository projectRepository;
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ProjectValidator(ProjectRepository projectRepository, GroupRepository groupRepository, EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
        this.groupRepository = groupRepository;
        this.projectRepository = projectRepository;
    }

    public void validateCommon(String name, String customer, LocalDate startDate, LocalDate endDate, Long groupId, Set<String> inputVisas) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project Name is mandatory");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("Project Name must not exceed 50 characters");
        }
        if (customer == null || customer.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer is mandatory");
        }
        if (customer.length() > 50) {
            throw new IllegalArgumentException("Customer must not exceed 50 characters");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("Start Date is mandatory");
        }
        if (groupId == null) {
            throw new IllegalArgumentException("Group Id is mandatory");
        }
        if (endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date must be after or equal to Start date");
        }
        if (!groupRepository.existsGroupById(groupId)) {
            throw new IllegalArgumentException("Group not found with id: " + groupId);
        }

        if (inputVisas != null && !inputVisas.isEmpty()) {
            List<Employee> existingEmployees = employeeRepository.findByVisaIn(inputVisas);
            Set<String> existingVisas = existingEmployees.stream()
                    .map(Employee::getVisa)
                    .collect(Collectors.toSet());

            Set<String> notFoundVisas = new HashSet<>(inputVisas);
            notFoundVisas.removeAll(existingVisas);

            if (!notFoundVisas.isEmpty()) {
                throw new EmployeeVisaNotFoundException(notFoundVisas);
            }
        }
    }

    public void validateCommon(ProjectRequestDto dto) {
        validateCommon(dto.getName(), dto.getCustomer(), dto.getStartDate(), dto.getEndDate(), dto.getGroupId(), dto.getMemberVisas());
    }

    public void validateForCreate(ProjectRequestDto dto) {
        if (dto.getProjectNumber() == null) {
            throw new IllegalArgumentException("Project number is mandatory");
        }

        if (projectRepository.existsByProjectNumber(dto.getProjectNumber())) {
            throw new ProjectNumberAlreadyException(dto.getProjectNumber());
        }

        validateCommon(dto);
    }

    public void validateForUpdate(Project existingProject, ProjectRequestDto dto) {
        if (dto.getProjectNumber() != null && !dto.getProjectNumber().equals(existingProject.getProjectNumber())) {
            throw new IllegalArgumentException("Project number cannot be changed in edit mode");
        }

        if (dto.getVersion() != null && !dto.getVersion().equals(existingProject.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Project.class, existingProject.getId());
        }

        validateCommon(dto);
    }
}
