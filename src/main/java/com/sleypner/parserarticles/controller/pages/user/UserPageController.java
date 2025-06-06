package com.sleypner.parserarticles.controller.pages.user;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import com.sleypner.parserarticles.model.source.entityes.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.Set;

@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserPageController {
    private final UsersService usersService;
    private final RolesService rolesService;

    @Transactional
    @GetMapping("/profile")
    public String profile(Model model, @RequestParam(value = "i") int id) {

        Users user = usersService.getById(id).orElse(null);
        Set<UserActionLogs> userActionLogs = user.getUserActionLogs();
        UserActionLogs userActionLog = userActionLogs.stream().max(Comparator.comparingInt(UserActionLogs::getId)).orElse(null);

        model.addAttribute("userActionLog", userActionLog);
        model.addAttribute("user", user);
        model.addAttribute("loc", "user/profile");

        return "user/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {

        model.addAttribute("loc", "user/settings");

        return "user/settings";
    }
}