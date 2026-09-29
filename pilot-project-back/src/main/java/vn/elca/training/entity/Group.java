package vn.elca.training.entity;

import javax.persistence.*;

@Entity
@Table(name = "`GROUP`")
public class Group extends AbstractEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_LEADER_ID", nullable = false)
    private Employee groupLeader;

    public Group() {
    }

    public Group(Employee groupLeader) {
        this.groupLeader = groupLeader;
    }

    public Employee getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(Employee groupLeader) {
        this.groupLeader = groupLeader;
    }
}
