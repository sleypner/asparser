package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;

@Getter
@Setter
public class CustomOidcUser extends DefaultOidcUser implements OidcUser {
    private final String username;

    public CustomOidcUser(OidcUser user, String username) {
        super(user.getAuthorities(), user.getIdToken(), user.getUserInfo());
        this.username = username;
    }

}
