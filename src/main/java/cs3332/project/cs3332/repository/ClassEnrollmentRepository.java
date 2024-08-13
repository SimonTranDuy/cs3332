package cs3332.project.cs3332.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cs3332.project.cs3332.model.ClassEnrollment;
import cs3332.project.cs3332.model.Student;

@Repository
public interface ClassEnrollmentRepository extends JpaRepository<ClassEnrollment, Integer> {
    Optional<ClassEnrollment> findByStudentAndEnrolledClass(Student student,
            cs3332.project.cs3332.model.Class enrolledClass);

    // Phương thức tùy chỉnh để tìm tất cả các bản ghi ClassEnrollment của một sinh
    // viên
    List<ClassEnrollment> findByStudent(Student student);
}
