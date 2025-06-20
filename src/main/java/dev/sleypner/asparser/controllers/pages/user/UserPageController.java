package dev.sleypner.asparser.controllers.pages.user;

import dev.sleypner.asparser.domain.model.UserActionLog;
import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.service.core.auth.roles.RolesService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
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

        User user = usersService.getById(id).orElse(null);
        Set<UserActionLog> userActionLogs = user.getUserActionLogs();
        UserActionLog userActionLog = userActionLogs.stream().max(Comparator.comparingInt(UserActionLog::getId)).orElse(null);

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