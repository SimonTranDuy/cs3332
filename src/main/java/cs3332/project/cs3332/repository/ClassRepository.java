package cs3332.project.cs3332.repository;

import cs3332.project.cs3332.model.Class;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {
    List<Class> findByCourseCourseCode(String courseCode); // Tìm các lớp theo courseCode
    Optional<Class> findByClassCodeAndCourseCourseCode(String classCode, String courseCode);
    Optional<Class> findByClassCode(String classCode);
}
