package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@Setter
public class CustomOauth2User implements OAuth2User {

    private final OAuth2User oauth2User;
    private final String clientName;
    private Map<String, Object> attributes;

    public CustomOauth2User(OAuth2User oauth2User, String clientName, Map<String, Object> attributes) {
        this.oauth2User = oauth2User;
        this.clientName = clientName;
        this.attributes = attributes;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oauth2User.getAuthorities();
    }

    @Override
    public String getName() {
        return this.attributes.get("name").toString();
    }
}
