package cs3332.project.cs3332.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.model.ResponseObject;
import cs3332.project.cs3332.service.ClassService;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    // http://localhost:8080/api/classes/show-all
    // Lấy danh sách tất cả các lớp học
    @GetMapping("/show-all")
    public ResponseEntity<ResponseObject> showAllClasses() {
        List<Class> classes = classService.showAllClasses();
        if (classes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "No classes found", null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "Classes found", classes));
    }

    // http://localhost:8080/api/classes/open
    // Lấy danh sách tất cả các lớp học đang mở
    @GetMapping("/open")
    public ResponseEntity<ResponseObject> showAllClassesIsOpen() {
        List<Class> openClasses = classService.showAllClassesIsOpen();
        if (openClasses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "No open classes found", null));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseObject("success", "Open classes found", openClasses));
    }

    // http://localhost:8080/api/classes/CS123
    // Tìm kiếm một lớp học theo classCode
    @GetMapping("/{classCode}")
    public ResponseEntity<ResponseObject> searchClass(@PathVariable String classCode) {
        Class foundClass = classService.getClassByClassCode(classCode);
        if (foundClass != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class found", foundClass));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("error", "Class not found", null));
    }

    // http://localhost:8080/api/classes/create
    // Tạo mới một lớp học
    @PostMapping("/create")
    public ResponseEntity<ResponseObject> createClass(@RequestBody Class newClass) {
        try {
            Class createdClass = classService.createClass(newClass);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("success", "Class created successfully", createdClass));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    new ResponseObject("error", e.getMessage(), null));
        } catch (Exception e) {
            // Log exception details
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ResponseObject("error", "An error occurred while creating the class", null));
        }
    }

    // http://localhost:8080/api/classes/open/CS123?endDate=2022-12-31
    // Mở lớp học
    @PutMapping("/open/{classCode}")
    public ResponseEntity<ResponseObject> openClass(
            @PathVariable String classCode,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        try {
            Class openedClass = classService.openClass(classCode, endDate);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class opened successfully", openedClass));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseObject("error", "Class not found", null));
        }
    }

    // http://localhost:8080/api/classes/close/CS123
    // Đóng lớp học
    // @PutMapping("/close/{classCode}")
    // public ResponseEntity<ResponseObject> closeClass(@PathVariable String classCode) {
    //     Class closedClass = classService.closeClass(classCode);
    //     if (closedClass != null) {
    //         return ResponseEntity.status(HttpStatus.OK)
    //             .body(new ResponseObject("success", "Class closed successfully", closedClass));
    //     }
    //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
    //         .body(new ResponseObject("error", "Class not found", null));
    // }
    // http://localhost:8080/api/classes/max-students/CS123?maxStudents=50
    // Đặt số lượng sinh viên tối đa cho lớp học
    @PutMapping("/max-students/{classCode}")
    public ResponseEntity<ResponseObject> setMaxStudents(
            @PathVariable String classCode,
            @RequestParam int maxStudents) {
        Class updatedClass = classService.setMaxStudents(classCode, maxStudents);
        if (updatedClass != null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Max students set successfully", updatedClass));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("error", "Class not found", null));
    }

    // http://localhost:8080/api/classes/edit/CS123
    // Chỉnh sửa thông tin lớp học
    @PutMapping("/edit/{classCode}")
    public ResponseEntity<ResponseObject> editClass(
            @PathVariable String classCode,
            @RequestBody Class updatedClass) {
        try {
            // Attempt to edit the class using the service layer
            Class editedClass = classService.editClass(classCode, updatedClass);

            if (editedClass != null) {
                // Return a success response if the class was found and edited
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseObject("success", "Class edited successfully", editedClass));
            } else {
                // Return a not found response if the class does not exist
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseObject("error", "Class with code " + classCode + " not found", null));
            }
        } catch (IllegalArgumentException e) {
            // Return a bad request response for validation errors
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseObject("error", e.getMessage(), null));
        } catch (RuntimeException e) {
            // Return a generic error response for other runtime exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject("error", "An unexpected error occurred: " + e.getMessage(), null));
        }
    }

    // http://localhost:8080/api/classes/delete/CS123
    // Xóa lớp học
    @DeleteMapping("/delete/{classCode}")
    public ResponseEntity<ResponseObject> deleteClass(@PathVariable String classCode) {
        if (classService.deleteClass(classCode)) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseObject("success", "Class deleted successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseObject("error", "Class not found", null));
    }
}
