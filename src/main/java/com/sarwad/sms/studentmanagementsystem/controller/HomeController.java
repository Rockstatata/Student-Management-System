package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.security.CustomUserDetails;
import com.sarwad.sms.studentmanagementsystem.service.CourseService;
import com.sarwad.sms.studentmanagementsystem.service.DepartmentService;
import com.sarwad.sms.studentmanagementsystem.service.StudentService;
import com.sarwad.sms.studentmanagementsystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DepartmentService departmentService;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final CourseService courseService;

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("user", userDetails);
        model.addAttribute("departmentCount", departmentService.getAllDepartments().size());
        model.addAttribute("studentCount", studentService.getAllStudents().size());
        model.addAttribute("teacherCount", teacherService.getAllTeachers().size());
        model.addAttribute("courseCount", courseService.getAllCourses().size());
        return "dashboard";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("error", "You don't have permission to access this resource.");
        model.addAttribute("errorTitle", "Access Denied");
        return "error/error";
    }
}
