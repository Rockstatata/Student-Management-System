package com.sarwad.sms.studentmanagementsystem.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Entity tests for Student
 * Tests getters, setters, builder, and equals/hashCode
 */
@DisplayName("Student Entity Tests")
class StudentTest {

    @Test
    @DisplayName("Should create student using builder")
    void builder_Success() {
        // Arrange & Act
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        Student student = Student.builder()
                .id(1L)
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.STUDENT)
                .department(department)
                .courses(new HashSet<>())
                .teachers(new HashSet<>())
                .build();

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getName()).isEqualTo("John Doe");
        assertThat(student.getRoll()).isEqualTo("CS001");
        assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(student.getPassword()).isEqualTo("password123");
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
        assertThat(student.getDepartment()).isEqualTo(department);
        assertThat(student.getCourses()).isEmpty();
        assertThat(student.getTeachers()).isEmpty();
    }

    @Test
    @DisplayName("Should use no-args constructor")
    void noArgsConstructor_Success() {
        // Act
        Student student = new Student();

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getId()).isNull();
        assertThat(student.getName()).isNull();
    }

    @Test
    @DisplayName("Should use all-args constructor")
    void allArgsConstructor_Success() {
        // Arrange
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        // Act
        Student student = new Student(
                1L,
                "John Doe",
                "CS001",
                "john.doe@example.com",
                "password123",
                Role.STUDENT,
                department,
                new HashSet<>(),
                new HashSet<>()
        );

        // Assert
        assertThat(student).isNotNull();
        assertThat(student.getId()).isEqualTo(1L);
        assertThat(student.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should get and set id")
    void getId_SetId() {
        // Arrange
        Student student = new Student();

        // Act
        student.setId(1L);

        // Assert
        assertThat(student.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get and set name")
    void getName_SetName() {
        // Arrange
        Student student = new Student();

        // Act
        student.setName("John Doe");

        // Assert
        assertThat(student.getName()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Should get and set roll")
    void getRoll_SetRoll() {
        // Arrange
        Student student = new Student();

        // Act
        student.setRoll("CS001");

        // Assert
        assertThat(student.getRoll()).isEqualTo("CS001");
    }

    @Test
    @DisplayName("Should get and set email")
    void getEmail_SetEmail() {
        // Arrange
        Student student = new Student();

        // Act
        student.setEmail("john.doe@example.com");

        // Assert
        assertThat(student.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should get and set password")
    void getPassword_SetPassword() {
        // Arrange
        Student student = new Student();

        // Act
        student.setPassword("password123");

        // Assert
        assertThat(student.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Should get and set role")
    void getRole_SetRole() {
        // Arrange
        Student student = new Student();

        // Act
        student.setRole(Role.STUDENT);

        // Assert
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("Should get and set department")
    void getDepartment_SetDepartment() {
        // Arrange
        Student student = new Student();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        // Act
        student.setDepartment(department);

        // Assert
        assertThat(student.getDepartment()).isEqualTo(department);
    }

    @Test
    @DisplayName("Should get and set courses")
    void getCourses_SetCourses() {
        // Arrange
        Student student = new Student();
        Course course = Course.builder()
                .id(1L)
                .name("Data Structures")
                .build();
        HashSet<Course> courses = new HashSet<>();
        courses.add(course);

        // Act
        student.setCourses(courses);

        // Assert
        assertThat(student.getCourses()).hasSize(1);
        assertThat(student.getCourses()).contains(course);
    }

    @Test
    @DisplayName("Should get and set teachers")
    void getTeachers_SetTeachers() {
        // Arrange
        Student student = new Student();
        Teacher teacher = Teacher.builder()
                .id(1L)
                .name("Prof. Smith")
                .build();
        HashSet<Teacher> teachers = new HashSet<>();
        teachers.add(teacher);

        // Act
        student.setTeachers(teachers);

        // Assert
        assertThat(student.getTeachers()).hasSize(1);
        assertThat(student.getTeachers()).contains(teacher);
    }

    @Test
    @DisplayName("Should have default role STUDENT when using builder")
    void builder_DefaultRole() {
        // Act
        Student student = Student.builder()
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .password("password123")
                .build();

        // Assert
        assertThat(student.getRole()).isEqualTo(Role.STUDENT);
    }

    @Test
    @DisplayName("Should initialize courses as empty set when using builder")
    void builder_DefaultCourses() {
        // Act
        Student student = Student.builder()
                .name("John Doe")
                .build();

        // Assert
        assertThat(student.getCourses()).isNotNull();
        assertThat(student.getCourses()).isEmpty();
    }

    @Test
    @DisplayName("Should initialize teachers as empty set when using builder")
    void builder_DefaultTeachers() {
        // Act
        Student student = Student.builder()
                .name("John Doe")
                .build();

        // Assert
        assertThat(student.getTeachers()).isNotNull();
        assertThat(student.getTeachers()).isEmpty();
    }
}
