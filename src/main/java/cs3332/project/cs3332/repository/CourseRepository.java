package cs3332.project.cs3332.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cs3332.project.cs3332.model.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

    // Tìm kiếm khóa học theo mã khóa học
    Optional<Course> findByCourseCode(String courseCode);

    // Kiểm tra sự tồn tại của khóa học theo mã khóa học
    boolean existsByCourseCode(String courseCode);

    // Xóa khóa học theo mã khóa học
    void deleteByCourseCode(String courseCode);
}
