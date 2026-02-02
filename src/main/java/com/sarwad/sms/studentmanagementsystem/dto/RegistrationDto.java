package com.sarwad.sms.studentmanagementsystem.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    @NotBlank(message = "User type is required")
    @Pattern(regexp = "STUDENT|TEACHER", message = "User type must be STUDENT or TEACHER")
    private String userType;

    // For students only
    @Size(min = 2, max = 20, message = "Roll must be between 2 and 20 characters")
    private String roll;

    private Long departmentId;
}
