package cs3332.project.cs3332.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.repository.ClassRepository;
import cs3332.project.cs3332.repository.CourseRepository;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    private boolean checkCourseExists(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }

    // Get a list of all courses
    public List<Course> showAllCourses() {
        return courseRepository.findAll();
    }

    // Find a course by its code
    public Course searchCourse(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
    }

    // Create a new course
    public Course createCourse(Course newCourse) {
        if (courseRepository.existsByCourseCode(newCourse.getCourseCode())) {
            throw new RuntimeException("Course with code " + newCourse.getCourseCode() + " already exists");
        }
        return courseRepository.save(newCourse);
    }

    // Edit an existing course
    public Course editCourse(String courseCode, Course updatedCourse) {
        Course existingCourse = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found with code: " + courseCode));
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDuration(updatedCourse.getDuration());
        existingCourse.setCredits(updatedCourse.getCredits());
        existingCourse.setTuitionFeeCredits(updatedCourse.getTuitionFeeCredits());
        existingCourse.setWeight(updatedCourse.getWeight());
        return courseRepository.save(existingCourse);
    }

    // Delete a course
    @Transactional
    public boolean deleteCourse(String courseCode) {
        if (checkCourseExists(courseCode)) {
            // Xóa tất cả các lớp liên quan đến khóa học trước khi xóa khóa học
            classRepository.deleteByCourseCode(courseCode);

            // Sau đó, xóa khóa học
            courseRepository.deleteByCourseCode(courseCode);

            return true;
        }
        return false;
    }

}
