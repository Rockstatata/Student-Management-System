package com.sarwad.sms.studentmanagementsystem.repository;

import com.sarwad.sms.studentmanagementsystem.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT t FROM Teacher t WHERE t.department.id = :departmentId")
    List<Teacher> findByDepartmentId(@Param("departmentId") Long departmentId);
}
