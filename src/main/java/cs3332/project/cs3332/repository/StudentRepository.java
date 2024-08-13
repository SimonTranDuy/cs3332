package cs3332.project.cs3332.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cs3332.project.cs3332.model.Student;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    Student findByUsername(String username);

    void deleteByUsername(String username);

}