package cs3332.project.cs3332.service;

import cs3332.project.cs3332.model.*;
import cs3332.project.cs3332.repository.ClassEnrollmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cs3332.project.cs3332.repository.ClassRepository;
import cs3332.project.cs3332.repository.StudentRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import cs3332.project.cs3332.model.Class;

@Service
public class ClassEnrollmentService {

    @Autowired
    private ClassEnrollmentRepository classEnrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    // Thêm lớp vào cart
    public ClassEnrollment addToCart(String studentUsername, String classCode) throws Exception {
        removePendingClassesAfterStartDate();
    
        Student student = studentRepository.findByUsername(studentUsername);
        Class enrolledClass = classRepository.findByClassCode(classCode)
                .orElseThrow(() -> new Exception("Class not found"));
    
        // Kiểm tra nếu ngày hiện tại đã vượt quá registrationDeadline
        if (LocalDate.now().isAfter(enrolledClass.getRegistrationDeadline())) {
            throw new Exception("You cannot add this class to the cart after the registration deadline.");
        }
    
        // Kiểm tra nếu lớp học đã đạt maxStudents
        if (enrolledClass.getCurrentStudentCount() >= enrolledClass.getMaxStudents()) {
            throw new Exception("This class has reached its maximum capacity and cannot be added to the cart.");
        }
    
        // Kiểm tra nếu lớp học đã có trong giỏ hàng (trạng thái PENDING hoặc REGISTERED)
        boolean alreadyAdded = classEnrollmentRepository.findByStudent(student).stream()
                .anyMatch(enrollment -> enrollment.getEnrolledClass().getClassCode().equals(classCode) &&
                        (enrollment.getStatus().equals("PENDING") || enrollment.getStatus().equals("REGISTERED")));
    
        if (alreadyAdded) {
            throw new Exception("This class is already in your cart or has been registered.");
        }
    
        // Kiểm tra nếu số tín chỉ hiện tại của sinh viên cộng với số tín chỉ của lớp mới vượt quá maxCredits
        int currentCredits = classEnrollmentRepository.findByStudentAndStatus(student, "REGISTERED").stream()
                .mapToInt(enrollment -> enrollment.getEnrolledClass().getCourse().getCredits())
                .sum();
    
        int newClassCredits = enrolledClass.getCourse().getCredits();
    
        if (currentCredits + newClassCredits > student.getMaxCredits()) {
            throw new Exception("You cannot register for this class. Maximum credits exceeded.");
        }
    
        ClassEnrollment enrollment = new ClassEnrollment();
        enrollment.setStudent(student);
        enrollment.setEnrolledClass(enrolledClass);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus("PENDING");
    
        return classEnrollmentRepository.save(enrollment);
    }
    

    // Xóa lớp khỏi cart
    public void removeFromCart(String studentUsername, String classCode) throws Exception {
        Student student = studentRepository.findByUsername(studentUsername);
    
        ClassEnrollment enrollment = classEnrollmentRepository.findByStudentAndStatus(student, "PENDING").stream()
                .filter(e -> e.getEnrolledClass().getClassCode().equals(classCode))
                .findFirst()
                .orElseThrow(() -> new Exception("Class not found in cart."));
    
        // Kiểm tra nếu ngày hiện tại đã vượt quá registrationDeadline
        if (LocalDate.now().isAfter(enrollment.getEnrolledClass().getRegistrationDeadline())) {
            throw new Exception("You cannot remove this class from the cart after the registration deadline.");
        }
    
        classEnrollmentRepository.delete(enrollment);
    }

    // Đăng kí lớp từ cart
    public ClassEnrollment registerClass(String studentUsername, String classCode) throws Exception {
        removePendingClassesAfterStartDate();

        Student student = studentRepository.findByUsername(studentUsername);
    
        ClassEnrollment enrollment = classEnrollmentRepository.findByStudentAndStatus(student, "PENDING").stream()
                .filter(e -> e.getEnrolledClass().getClassCode().equals(classCode))
                .findFirst()
                .orElseThrow(() -> new Exception("Class not found in cart"));
    
        // Kiểm tra nếu ngày hiện tại đã vượt quá registrationDeadline
        if (LocalDate.now().isAfter(enrollment.getEnrolledClass().getRegistrationDeadline())) {
            throw new Exception("You cannot register for this class after the registration deadline.");
        }
    
        // Kiểm tra nếu lớp học đã đạt maxStudents
        Class enrolledClass = enrollment.getEnrolledClass();
        if (enrolledClass.getCurrentStudentCount() >= enrolledClass.getMaxStudents()) {
            throw new Exception("This class has reached its maximum capacity and cannot be registered.");
        }
    
        // Cập nhật trạng thái của enrollment thành REGISTERED
        enrollment.setStatus("REGISTERED");
        ClassEnrollment savedEnrollment = classEnrollmentRepository.save(enrollment);
    
        // Tăng currentStudentCount của lớp học lên 1
        int currentCount = enrolledClass.getCurrentStudentCount();
        enrolledClass.setCurrentStudentCount(currentCount + 1);
    
        // Lưu lại thông tin của lớp học
        classRepository.save(enrolledClass);
    
        return savedEnrollment;
    }
    
    

