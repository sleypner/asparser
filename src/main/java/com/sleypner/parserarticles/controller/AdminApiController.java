package com.sleypner.parserarticles.controller;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.parsing.Special;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminApiController {
    UsersService usersService;
    RolesService rolesService;

    AdminApiController(UsersService usersService, RolesService rolesService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
    }

    @GetMapping(value = "/users/manage", produces = "application/json")
    public List<Users> getUsers(@RequestParam(name = "search", required = false) String search,
                                @RequestParam(name = "page", required = false) int page) {
        List<Users> usersList = new ArrayList<>();

        if (search == null || search.isEmpty()) {
            usersList = usersService.getAll();
            for (Users user : usersList) {
                for (Roles role : user.getRoles()) {
                    role.setUser(null);
                }
            }
        } else {
            usersList = usersService.search(search);
            for (Users user : usersList) {
                for (Roles role : user.getRoles()) {
                    role.setUser(null);
                }
            }
        }
        return usersList;
    }

    @GetMapping(value = "/users/manage/{id}/edit", produces = "application/json")
    public Users editUser(@PathVariable int id) {


        Users user = usersService.getById(id);
        for (Roles role : user.getRoles()) {
            role.setUser(null);
        }
        System.out.println(user);
//        for (Users user : usersList) {
//            user.setRoles(new HashSet<>(rolesService.getByUserId(user.id)));
//        }

        return user;
    }

    @RequestMapping(value = "/users/manage/add", method = RequestMethod.POST)
    public ResponseEntity<?> addUser(@RequestBody Users user) {

        Users savedUser = null;
        if (user != null) {
            try {
                savedUser = usersService.getByUsername(user.getUsername());
                if (savedUser != null) {
                    return ResponseEntity.badRequest().body("User already exists");
                }
                Set<Roles> rolesSet = user.getRoles();
                user.setRoles(new HashSet<>());
                savedUser = usersService.save(user);
                for (Roles role : rolesSet) {
                    role.setUsername(user.getUsername().toLowerCase(Locale.ROOT));
                    role.setUser(savedUser);
                    rolesService.save(role);
                }
                return ResponseEntity.ok(savedUser);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.badRequest().body("Something went wrong");
    }

    @RequestMapping(value = "/users/manage/{id}", produces = "application/json", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable int id) {

        rolesService.deleteByUserId(id);
        usersService.delete(id);

    }

    @RequestMapping(value = "/users/manage/{id}", method = RequestMethod.PUT)
    public void updateUser(@RequestBody Users user, @PathVariable int id) {

        System.out.println(user);
        Users savedUser = null;
        if (user != null) {
            savedUser = usersService.getById(id);
            if (savedUser != null) {
                String username = user.getUsername();
                if (username != null && !(username == savedUser.getUsername())) {
                    savedUser.setUsername(username.toLowerCase(Locale.ROOT));
                }
                boolean enabled = user.isEnabled();
                if (!(enabled == savedUser.isEnabled())) {
                    savedUser.setEnabled(enabled);
                }
                String password = user.getPassword();
                if (password != null) {
                    String newPassword = Special.createPassword(password);
                    if (!(newPassword == savedUser.getPassword())) {
                        savedUser.setPassword(newPassword);
                    }
                }
                String email = user.getEmail();
                if (email != null && !(email == savedUser.getEmail())) {
                    savedUser.setEmail(email);
                }
                String name = user.getName();
                if (name != null && !(name == savedUser.getName())) {
                    savedUser.setName(user.getName());
                }

                savedUser = usersService.save(savedUser);
                List<Roles> savedRoles = rolesService.getByUserId(id);
                for (Roles role : user.getRoles()) {
                    role.setUsername(username.toLowerCase(Locale.ROOT));
                    role.setUser(savedUser);
                    if (!savedRoles.contains(role)) {
                        rolesService.save(role);
                    }
                    rolesService.delete(role);
                }
            }
        }

    }
}
