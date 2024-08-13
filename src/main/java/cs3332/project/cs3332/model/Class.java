package cs3332.project.cs3332.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "registration_deadline")
    private LocalDate registrationDeadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek; // Ngày học trong tuần

    // Enum DayOfWeek
    public enum DayOfWeek {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY
    }

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

    public LocalDate getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDate registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Class() {
    }

    public Class(String classCode, Course course, int maxStudents, int currentStudentCount, LocalDate startDate,
            LocalDate endDate, LocalDate registrationDeadline, DayOfWeek dayOfWeek) {
        this.classCode = classCode;
        this.course = course;
        this.maxStudents = maxStudents;
        this.currentStudentCount = currentStudentCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registrationDeadline = registrationDeadline;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return "Class{" +
                "classCode='" + classCode + '\'' +
                ", course=" + course +
                ", maxStudents=" + maxStudents +
                ", currentStudentCount=" + currentStudentCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", registrationDeadline=" + registrationDeadline +
                ", dayOfWeek=" + dayOfWeek +
                '}';
    }
}
