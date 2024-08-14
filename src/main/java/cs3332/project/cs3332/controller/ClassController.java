package cs3332.project.cs3332.controller;

import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    // Tạo lớp học mới
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createClass(@RequestParam String courseCode, @RequestBody Class newClass) {
        try {
            Class createdClass = classService.createClass(courseCode, newClass);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseObject("success", "Class created successfully", createdClass));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        }
    }

    // Sửa lớp học theo classCode và courseCode
    @PutMapping("/update/{classCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateClass(@PathVariable String classCode, @RequestBody Class updatedClass) {
        Optional<Class> updated = classService.updateClass(classCode, updatedClass);
        if (updated.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class updated successfully", updated.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Class not found for update", null));
        }
    }

    // Xóa lớp học theo classCode và courseCode
    @DeleteMapping("/delete/{classCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteClass(@PathVariable String classCode) {
        Optional<Class> foundClass = classService.searchClassInCourse(classCode);
        if (foundClass.isPresent()) {
            classService.deleteClass(classCode);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class deleted successfully", null));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Class not found for deletion", null));
        }
    }

    // Hiển thị các lớp theo courseCode
    @GetMapping("/by-course/{courseCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getClassesByCourseCode(@PathVariable String courseCode) {
        List<Class> classes = classService.getClassesByCourseCode(courseCode);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "Classes retrieved successfully", classes));
    }

    // Hiển thị tất cả các lớp
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllClasses() {
        List<Class> classes = classService.getAllClasses();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "All classes retrieved successfully", classes));
    }

    @GetMapping("/search/{classCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")  
    public ResponseEntity<ResponseObject> searchClass(@PathVariable String classCode) {
        Optional<Class> foundClass = classService.searchClassInCourse(classCode);
        if (foundClass.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class found", foundClass.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("failed", "Class not found", null));
        }
    }

}
