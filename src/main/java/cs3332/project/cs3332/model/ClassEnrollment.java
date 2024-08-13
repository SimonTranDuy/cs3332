package cs3332.project.cs3332.model;

import java.time.LocalDate;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "class_enrollment")
public class ClassEnrollment {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "class_code", referencedColumnName = "class_code", nullable = false)
    private Class enrolledClass;

    @Column(name = "day_enrollment")
    private LocalDate dayEnrollment;

    // them status dang String
    @Column(name = "state")
    private String state = "Not Registered"; // Default value is "Not Registered"

    public ClassEnrollment() {
    }

    public ClassEnrollment(Integer id, Student student, Class enrolledClass, LocalDate dayEnrollment, String state) {
        this.id = id;
        this.student = student;
        this.enrolledClass = enrolledClass;
        this.dayEnrollment = dayEnrollment;
        this.state = state;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Class getEnrolledClass() {
        return enrolledClass;
    }

    public void setEnrolledClass(Class enrolledClass) {
        this.enrolledClass = enrolledClass;
    }

    public LocalDate getDayEnrollment() {
        return dayEnrollment;
    }

    public void setDayEnrollment(LocalDate dayEnrollment) {
        this.dayEnrollment = dayEnrollment;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "ClassEnrollment [id=" + id + ", student=" + student + ", enrolledClass=" + enrolledClass
                + ", dayEnrollment=" + dayEnrollment + ", state=" + state + "]";
    }

}
