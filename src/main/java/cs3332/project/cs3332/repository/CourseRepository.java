package cs3332.project.cs3332.repository;

import cs3332.project.cs3332.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByNameContainingIgnoreCase(String name);
    Optional<Course> findByCourseCode(String courseCode);
}
