package vn.elca.training.model.dto;

public class GroupDto {
    private Long id;
    private Long groupLeaderId;
    private String groupLeaderVisa;
    private String groupLeaderName;

    public GroupDto() {
    }

    public GroupDto(Long id, Long groupLeaderId, String groupLeaderVisa, String groupLeaderName) {
        this.id = id;
        this.groupLeaderId = groupLeaderId;
        this.groupLeaderVisa = groupLeaderVisa;
        this.groupLeaderName = groupLeaderName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupLeaderId() {
        return groupLeaderId;
    }

    public void setGroupLeaderId(Long groupLeaderId) {
        this.groupLeaderId = groupLeaderId;
    }

    public String getGroupLeaderVisa() {
        return groupLeaderVisa;
    }

    public void setGroupLeaderVisa(String groupLeaderVisa) {
        this.groupLeaderVisa = groupLeaderVisa;
    }

    public String getGroupLeaderName() {
        return groupLeaderName;
    }

    public void setGroupLeaderName(String groupLeaderName) {
        this.groupLeaderName = groupLeaderName;
    }
}
