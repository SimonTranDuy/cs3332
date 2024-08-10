package cs3332.project.cs3332.controller;

import cs3332.project.cs3332.model.Admin;
import cs3332.project.cs3332.model.Student;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.AdminService;
import cs3332.project.cs3332.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdminService adminService;

    // Sinh viên xem thông tin của chính mình
    @GetMapping("/student/me")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> viewStudentInfo() {
        Student student = studentService.viewMyInformation();
        return ResponseEntity.ok(new ResponseObject("success", "Student information retrieved successfully", student));
    }

    // Sinh viên đổi mật khẩu
    @PutMapping("/student/change-password")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> changePassword(@RequestBody ChangePasswordRequest request) {
        studentService.changePassword(request.getOldPassword(), request.getNewPassword());
        return ResponseEntity.ok(new ResponseObject("success", "Password changed successfully", null));
    }

    // Admin xem thông tin của chính mình
    @GetMapping("/admin/me")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> viewAdminInfo() {
        Admin admin = adminService.viewMyInformation();
        return ResponseEntity.ok(new ResponseObject("success", "Admin information retrieved successfully", admin));
    }

    // Admin xem thông tin của tất cả sinh viên
    @GetMapping("/admin/students")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> viewAllStudents() {
        List<Student> students = adminService.viewAllStudents();
        return ResponseEntity.ok(new ResponseObject("success", "All students' information retrieved successfully", students));
    }
}

class ChangePasswordRequest {
    private String oldPassword;
    private String newPassword;

    // Getters and Setters
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
