package vn.elca.training.dto;

public class EmployeeDto {
    private Long id;
    private String visa;
    private String firstName;
    private String lastName;

    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String visa, String firstName, String lastName) {
        this.id = id;
        this.visa = visa;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVisa() {
        return visa;
    }

    public void setVisa(String visa) {
        this.visa = visa;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "");
    }
}
