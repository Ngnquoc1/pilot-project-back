package vn.elca.training.util;

import org.springframework.stereotype.Component;
import vn.elca.training.model.dto.ProjectDto;
import vn.elca.training.model.entity.Employee;
import vn.elca.training.model.entity.Project;

import java.util.stream.Collectors;

/**
 * Mapper utility component to convert between entities and DTOs.
 *
 * @author gtn
 */
@Component
public class ApplicationMapper {

    public ProjectDto projectToProjectDto(Project entity) {
        if (entity == null) {
            return null;
        }
        ProjectDto dto = new ProjectDto();
        dto.setId(entity.getId());
        dto.setProjectNumber(entity.getProjectNumber());
        dto.setName(entity.getName());
        dto.setCustomer(entity.getCustomer());
        dto.setStatus(entity.getStatus());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setVersion(entity.getVersion());

        if (entity.getGroup() != null) {
            dto.setGroupId(entity.getGroup().getId());
            if (entity.getGroup().getGroupLeader() != null) {
                dto.setGroupLeaderVisa(entity.getGroup().getGroupLeader().getVisa());
            }
        }

        if (entity.getMembers() != null) {
            dto.setMemberVisas(entity.getMembers().stream()
                    .map(Employee::getVisa)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}
