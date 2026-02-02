package com.sarwad.sms.studentmanagementsystem.config;

import com.sarwad.sms.studentmanagementsystem.entity.*;
import com.sarwad.sms.studentmanagementsystem.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize if no data exists
        if (departmentRepository.count() > 0) {
            log.info("Data already exists, skipping initialization");
            return;
        }

        log.info("Initializing sample data...");

        // Create Departments
        Department csDept = Department.builder()
                .name("Computer Science")
                .description("Department of Computer Science and Engineering")
                .build();
        csDept = departmentRepository.save(csDept);

        Department eeDept = Department.builder()
                .name("Electrical Engineering")
                .description("Department of Electrical and Electronics Engineering")
                .build();
        eeDept = departmentRepository.save(eeDept);

        Department meDept = Department.builder()
                .name("Mechanical Engineering")
                .description("Department of Mechanical Engineering")
                .build();
        meDept = departmentRepository.save(meDept);

        // Create Teachers
        Teacher teacher1 = Teacher.builder()
                .name("Dr. John Smith")
                .email("teacher@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.TEACHER)
                .department(csDept)
                .build();
        teacher1 = teacherRepository.save(teacher1);

        Teacher teacher2 = Teacher.builder()
                .name("Dr. Sarah Johnson")
                .email("sarah@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.TEACHER)
                .department(eeDept)
                .build();
        teacherRepository.save(teacher2);

        Teacher teacher3 = Teacher.builder()
                .name("Prof. Michael Brown")
                .email("michael@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.TEACHER)
                .department(meDept)
                .build();
        teacherRepository.save(teacher3);

        // Create Courses for CS Department
        Course course1 = Course.builder()
                .name("Data Structures")
                .description("Introduction to data structures and algorithms")
                .credits(4)
                .department(csDept)
                .build();
        course1 = courseRepository.save(course1);

        Course course2 = Course.builder()
                .name("Database Systems")
                .description("Fundamentals of database management systems")
                .credits(3)
                .department(csDept)
                .build();
        course2 = courseRepository.save(course2);

        Course course3 = Course.builder()
                .name("Web Development")
                .description("Modern web development technologies")
                .credits(3)
                .department(csDept)
                .build();
        course3 = courseRepository.save(course3);

        // Create Courses for EE Department
        Course course4 = Course.builder()
                .name("Circuit Analysis")
                .description("Fundamentals of electrical circuits")
                .credits(4)
                .department(eeDept)
                .build();
        courseRepository.save(course4);

        Course course5 = Course.builder()
                .name("Digital Electronics")
                .description("Digital logic and electronics")
                .credits(3)
                .department(eeDept)
                .build();
        courseRepository.save(course5);

        // Create Courses for ME Department
        Course course6 = Course.builder()
                .name("Thermodynamics")
                .description("Principles of thermodynamics")
                .credits(4)
                .department(meDept)
                .build();
        courseRepository.save(course6);

        // Create Students
        Student student1 = Student.builder()
                .name("Alice Williams")
                .roll("CS2024001")
                .email("student@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .department(csDept)
                .build();
        student1.getCourses().add(course1);
        student1.getCourses().add(course2);
        studentRepository.save(student1);

        Student student2 = Student.builder()
                .name("Bob Anderson")
                .roll("CS2024002")
                .email("bob@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .department(csDept)
                .build();
        student2.getCourses().add(course1);
        student2.getCourses().add(course3);
        studentRepository.save(student2);

        Student student3 = Student.builder()
                .name("Carol Davis")
                .roll("EE2024001")
                .email("carol@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .department(eeDept)
                .build();
        studentRepository.save(student3);

        Student student4 = Student.builder()
                .name("David Miller")
                .roll("ME2024001")
                .email("david@example.com")
                .password(passwordEncoder.encode("password123"))
                .role(Role.STUDENT)
                .department(meDept)
                .build();
        studentRepository.save(student4);

        log.info("Sample data initialization completed!");
        log.info("===========================================");
        log.info("Login Credentials:");
        log.info("Teacher: teacher@example.com / password123");
        log.info("Student: student@example.com / password123");
        log.info("===========================================");
    }
}
