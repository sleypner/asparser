package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UserActionLogsService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import com.sleypner.parserarticles.model.source.entityes.Users;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ua_parser.Client;
import ua_parser.Parser;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sleypner.parserarticles.special.Special.getAction;

@Service
public class CustomOauth2UserService extends DefaultOAuth2UserService {
    RolesService rolesService;
    UsersService usersService;
    UserActionLogsService userActionLogsService;
    Environment environment;

    public CustomOauth2UserService(RolesService rolesService, UsersService usersService, Environment environment, UserActionLogsService userActionLogsService) {
        this.rolesService = rolesService;
        this.usersService = usersService;
        this.environment = environment;
        this.userActionLogsService = userActionLogsService;
    }

    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return (userRequest) -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            Map<String, Object> attributes = oAuth2User.getAttributes();
            Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();

            UserActionLogs userAction = getAction(request);
            Users user = saveOauth2Users(userRequest, authorities, attributes, userAction);


            return new CustomOauth2User(
                    oAuth2User,
                    userRequest.getClientRegistration().getClientName(),
                    user.getAttributes()
            );
        };
    }

    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {

            OidcUser oidcUser = delegate.loadUser(userRequest);
            Map<String, Object> attributes = oidcUser.getAttributes();
            Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();

            HttpServletRequest request =
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            UserActionLogs userAction = getAction(request);
            Users user = saveOauth2Users(userRequest, authorities, attributes, userAction);

            return new CustomOidcUser(
                    oidcUser,
                    userRequest.getClientRegistration().getClientName(),
                    user.getAttributes()
            );
        };
    }

    private Users saveOauth2Users(OAuth2UserRequest request,
                                  Collection<? extends GrantedAuthority> authorities,
                                  Map<String, Object> attributes,
                                  UserActionLogs userAction) {
        String token = request.getAccessToken().getTokenValue();
        String providerId = request.getClientRegistration().getRegistrationId();

        Users user = new Users();
        switch (providerId) {
            case "google" -> {
                String username = (String) attributes.get("given_name");

                user = prepaerUsers(username.toLowerCase(), authorities, userAction);

                user.setExternalId((String) attributes.get("sub"));
                user.setEmail((String) attributes.get("email"));
                user.setName((String) attributes.get("given_name"));
                user.setFirstName((String) attributes.get("given_name"));
                user.setLastName((String) attributes.get("family_name"));
                user.setImg((String) attributes.get("picture"));
            }
            case "discord" -> {
                String username = (String) attributes.get("username");

                user = prepaerUsers(username.toLowerCase(), authorities, userAction);

                user.setExternalId((String) attributes.get("id"));
                user.setEmail((String) attributes.get("email"));
                user.setName((String) attributes.get("global_name"));
                user.setImg((String) attributes.get("avatar"));
            }
            case "yandex" -> {
                String username = (String) attributes.get("login");

                user = prepaerUsers(username.toLowerCase(), authorities, userAction);

                user.setExternalId((String) attributes.get("id"));
                user.setEmail((String) attributes.get("default_email"));
                user.setName((String) attributes.get("login"));
                user.setFirstName((String) attributes.get("first_name"));
                user.setLastName((String) attributes.get("last_name"));
                String birthday = (String) attributes.get("birthday");
                if (birthday != "") {
                    user.setBirthday(LocalDateTime.parse(birthday));
                }
                user.setGender((String) attributes.get("gender"));
                Map<String, Object> phones = (Map<String, Object>) attributes.get("default_phone");
                user.setPhone((String) phones.get("number"));
                user.setImg((String) attributes.get("default_avatar_id"));
            }
        }
        user.setEnabled(true);
        user.setOauth(1);
        user.setToken(token);
        user.setProvider(providerId);
        user = usersService.save(user);
        for (GrantedAuthority authority : authorities) {
            if (authority instanceof OAuth2UserAuthority) {
                List<Roles> savedRoles = rolesService.getByUserId(user.getId());
                if (savedRoles.isEmpty()) {
                    rolesService.save(new Roles(user.getUsername(), authority.getAuthority(), user));
                }
            }
        }

        userAction.setUser(user);
        userAction.setActionType("oauth2 login");
        userActionLogsService.save(userAction);

        return user;
    }

    public Users prepaerUsers(String username, Collection<? extends GrantedAuthority> authorities, UserActionLogs userAction) {
        return usersService.getOptionalByUsername(username).orElseGet(() -> {
            Users newUser = new Users();
            newUser.setUsername(username);

            newUser.setUserActionLogs(Set.of(userAction));
            return newUser;
        });
    }
}
