package vn.elca.training.model.dto;

import vn.elca.training.model.dto.response.ProjectResponseDto;

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
}
