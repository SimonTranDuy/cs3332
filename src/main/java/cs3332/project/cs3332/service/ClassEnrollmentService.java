package cs3332.project.cs3332.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import cs3332.project.cs3332.model.ClassEnrollment;
import cs3332.project.cs3332.model.Student;
import cs3332.project.cs3332.components.CookieUtil;
import cs3332.project.cs3332.model.Class;
import cs3332.project.cs3332.repository.ClassEnrollmentRepository;
import cs3332.project.cs3332.repository.ClassRepository;
import cs3332.project.cs3332.repository.StudentRepository;

import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service
public class ClassEnrollmentService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private ClassEnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    // Lấy danh sách lớp theo courseCode
    public List<Class> getClassesByCourseCode(String courseCode) {
        return classRepository.findByCourseCourseCode(courseCode);
    }

    // Thêm lớp học vào cookies (Lưu trữ trạng thái)
    public void addClassToCookie(String classCode, String courseCode, HttpServletRequest request,
            HttpServletResponse response) {
        List<String> registrationList = CookieUtil.getRegistrationListFromCookie(request);
        String combinedCode = classCode + ":" + courseCode;
        String encodedValue = Base64.getEncoder().encodeToString(combinedCode.getBytes());

        if (!registrationList.contains(encodedValue)) {
            registrationList.add(encodedValue);

            // Create ClassEnrollment with "Pending" state
            ClassEnrollment enrollment = new ClassEnrollment();
            enrollment.setState("Pending"); // Set state to "Pending"
            enrollment.setEnrolledClass(
                    classRepository.findByClassCodeAndCourseCourseCode(classCode, courseCode)
                            .orElseThrow(() -> new RuntimeException("Class not found")));

            CookieUtil.setRegistrationListCookie(registrationList, response);
        }
    }

    // Xóa lớp học khỏi cookies và cập nhật trạng thái về "Not Registered"
    public void removeClassFromCookie(HttpServletRequest request, HttpServletResponse response, String classCode,
            String courseCode) {
        Integer studentId = (Integer) request.getSession().getAttribute("studentId");
        if (studentId == null) {
            throw new RuntimeException("Student ID not found in session.");
        }

        String combinedCode = classCode + ":" + courseCode;
        String encodedValue = Base64.getEncoder().encodeToString(combinedCode.getBytes());

        List<String> registrationList = CookieUtil.getRegistrationListFromCookie(request);
        if (registrationList.remove(encodedValue)) { // Kiểm tra và xóa nếu tồn tại
            // Cập nhật trạng thái về "Not Registered" trong cơ sở dữ liệu
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found"));

            Class enrolledClass = classRepository.findByClassCodeAndCourseCourseCode(classCode, courseCode)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            ClassEnrollment enrollment = enrollmentRepository.findByStudentAndEnrolledClass(student, enrolledClass)
                    .orElseThrow(() -> new RuntimeException("Enrollment not found"));

            enrollment.setState("Not Registered"); // Đặt lại trạng thái về "Not Registered"
            enrollmentRepository.save(enrollment);

            CookieUtil.setRegistrationListCookie(registrationList, response);
        } else {
            throw new RuntimeException("Class not found in registration list");
        }
    }

    // Đăng ký tất cả các lớp từ cookies
    public void enrollAllClassesFromCookie(HttpServletRequest request, HttpServletResponse response,
            String courseCode) {
        Integer studentId = (Integer) request.getSession().getAttribute("studentId");
        if (studentId == null) {
            throw new RuntimeException("Student ID not found in session.");
        }

        // Tìm kiếm sinh viên theo ID
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<String> registrationList = CookieUtil.getRegistrationListFromCookie(request);

        // Tính tổng số tín chỉ hiện tại mà sinh viên đã đăng ký
        int totalCurrentCredits = enrollmentRepository.findByStudent(student)
                .stream()
                .mapToInt(enrollment -> enrollment.getEnrolledClass().getCourse().getCredits())
                .sum();

        // Tính tổng số tín chỉ của các lớp học trong cookies
        int totalNewCredits = 0;
        for (String encodedClassCode : registrationList) {
            String decodedClassCode = new String(Base64.getDecoder().decode(encodedClassCode));
            String[] parts = decodedClassCode.split(":");
            String classCode = parts[0];
            String courseCodeFromCookie = parts[1];

            if (!courseCode.equals(courseCodeFromCookie)) {
                continue;
            }

            Class enrolledClass = classRepository.findByClassCodeAndCourseCourseCode(classCode, courseCode)
                    .orElseThrow(() -> new RuntimeException("Class not found"));
            totalNewCredits += enrolledClass.getCourse().getCredits();
        }

        // Kiểm tra nếu tổng số tín chỉ vượt quá giới hạn
        if (totalCurrentCredits + totalNewCredits > student.getMaxCredits()) {
            throw new RuntimeException("You have exceeded the maximum credit limit.");
        }

        // Nếu không vượt quá giới hạn, tiếp tục đăng ký
        for (String encodedClassCode : registrationList) {
            String decodedClassCode = new String(Base64.getDecoder().decode(encodedClassCode));
            String[] parts = decodedClassCode.split(":");
            String classCode = parts[0];
            String courseCodeFromCookie = parts[1];

            if (!courseCode.equals(courseCodeFromCookie)) {
                continue;
            }

            Class enrolledClass = classRepository.findByClassCodeAndCourseCourseCode(classCode, courseCode)
                    .orElseThrow(() -> new RuntimeException("Class not found"));

            ClassEnrollment enrollment = new ClassEnrollment();
            enrollment.setStudent(student); // Gán sinh viên vào enrollment
            enrollment.setEnrolledClass(enrolledClass);
            enrollment.setState("Enrollment Successfully"); // Cập nhật trạng thái
            enrollment.setDayEnrollment(LocalDate.now());

            enrollmentRepository.save(enrollment);
        }

        // Xóa cookies sau khi đăng ký
        CookieUtil.clearRegistrationListCookie(response);
    }

    // Xóa đăng kí
    public void dropClass(Integer studentId, String classCode, String courseCode) {
        Class enrolledClass = classRepository.findByClassCodeAndCourseCourseCode(classCode, courseCode)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        ClassEnrollment enrollment = enrollmentRepository.findByStudentAndEnrolledClass(
                studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found")),
                enrolledClass)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        enrollmentRepository.delete(enrollment); // Xóa đăng ký
    }

    // Lịch sử đăng kí
    public List<ClassEnrollment> getEnrollmentHistory(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        return enrollmentRepository.findByStudent(student);
    }
}