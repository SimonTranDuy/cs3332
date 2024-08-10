package cs3332.project.cs3332.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.CourseService;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // Get all courses
    @GetMapping("/show-all")
    public ResponseEntity<ResponseObject> showAllCourses() {
        try {
            List<Course> courses = courseService.showAllCourses();
            if (courses == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                        new ResponseObject("success", "No courses found", null));
            }
            return ResponseEntity.ok(new ResponseObject("success", "Courses found", courses));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    // Find a course by courseCode
    @GetMapping("/{courseCode}")
    public ResponseEntity<ResponseObject> searchCourse(@PathVariable String courseCode) {
        try {
            Course foundCourse = courseService.searchCourse(courseCode);
            if (foundCourse != null) {
                return ResponseEntity.ok(new ResponseObject("success", "Course found", foundCourse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("error", "Course not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    // Create a new course
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createCourse(@RequestBody Course newCourse) {
        try {
            Course createdCourse = courseService.createCourse(newCourse);
            if (createdCourse != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                        new ResponseObject("success", "Course created successfully", createdCourse));
            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                        new ResponseObject("error", "Course already exists", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    // Edit a course
    @PutMapping("/edit/{courseCode}")
    public ResponseEntity<ResponseObject> editCourse(@PathVariable String courseCode, @RequestBody Course updatedCourse) {
        try {
            Course editedCourse = courseService.editCourse(courseCode, updatedCourse);
            if (editedCourse != null) {
                return ResponseEntity.ok(new ResponseObject("success", "Course edited successfully", editedCourse));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("error", "Course not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    // Delete a course
    @DeleteMapping("/delete/{courseCode}")
    public ResponseEntity<ResponseObject> deleteCourse(@PathVariable String courseCode) {
        try {
            boolean isDeleted = courseService.deleteCourse(courseCode);
            if (isDeleted) {
                return ResponseEntity.ok(new ResponseObject("success", "Course deleted successfully", null));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("error", "Course not found", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }
}
