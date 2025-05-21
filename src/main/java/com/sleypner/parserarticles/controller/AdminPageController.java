package com.sleypner.parserarticles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {
    @GetMapping("/admin")
    String getDashboard(Model model) {

        model.addAttribute("loc", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    String getUsers(Model model) {

        model.addAttribute("loc", "users");

        return "admin/users";
    }

    @GetMapping("/admin/settings")
    String getSettings(Model model) {

        model.addAttribute("loc", "settings");

        return "admin/settings";
    }

    @GetMapping("/admin/statistics")
    String getStatistics(Model model) {

        model.addAttribute("loc", "statistics");

        return "admin/statistics";
    }

    @GetMapping("/admin/logs")
    String getLogs(Model model) {

        model.addAttribute("loc", "logs");

        return "admin/logs";
    }
}
