package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.repository.CourseRepository;
import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.repository.ClassRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassRepository classRepository;

    // Tạo khóa học mới
    public Course createCourse(Course newCourse) {
        return courseRepository.save(newCourse);
    }

    // Sửa khóa học theo courseCode
    public Optional<Course> updateCourse(String courseCode, Course updatedCourse) {
        return courseRepository.findById(courseCode).map(existingCourse -> {
            existingCourse.setName(updatedCourse.getName());
            existingCourse.setDuration(updatedCourse.getDuration());
            existingCourse.setCredits(updatedCourse.getCredits());
            existingCourse.setTuitionFeeCredits(updatedCourse.getTuitionFeeCredits());
            existingCourse.setWeight(updatedCourse.getWeight());

            // Lưu khóa học sau khi cập nhật
            Course savedCourse = courseRepository.save(existingCourse);

            // Cập nhật thông tin liên quan trong lớp học
            List<Class> relatedClasses = classRepository.findByCourseCourseCode(courseCode);
            for (Class cls : relatedClasses) {
                cls.setCourse(savedCourse);
                classRepository.save(cls);
            }

            return savedCourse;
        });
    }

    // Xóa khóa học theo courseCode
    public void deleteCourse(String courseCode) {
        // Tìm các lớp học liên quan đến khóa học
        List<Class> relatedClasses = classRepository.findByCourseCourseCode(courseCode);

        // Xóa tất cả các lớp học liên quan
        classRepository.deleteAll(relatedClasses);

        // Xóa khóa học
        courseRepository.deleteById(courseCode);
    }

    // Tìm khóa học theo courseCode
    public Optional<Course> searchCourseById(String courseCode) {
        return courseRepository.findById(courseCode);
    }

    // Tìm kiếm các khóa học theo tên
    public List<Course> searchCoursesByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name.trim());
    }

    // Hiển thị tất cả các khóa học
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
