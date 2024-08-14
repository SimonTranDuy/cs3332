package cs3332.project.cs3332.controller;

import cs3332.project.cs3332.model.ClassEnrollment;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.ClassEnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.HttpStatus;
import java.util.List;

@RestController
@RequestMapping("/api/class-enrollment")
public class ClassEnrollmentController {

    @Autowired
    private ClassEnrollmentService classEnrollmentService;

    // Thêm lớp vào cart
    @PostMapping("/cart/add")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> addToCart(@RequestParam String classCode) {
        try {
            String username = getCurrentUsername();
            ClassEnrollment enrollment = classEnrollmentService.addToCart(username, classCode);
            return ResponseEntity.ok(new ResponseObject("success", "Class added to cart", enrollment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Xóa lớp khỏi cart
    @DeleteMapping("/cart/remove")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> removeFromCart(@RequestParam String classCode) {
        try {
            String username = getCurrentUsername();
            classEnrollmentService.removeFromCart(username, classCode);
            return ResponseEntity.ok(new ResponseObject("success", "Class removed from cart", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Đăng kí lớp từ cart
    @PostMapping("/register")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> registerClass(@RequestParam String classCode) {
        try {
            String username = getCurrentUsername();
            ClassEnrollment enrollment = classEnrollmentService.registerClass(username, classCode);
            return ResponseEntity.ok(new ResponseObject("success", "Class registered successfully", enrollment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    @PostMapping("/cart/register-all")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> registerAllClassesInCart() {
        try {
            String username = getCurrentUsername();
            List<ClassEnrollment> enrollments = classEnrollmentService.registerAllClassesInCart(username);
            return ResponseEntity.ok(new ResponseObject("success", "All classes registered successfully", enrollments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Hủy đăng ký lớp học (Drop Class)
    @DeleteMapping("/drop")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> dropClass(@RequestParam String classCode) {
        try {
            String username = getCurrentUsername();
            classEnrollmentService.dropClass(username, classCode);
            return ResponseEntity.ok(new ResponseObject("success", "Class dropped successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Xem lịch sử đã đăng kí
    @GetMapping("/history")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> viewEnrollmentHistory() {
        String username = getCurrentUsername();
        List<ClassEnrollment> enrollments = classEnrollmentService.viewEnrollmentHistory(username);
        return ResponseEntity
                .ok(new ResponseObject("success", "Enrollment history retrieved successfully", enrollments));
    }

    // Xem giỏ hàng
    @GetMapping("/cart")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> viewCart() {
        try {
            String username = getCurrentUsername();
            List<ClassEnrollment> cart = classEnrollmentService.viewCart(username);
            return ResponseEntity.ok(new ResponseObject("success", "Cart retrieved successfully", cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Lấy username của user hiện tại từ SecurityContext
    private String getCurrentUsername() {
        return ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
