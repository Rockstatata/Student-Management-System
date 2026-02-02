package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.dto.DepartmentDto;
import com.sarwad.sms.studentmanagementsystem.security.CustomUserDetails;
import com.sarwad.sms.studentmanagementsystem.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public String listDepartments(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("user", userDetails);
        return "department/list";
    }

    @GetMapping("/{id}")
    public String viewDepartment(@PathVariable Long id,
                                  @AuthenticationPrincipal CustomUserDetails userDetails,
                                  Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        model.addAttribute("user", userDetails);
        return "department/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("department", new DepartmentDto());
        model.addAttribute("user", userDetails);
        return "department/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String createDepartment(@Valid @ModelAttribute("department") DepartmentDto dto,
                                    BindingResult result,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDetails);
            return "department/form";
        }

        try {
            departmentService.createDepartment(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
            return "redirect:/departments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userDetails);
            return "department/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        model.addAttribute("department", departmentService.getDepartmentById(id));
        model.addAttribute("user", userDetails);
        return "department/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateDepartment(@PathVariable Long id,
                                    @Valid @ModelAttribute("department") DepartmentDto dto,
                                    BindingResult result,
                                    @AuthenticationPrincipal CustomUserDetails userDetails,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("user", userDetails);
            return "department/form";
        }

        try {
            departmentService.updateDepartment(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Department updated successfully!");
            return "redirect:/departments";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("user", userDetails);
            return "department/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/departments";
    }
}
