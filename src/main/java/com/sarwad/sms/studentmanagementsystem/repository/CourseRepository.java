package com.sarwad.sms.studentmanagementsystem.repository;

import com.sarwad.sms.studentmanagementsystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.department.id = :departmentId")
    List<Course> findByDepartmentId(@Param("departmentId") Long departmentId);

    boolean existsByNameAndDepartmentId(String name, Long departmentId);
}
