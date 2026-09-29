package vn.elca.training.dto.request;

import org.springframework.format.annotation.DateTimeFormat;
import vn.elca.training.entity.ProjectStatus;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class ProjectSearchCriteriaDto {
    private String keyword;
    private ProjectStatus status;

    private String groupLeaderVisa;

    private Set<String> memberVisas = new HashSet<>();

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDateTo;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDateFrom;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDateTo;

    public ProjectSearchCriteriaDto() {
    }

    public ProjectSearchCriteriaDto(String keyword, ProjectStatus status) {
        this.keyword=keyword;
        this.status=status;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public String getGroupLeaderVisa() {
        return groupLeaderVisa;
    }

    public void setGroupLeaderVisa(String groupLeaderVisa) {
        this.groupLeaderVisa = groupLeaderVisa;
    }

    public Set<String> getMemberVisas() {
        return memberVisas;
    }

    public void setMemberVisas(Set<String> memberVisas) {
        this.memberVisas = memberVisas;
    }

    public void setMemberVisa(String memberVisa) {
        if (memberVisa != null && !memberVisa.trim().isEmpty()) {
            if (this.memberVisas == null) {
                this.memberVisas = new HashSet<>();
            }
            String[] split = memberVisa.split(",");
            for (String s : split) {
                if (!s.trim().isEmpty()) {
                    this.memberVisas.add(s.trim());
                }
            }
        }
    }

    public LocalDate getStartDateFrom() {
        return startDateFrom;
    }

    public void setStartDateFrom(LocalDate startDateFrom) {
        this.startDateFrom = startDateFrom;
    }

    public LocalDate getStartDateTo() {
        return startDateTo;
    }

    public void setStartDateTo(LocalDate startDateTo) {
        this.startDateTo = startDateTo;
    }

    public LocalDate getEndDateFrom() {
        return endDateFrom;
    }

    public void setEndDateFrom(LocalDate endDateFrom) {
        this.endDateFrom = endDateFrom;
    }

    public LocalDate getEndDateTo() {
        return endDateTo;
    }

    public void setEndDateTo(LocalDate endDateTo) {
        this.endDateTo = endDateTo;
    }
}
