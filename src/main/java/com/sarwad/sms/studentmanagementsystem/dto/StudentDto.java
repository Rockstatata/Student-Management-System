package com.sarwad.sms.studentmanagementsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDto {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Roll number is required")
    @Size(min = 2, max = 20, message = "Roll must be between 2 and 20 characters")
    private String roll;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private Long departmentId;
    private String departmentName;

    private Set<Long> courseIds;
}
