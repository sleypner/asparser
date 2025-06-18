package dev.sleypner.asparser.config;

import dev.sleypner.asparser.domain.model.Role;
import dev.sleypner.asparser.domain.model.User;
import dev.sleypner.asparser.domain.model.UserActionLog;
import dev.sleypner.asparser.service.core.auth.log.UserActionLogsService;
import dev.sleypner.asparser.service.core.auth.user.UsersService;
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
        Optional<User> optUser = usersService.getByUsername(username);
        optUser.orElseThrow(() -> new UsernameNotFoundException(username));

        User user = optUser.get();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        UserActionLog userAction = UserActionLog.getAction(user, request, "login");
        userActionLogsService.save(userAction);

        Collection<? extends GrantedAuthority> authorities = loadUserAuthorities(user.getRoles());
        Map<String, Object> attributes = user.getAttributes();

        return new CustomUser(username, user.getPassword(), user.isEnabled(), authorities, attributes);
    }

    private Collection<? extends GrantedAuthority> loadUserAuthorities(Set<Role> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (roles != null) {
            for (Role role : roles) {
                authorities.add(new CustomAuthority(role.getRole()));
            }
        }
        return authorities;
    }

}
