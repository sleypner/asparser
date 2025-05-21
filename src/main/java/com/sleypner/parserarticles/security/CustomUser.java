package com.sleypner.parserarticles.security;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Setter
@Getter
public class CustomUser implements UserDetails, CredentialsContainer {

    private static final Log logger = LogFactory.getLog(CustomUser.class);

    private String password;

    private final String username;

    private final String name;

    private final Set<GrantedAuthority> authorities;

    private final boolean accountNonExpired;

    private final boolean accountNonLocked;

    private final boolean credentialsNonExpired;

    private final boolean enabled;

    private final Map<String, Object> attributes;

    public CustomUser(String username, String password,Boolean enabled, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this(username, password, enabled, true, true, true, authorities, attributes);
    }

    public CustomUser(String username, String password, boolean enabled, boolean accountNonExpired,
                      boolean credentialsNonExpired, boolean accountNonLocked,
                      Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.name = attributes.get("name").toString();
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        this.username = username;
        this.password = password;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    private static SortedSet<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {

        SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());
        for (GrantedAuthority grantedAuthority : authorities) {
            sortedAuthorities.add(grantedAuthority);
        }
        return sortedAuthorities;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CustomUser user) {
            return this.username.equals(user.getUsername());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.username.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getName()).append(" [");
        sb.append("Username=").append(this.username).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Enabled=").append(this.enabled).append(", ");
        sb.append("AccountNonExpired=").append(this.accountNonExpired).append(", ");
        sb.append("CredentialsNonExpired=").append(this.credentialsNonExpired).append(", ");
        sb.append("AccountNonLocked=").append(this.accountNonLocked).append(", ");
        sb.append("Granted Authorities=").append(this.authorities).append("], ");
        sb.append("User Attributes=").append(this.attributes).append("]");
        return sb.toString();
    }

    public static UserBuilder withUsername(String username) {
        return builder().username(username);
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static UserBuilder withDefaultPasswordEncoder() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        return builder().passwordEncoder(encoder::encode);
    }

    public static UserBuilder withUserDetails(UserDetails userDetails) {
        return withUsername(userDetails.getUsername())
                .password(userDetails.getPassword())
                .accountExpired(!userDetails.isAccountNonExpired())
                .accountLocked(!userDetails.isAccountNonLocked())
                .authorities(userDetails.getAuthorities())
                .credentialsExpired(!userDetails.isCredentialsNonExpired())
                .disabled(!userDetails.isEnabled());
    }

    private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {


        @Override
        public int compare(GrantedAuthority g1, GrantedAuthority g2) {
            if (g2.getAuthority() == null) {
                return -1;
            }
            if (g1.getAuthority() == null) {
                return 1;
            }
            return g1.getAuthority().compareTo(g2.getAuthority());
        }

    }

    public static final class UserBuilder {

        private String username;

        private String password;

        private List<GrantedAuthority> authorities = new ArrayList<>();

        private boolean accountExpired;

        private boolean accountLocked;

        private boolean credentialsExpired;

        private boolean disabled;

        private Map<String, Object> attributes;


        private Function<String, String> passwordEncoder = (password) -> password;

        private UserBuilder() {
        }

        public UserBuilder username(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder password(String password) {
            this.password = password;
            return this;
        }

        public UserBuilder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public UserBuilder passwordEncoder(Function<String, String> encoder) {
            this.passwordEncoder = encoder;
            return this;
        }

        public UserBuilder roles(String... roles) {
            List<GrantedAuthority> authorities = new ArrayList<>(roles.length);
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
            return authorities(authorities);
        }

        public UserBuilder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        public UserBuilder att(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = new ArrayList<>(authorities);
            return this;
        }

        public UserBuilder authorities(String... authorities) {
            return authorities(AuthorityUtils.createAuthorityList(authorities));
        }

        public UserBuilder accountExpired(boolean accountExpired) {
            this.accountExpired = accountExpired;
            return this;
        }

        public UserBuilder accountLocked(boolean accountLocked) {
            this.accountLocked = accountLocked;
            return this;
        }

        public UserBuilder credentialsExpired(boolean credentialsExpired) {
            this.credentialsExpired = credentialsExpired;
            return this;
        }

        public UserBuilder disabled(boolean disabled) {
            this.disabled = disabled;
            return this;
        }

        public UserDetails build() {
            String encodedPassword = this.passwordEncoder.apply(this.password);
            return new CustomUser(this.username, encodedPassword, !this.disabled, !this.accountExpired,
                    !this.credentialsExpired, !this.accountLocked, this.authorities, this.attributes);
        }

    }

}
