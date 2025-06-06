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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersService usersService;
    @Autowired
    private UserActionLogsService userActionLogsService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> optUser = usersService.getByUsername(username);
        optUser.orElseThrow(() -> new UsernameNotFoundException(username));

        Users user = optUser.get();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        UserActionLogs userAction = UserActionLogs.getAction(user,request,"login");
        userActionLogsService.save(userAction);

        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(user.getRoles());
        Map<String, Object> attributes = user.getAttributes();

        return new CustomUser(username, user.getPassword(), user.isEnabled(), authorities, attributes);
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
