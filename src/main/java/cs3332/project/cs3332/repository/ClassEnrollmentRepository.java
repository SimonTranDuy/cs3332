package cs3332.project.cs3332.repository;

import cs3332.project.cs3332.model.ClassEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import cs3332.project.cs3332.model.Student;

import java.util.List;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Long> {
    List<ClassEnrollment> findByStudentAndStatus(Student student, String status);
    List<ClassEnrollment> findByStudent(Student student);
}
