package com.sarwad.sms.studentmanagementsystem.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Entity tests for Course
 * Tests getters, setters, and builder
 */
@DisplayName("Course Entity Tests")
class CourseTest {

    @Test
    @DisplayName("Should create course using builder")
    void builder_Success() {
        // Arrange
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        // Act
        Course course = Course.builder()
                .id(1L)
                .name("Data Structures")
                .description("Learn about data structures and algorithms")
                .credits(3)
                .department(department)
                .students(new HashSet<>())
                .build();

        // Assert
        assertThat(course).isNotNull();
        assertThat(course.getId()).isEqualTo(1L);
        assertThat(course.getName()).isEqualTo("Data Structures");
        assertThat(course.getDescription()).isEqualTo("Learn about data structures and algorithms");
        assertThat(course.getCredits()).isEqualTo(3);
        assertThat(course.getDepartment()).isEqualTo(department);
        assertThat(course.getStudents()).isEmpty();
    }

    @Test
    @DisplayName("Should use no-args constructor")
    void noArgsConstructor_Success() {
        // Act
        Course course = new Course();

        // Assert
        assertThat(course).isNotNull();
        assertThat(course.getId()).isNull();
        assertThat(course.getName()).isNull();
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
        Course course = new Course(
                1L,
                "Data Structures",
                "Learn about data structures and algorithms",
                3,
                department,
                new HashSet<>()
        );

        // Assert
        assertThat(course).isNotNull();
        assertThat(course.getId()).isEqualTo(1L);
        assertThat(course.getName()).isEqualTo("Data Structures");
    }

    @Test
    @DisplayName("Should get and set id")
    void getId_SetId() {
        // Arrange
        Course course = new Course();

        // Act
        course.setId(1L);

        // Assert
        assertThat(course.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should get and set name")
    void getName_SetName() {
        // Arrange
        Course course = new Course();

        // Act
        course.setName("Data Structures");

        // Assert
        assertThat(course.getName()).isEqualTo("Data Structures");
    }

    @Test
    @DisplayName("Should get and set description")
    void getDescription_SetDescription() {
        // Arrange
        Course course = new Course();

        // Act
        course.setDescription("Learn about data structures and algorithms");

        // Assert
        assertThat(course.getDescription()).isEqualTo("Learn about data structures and algorithms");
    }

    @Test
    @DisplayName("Should get and set credits")
    void getCredits_SetCredits() {
        // Arrange
        Course course = new Course();

        // Act
        course.setCredits(3);

        // Assert
        assertThat(course.getCredits()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should get and set department")
    void getDepartment_SetDepartment() {
        // Arrange
        Course course = new Course();
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        // Act
        course.setDepartment(department);

        // Assert
        assertThat(course.getDepartment()).isEqualTo(department);
    }

    @Test
    @DisplayName("Should get and set students")
    void getStudents_SetStudents() {
        // Arrange
        Course course = new Course();
        Student student = Student.builder()
                .id(1L)
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .build();
        HashSet<Student> students = new HashSet<>();
        students.add(student);

        // Act
        course.setStudents(students);

        // Assert
        assertThat(course.getStudents()).hasSize(1);
        assertThat(course.getStudents()).contains(student);
    }

    @Test
    @DisplayName("Should initialize students as empty set when using builder")
    void builder_DefaultStudents() {
        // Arrange
        Department department = Department.builder()
                .id(1L)
                .name("Computer Science")
                .build();

        // Act
        Course course = Course.builder()
                .name("Data Structures")
                .department(department)
                .build();

        // Assert
        assertThat(course.getStudents()).isNotNull();
        assertThat(course.getStudents()).isEmpty();
    }

    @Test
    @DisplayName("Should handle null description")
    void nullDescription() {
        // Act
        Course course = Course.builder()
                .name("Data Structures")
                .description(null)
                .build();

        // Assert
        assertThat(course.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should handle null credits")
    void nullCredits() {
        // Act
        Course course = Course.builder()
                .name("Data Structures")
                .credits(null)
                .build();

        // Assert
        assertThat(course.getCredits()).isNull();
    }
}
