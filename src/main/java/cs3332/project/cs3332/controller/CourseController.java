package cs3332.project.cs3332.controller;

import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    // Tạo khóa học mới
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> createCourse(@RequestBody Course newCourse) {
        Course createdCourse = courseService.createCourse(newCourse);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ResponseObject("success", "Course created successfully", createdCourse));
    }

    // Sửa khóa học theo courseCode
    @PutMapping("/update/{courseCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> updateCourse(@PathVariable String courseCode, @RequestBody Course updatedCourse) {
        Optional<Course> courseOptional = courseService.updateCourse(courseCode, updatedCourse);
        if (courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Course updated successfully", courseOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "Course not found", null));
        }
    }

    // Xóa khóa học theo courseCode
    @DeleteMapping("/delete/{courseCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ResponseObject> deleteCourse(@PathVariable String courseCode) {
        courseService.deleteCourse(courseCode);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "Course deleted successfully", null));
    }

    // Tìm khóa học theo courseCode
    @GetMapping("/id/{courseCode}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> searchCourseById(@PathVariable String courseCode) {
        Optional<Course> courseOptional = courseService.searchCourseById(courseCode);
        if (courseOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Course found", courseOptional.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "Course not found", null));
        }
    }

    // Tìm khóa học theo tên
    @PostMapping("/search")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> searchCoursesByName(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        List<Course> courses = courseService.searchCoursesByName(name);
        if (courses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "No courses found with the given name", null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "Courses found", courses));
    }

    // Hiển thị tất cả các khóa học
    @GetMapping("/all")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT')")
    public ResponseEntity<ResponseObject> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "All courses retrieved successfully", courses));
    }
}
