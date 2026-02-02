package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.dto.StudentDto;
import com.sarwad.sms.studentmanagementsystem.exception.UnauthorizedAccessException;
import com.sarwad.sms.studentmanagementsystem.security.CustomUserDetails;
import com.sarwad.sms.studentmanagementsystem.service.CourseService;
import com.sarwad.sms.studentmanagementsystem.service.DepartmentService;
import com.sarwad.sms.studentmanagementsystem.service.StudentService;
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
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final DepartmentService departmentService;
    private final CourseService courseService;

    @GetMapping
    public String listStudents(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @RequestParam(required = false) Long departmentId,
                                Model model) {
        if (departmentId != null) {
            model.addAttribute("students", studentService.getStudentsByDepartment(departmentId));
        } else {
            model.addAttribute("students", studentService.getAllStudents());
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("user", userDetails);
        model.addAttribute("selectedDepartmentId", departmentId);
        return "student/list";
    }

    @GetMapping("/{id}")
    public String viewStudent(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        StudentDto student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        model.addAttribute("user", userDetails);
        model.addAttribute("courses", courseService.getAllCourses());
        return "student/view";
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String showCreateForm(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("student", new StudentDto());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("user", userDetails);
        return "student/form";
    }

    @PostMapping("/new")
    @PreAuthorize("hasRole('TEACHER')")
    public String createStudent(@Valid @ModelAttribute("student") StudentDto dto,
                                 BindingResult result,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("user", userDetails);
            return "student/form";
        }

        try {
            studentService.createStudent(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Student created successfully!");
            return "redirect:/students";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("user", userDetails);
            return "student/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        StudentDto student = studentService.getStudentById(id);

        // Students can only edit their own profile
        if (userDetails.isStudent() && !userDetails.getId().equals(id)) {
            throw new UnauthorizedAccessException("You can only edit your own profile");
        }

        model.addAttribute("student", student);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("user", userDetails);
        model.addAttribute("isEditing", true);
        return "student/form";
    }

    @PostMapping("/{id}/edit")
    public String updateStudent(@PathVariable Long id,
                                 @Valid @ModelAttribute("student") StudentDto dto,
                                 BindingResult result,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        // Students can only edit their own profile
        boolean isSelfUpdate = userDetails.isStudent();

        if (isSelfUpdate && !userDetails.getId().equals(id)) {
            throw new UnauthorizedAccessException("You can only edit your own profile");
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("user", userDetails);
            model.addAttribute("isEditing", true);
            return "student/form";
        }

        try {
            studentService.updateStudent(id, dto, isSelfUpdate, userDetails.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Student updated successfully!");
            return "redirect:/students/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departments", departmentService.getAllDepartments());
            model.addAttribute("courses", courseService.getAllCourses());
            model.addAttribute("user", userDetails);
            model.addAttribute("isEditing", true);
            return "student/form";
        }
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/students";
    }
}
