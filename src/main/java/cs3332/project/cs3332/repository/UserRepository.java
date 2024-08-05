package cs3332.project.cs3332.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cs3332.project.cs3332.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
