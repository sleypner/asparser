package dev.sleypner.asparser.controllers.pages.auth;

import dev.sleypner.asparser.service.core.auth.captcha.CaptchaService;
import dev.sleypner.asparser.service.core.auth.email.EmailService;
import dev.sleypner.asparser.service.core.auth.roles.RolesService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
import dev.sleypner.asparser.service.core.auth.verification.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@RequestMapping("/auth")
public class AuthPageController {
    private final UsersService usersService;
    private final RolesService rolesService;
    private final CaptchaService captchaService;
    private final EmailService emailService;
    private final VerificationCodeService verificationCodeService;
    private final Logger logger = LoggerFactory.getLogger(AuthPageController.class);

    @GetMapping("/signin")
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

    @GetMapping("/signup")
    public String showRegistrationPage() {
        return "auth/registration";
    }

    @GetMapping("/email-verification")
    public String emailVerification(@RequestParam(value = "email") String email, Model model) {

        boolean isSendingEmail = usersService.sendVerificationCode(email);

        if (!isSendingEmail) {
            model.addAttribute("error", true);
        }

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

        boolean isVerified = false;
        try {
            int enteredCode = Integer.parseInt(digit1 + digit2 + digit3 + digit4 + digit5 + digit6);
            isVerified = usersService.verifyEmailCode(email, enteredCode);
        } catch (NumberFormatException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
        }

        if (!isVerified) {
            model.addAttribute("error", true);
        }
        model.addAttribute("verified", true);
        model.addAttribute("email", email);

        return "auth/email-verification";
    }
}
