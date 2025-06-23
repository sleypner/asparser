package dev.sleypner.asparser.controllers.api;

import dev.sleypner.asparser.domain.model.Role;
import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.dto.SignupRequest;
import dev.sleypner.asparser.service.core.auth.captcha.CaptchaService;
import dev.sleypner.asparser.service.core.auth.roles.RolesService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import dev.sleypner.asparser.util.StringExtension;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthRestController {

    private final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

    private final UsersService usersService;
    private final RolesService rolesService;
    private final CaptchaService captchaService;

    @GetMapping(value = "/resend-verification")
    public boolean resendVerification(
            @RequestParam(value = "email") String email,
            Model model
    ) {
        boolean isSendingEmail = usersService.sendVerificationCode(email);

        if (!isSendingEmail) {
            model.addAttribute("error", true);
        }

        model.addAttribute("email", email);

        return isSendingEmail;
    }

    @RequestMapping(value = "/check-username", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public boolean checkUsername(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        boolean checked = false;
        Optional<User> user = usersService.getByUsername(username);
        if (user.isPresent()) {
            checked = true;
        }
        return checked;
    }

    @RequestMapping(value = "/check-email", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public boolean checkEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean checked = false;

        Optional<User> user = usersService.getByEmail(email);
        if (user.isPresent()) {
            checked = true;
        }
        return checked;
    }

    @RequestMapping(value = "/signup-process", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> registrationProcess(
            Model model,
            @Valid @RequestBody SignupRequest request,
            BindingResult result
    ) {

        Map<String, String> errors = new HashMap<>();

        if (!captchaService.checkCaptcha(request.getGRecaptchaResponse())) {
            errors.put("captcha", "Please confirm that you are not a robot!");
            return ResponseEntity.ok().body(errors);
        }
        if (result.hasErrors()) {
            return ResponseEntity.ok()
                    .body(result.getFieldErrors()
                            .stream()
                            .collect(Collectors.toMap(
                                    fieldError -> fieldError.getField().replace("user.", ""),
                                    fieldError1 -> fieldError1.getDefaultMessage() == null ? "" : fieldError1.getDefaultMessage()
                            )));
        }
        User user = request.getUser();
        String username = user.getUsername();
        Optional<User> optionalDbUser = usersService.getByUsername(username);

        if (!optionalDbUser.isPresent()) {

            String newPassword = StringExtension.createPassword(user.getPassword());

            user = usersService.save(user.setPassword(newPassword)
                    .setName(username));

            rolesService.save(Role.builder()
                    .role("ROLE_USER")
                    .username(username)
                    .user(user)
                    .build());

            String uri = "/auth/email-verification?email=" + user.getEmail();
            return ResponseEntity.ok().body(Collections.singletonMap("redirectUrl", uri));
        } else {
            User dbUser = optionalDbUser.get();

            if (Objects.equals(dbUser.getEmail(), user.getEmail())) {
                errors.put("username", "User with email " + user.getEmail() + " already exists");
                return ResponseEntity.ok().body(errors);
            }

            errors.put("email", "User with email " + username + " already exists");
            return ResponseEntity.ok().body("Please confirm that you are not a robot!");
        }
    }
}
