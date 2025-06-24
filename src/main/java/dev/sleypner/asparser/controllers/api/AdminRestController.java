package dev.sleypner.asparser.controllers.api;

import dev.sleypner.asparser.domain.model.Role;
import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.service.core.auth.roles.RolesService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
import dev.sleypner.asparser.util.StringExtension;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
        List<User> userList = new ArrayList<>();

        if (search == null) {
            userList = usersService.getAll();
        } else {
            userList = usersService.search(search);
            if (userList == null) {
                ResponseEntity.ok().body("No users found");
            }
        }
        return ResponseEntity.ok(userList);
    }

    @GetMapping(value = "/users/manage/{id}/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    public User editUser(@PathVariable int id) {

        return usersService.getById(id).orElse(null);
    }

    @RequestMapping(value = "/users/manage/add", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@Valid @RequestBody User user) {

        String log;
        Optional<User> optionalDbUser = usersService.getByUsername(user.getUsername());
        if (optionalDbUser.isPresent()) {
            log = "User already exists";
        } else {
            User savedUser = usersService.save(user);
            Set<Role> roleSet = user.getRoles();
            for (Role role : roleSet) {
                role.setUsername(user.getUsername().toLowerCase(Locale.ROOT))
                        .setUser(savedUser);
                rolesService.save(role);
            }
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
    public void updateUser(@RequestBody User user, @PathVariable Integer id) {
        User savedUser = null;
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
                    String newPassword = StringExtension.createPassword(password);
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
                List<Role> savedRoles = rolesService.getByUserId(id);
                for (Role role : user.getRoles()) {
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
