package cs3332.project.cs3332.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import cs3332.project.cs3332.components.CookieUtil;
import cs3332.project.cs3332.model.ClassEnrollment;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.ClassEnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@RequestMapping("/api/enrollment")
public class ClassEnrollmentController {

    @Autowired
    private ClassEnrollmentService enrollmentService;

    // API để lấy danh sách lớp học theo courseCode
    @GetMapping("/classes")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getClassesByCourseCode(
            @RequestParam String courseCode) {
        List<cs3332.project.cs3332.model.Class> classes = enrollmentService.getClassesByCourseCode(courseCode);
        return ResponseEntity.ok(new ResponseObject("success", "Classes retrieved successfully", classes));
    }

    // Đưa vào cart
    @PostMapping("/add-to-cart")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> addToCart(@RequestParam String classCode, @RequestParam String courseCode,
            HttpServletRequest request,
            HttpServletResponse response) {
        enrollmentService.addClassToCookie(classCode, courseCode, request, response);
        return ResponseEntity.ok(new ResponseObject("success", "Class added to cart.", null));
    }

    // Xóa khỏi cart
    @PostMapping("/remove-class")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> removeClass(@RequestParam String classCode, @RequestParam String courseCode,
            HttpServletRequest request, HttpServletResponse response) {
        enrollmentService.removeClassFromCookie(request, response, classCode, courseCode);
        return ResponseEntity.ok(new ResponseObject("success", "Class removed from cart.", null));
    }

    // API để xem danh sách lớp học trong cookie
    @GetMapping("/cart")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> viewCart(HttpServletRequest request) {
        List<String> cart = CookieUtil.getRegistrationListFromCookie(request);
        return ResponseEntity.ok(new ResponseObject("success", "Cart retrieved successfully", cart));
    }

    // Đưa tất cả enroll-classes vào database
    @PostMapping("/enroll-classes")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> enrollClasses(@RequestParam Integer studentId,
            @RequestParam String courseCode,
            HttpServletRequest request, HttpServletResponse response) {
        enrollmentService.enrollAllClassesFromCookie(request, response, courseCode);
        return ResponseEntity.ok(new ResponseObject("success", "Classes enrolled successfully.", null));
    }

    // API để xóa lớp học
    @PostMapping("/drop-class")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> dropClass(@RequestParam Integer studentId, @RequestParam String classCode,
            @RequestParam String courseCode) {
        enrollmentService.dropClass(studentId, classCode, courseCode);
        return ResponseEntity.ok(new ResponseObject("success", "Class dropped successfully.", null));
    }

    // Lịch sử đăng kí
    @GetMapping("/enrollment-history")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getEnrollmentHistory(@RequestParam Integer studentId) {
        List<ClassEnrollment> enrollmentHistory = enrollmentService.getEnrollmentHistory(studentId);
        return ResponseEntity
                .ok(new ResponseObject("success", "Enrollment history retrieved successfully", enrollmentHistory));
    }
}
