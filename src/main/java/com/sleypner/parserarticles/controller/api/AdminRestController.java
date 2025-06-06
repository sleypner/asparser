package com.sleypner.parserarticles.controller.api;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.special.Special;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AdminRestController {
    private final UsersService usersService;
    private final RolesService rolesService;

    @GetMapping(value = "/users/manage", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page) {
        List<Users> usersList = new ArrayList<>();

        if (search == null) {
            usersList = usersService.getAll();
        } else {
            usersList = usersService.search(search);
            if (usersList == null) {
                ResponseEntity.ok().body("No users found");
            }
        }
        return ResponseEntity.ok(usersList);
    }

    @GetMapping(value = "/users/manage/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public Users editUser(@PathVariable int id) {

        return usersService.getById(id).orElse(null);
    }

    @RequestMapping(value = "/users/manage/add", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@Valid @RequestBody Users user) {

        String log;
        Optional<Users> optionalDbUser = usersService.getByUsername(user.getUsername());
        if (optionalDbUser.isPresent()) {
            log = "User already exists";
        } else {
            usersService.save(user);
//            Set<Roles> rolesSet = user.getRoles();
//            for (Roles role : rolesSet) {
//                role.setUsername(user.getUsername().toLowerCase(Locale.ROOT))
//                        .setUser(savedUser);
//                rolesService.save(role);
//            }
            log = "User added successfully";
        }
        return ResponseEntity.badRequest().body(log);
    }

    @RequestMapping(value = "/users/manage/{id}", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable int id) {

        rolesService.deleteByUserId(id);
        usersService.delete(id);

    }

    @RequestMapping(value = "/users/manage/{id}", method = RequestMethod.PUT)
    public void updateUser(@RequestBody Users user, @PathVariable Integer id) {
        Users savedUser = null;
        if (user != null) {
            savedUser = usersService.getById(id).orElse(null);
            if (savedUser != null) {
                String username = user.getUsername();
                if (username != null && !(username.equals(savedUser.getUsername()))) {
                    savedUser.setUsername(username.toLowerCase(Locale.ROOT));
                }
                boolean enabled = user.isEnabled();
                if (!(enabled == savedUser.isEnabled())) {
                    savedUser.setEnabled(enabled);
                }
                String password = user.getPassword();
                if (password != null) {
                    String newPassword = Special.createPassword(password);
                    if (!(newPassword.equals(savedUser.getPassword()))) {
                        savedUser.setPassword(newPassword);
                    }
                }
                String email = user.getEmail();
                if (email != null && !(email.equals(savedUser.getEmail()))) {
                    savedUser.setEmail(email);
                }
                String name = user.getName();
                if (name != null && !(name.equals(savedUser.getName()))) {
                    savedUser.setName(user.getName());
                }

                savedUser = usersService.save(savedUser);
                List<Roles> savedRoles = rolesService.getByUserId(id);
                for (Roles role : user.getRoles()) {
                    role.setUsername(username.toLowerCase(Locale.ROOT))
                            .setUser(savedUser);
                    if (!savedRoles.contains(role)) {
                        rolesService.save(role);
                    }
                    rolesService.delete(role);
                }
            }
        }
    }
}
