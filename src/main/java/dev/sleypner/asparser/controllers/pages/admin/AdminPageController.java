package dev.sleypner.asparser.controllers.pages.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {
    @GetMapping("")
    String getDashboard(Model model) {

        model.addAttribute("loc", "dashboard");

        return "admin/dashboard";
    }

    @GetMapping("/users")
    String getUsers(Model model) {

        model.addAttribute("loc", "users");

        return "admin/users";
    }

    @GetMapping("/settings")
    String getSettings(Model model) {

        model.addAttribute("loc", "settings");

        return "admin/settings";
    }

    @GetMapping("/statistics")
    String getStatistics(Model model) {

        model.addAttribute("loc", "statistics");

        return "admin/statistics";
    }

    @GetMapping("/logs")
    String getLogs(Model model) {

        model.addAttribute("loc", "logs");

        return "admin/logs";
    }
}
