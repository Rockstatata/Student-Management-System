package com.sarwad.sms.studentmanagementsystem.service;

import com.sarwad.sms.studentmanagementsystem.dto.CourseDto;
import com.sarwad.sms.studentmanagementsystem.entity.Course;
import com.sarwad.sms.studentmanagementsystem.entity.Department;
import com.sarwad.sms.studentmanagementsystem.exception.DuplicateResourceException;
import com.sarwad.sms.studentmanagementsystem.exception.ResourceNotFoundException;
import com.sarwad.sms.studentmanagementsystem.repository.CourseRepository;
import com.sarwad.sms.studentmanagementsystem.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CourseService using Mockito
 * Following AAA (Arrange-Act-Assert) pattern
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService Unit Tests")
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private CourseService courseService;

    private Course testCourse;
    private Department testDepartment;
    private CourseDto testCourseDto;

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
                .description("Learn about data structures and algorithms")
                .credits(3)
                .department(testDepartment)
                .students(new HashSet<>())
                .build();

        testCourseDto = CourseDto.builder()
                .id(1L)
                .name("Data Structures")
                .description("Learn about data structures and algorithms")
                .credits(3)
                .departmentId(1L)
                .departmentName("Computer Science")
                .studentCount(0)
                .build();
    }

    @Test
    @DisplayName("Should return all courses successfully")
    void getAllCourses_Success() {
        // Arrange
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findAll()).thenReturn(courses);

        // Act
        List<CourseDto> result = courseService.getAllCourses();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Data Structures");
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no courses exist")
    void getAllCourses_EmptyList() {
        // Arrange
        when(courseRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<CourseDto> result = courseService.getAllCourses();

        // Assert
        assertThat(result).isEmpty();
        verify(courseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return course by ID successfully")
    void getCourseById_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // Act
        CourseDto result = courseService.getCourseById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Data Structures");
        assertThat(result.getCredits()).isEqualTo(3);
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when course not found")
    void getCourseById_NotFound() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.getCourseById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course")
                .hasMessageContaining("999");

        verify(courseRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should create course successfully")
    void createCourse_Success() {
        // Arrange
        when(courseRepository.existsByNameAndDepartmentId("Data Structures", 1L)).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        CourseDto result = courseService.createCourse(testCourseDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Data Structures");
        assertThat(result.getDepartmentId()).isEqualTo(1L);
        verify(courseRepository, times(1)).existsByNameAndDepartmentId("Data Structures", 1L);
        verify(departmentRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when course name already exists in department")
    void createCourse_DuplicateName() {
        // Arrange
        when(courseRepository.existsByNameAndDepartmentId("Data Structures", 1L)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> courseService.createCourse(testCourseDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("name")
                .hasMessageContaining("Data Structures");

        verify(courseRepository, times(1)).existsByNameAndDepartmentId("Data Structures", 1L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found during creation")
    void createCourse_DepartmentNotFound() {
        // Arrange
        when(courseRepository.existsByNameAndDepartmentId("Data Structures", 1L)).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.createCourse(testCourseDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Department");

        verify(departmentRepository, times(1)).findById(1L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should update course successfully")
    void updateCourse_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));
        when(courseRepository.existsByNameAndDepartmentId("Data Structures", 1L)).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
        when(courseRepository.save(any(Course.class))).thenReturn(testCourse);

        // Act
        CourseDto result = courseService.updateCourse(1L, testCourseDto);

        // Assert
        assertThat(result).isNotNull();
        verify(courseRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).save(any(Course.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent course")
    void updateCourse_NotFound() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.updateCourse(999L, testCourseDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course");

        verify(courseRepository, times(1)).findById(999L);
        verify(courseRepository, never()).save(any(Course.class));
    }

    @Test
    @DisplayName("Should delete course successfully")
    void deleteCourse_Success() {
        // Arrange
        when(courseRepository.existsById(1L)).thenReturn(true);

        // Act
        courseService.deleteCourse(1L);

        // Assert
        verify(courseRepository, times(1)).existsById(1L);
        verify(courseRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent course")
    void deleteCourse_NotFound() {
        // Arrange
        when(courseRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> courseService.deleteCourse(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(courseRepository, times(1)).existsById(999L);
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should return courses by department ID")
    void getCoursesByDepartment_Success() {
        // Arrange
        List<Course> courses = Arrays.asList(testCourse);
        when(courseRepository.findByDepartmentId(1L)).thenReturn(courses);

        // Act
        List<CourseDto> result = courseService.getCoursesByDepartment(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentId()).isEqualTo(1L);
        verify(courseRepository, times(1)).findByDepartmentId(1L);
    }

    @Test
    @DisplayName("Should get enrolled students for a course")
    void getEnrolledStudents_Success() {
        // Arrange
        when(courseRepository.findById(1L)).thenReturn(Optional.of(testCourse));

        // Act
        var result = courseService.getEnrolledStudents(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty(); // testCourse has no students
        verify(courseRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when getting enrolled students for non-existent course")
    void getEnrolledStudents_CourseNotFound() {
        // Arrange
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> courseService.getEnrolledStudents(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Course");

        verify(courseRepository, times(1)).findById(999L);
    }
}
