package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.model.services.*;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.model.source.entityes.VerificationCode;
import com.sleypner.parserarticles.special.Special;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
public class LoginController {
    private final UsersService usersService;
    private final RolesService rolesService;
    private final CaptchaService captchaService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;

    public LoginController(UsersService usersService,
                           RolesService rolesService,
                           CaptchaService captchaService,
                           EmailService emailService,
                           VerificationCodeService verificationCodeService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
        this.captchaService = captchaService;
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
    }

    @GetMapping("/login")
    public String showLoginPage(Model model,
                                @RequestParam(value = "error", defaultValue = "false") boolean error,
                                @RequestParam(value = "logout", defaultValue = "false") boolean logout) {

        if (error) {
            model.addAttribute("errorMessage", "Incorrect credentials");
        }
        if (logout) {
            model.addAttribute("logoutMessage", "You have successfully logged out.");
        }

        return "auth/login";
    }

    @GetMapping("/access-denied")
    public String showAccessDenied() {
        return "home/access-denied";
    }

    @GetMapping("/registration")
    public String showRegistrationPage() {
        return "auth/registration";
    }

    @RequestMapping(value = "/registration-process", method = RequestMethod.POST)
    public String registrationProcess(Model model,
                                      @Valid @ModelAttribute("user") Users user,
                                      BindingResult result,
                                      @RequestParam(name = "g-recaptcha-response") String captchaResponse
    ) {

        if (!captchaService.checkCaptcha(captchaResponse)) {
            model.addAttribute("errorMessage", "Please confirm that you are not a robot");
            return "auth/registration";
        }
        if (result.hasErrors()) {
            return "auth/registration";
        }
        String username = user.getUsername();
        Users savedUser = usersService.getByUsername(username);

        if (savedUser == null) {
            Roles role = new Roles();
            String encryptedPassword = "{bcrypt}" + Special.encodeBCrypt(user.getPassword());
            user.setPassword(encryptedPassword);
            user.setName(username);

            user = usersService.save(user);

            role.setRole("ROLE_USER");
            role.setUsername(username);
            role.setUser(user);

            rolesService.save(role);
            return "redirect:/email-verification?email=" + user.getEmail();
        } else {
            model.addAttribute("errorMessage", "User with username " + username + " already exists");
            return "redirect:/registration";
        }
    }

    @GetMapping("/email-verification")
    public String emailVerification(@RequestParam(value = "email") String email, Model model) {
        int code = usersService.generateRandomCode();
        VerificationCode verificationCode = new VerificationCode(email, code);
        verificationCodeService.save(verificationCode);
        emailService.sendVerificationCode(email, code);
        model.addAttribute("email", email);

        return "auth/email-verification";
    }

    @RequestMapping(value = "/verify-code", method = RequestMethod.POST)
    public String verifyCode(
            @RequestParam String digit1,
            @RequestParam String digit2,
            @RequestParam String digit3,
            @RequestParam String digit4,
            @RequestParam String digit5,
            @RequestParam String digit6,
            @RequestParam String email,
            Model model) {

        int enteredCode = Integer.parseInt(digit1 + digit2 + digit3 + digit4 + digit5 + digit6);
        boolean isVerified = usersService.verifyEmailCode(email, enteredCode);

        if (isVerified) {

            model.addAttribute("verified", isVerified);
            model.addAttribute("email", email);
            return "auth/email-verification";
        } else {
            model.addAttribute("error", true);
            model.addAttribute("email", email);
            return "auth/email-verification";
        }
    }

//    @GetMapping("/verify-email")
//    public String showVerificationResult(
//            @RequestParam(required = false) boolean success,
//            @RequestParam String email,
//            Model model) {
//
//        model.addAttribute("verified", success);
//        model.addAttribute("email", email);
//        return "auth/email-verification";
//    }

    @RequestMapping(value = "/resend-verification", method = RequestMethod.POST)
    public String resendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        usersService.resendVerificationCode(email);
        return "auth/email-verification";
    }
}
