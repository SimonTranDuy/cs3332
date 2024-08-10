package cs3332.project.cs3332.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "classes")
public class Class {

    @Id
    @Column(name = "class_code", unique = true, nullable = false)
    private String classCode;

    @ManyToOne
    @JoinColumn(name = "course_code", referencedColumnName = "course_code", nullable = false)
    private Course course;

    @Column(name = "max_students")
    private int maxStudents;

    @Column(name = "current_student_count")
    private int currentStudentCount;

    @Column(name = "is_open")
    private boolean isOpen;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ElementCollection
    @CollectionTable(name = "class_days", joinColumns = @JoinColumn(name = "class_code"))
    @Column(name = "class_day")
    private List<String> classDays;

    // Getters and setters
    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(int maxStudents) {
        this.maxStudents = maxStudents;
    }

    public int getCurrentStudentCount() {
        return currentStudentCount;
    }

    public void setCurrentStudentCount(int currentStudentCount) {
        this.currentStudentCount = currentStudentCount;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public List<String> getClassDays() {
        return classDays;
    }

    public void setClassDays(List<String> classDays) {
        this.classDays = classDays;
    }

    public Class() {
    }

    public Class(String classCode, Course course, int maxStudents, int currentStudentCount, boolean isOpen, LocalDate startDate, LocalDate endDate, List<String> classDays) {
        this.classCode = classCode;
        this.course = course;
        this.maxStudents = maxStudents;
        this.currentStudentCount = currentStudentCount;
        this.isOpen = isOpen;
        this.startDate = startDate;
        this.endDate = endDate;
        this.classDays = classDays;
    }

    @Override
    public String toString() {
        return "Class{" +
                "classCode='" + classCode + '\'' +
                ", course=" + course +
                ", maxStudents=" + maxStudents +
                ", currentStudentCount=" + currentStudentCount +
                ", isOpen=" + isOpen +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", classDays=" + classDays +
                '}';
    }
}
