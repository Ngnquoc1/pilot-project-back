package vn.elca.training.model.dto.response;

import vn.elca.training.model.dto.EmployeeDto;
import vn.elca.training.model.entity.ProjectStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Response DTO for Project details and listing.
 * Includes complete information including member details (full names) for UI display.
 * Note: Member visas are cleanly extracted directly from the members collection.
 *
 * @author nnnq
 */
public class ProjectResponseDto {
    private Long id;
    private Integer projectNumber;
    private String name;
    private String customer;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long groupId;
    private String groupLeaderVisa;
    private Set<EmployeeDto> members = new HashSet<>();
    private Long version;

    public ProjectResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getProjectNumber() {
        return projectNumber;
    }

    public void setProjectNumber(Integer projectNumber) {
        this.projectNumber = projectNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupLeaderVisa() {
        return groupLeaderVisa;
    }

    public void setGroupLeaderVisa(String groupLeaderVisa) {
        this.groupLeaderVisa = groupLeaderVisa;
    }

    public Set<EmployeeDto> getMembers() {
        return members;
    }

    public void setMembers(Set<EmployeeDto> members) {
        this.members = members;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
