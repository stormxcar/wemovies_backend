package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import com.example.demo.models.Admin;
import com.example.demo.services.AdminService;


@Controller
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin/login";
    }
    @PostMapping("/admin/login")
    public String login(String username, String password, Model model) {
        Admin admin = adminService.findByUserName(username);
        if (admin != null && admin.getPassword().equals(password))
        {
            return "redirect:/admin/categories";
        }
        model.addAttribute("error", "Invalid username or password");
        return "admin/login";
    }
}
