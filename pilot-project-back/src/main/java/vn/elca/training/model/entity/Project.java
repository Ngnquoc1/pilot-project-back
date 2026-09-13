package vn.elca.training.model.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vlp
 */
@Entity
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private LocalDate finishingDate;

    @Column
    private String customer;

    @Column(columnDefinition = "boolean default true")
    private boolean activated = true;

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY)
    private Set<Task> tasks = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToMany
    @JoinTable(
            name="PROJECT_USER",
            joinColumns=@JoinColumn(name="project_id"),
            inverseJoinColumns =@JoinColumn(name="user_id")
    )
    private Set<User> projectMembers = new HashSet<User>();

    public Project() {
    }

    public Project(String name, LocalDate finishingDate) {
        this.name = name;
        this.finishingDate = finishingDate;
    }

    public Project(String name, LocalDate finishingDate, String customer) {
        this.name = name;
        this.finishingDate = finishingDate;
        this.customer = customer;
    }

    public Project(String name, LocalDate finishingDate, String customer, Group group) {
        this.name = name;
        this.finishingDate = finishingDate;
        this.customer = customer;
        this.group = group;
    }

    public Project(Long id, String name, LocalDate finishingDate) {
        this.id = id;
        this.name = name;
        this.finishingDate = finishingDate;
    }

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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Set<User> getProjectMembers() {
        return projectMembers;
    }

    public void setProjectMembers(Set<User> projectMembers) {
        this.projectMembers = projectMembers;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
}