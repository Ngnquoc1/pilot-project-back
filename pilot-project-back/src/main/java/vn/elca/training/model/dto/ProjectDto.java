package vn.elca.training.model.dto;

import vn.elca.training.model.dto.response.ProjectResponseDto;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Project.
 * Maintained for backward compatibility with existing tests and services,
 * extends ProjectResponseDto.
 *
 * @author gtn
 */
public class ProjectDto extends ProjectResponseDto {

    public ProjectDto() {
        super();
    }

    /**
     * Convenience helper to get member visas derived from members collection.
     *
     * @return set of member visas
     */
    public Set<String> getMemberVisas() {
        if (getMembers() == null) {
            return Collections.emptySet();
        }
        return getMembers().stream().map(EmployeeDto::getVisa).collect(Collectors.toSet());
    }
}
