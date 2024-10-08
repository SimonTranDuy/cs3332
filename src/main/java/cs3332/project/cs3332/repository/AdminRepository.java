package cs3332.project.cs3332.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cs3332.project.cs3332.model.Admin;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByUsername(String username);
    void deleteByUsername(String username);
}