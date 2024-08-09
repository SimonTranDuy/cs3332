package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.Admin;
import cs3332.project.cs3332.model.Student;
import cs3332.project.cs3332.repository.AdminRepository;
import cs3332.project.cs3332.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Admin xem thông tin của chính mình
    public Admin viewMyInformation() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return adminRepository.findByUsername(username);
    }

    // Admin xem thông tin của tất cả sinh viên
    public List<Student> viewAllStudents() {
        return studentRepository.findAll();
    }
}
