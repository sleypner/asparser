package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersService usersService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users savedUser = usersService.getByUsername(username);
        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(savedUser.getRoles());
        Map<String, Object> attributes = getAttributes(savedUser);
        CustomUser user = new CustomUser(username, savedUser.getPassword(), savedUser.isEnabled(), authorities, attributes);
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

    private Map<String, Object> getAttributes(Users user) {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", user.getId());
        attributes.put("name", user.getName());
        attributes.put("username", user.getUsername());
        attributes.put("email", user.getEmail());
        attributes.put("oauth", user.getOauth());
        attributes.put("externalId", user.getExternalId());
        attributes.put("provider", user.getProvider());
        return attributes;
    }

}
