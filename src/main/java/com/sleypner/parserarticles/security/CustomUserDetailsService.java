package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.UserActionLogsService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import com.sleypner.parserarticles.model.source.entityes.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static com.sleypner.parserarticles.special.Special.getAction;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserActionLogsService userActionLogsService;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users savedUser = usersService.getByUsername(username);
        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(savedUser.getRoles());
        Map<String, Object> attributes = savedUser.getAttributes();
        CustomUser user = new CustomUser(username, savedUser.getPassword(), savedUser.isEnabled(), authorities, attributes);

        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        UserActionLogs userAction = getAction(request);
        userAction.setActionType("login");
        userAction.setUser(savedUser);
        userActionLogsService.save(userAction);

        if(user != null && user.isEnabled()) {
            return user;
        }
        else {
            throw new UsernameNotFoundException("username not found");
        }
    }

    private Collection<? extends GrantedAuthority> loadUserAuthorities(Set<Roles> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            for (Roles role : roles) {
                authorities.add(new CustomAuthority(role.getRole()));
            }
        }
        return authorities;
    }

}
