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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final DepartmentRepository departmentRepository;
    private final CourseRepository courseRepository;
    private final PasswordEncoder passwordEncoder;

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
        return toDto(student);
    }

    public Student getStudentEntity(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    public Optional<Student> findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public List<StudentDto> getStudentsByDepartment(Long departmentId) {
        return studentRepository.findByDepartmentId(departmentId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto createStudent(StudentDto dto) {
        if (studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Student with email '" + dto.getEmail() + "' already exists");
        }
        if (studentRepository.existsByRoll(dto.getRoll())) {
            throw new DuplicateResourceException("Student with roll '" + dto.getRoll() + "' already exists");
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
        }

        Student student = Student.builder()
                .name(dto.getName())
                .roll(dto.getRoll())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.STUDENT)
                .department(department)
                .build();

        // Handle course assignments
        if (dto.getCourseIds() != null && !dto.getCourseIds().isEmpty()) {
            Set<Course> courses = new HashSet<>();
            for (Long courseId : dto.getCourseIds()) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
                courses.add(course);
            }
            student.setCourses(courses);
        }

        student = studentRepository.save(student);
        return toDto(student);
    }

    public StudentDto updateStudent(Long id, StudentDto dto, boolean isSelfUpdate, Long currentUserId) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));

        // If student is updating themselves, they can only update certain fields
        if (isSelfUpdate && !student.getId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You can only update your own profile");
        }

        if (!student.getEmail().equals(dto.getEmail()) &&
            studentRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Student with email '" + dto.getEmail() + "' already exists");
        }
        if (!student.getRoll().equals(dto.getRoll()) &&
            studentRepository.existsByRoll(dto.getRoll())) {
            throw new DuplicateResourceException("Student with roll '" + dto.getRoll() + "' already exists");
        }

        student.setName(dto.getName());
        student.setEmail(dto.getEmail());
        student.setRoll(dto.getRoll());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            student.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Only teachers can change department
        if (!isSelfUpdate && dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
            student.setDepartment(department);
        }

        // Handle course assignments
        if (dto.getCourseIds() != null) {
            Set<Course> courses = new HashSet<>();
            for (Long courseId : dto.getCourseIds()) {
                Course course = courseRepository.findById(courseId)
                        .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
                courses.add(course);
            }
            student.setCourses(courses);
        }

        student = studentRepository.save(student);
        return toDto(student);
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student", id);
        }
        studentRepository.deleteById(id);
    }

    public void enrollInCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        student.getCourses().add(course);
        studentRepository.save(student);
    }

    public void unenrollFromCourse(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        student.getCourses().remove(course);
        studentRepository.save(student);
    }

    private StudentDto toDto(Student student) {
        Set<Long> courseIds = student.getCourses() != null
            ? student.getCourses().stream().map(Course::getId).collect(Collectors.toSet())
            : new HashSet<>();

        return StudentDto.builder()
                .id(student.getId())
                .name(student.getName())
                .roll(student.getRoll())
                .email(student.getEmail())
                .departmentId(student.getDepartment() != null ? student.getDepartment().getId() : null)
                .departmentName(student.getDepartment() != null ? student.getDepartment().getName() : null)
                .courseIds(courseIds)
                .build();
    }
}
