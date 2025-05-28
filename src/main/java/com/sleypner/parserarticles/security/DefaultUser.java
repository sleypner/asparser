package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import com.sleypner.parserarticles.special.Special;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultUser {

    private static UsersService usersService;
    private static  RolesService rolesService;
    private static Environment environment;

    @Autowired
    public DefaultUser(UsersService usersService, RolesService rolesService, Environment environment) {
        this.usersService = usersService;
        this.rolesService = rolesService;
        this.environment = environment;
    }

    @Bean
    public static void createDefaultUserIfNotExist(){

        List<Roles> listRoles = rolesService.getAll();
        List<Users> listUsers = usersService.getAll();

        Users admin = listUsers.stream().filter(u -> u.getUsername().equals("admin")).findFirst().orElse(null);
        if(admin != null){
            admin.setName("Admin");
            usersService.save(admin);
        }
        if (listUsers.size() < 1 && listRoles.size() < 1){
            // login: admin Password: admin123
            String username = environment.getProperty("admin.username");
            String password = Special.createPassword(environment.getProperty("admin.password"));
            Users newUser = usersService.save(new Users(true,password,username,username));

            rolesService.save(new Roles("admin","ROLE_USER",newUser));
            rolesService.save(new Roles("admin","ROLE_MANAGER",newUser));
            rolesService.save(new Roles("admin","ROLE_ADMIN",newUser));
        }
    }
}
