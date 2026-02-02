package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.dto.RegistrationDto;
import com.sarwad.sms.studentmanagementsystem.exception.DuplicateResourceException;
import com.sarwad.sms.studentmanagementsystem.service.DepartmentService;
import com.sarwad.sms.studentmanagementsystem.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final RegistrationService registrationService;
    private final DepartmentService departmentService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationDto", new RegistrationDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "auth/register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("registrationDto") RegistrationDto dto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {

        // Additional validation
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match");
        }

        if ("STUDENT".equals(dto.getUserType())) {
            if (dto.getRoll() == null || dto.getRoll().trim().isEmpty()) {
                result.rejectValue("roll", "error.roll", "Roll number is required for students");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "auth/register";
        }

        try {
            registrationService.registerUser(dto);
            redirectAttributes.addFlashAttribute("registrationSuccess", true);
            log.info("User registered successfully: {}", dto.getEmail());
            return "redirect:/login?registered=true";
        } catch (DuplicateResourceException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "auth/register";
        } catch (Exception e) {
            log.error("Error during registration", e);
            model.addAttribute("error", "An error occurred during registration. Please try again.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "auth/register";
        }
    }
}
