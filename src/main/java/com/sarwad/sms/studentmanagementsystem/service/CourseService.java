package com.sarwad.sms.studentmanagementsystem.service;

import com.sarwad.sms.studentmanagementsystem.dto.CourseDto;
import com.sarwad.sms.studentmanagementsystem.entity.Course;
import com.sarwad.sms.studentmanagementsystem.entity.Department;
import com.sarwad.sms.studentmanagementsystem.exception.DuplicateResourceException;
import com.sarwad.sms.studentmanagementsystem.exception.ResourceNotFoundException;
import com.sarwad.sms.studentmanagementsystem.repository.CourseRepository;
import com.sarwad.sms.studentmanagementsystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CourseDto getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
        return toDto(course);
    }

    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    public List<CourseDto> getCoursesByDepartment(Long departmentId) {
        return courseRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CourseDto createCourse(CourseDto dto) {
        if (courseRepository.existsByNameAndDepartmentId(dto.getName(), dto.getDepartmentId())) {
            throw new DuplicateResourceException("Course with name '" + dto.getName() + "' already exists in this department");
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));

        Course course = Course.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .credits(dto.getCredits())
                .department(department)
                .build();

        course = courseRepository.save(course);
        return toDto(course);
    }

    public CourseDto updateCourse(Long id, CourseDto dto) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));

        if (!course.getName().equals(dto.getName()) &&
            courseRepository.existsByNameAndDepartmentId(dto.getName(), dto.getDepartmentId())) {
            throw new DuplicateResourceException("Course with name '" + dto.getName() + "' already exists in this department");
        }

        course.setName(dto.getName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
            course.setDepartment(department);
        }

        course = courseRepository.save(course);
        return toDto(course);
    }

    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Course", id);
        }
        courseRepository.deleteById(id);
    }

    private CourseDto toDto(Course course) {
        return CourseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .credits(course.getCredits())
                .departmentId(course.getDepartment().getId())
                .departmentName(course.getDepartment().getName())
                .studentCount(course.getStudents() != null ? course.getStudents().size() : 0)
                .build();
    }
}
