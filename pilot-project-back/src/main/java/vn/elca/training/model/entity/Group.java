package vn.elca.training.model.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PROJECT_GROUP")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "GROUP_LEADER_ID")
    private User groupLeader;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Project> projects = new java.util.ArrayList<>();

    public Group() {
    }

    public Group(User groupLeader) {
        this.groupLeader = groupLeader;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(User groupLeader) {
        this.groupLeader = groupLeader;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }
}
