package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.dto.CourseDto;
import com.sarwad.sms.studentmanagementsystem.security.CustomUserDetails;
import com.sarwad.sms.studentmanagementsystem.service.CourseService;
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
@RequestMapping("/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listCourses(@AuthenticationPrincipal CustomUserDetails userDetails,
                               @RequestParam(required = false) Long departmentId,
                               Model model) {
        if (departmentId != null) {
            model.addAttribute("courses", courseService.getCoursesByDepartment(departmentId));
        } else {
            model.addAttribute("courses", courseService.getAllCourses());
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("user", userDetails);
        model.addAttribute("selectedDepartmentId", departmentId);
        return "course/list";
    }

    @GetMapping("/{id}")
    public String viewCourse(@PathVariable Long id,
                              @AuthenticationPrincipal CustomUserDetails userDetails,
                              Model model) {
        CourseDto course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        model.addAttribute("user", userDetails);
        return "course/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("course", new CourseDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("user", userDetails);
        return "course/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String createCourse(@Valid @ModelAttribute("course") CourseDto dto,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("user", userDetails);
            return "course/form";
        }

        try {
            courseService.createCourse(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Course created successfully!");
            return "redirect:/courses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("user", userDetails);
            return "course/form";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String showEditForm(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("user", userDetails);
        return "course/form";
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('TEACHER')")
    public String updateCourse(@PathVariable Long id,
                                @Valid @ModelAttribute("course") CourseDto dto,
                                BindingResult result,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("user", userDetails);
            return "course/form";
        }

        try {
            courseService.updateCourse(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Course updated successfully!");
            return "redirect:/courses";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("user", userDetails);
            return "course/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.deleteCourse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Course deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/courses";
    }
}
