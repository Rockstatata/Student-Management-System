package com.sarwad.sms.studentmanagementsystem.controller;

import com.sarwad.sms.studentmanagementsystem.dto.TeacherDto;
import com.sarwad.sms.studentmanagementsystem.security.CustomUserDetails;
import com.sarwad.sms.studentmanagementsystem.service.DepartmentService;
import com.sarwad.sms.studentmanagementsystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final DepartmentService departmentService;

    @GetMapping
    public String listTeachers(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("teachers", teacherService.getAllTeachers());
        model.addAttribute("user", userDetails);
        return "teacher/list";
    }

    @GetMapping("/{id}")
    public String viewTeacher(@PathVariable Long id,
                               @AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model) {
        TeacherDto teacher = teacherService.getTeacherById(id);
        model.addAttribute("teacher", teacher);
        model.addAttribute("user", userDetails);
        return "teacher/view";
    }
}
