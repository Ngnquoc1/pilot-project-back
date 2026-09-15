package vn.elca.training.model.dto;

import java.time.LocalDate;

/**
 * @author gtn
 *
 */
public class ProjectDto {
    private Long id;
    private String name;
    private LocalDate finishingDate;
    private String customer;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getFinishingDate() {
        return finishingDate;
    }

    public void setFinishingDate(LocalDate finishingDate) {
        this.finishingDate = finishingDate;
    }

    public String getCustomer() { return customer; }

    public void setCustomer(String customer) { this.customer = customer; }

    private boolean activated;

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}
