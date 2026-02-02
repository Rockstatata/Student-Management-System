package com.sarwad.sms.studentmanagementsystem.security;

import com.sarwad.sms.studentmanagementsystem.entity.Student;
import com.sarwad.sms.studentmanagementsystem.entity.Teacher;
import com.sarwad.sms.studentmanagementsystem.repository.StudentRepository;
import com.sarwad.sms.studentmanagementsystem.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // First try to find a teacher
        Optional<Teacher> teacher = teacherRepository.findByEmail(email);
        if (teacher.isPresent()) {
            return new CustomUserDetails(teacher.get());
        }

        // Then try to find a student
        Optional<Student> student = studentRepository.findByEmail(email);
        if (student.isPresent()) {
            return new CustomUserDetails(student.get());
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
