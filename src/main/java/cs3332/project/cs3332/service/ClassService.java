package cs3332.project.cs3332.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.repository.ClassRepository;
import cs3332.project.cs3332.repository.CourseRepository;
import jakarta.transaction.Transactional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Phương thức kiểm tra xem class có tồn tại hay không
    private boolean checkClassExists(String classCode) {
        return classRepository.existsByClassCode(classCode);
    }

    public Class getClassByClassCode(String classCode) {
        if (checkClassExists(classCode)) {
            return classRepository.findByClassCode(classCode);
        }
        return null;
    }

    public List<Class> showAllClasses() {
        return classRepository.findAll();
    }

    public List<Class> showAllClassesIsOpen() {
        return classRepository.findByIsOpenTrue();
    }

    @Transactional
    public Class createClass(Class newClass) {
        // Kiểm tra tính hợp lệ của lớp học
        validateClass(newClass);

        // Kiểm tra sự tồn tại của khóa học
        if (newClass.getCourse() != null) {
            Course course = courseRepository.findByCourseCode(newClass.getCourse().getCourseCode())
                    .orElseThrow(() -> new RuntimeException("Course with code " + newClass.getCourse().getCourseCode() + " does not exist"));
            newClass.setCourse(course);
        }

        // Kiểm tra sự tồn tại của lớp
        if (checkClassExists(newClass.getClassCode())) {
            throw new RuntimeException("Class with code " + newClass.getClassCode() + " already exists");
        }

        // Lưu lớp học mới vào cơ sở dữ liệu
        return classRepository.save(newClass);
    }

    private boolean validateClass(Class newClass) {
        if (newClass.getCourse() == null) {
            throw new IllegalArgumentException("Course is required.");
        }
        if (newClass.getClassCode() == null || newClass.getClassCode().isEmpty()) {
            throw new IllegalArgumentException("Class code is required.");
        }
        if (newClass.getStartDate() == null) {
            throw new IllegalArgumentException("Start date is required.");
        }
        if (newClass.getEndDate() == null || newClass.getEndDate().isBefore(newClass.getStartDate())) {
            throw new IllegalArgumentException("End date is required and must be after the start date.");
        }
        if (newClass.getClassDays() == null || newClass.getClassDays().isEmpty()) {
            throw new IllegalArgumentException("Class days are required.");
        }
        if (newClass.getCurrentStudentCount() <= 0) {
            throw new IllegalArgumentException("Current student count must be greater than 0.");
        }
        if (newClass.getMaxStudents() <= newClass.getCurrentStudentCount()) {
            throw new IllegalArgumentException("Max students must be greater or equal to current student count.");
        }

        // Kiểm tra nếu ngày bắt đầu và ngày kết thúc trong quá khứ, mở lớp học
        if (newClass.getStartDate().isBefore(LocalDate.now()) && newClass.getEndDate().isAfter(LocalDate.now())) {
            newClass.setIsOpen(true);
        } else {
            newClass.setIsOpen(false);
        }

        return true;
    }

    public Class openClass(String classCode, LocalDate endDate) {
        if (checkClassExists(classCode)) {
            Class classToOpen = getClassByClassCode(classCode);

            // Set the current date as the start date
            classToOpen.setStartDate(LocalDate.now());

            // Ensure the end date is in the future
            if (endDate.isAfter(LocalDate.now())) {
                classToOpen.setEndDate(endDate);
            } else {
                throw new IllegalArgumentException("End date must be in the future.");
            }

            classToOpen.setIsOpen(true);
            return classRepository.save(classToOpen);
        }
        return null;
    }

    public Class setMaxStudents(String classCode, int maxStudents) {
        if (checkClassExists(classCode)) {
            Class classToEdit = getClassByClassCode(classCode);
            if (maxStudents <= 0) {
                throw new IllegalArgumentException("Max students must be greater than 0.");
            }
            classToEdit.setMaxStudents(maxStudents);
            return classRepository.save(classToEdit);
        }
        return null;
    }

    public Class editClass(String classCode, Class updatedClass) {
        // Validate the updated class details
        if (!validateClass(updatedClass)) {
            throw new IllegalArgumentException("Invalid class details.");
        }
    
        // Check if the class exists
        if (!checkClassExists(classCode)) {
            throw new RuntimeException("Class with code " + classCode + " does not exist.");
        }
    
        // Fetch the class to edit
        Class classToEdit = getClassByClassCode(classCode);
    
        // Update class details
        classToEdit.setMaxStudents(updatedClass.getMaxStudents());
        classToEdit.setCurrentStudentCount(updatedClass.getCurrentStudentCount());
        classToEdit.setIsOpen(updatedClass.isOpen());
        classToEdit.setStartDate(updatedClass.getStartDate());
        classToEdit.setEndDate(updatedClass.getEndDate());
        classToEdit.setClassDays(updatedClass.getClassDays());
    
        // Save and return updated class
        return classRepository.save(classToEdit);
    }
    

    @Transactional
    public boolean deleteClass(String classCode) {
        if (checkClassExists(classCode)) {
            classRepository.deleteByClassCode(classCode);
            return true;
        }
        return false;
    }

}
