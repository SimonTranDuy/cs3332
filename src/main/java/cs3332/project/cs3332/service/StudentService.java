package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.Student;
import cs3332.project.cs3332.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Sinh viên xem thông tin của chính mình
    public Student viewMyInformation() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return studentRepository.findByUsername(username);
    }

    // Sinh viên thay đổi mật khẩu
    public void changePassword(String oldPassword, String newPassword) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        Student student = studentRepository.findByUsername(username);

        if (passwordEncoder.matches(oldPassword, student.getPassword())) {
            student.setPassword(passwordEncoder.encode(newPassword));
            studentRepository.save(student);
        } else {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    }

    public void deleteStudentByUsername(String username) {
        studentRepository.deleteByUsername(username);
    }

}
