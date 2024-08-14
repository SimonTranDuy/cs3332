package cs3332.project.cs3332.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

import java.time.Year;
import java.util.Set;
import jakarta.persistence.PrePersist;
import java.util.concurrent.atomic.AtomicInteger;

@Entity
@Table(name = "students")
public class Student extends User {

    @Column(name = "student_id", unique = true, nullable = false)
    private String studentId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "program", nullable = false)
    private String program;

    @Column(name = "max_credits", nullable = false)
    private int maxCredits;

    public Student() {
        super();
    }

    public Student(String username, String password, String name, String program, int maxCredits) {
        super(username, password, Set.of("ROLE_STUDENT"));
        this.name = name;
        this.program = program;
        this.maxCredits = maxCredits;
    }

    private static final AtomicInteger sequence = new AtomicInteger(100000);

    @PrePersist
    protected void onCreate() {
        int year = Year.now().getValue() % 100; // Get last two digits of the current year
        int seq = sequence.getAndIncrement(); // Generate sequence number starting from 100000
        this.studentId = String.format("%02d%06d", year, seq); // Format as YYXXXXXX
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getMaxCredits() {
        return maxCredits;
    }

    public void setMaxCredits(int maxCredits) {
        this.maxCredits = maxCredits;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", program='" + program + '\'' +
                ", maxCredits=" + maxCredits +
                ", username='" + getUsername() + '\'' +
                ", roles='" + getRoles() + '\'' +
                '}';
    }
}
