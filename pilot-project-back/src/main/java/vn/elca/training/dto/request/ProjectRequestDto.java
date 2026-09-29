package vn.elca.training.dto.request;

import vn.elca.training.entity.ProjectStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Request DTO for creating and updating a Project.
 * Contains only input fields needed from the client, avoiding redundant data.
 *
 * @author nnnq
 */
public class ProjectRequestDto {
    private Integer projectNumber;
    private String name;
    private String customer;
    private ProjectStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long groupId;
    private Set<String> memberVisas = new HashSet<>();
    private Long version;

    public ProjectRequestDto() {
    }

    public ProjectRequestDto(Integer projectNumber, String name, String customer, ProjectStatus status,
                             LocalDate startDate, LocalDate endDate, Long groupId,
                             Set<String> memberVisas, Long version) {
        this.projectNumber = projectNumber;
        this.name = name;
        this.customer = customer;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.groupId = groupId;
        this.memberVisas = memberVisas != null ? memberVisas : new HashSet<>();
        this.version = version;
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

    public Set<String> getMemberVisas() {
        return memberVisas;
    }

    public void setMemberVisas(Set<String> memberVisas) {
        this.memberVisas = memberVisas;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
