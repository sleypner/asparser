package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.special.Special;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultUser {

    private final UsersService usersService;
    private final RolesService rolesService;
    private final Environment environment;

    @Bean
    public void createDefaultUserIfNotExist() {

        List<Users> listUsers = usersService.getAll();

        Users admin = listUsers.stream()
                .filter(u -> u.getUsername()
                        .equals("admin"))
                .findFirst()
                .orElse(null);
        if (admin == null) {
            // login: admin Password: admin123
            String username = environment.getProperty("admin.username");
            String password = Special.createPassword(environment.getProperty("admin.password"));
            Users newUser = usersService.save(Users.builder()
                    .enabled(true)
                    .username(username)
                    .password(password)
                    .email("admin@admin.ru")
                    .name(Special.capitalizeFirstLetter(username))
                    .build());

            String[] rolesArr = {"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"};
            for (String role : rolesArr) {
                rolesService.save(Roles.builder()
                        .username(username)
                        .role(role)
                        .user(newUser)
                        .build());
            }
        }
    }
}
