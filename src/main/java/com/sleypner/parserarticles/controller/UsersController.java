package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import com.sleypner.parserarticles.model.source.entityes.Users;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.Set;

@Controller
public class UsersController {
    UsersService usersService;
    RolesService rolesService;

    public UsersController(UsersService usersService, RolesService rolesService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
    }

    @GetMapping("/profile")
    public String profile(Model model,@RequestParam(value = "i") int id) {

        Users user = usersService.getById(id);
        Set<UserActionLogs> userActionLogs = user.getUserActionLogs();
        UserActionLogs userActionLog = userActionLogs.stream().max(Comparator.comparingInt(UserActionLogs::getId)).orElse(null);

        model.addAttribute("userActionLog", userActionLog);
        model.addAttribute("user", user);
        model.addAttribute("loc","user/profile");

        return "user/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {

        model.addAttribute("loc","user/settings");

        return "user/settings";
    }
}
