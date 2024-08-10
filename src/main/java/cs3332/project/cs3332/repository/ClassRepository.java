package cs3332.project.cs3332.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import cs3332.project.cs3332.model.Class;

@Repository
public interface ClassRepository extends JpaRepository<Class, String> {

    // Tìm tất cả các lớp dựa trên mã khóa học
    List<Class> findByCourse_CourseCode(String courseCode);

    // Tìm kiếm class dựa trên classCode
    Class findByClassCode(String classCode);

    // Kiểm tra sự tồn tại của class dựa trên classCode
    boolean existsByClassCode(String classCode);

    // Tìm tất cả các class có trạng thái mở (isOpen = true)
    List<Class> findByIsOpenTrue();

    // Xóa class dựa trên classCode
    void deleteByClassCode(String classCode);

    // Tìm tất cả các class liên quan đến courseCode
    // List<Class> findByCourseCode(String courseCode);

    // Xóa tất cả các class liên quan đến courseCode
    @Modifying
    @Query("DELETE FROM Class c WHERE c.course.courseCode = :courseCode")
    void deleteByCourseCode(@Param("courseCode") String courseCode);
}

