package cs3332.project.cs3332.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {
    @Id
    @Column(name = "course_code", unique = true, nullable = false)
    private String courseCode;

    @Column(name = "name")
    private String name;

    @Column(name = "duration")
    private String duration;

    @Column(name = "credits")
    private int credits;

    @Column(name = "tuition_fee_credits")
    private double tuitionFeeCredits;

    @Column(name = "weight")
    private double weight;

    // Getters and setters

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    
    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public double getTuitionFeeCredits() {
        return tuitionFeeCredits;
    }

    public void setTuitionFeeCredits(double tuitionFeeCredits) {
        this.tuitionFeeCredits = tuitionFeeCredits;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Override toString method
    @Override
    public String toString() {
        return "Course{" +
                "courseCode='" + courseCode + '\'' +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", credits=" + credits +
                ", tuitionFeeCredits=" + tuitionFeeCredits +
                ", weight=" + weight +
                '}';
    }

    public Course() {
    }

    public Course(String courseCode, String name, String duration, int credits, double tuitionFeeCredits, double weight) {
        this.courseCode = courseCode;
        this.name = name;
        this.duration = duration;
        this.credits = credits;
        this.tuitionFeeCredits = tuitionFeeCredits;
        this.weight = weight;
    }



}
