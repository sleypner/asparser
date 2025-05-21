package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultUser {

    private static UsersService usersService;
    private static  RolesService rolesService;

    @Autowired
    public DefaultUser(UsersService usersService, RolesService rolesService) {
        this.usersService = usersService;
        this.rolesService = rolesService;
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
            Users newUser = usersService.save(new Users(true,"{bcrypt}$2a$12$H.HsU9vyyXVcElZWMXRzuOb02M0N2SpWMs/lQkga/u/uIcZHzfxny","admin","Admin"));

            rolesService.save(new Roles("admin","ROLE_USER",newUser));
            rolesService.save(new Roles("admin","ROLE_MANAGER",newUser));
            rolesService.save(new Roles("admin","ROLE_ADMIN",newUser));
        }
    }
}
