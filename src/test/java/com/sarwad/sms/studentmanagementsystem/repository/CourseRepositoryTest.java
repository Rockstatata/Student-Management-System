package com.sarwad.sms.studentmanagementsystem.repository;

import com.sarwad.sms.studentmanagementsystem.entity.Course;
import com.sarwad.sms.studentmanagementsystem.entity.Department;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository tests for CourseRepository using @SpringBootTest
 * Tests run with H2 in-memory database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayName("CourseRepository Tests")
class CourseRepositoryTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Course testCourse;

    @BeforeEach
    void setUp() {
        // Arrange - Create test data with unique names for each test
        String uniqueSuffix = "_" + System.nanoTime();
        testDepartment = Department.builder()
                .name("Computer Science" + uniqueSuffix)
                .description("CS Department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testCourse = Course.builder()
                .name("Data Structures")
                .description("Learn about data structures and algorithms")
                .credits(3)
                .department(testDepartment)
                .build();
    }

    @Test
    @DisplayName("Should save course successfully")
    void save_Success() {
        // Act
        Course savedCourse = courseRepository.save(testCourse);

        // Assert
        assertThat(savedCourse).isNotNull();
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("Data Structures");
        assertThat(savedCourse.getCredits()).isEqualTo(3);
        assertThat(savedCourse.getDepartment()).isEqualTo(testDepartment);
    }

    @Test
    @DisplayName("Should find course by ID successfully")
    void findById_Success() {
        // Arrange
        Course savedCourse = courseRepository.save(testCourse);

        // Act
        Optional<Course> foundCourse = courseRepository.findById(savedCourse.getId());

        // Assert
        assertThat(foundCourse).isPresent();
        assertThat(foundCourse.get().getName()).isEqualTo("Data Structures");
        assertThat(foundCourse.get().getDescription()).isEqualTo("Learn about data structures and algorithms");
    }

    @Test
    @DisplayName("Should return empty when course ID does not exist")
    void findById_NotFound() {
        // Act
        Optional<Course> foundCourse = courseRepository.findById(999L);

        // Assert
        assertThat(foundCourse).isEmpty();
    }

    @Test
    @DisplayName("Should find courses by department ID using custom query")
    void findByDepartmentId_Success() {
        // Arrange
        courseRepository.save(testCourse);

        Course anotherCourse = Course.builder()
                .name("Algorithms")
                .description("Advanced algorithms")
                .credits(4)
                .department(testDepartment)
                .build();
        courseRepository.save(anotherCourse);

        // Act
        List<Course> courses = courseRepository.findByDepartmentId(testDepartment.getId());

        // Assert
        assertThat(courses).hasSize(2);
        assertThat(courses).extracting(Course::getName)
                .containsExactlyInAnyOrder("Data Structures", "Algorithms");
    }

    @Test
    @DisplayName("Should return empty list when no courses in department")
    void findByDepartmentId_EmptyList() {
        // Arrange
        String uniqueSuffix = "_" + System.nanoTime();
        Department anotherDepartment = Department.builder()
                .name("Mathematics" + uniqueSuffix)
                .description("Math Department")
                .build();
        anotherDepartment = departmentRepository.save(anotherDepartment);

        // Act
        List<Course> courses = courseRepository.findByDepartmentId(anotherDepartment.getId());

        // Assert
        assertThat(courses).isEmpty();
    }

    @Test
    @DisplayName("Should check if course exists by name and department ID")
    void existsByNameAndDepartmentId_Success() {
        // Arrange
        courseRepository.save(testCourse);

        // Act
        boolean exists = courseRepository.existsByNameAndDepartmentId("Data Structures", testDepartment.getId());
        boolean notExists = courseRepository.existsByNameAndDepartmentId("NonExistent Course", testDepartment.getId());

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should return false when checking name in different department")
    void existsByNameAndDepartmentId_DifferentDepartment() {
        // Arrange
        courseRepository.save(testCourse);

        String uniqueSuffix = "_" + System.nanoTime();
        Department anotherDepartment = Department.builder()
                .name("Mathematics" + uniqueSuffix)
                .description("Math Department")
                .build();
        anotherDepartment = departmentRepository.save(anotherDepartment);

        // Act
        boolean exists = courseRepository.existsByNameAndDepartmentId("Data Structures", anotherDepartment.getId());

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should delete course successfully")
    void delete_Success() {
        // Arrange
        Course savedCourse = courseRepository.save(testCourse);
        Long courseId = savedCourse.getId();

        // Act
        courseRepository.deleteById(courseId);
        // flush();

        // Assert
        Optional<Course> deletedCourse = courseRepository.findById(courseId);
        assertThat(deletedCourse).isEmpty();
    }

    @Test
    @DisplayName("Should handle transactional behavior correctly")
    void transactional_Behavior() {
        // Arrange
        Course savedCourse = courseRepository.save(testCourse);

        // Act - Update course
        savedCourse.setName("Advanced Data Structures");
        savedCourse.setCredits(4);
        courseRepository.save(savedCourse);
        // flush();

        // Assert
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertThat(updatedCourse.getName()).isEqualTo("Advanced Data Structures");
        assertThat(updatedCourse.getCredits()).isEqualTo(4);
    }

    @Test
    @DisplayName("Should find all courses")
    void findAll_Success() {
        // Arrange
        courseRepository.save(testCourse);

        Course anotherCourse = Course.builder()
                .name("Algorithms")
                .description("Advanced algorithms")
                .credits(4)
                .department(testDepartment)
                .build();
        courseRepository.save(anotherCourse);

        // Act
        List<Course> courses = courseRepository.findAll();

        // Assert
        assertThat(courses).hasSizeGreaterThanOrEqualTo(2);
        assertThat(courses).extracting(Course::getName)
                .contains("Data Structures", "Algorithms");
    }
}
