package com.sleypner.parserarticles.security;

import com.sleypner.parserarticles.model.services.RolesService;
import com.sleypner.parserarticles.model.services.UsersService;
import com.sleypner.parserarticles.model.source.entityes.Roles;
import com.sleypner.parserarticles.model.source.entityes.Users;
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

import java.util.*;

@Service
public class CustomOauth2UserService {
    RolesService rolesService;
    UsersService usersService;
    Environment environment;

    public CustomOauth2UserService(RolesService rolesService, UsersService usersService, Environment environment) {
        this.rolesService = rolesService;
        this.usersService = usersService;
        this.environment = environment;
    }

    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return (userRequest) -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            Map<String, Object> attributes = oAuth2User.getAttributes();
            Collection<? extends GrantedAuthority> authorities = oAuth2User.getAuthorities();
            CustomOauth2User customOauth2User = new CustomOauth2User(oAuth2User, userRequest.getClientRegistration().getClientName());
            saveOauth2Users(userRequest, authorities, attributes);
            return customOauth2User;
        };
    }

    public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
        final OidcUserService delegate = new OidcUserService();

        return (userRequest) -> {

            OidcUser oidcUser = delegate.loadUser(userRequest);
            Map<String, Object> attributes = oidcUser.getAttributes();
            Collection<? extends GrantedAuthority> authorities = oidcUser.getAuthorities();
            CustomOidcUser customOidcUser = new CustomOidcUser(oidcUser, userRequest.getClientRegistration().getClientName());
            saveOauth2Users(userRequest, authorities, attributes);
            return customOidcUser;
        };
    }

    private void saveOauth2Users(OAuth2UserRequest request, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        String token = request.getAccessToken().getTokenValue();
        String providerId = request.getClientRegistration().getRegistrationId();
        String externalId = "";
        String username = "";
        String email = "";
        switch (providerId) {
            case "google" -> {
                externalId = (String) attributes.get("sub");
                username = (String) attributes.get("given_name");
                email = (String) attributes.get("email");

            }
            case "discord" -> {
                externalId = (String) attributes.get("id");
                username = (String) attributes.get("username");
                email = (String) attributes.get("email");

            }
            case "yandex" -> {
                externalId = (String) attributes.get("id");
                username = (String) attributes.get("login");
                ArrayList emails = (ArrayList) attributes.get("emails");
                email = (String) emails.getFirst();
            }
        }
        Users user = usersService.getByUsername(username);
        if (user == null) {
            user = new Users();
            user.setUsername(username.toLowerCase());
        }
        user.setEmail(email);
        user.setName(username);
        user.setEnabled(true);
        user.setOauth(1);
        user.setToken(token);
        user.setExternalId(externalId);
        user.setProvider(providerId);
        Users resultUser = usersService.save(user);
        Set<Roles> roles = user.getRoles();
        String finalUsername = username;
        int userId = user.getId();
        authorities.forEach(role -> {
            if (role instanceof OAuth2UserAuthority) {
                Roles rolesOnSave = new Roles(finalUsername, role.getAuthority(), resultUser);
                List<Roles> savedRoles = rolesService.getByUserId(userId);
                Optional<Roles> savedRole = savedRoles.stream().filter(obj -> rolesOnSave.equals(obj)).findFirst();
                if (!savedRole.isPresent()) {
                    rolesService.save(rolesOnSave);
                }
            }
        });
    }
}
