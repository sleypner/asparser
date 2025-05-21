package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CustomOidcUser implements OidcUser {
    private final OidcUser oidcUser;
    private final String clientName;
    private Map<String, Object> attributes;

    public CustomOidcUser(OidcUser oidcUser, String clientName) {
        this.oidcUser = oidcUser;
        this.clientName = clientName;
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {

        Map<String, Object> customAttributes = new HashMap<>();
        Map<String, Object> attributes = oidcUser.getAttributes();
        System.out.println();
        customAttributes.put("name", attributes.get("given_name"));
        customAttributes.put("external_id", attributes.get("sub"));
        customAttributes.put("login", attributes.get("email"));
        customAttributes.put("last_name", attributes.get("family_name"));
        customAttributes.put("sex", attributes.get(""));
        customAttributes.put("email", attributes.get("email"));
        customAttributes.put("birthday", attributes.get(""));
        customAttributes.put("phone", "");
        customAttributes.put("client_id", attributes.get(""));
        this.attributes = customAttributes;

        return customAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public String getName() {
        return oidcUser.getFullName();
    }
}
