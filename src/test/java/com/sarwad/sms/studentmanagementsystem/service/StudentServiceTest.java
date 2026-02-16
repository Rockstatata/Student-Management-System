package com.sarwad.sms.studentmanagementsystem.service;

import com.sarwad.sms.studentmanagementsystem.dto.StudentDto;
import com.sarwad.sms.studentmanagementsystem.entity.Course;
import com.sarwad.sms.studentmanagementsystem.entity.Department;
import com.sarwad.sms.studentmanagementsystem.entity.Role;
import com.sarwad.sms.studentmanagementsystem.entity.Student;
import com.sarwad.sms.studentmanagementsystem.exception.DuplicateResourceException;
import com.sarwad.sms.studentmanagementsystem.exception.ResourceNotFoundException;
import com.sarwad.sms.studentmanagementsystem.exception.UnauthorizedAccessException;
import com.sarwad.sms.studentmanagementsystem.repository.CourseRepository;
import com.sarwad.sms.studentmanagementsystem.repository.DepartmentRepository;
import com.sarwad.sms.studentmanagementsystem.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService using Mockito
 * Following AAA (Arrange-Act-Assert) pattern
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Unit Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private StudentService studentService;

    private Student testStudent;
    private Department testDepartment;
    private Course testCourse;
    private StudentDto testStudentDto;

    @BeforeEach
    void setUp() {
        // Arrange - Set up test data
        testDepartment = Department.builder()
                .id(1L)
                .name("Computer Science")
                .description("CS Department")
                .build();

        testCourse = Course.builder()
                .id(1L)
                .name("Data Structures")
                .description("Learn about data structures")
                .credits(3)
                .department(testDepartment)
                .build();

        testStudent = Student.builder()
                .id(1L)
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .department(testDepartment)
                .courses(new HashSet<>(Collections.singletonList(testCourse)))
                .build();

        testStudentDto = StudentDto.builder()
                .id(1L)
                .name("John Doe")
                .roll("CS001")
                .email("john.doe@example.com")
                .password("password123")
                .departmentId(1L)
                .departmentName("Computer Science")
                .courseIds(new HashSet<>(Collections.singletonList(1L)))
                .build();
    }

    @Test
    @DisplayName("Should return all students successfully")
    void getAllStudents_Success() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        when(studentRepository.findAll()).thenReturn(students);

        // Act
        List<StudentDto> result = studentService.getAllStudents();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no students exist")
    void getAllStudents_EmptyList() {
        // Arrange
        when(studentRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<StudentDto> result = studentService.getAllStudents();

        // Assert
        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return student by ID successfully")
    void getStudentById_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // Act
        StudentDto result = studentService.getStudentById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when student not found")
    void getStudentById_NotFound() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.getStudentById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student")
                .hasMessageContaining("999");

        verify(studentRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create student successfully")
    void createStudent_Success() {
        // Arrange
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByRoll(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        StudentDto result = studentService.createStudent(testStudentDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(studentRepository, times(1)).existsByRoll("CS001");
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void createStudent_DuplicateEmail() {
        // Arrange
        when(studentRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(testStudentDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("email")
                .hasMessageContaining("john.doe@example.com");

        verify(studentRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when roll already exists")
    void createStudent_DuplicateRoll() {
        // Arrange
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByRoll("CS001")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(testStudentDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("roll")
                .hasMessageContaining("CS001");

        verify(studentRepository, times(1)).existsByRoll("CS001");
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found during creation")
    void createStudent_DepartmentNotFound() {
        // Arrange
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByRoll(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.createStudent(testStudentDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");

        verify(departmentRepository, times(1)).findById(1L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should update student successfully")
    void updateStudent_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByRoll(anyString())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        StudentDto result = studentService.updateStudent(1L, testStudentDto, false, 2L);

        // Assert
        assertThat(result).isNotNull();
        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw UnauthorizedAccessException when student updates another student's profile")
    void updateStudent_UnauthorizedSelfUpdate() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));

        // Act & Assert
        assertThatThrownBy(() -> studentService.updateStudent(1L, testStudentDto, true, 2L))
                .isInstanceOf(UnauthorizedAccessException.class)
                .hasMessageContaining("your own profile");

        verify(studentRepository, times(1)).findById(1L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should delete student successfully")
    void deleteStudent_Success() {
        // Arrange
        when(studentRepository.existsById(1L)).thenReturn(true);

        // Act
        studentService.deleteStudent(1L);

        // Assert
        verify(studentRepository, times(1)).existsById(1L);
        verify(studentRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent student")
    void deleteStudent_NotFound() {
        // Arrange
        when(studentRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> studentService.deleteStudent(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(studentRepository, times(1)).existsById(999L);
        verify(studentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should enroll student in course successfully")
    void enrollInCourse_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(testCourse));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        studentService.enrollInCourse(1L, 2L);

        // Assert
        verify(studentRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(2L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when enrolling non-existent student")
    void enrollInCourse_StudentNotFound() {
        // Arrange
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.enrollInCourse(999L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Student");

        verify(studentRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when enrolling in non-existent course")
    void enrollInCourse_CourseNotFound() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> studentService.enrollInCourse(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course");

        verify(courseRepository, times(1)).findById(999L);
        verify(studentRepository, never()).save(any(Student.class));
    }

    @Test
    @DisplayName("Should unenroll student from course successfully")
    void unenrollFromCourse_Success() {
        // Arrange
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(studentRepository.save(any(Student.class))).thenReturn(testStudent);

        // Act
        studentService.unenrollFromCourse(1L, 1L);

        // Assert
        verify(studentRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findById(1L);
        verify(studentRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should return students by department ID")
    void getStudentsByDepartment_Success() {
        // Arrange
        List<Student> students = Arrays.asList(testStudent);
        when(studentRepository.findByDepartmentId(1L)).thenReturn(students);

        // Act
        List<StudentDto> result = studentService.getStudentsByDepartment(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(1L);
        verify(studentRepository, times(1)).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("Should find student by email")
    void findByEmail_Success() {
        // Arrange
        when(studentRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testStudent));

        // Act
        Optional<Student> result = studentService.findByEmail("john.doe@example.com");

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john.doe@example.com");
        verify(studentRepository, times(1)).findByEmail("john.doe@example.com");
    }
}
