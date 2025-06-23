package dev.sleypner.asparser.config;

import dev.sleypner.asparser.domain.model.Role;
import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.service.core.auth.roles.RolesService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
import dev.sleypner.asparser.util.StringExtension;
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

        List<User> listUsers = usersService.getAll();

        User admin = listUsers.stream()
                .filter(u -> u.getUsername()
                        .equals("admin"))
                .findFirst()
                .orElse(null);
        if (admin == null) {
            // login: admin Password: admin123
            String username = environment.getProperty("admin.username");
            String password = environment.getProperty("admin.password");
            String newPassword = StringExtension.createPassword(password);
            User newUser = usersService.save(User.builder()
                    .enabled(true)
                    .username(username)
                    .password(newPassword)
                    .email("admin@admin.ru")
                    .name(StringExtension.capitalizeFirstLetter(username))
                    .build());

            String[] rolesArr = {"ROLE_USER", "ROLE_MANAGER", "ROLE_ADMIN"};
            for (String role : rolesArr) {
                rolesService.save(Role.builder()
                        .username(username)
                        .role(role)
                        .user(newUser)
                        .build());
            }
        }
    }
}