    // Hủy đăng kí lớp
    public void dropClass(String studentUsername, String classCode) throws Exception {
        Student student = studentRepository.findByUsername(studentUsername);
    
        // Tìm kiếm lớp học đã đăng ký (trạng thái REGISTERED)
        ClassEnrollment enrollment = classEnrollmentRepository.findByStudentAndStatus(student, "REGISTERED").stream()
            .filter(e -> e.getEnrolledClass().getClassCode().equals(classCode))
            .findFirst()
            .orElseThrow(() -> new Exception("You are not registered for this class."));
    
        // Kiểm tra nếu ngày hiện tại đã vượt qua registrationDeadline
        if (LocalDate.now().isAfter(enrollment.getEnrolledClass().getRegistrationDeadline())) {
            throw new Exception("You cannot drop this class after the registration deadline.");
        }
    
        // Giảm currentStudentCount của lớp học đi 1
        Class enrolledClass = enrollment.getEnrolledClass();
        int currentCount = enrolledClass.getCurrentStudentCount();
        
        // Đảm bảo currentStudentCount không âm
        if (currentCount > 0) {
            enrolledClass.setCurrentStudentCount(currentCount - 1);
            // Lưu lại thông tin của lớp học
            classRepository.save(enrolledClass);
        }
    
        // Hủy đăng ký lớp bằng cách xóa enrollment
        classEnrollmentRepository.delete(enrollment);
    }
    

    // Xem lịch sử đã đăng kí
    public List<ClassEnrollment> viewEnrollmentHistory(String studentUsername) {
        Student student = studentRepository.findByUsername(studentUsername);
        return classEnrollmentRepository.findByStudentAndStatus(student, "PENDING");
    }

    public List<ClassEnrollment> registerAllClassesInCart(String studentUsername) throws Exception {
        removePendingClassesAfterStartDate();
        
        Student student = studentRepository.findByUsername(studentUsername);
    
        // Lấy danh sách các lớp học trong giỏ hàng (có trạng thái PENDING)
        List<ClassEnrollment> pendingEnrollments = classEnrollmentRepository.findByStudentAndStatus(student, "PENDING");
    
        int currentCredits = classEnrollmentRepository.findByStudentAndStatus(student, "REGISTERED").stream()
                .mapToInt(enrollment -> enrollment.getEnrolledClass().getCourse().getCredits())
                .sum();
    
        List<ClassEnrollment> registeredEnrollments = new ArrayList<>();
        List<String> errors = new ArrayList<>();
    
        for (ClassEnrollment enrollment : pendingEnrollments) {
            Class enrolledClass = enrollment.getEnrolledClass();
            int newClassCredits = enrolledClass.getCourse().getCredits();
    
            // Kiểm tra nếu lớp học đã đạt maxStudents
            if (enrolledClass.getCurrentStudentCount() >= enrolledClass.getMaxStudents()) {
                errors.add("Cannot register for class " + enrolledClass.getClassCode() + ": Maximum capacity reached.");
                continue; // Bỏ qua lớp này và tiếp tục với lớp khác
            }
    
            // Kiểm tra nếu số tín chỉ vượt quá maxCredits
            if (currentCredits + newClassCredits > student.getMaxCredits()) {
                errors.add("Cannot register for class " + enrolledClass.getClassCode() + ": Maximum credits exceeded.");
                continue; // Bỏ qua lớp này và tiếp tục với lớp khác
            }
    
            // Kiểm tra nếu ngày đăng ký đã vượt quá registrationDeadline
            if (LocalDate.now().isAfter(enrollment.getEnrolledClass().getRegistrationDeadline())) {
                errors.add("Cannot register for class " + enrolledClass.getClassCode()
                        + ": Registration deadline has passed.");
                continue; // Bỏ qua lớp này và tiếp tục với lớp khác
            }
    
            // Nếu mọi thứ đều hợp lệ, đăng ký lớp
            enrollment.setStatus("REGISTERED");
            registeredEnrollments.add(classEnrollmentRepository.save(enrollment));
    
            // Tăng currentStudentCount của lớp học lên 1
            int currentCount = enrolledClass.getCurrentStudentCount();
            enrolledClass.setCurrentStudentCount(currentCount + 1);
            classRepository.save(enrolledClass); // Lưu lại thông tin của lớp học
    
            currentCredits += newClassCredits; // Cập nhật số tín chỉ hiện tại sau khi đăng ký thành công
        }
    
        // Nếu có lỗi, ném ra ngoại lệ chứa danh sách các lỗi
        if (!errors.isEmpty()) {
            throw new Exception(String.join(", ", errors));
        }
    
        return registeredEnrollments;
    }    

    public List<ClassEnrollment> viewCart(String studentUsername) {
        removePendingClassesAfterStartDate();

        Student student = studentRepository.findByUsername(studentUsername);
        return classEnrollmentRepository.findByStudentAndStatus(student, "PENDING");
    }

    public void removePendingClassesAfterStartDate() {
        List<ClassEnrollment> pendingEnrollments = classEnrollmentRepository.findAll().stream()
                .filter(enrollment -> enrollment.getStatus().equals("PENDING") &&
                        enrollment.getEnrolledClass().getStartDate().isBefore(LocalDate.now()))
                .collect(Collectors.toList());

        classEnrollmentRepository.deleteAll(pendingEnrollments);
    }
}
