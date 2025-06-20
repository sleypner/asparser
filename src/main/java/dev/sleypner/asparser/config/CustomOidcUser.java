package dev.sleypner.asparser.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class CustomOidcUser implements OidcUser {
    private final OidcUser oidcUser;
    private final String clientName;
    private Map<String, Object> attributes;

    public CustomOidcUser(OidcUser oidcUser, String clientName, Map<String, Object> attributes) {
        this.oidcUser = oidcUser;
        this.clientName = clientName;
        this.attributes = attributes;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return oidcUser.getAuthorities();
    }

    @Override
    public String getName() {
        return oidcUser.getFullName();
    }
}
