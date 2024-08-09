package cs3332.project.cs3332.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cs3332.project.cs3332.model.Student;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByUsername(String username);
}