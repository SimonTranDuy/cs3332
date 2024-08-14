package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.model.Course;
import cs3332.project.cs3332.repository.ClassRepository;
import cs3332.project.cs3332.repository.CourseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private CourseRepository courseRepository;

    // Tạo lớp học mới
    public Class createClass(String courseCode, Class newClass) throws Exception {
        // Tìm kiếm course theo courseCode
        Optional<Course> courseOpt = courseRepository.findById(courseCode);

        if (courseOpt.isPresent()) {
            // Nếu course tồn tại, gán course vào class và thực hiện validate ngày tháng
            Course course = courseOpt.get();
            newClass.setCourse(course);

            // Gọi hàm validate để kiểm tra ngày bắt đầu và kết thúc
            validateClassDates(newClass.getStartDate(), newClass.getEndDate());

            return classRepository.save(newClass);
        } else {
            // Nếu không tìm thấy course, ném ra ngoại lệ với thông báo phù hợp
            throw new Exception("Please create course first");
        }
    }

    // Tìm kiếm lớp học theo classCode trong một khóa học cụ thể
    public Optional<Class> searchClassInCourse(String classCode) {
        return classRepository.findByClassCode(classCode);
    }

    // Sửa lớp học theo classCode và courseCode
    @Transactional
    public Optional<Class> updateClass(String classCode, Class updatedClass) {
        return classRepository.findByClassCode(classCode)
                .map(existingClass -> {
                    // Lấy courseCode từ existingClass
                    String courseCode = existingClass.getCourse().getCourseCode();
    
                    // Tìm và gán đối tượng Course vào existingClass
                    Optional<Course> optionalCourse = courseRepository.findByCourseCode(courseCode);
                    if (!optionalCourse.isPresent()) {
                        throw new IllegalArgumentException("Course not found");
                    }
                    Course course = optionalCourse.get();
                    existingClass.setCourse(course);
    
                    // Gọi hàm validate để kiểm tra ngày bắt đầu và kết thúc
                    validateClassDates(updatedClass.getStartDate(), updatedClass.getEndDate());
    
                    // Cập nhật các thuộc tính khác của lớp học từ updatedClass vào existingClass
                    existingClass.setMaxStudents(updatedClass.getMaxStudents());
                    existingClass.setCurrentStudentCount(updatedClass.getCurrentStudentCount());
                    existingClass.setStartDate(updatedClass.getStartDate());
                    existingClass.setEndDate(updatedClass.getEndDate());
                    existingClass.setRegistrationDeadline(updatedClass.getRegistrationDeadline());
                    existingClass.setDayOfWeek(updatedClass.getDayOfWeek());
                    // Các thuộc tính khác nếu có
    
                    return classRepository.save(existingClass);
                });
    }
    
    // Xóa lớp học theo classCode và courseCode
    public void deleteClass(String classCode) {
        classRepository.findByClassCode(classCode).ifPresent(classRepository::delete);
    }

    // Hiển thị các lớp theo courseCode
    public List<Class> getClassesByCourseCode(String courseCode) {
        return classRepository.findByCourseCourseCode(courseCode);
    }

    // Hiển thị tất cả các lớp
    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    // Kiểm tra điều kiện ngày tháng
    private void validateClassDates(LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {
        LocalDate currentDate = LocalDate.now();

        // Kiểm tra ngày bắt đầu phải cách ngày tạo lớp ít nhất 2 tuần
        if (ChronoUnit.DAYS.between(currentDate, startDate) < 14) {
            throw new IllegalArgumentException("Start date must be at least 2 weeks after the current date.");
        }

        // Kiểm tra ngày kết thúc phải cách ngày bắt đầu ít nhất 3 tháng
        if (ChronoUnit.MONTHS.between(startDate, endDate) < 3) {
            throw new IllegalArgumentException("End date must be at least 3 months after the start date.");
        }
    }
}
