package com.sarwad.sms.studentmanagementsystem.security;

import com.sarwad.sms.studentmanagementsystem.entity.Role;
import com.sarwad.sms.studentmanagementsystem.entity.Student;
import com.sarwad.sms.studentmanagementsystem.entity.Teacher;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final String name;
    private final Role role;
    private final Long departmentId;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(Teacher teacher) {
        this.id = teacher.getId();
        this.email = teacher.getEmail();
        this.password = teacher.getPassword();
        this.name = teacher.getName();
        this.role = Role.TEACHER;
        this.departmentId = teacher.getDepartment() != null ? teacher.getDepartment().getId() : null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_TEACHER"));
    }

    public CustomUserDetails(Student student) {
        this.id = student.getId();
        this.email = student.getEmail();
        this.password = student.getPassword();
        this.name = student.getName();
        this.role = Role.STUDENT;
        this.departmentId = student.getDepartment() != null ? student.getDepartment().getId() : null;
        this.authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_STUDENT"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public boolean isTeacher() {
        return role == Role.TEACHER;
    }

    public boolean isStudent() {
        return role == Role.STUDENT;
    }
}
