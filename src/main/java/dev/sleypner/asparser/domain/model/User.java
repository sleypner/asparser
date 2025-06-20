package dev.sleypner.asparser.domain.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "enabled", length = 1)
    private boolean enabled;
    //    @NotBlank(message = "field password is blank")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$",
            message = "Password most be contains more then 8 characters, digit, uppercase and lowercase letters")
    @Column(name = "password", length = 68)
    private String password;
    @Column(name = "username", length = 50)
    @NotBlank(message = "field username is blank")
    @Size(min = 4, max = 32, message = "Name most be more then 4 and less then 32 characters")
    private String username;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "email", length = 50)
    @NotBlank(message = "field email is blank")
    @Email(message = "Please enter correct email address")
    private String email;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
//    @JsonDeserialize(using = RoleSetDeserializer.class)
    private Set<Role> roles;
    @Column(name = "oauth", length = 1)
    private Integer oauth;
    @Column(name = "token")
    private String token;
    @Column(name = "external_id")
    private String externalId;
    @Column(name = "provider")
    private String provider;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    @Column(name = "birthday")
    private LocalDateTime birthday;
    @Column(name = "gender")
    private String gender;
    @Column(name = "phone")
    private String phone;
    @Column(name = "language")
    private String language;
    @Column(name = "about")
    private String about;
    @Column(name = "img")
    private String img;
    @Column(name = "user_action_logs")
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<UserActionLog> userActionLogs;
    @Column(name = "two_factor_auth")
    @Builder.Default
    private boolean twoFactorAuth = false;


    public Map<String, Object> getAttributes() {
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("id", id);
        attributes.put("enabled", enabled);
        attributes.put("password", password);
        attributes.put("username", username);
        attributes.put("name", name);
        attributes.put("email", email);
        attributes.put("roles", roles);
        attributes.put("oauth", oauth);
        attributes.put("token", token);
        attributes.put("external_id", externalId);
        attributes.put("provider", provider);
        attributes.put("first_name", firstName);
        attributes.put("last_name", lastName);
        attributes.put("last_login", lastLogin);
        attributes.put("birthday", birthday);
        attributes.put("gender", gender);
        attributes.put("phone", phone);
        attributes.put("language", language);
        attributes.put("about", about);
        attributes.put("img", img);
        attributes.put("user_action_logs", userActionLogs);
        attributes.put("two_factor_auth", twoFactorAuth);
        attributes.put("created_date", getCreatedDate());
        attributes.put("updated_date", getUpdatedDate());
        return attributes;
    }

    @PrePersist
    private void onCreate() {
        super.setCreatedAt();
    }

    @PreUpdate
    private void onUpdate() {
        super.setUpdatedAt();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
