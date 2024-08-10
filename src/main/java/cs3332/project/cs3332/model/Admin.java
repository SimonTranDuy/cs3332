package cs3332.project.cs3332.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.Set;

@Entity
@Table(name = "admins")
public class Admin extends User {

    @Column(name = "admin_id", unique = true, nullable = false)
    private String adminId;

    @Column(name = "department", nullable = false)
    private String department;

    public Admin() {
        super();
    }

    public Admin(String username, String password, String adminId, String department) {
        super(username, password, Set.of("ROLE_ADMIN"));
        this.adminId = adminId;
        this.department = department;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", department='" + department + '\'' +
                ", username='" + getUsername() + '\'' +
                ", roles='" + getRoles() + '\'' +
                '}';
    }
}