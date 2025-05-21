package com.sleypner.parserarticles.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UsersController {

    @GetMapping("/profile")
    public String profile(Model model) {
        return "user/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        return "user/settings";
    }
}
