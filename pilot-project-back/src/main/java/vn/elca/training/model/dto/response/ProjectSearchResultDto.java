package vn.elca.training.model.dto.response;

import vn.elca.training.model.entity.ProjectStatus;

import java.time.LocalDate;

/**
 * Lightweight DTO representing a project item in search results.
 * Contains only the fields required for list/search display without lazy loading overhead.
 *
 * @author nnnq
 */
public class ProjectSearchResultDto {
    private Long id;
    private Integer projectNumber;
    private String name;
    private String customer;
    private ProjectStatus status;
    private LocalDate startDate;
    private Long version;

    public ProjectSearchResultDto() {
    }

    public ProjectSearchResultDto(Long id, Integer projectNumber, String name, String customer,
                                  ProjectStatus status, LocalDate startDate, Long version) {
        this.id = id;
        this.projectNumber = projectNumber;
        this.name = name;
        this.customer = customer;
        this.status = status;
        this.startDate = startDate;
        this.version = version;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
