package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class CustomOauth2User extends DefaultOAuth2User implements OAuth2User {

    private final String username;
    private final String nameAttributeKey;

    public CustomOauth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, String username) {
        super(authorities, attributes, nameAttributeKey);
        this.username = username;
        this.nameAttributeKey = nameAttributeKey;
    }
}
