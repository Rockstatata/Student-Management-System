package com.sarwad.sms.studentmanagementsystem.service;

import com.sarwad.sms.studentmanagementsystem.dto.RegistrationDto;
import com.sarwad.sms.studentmanagementsystem.entity.Department;
import com.sarwad.sms.studentmanagementsystem.entity.Role;
import com.sarwad.sms.studentmanagementsystem.entity.Student;
import com.sarwad.sms.studentmanagementsystem.entity.Teacher;
import com.sarwad.sms.studentmanagementsystem.exception.DuplicateResourceException;
import com.sarwad.sms.studentmanagementsystem.exception.ResourceNotFoundException;
import com.sarwad.sms.studentmanagementsystem.repository.DepartmentRepository;
import com.sarwad.sms.studentmanagementsystem.repository.StudentRepository;
import com.sarwad.sms.studentmanagementsystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class RegistrationService {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public void registerUser(RegistrationDto dto) {
        // Validate password confirmation
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Check if email already exists
        if (studentRepository.existsByEmail(dto.getEmail()) ||
            teacherRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("User with email '" + dto.getEmail() + "' already exists");
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", dto.getDepartmentId()));
        }

        if ("STUDENT".equals(dto.getUserType())) {
            registerStudent(dto, department);
        } else if ("TEACHER".equals(dto.getUserType())) {
            registerTeacher(dto, department);
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }
    }

    private void registerStudent(RegistrationDto dto, Department department) {
        // Validate roll number is provided for students
        if (dto.getRoll() == null || dto.getRoll().trim().isEmpty()) {
            throw new IllegalArgumentException("Roll number is required for students");
        }

        if (studentRepository.existsByRoll(dto.getRoll())) {
            throw new DuplicateResourceException("Student with roll '" + dto.getRoll() + "' already exists");
        }

        Student student = Student.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .roll(dto.getRoll())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.STUDENT)
                .department(department)
                .build();

        studentRepository.save(student);
        log.info("Student registered successfully: {}", dto.getEmail());
    }

    private void registerTeacher(RegistrationDto dto, Department department) {
        Teacher teacher = Teacher.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.TEACHER)
                .department(department)
                .build();

        teacherRepository.save(teacher);
        log.info("Teacher registered successfully: {}", dto.getEmail());
    }
}
