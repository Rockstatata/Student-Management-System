package com.sarwad.sms.studentmanagementsystem.repository;

import com.sarwad.sms.studentmanagementsystem.entity.Department;
import com.sarwad.sms.studentmanagementsystem.entity.Role;
import com.sarwad.sms.studentmanagementsystem.entity.Student;
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
 * Repository tests for StudentRepository using @SpringBootTest
 * Tests run with H2 in-memory database
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("StudentRepository Tests")
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department testDepartment;
    private Student testStudent;

    @BeforeEach
    void setUp() {
        // Arrange - Create test data
        testDepartment = Department.builder()
                .name("Computer Science")
                .description("CS Department")
                .build();
        testDepartment = departmentRepository.save(testDepartment);

        testStudent = Student.builder()
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .department(testDepartment)
                .build();
    }

    @Test
    @DisplayName("Should save student successfully")
    void save_Success() {
        // Act
        Student savedStudent = studentRepository.save(testStudent);

        // Assert
        assertThat(savedStudent).isNotNull();
        assertThat(savedStudent.getId()).isNotNull();
        assertThat(savedStudent.getName()).isEqualTo("John Doe");
        assertThat(savedStudent.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(savedStudent.getRoll()).isEqualTo("CS001");
    }

    @Test
    @DisplayName("Should find student by ID successfully")
    void findById_Success() {
        // Arrange
        Student savedStudent = studentRepository.save(testStudent);

        // Act
        Optional<Student> foundStudent = studentRepository.findById(savedStudent.getId());

        // Assert
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Doe");
        assertThat(foundStudent.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty when student ID does not exist")
    void findById_NotFound() {
        // Act
        Optional<Student> foundStudent = studentRepository.findById(999L);

        // Assert
        assertThat(foundStudent).isEmpty();
    }

    @Test
    @DisplayName("Should find student by email successfully")
    void findByEmail_Success() {
        // Arrange
        studentRepository.save(testStudent);

        // Act
        Optional<Student> foundStudent = studentRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Doe");
        assertThat(foundStudent.get().getRoll()).isEqualTo("CS001");
    }

    @Test
    @DisplayName("Should return empty when email does not exist")
    void findByEmail_NotFound() {
        // Act
        Optional<Student> foundStudent = studentRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(foundStudent).isEmpty();
    }

    @Test
    @DisplayName("Should find student by roll successfully")
    void findByRoll_Success() {
        // Arrange
        studentRepository.save(testStudent);

        // Act
        Optional<Student> foundStudent = studentRepository.findByRoll("CS001");

        // Assert
        assertThat(foundStudent).isPresent();
        assertThat(foundStudent.get().getName()).isEqualTo("John Doe");
        assertThat(foundStudent.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty when roll does not exist")
    void findByRoll_NotFound() {
        // Act
        Optional<Student> foundStudent = studentRepository.findByRoll("NONEXISTENT");

        // Assert
        assertThat(foundStudent).isEmpty();
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_Success() {
        // Arrange
        studentRepository.save(testStudent);

        // Act
        boolean exists = studentRepository.existsByEmail("john.doe@example.com");
        boolean notExists = studentRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should check if roll exists")
    void existsByRoll_Success() {
        // Arrange
        studentRepository.save(testStudent);

        // Act
        boolean exists = studentRepository.existsByRoll("CS001");
        boolean notExists = studentRepository.existsByRoll("NONEXISTENT");

        // Assert
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Should find students by department ID using custom query")
    void findByDepartmentId_Success() {
        // Arrange
        studentRepository.save(testStudent);

        Student anotherStudent = Student.builder()
                .name("Jane Smith")
                .roll("CS002")
                .email("jane.smith@example.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .department(testDepartment)
                .build();
        studentRepository.save(anotherStudent);

        // Act
        List<Student> students = studentRepository.findByDepartmentId(testDepartment.getId());

        // Assert
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getName)
                .containsExactlyInAnyOrder("John Doe", "Jane Smith");
    }

    @Test
    @DisplayName("Should return empty list when no students in department")
    void findByDepartmentId_EmptyList() {
        // Arrange
        Department anotherDepartment = Department.builder()
                .name("Mathematics")
                .description("Math Department")
                .build();
        anotherDepartment = departmentRepository.save(anotherDepartment);

        // Act
        List<Student> students = studentRepository.findByDepartmentId(anotherDepartment.getId());

        // Assert
        assertThat(students).isEmpty();
    }

    @Test
    @DisplayName("Should delete student successfully")
    void delete_Success() {
        // Arrange
        Student savedStudent = studentRepository.save(testStudent);
        Long studentId = savedStudent.getId();

        // Act
        studentRepository.deleteById(studentId);

        // Assert
        Optional<Student> deletedStudent = studentRepository.findById(studentId);
        assertThat(deletedStudent).isEmpty();
    }

    @Test
    @DisplayName("Should handle transactional behavior correctly")
    void transactional_Behavior() {
        // Arrange
        Student savedStudent = studentRepository.save(testStudent);

        // Act - Update student
        savedStudent.setName("John Updated");
        savedStudent.setEmail("john.updated@example.com");
        studentRepository.save(savedStudent);

        // Assert
        Student updatedStudent = studentRepository.findById(savedStudent.getId()).orElseThrow();
        assertThat(updatedStudent.getName()).isEqualTo("John Updated");
        assertThat(updatedStudent.getEmail()).isEqualTo("john.updated@example.com");
    }
}
