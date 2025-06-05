package com.sleypner.parserarticles.controller.api;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.Base64BinaryType;
import com.sleypner.parserarticles.model.services.CaptchaService;
import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.model.source.other.SignupRequest;
import com.sleypner.parserarticles.special.Special;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthRestController {

    private final UsersService usersService;
    private final RolesService rolesService;
    private final CaptchaService captchaService;
    private final Logger logger = LoggerFactory.getLogger(AuthRestController.class);

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
        Optional<Users> user = usersService.getByUsername(username);
        if (user.isPresent()) {
            checked = true;
        }
        return checked;
    }

    @RequestMapping(value = "/check-email", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public boolean checkEmail(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        boolean checked = false;

        Optional<Users> user = usersService.getByEmail(email);
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
        Users user = request.getUser();
        String username = user.getUsername();
        Optional<Users> optionalDbUser = usersService.getByUsername(username);

        if (!optionalDbUser.isPresent()) {

            user = usersService.save(user.setPassword("{bcrypt}" + Special.encodeBCrypt(user.getPassword()))
                    .setName(username));

            rolesService.save(Roles.builder()
                    .role("ROLE_USER")
                    .username(username)
                    .user(user)
                    .build());

            String uri  = "/auth/email-verification?email=" + user.getEmail();
            return ResponseEntity.ok().body(Collections.singletonMap("redirectUrl", uri));
        } else {
            Users dbUser = optionalDbUser.get();

            if (Objects.equals(dbUser.getEmail(), user.getEmail())) {
                errors.put("username", "User with email " + user.getEmail() + " already exists");
                return ResponseEntity.ok().body(errors);
            }

            errors.put("email", "User with email " + username + " already exists");
            return ResponseEntity.ok().body("Please confirm that you are not a robot!");
        }
    }
}
