package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "enabled", length = 1)
    private boolean enabled;
    @Column(name = "password", length = 68)
    private String password;
    @Column(name = "username", length = 50)
    private String username;
    @Column(name = "name", length = 50)
    private String name;
    @Column(name = "email", length = 50)
    private String email;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Roles> roles;
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
    @OneToMany(mappedBy = "user")
    private Set<UserActionLogs> userActionLogs;
    @Column(name = "two_factor_auth")
    private boolean twoFactorAuth = false;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime updatedDate;

    public Users(boolean enabled, String password, String username, String name) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.enabled = enabled;
        this.password = password;
        this.username = username;
        this.name = name;
    }
    public Users(){
        this.createdDate = LocalDateTime.now().withNano(0);
    }

    public Map<String,Object> getAttributes(){
        Map<String,Object> attributes = new HashMap<>();
        attributes.put("id",id);
        attributes.put("enabled",enabled);
        attributes.put("password",password);
        attributes.put("username",username);
        attributes.put("name",name);
        attributes.put("email",email);
        attributes.put("roles",roles);
        attributes.put("oauth",oauth);
        attributes.put("token",token);
        attributes.put("external_id",externalId);
        attributes.put("provider",provider);
        attributes.put("first_name",firstName);
        attributes.put("last_name",lastName);
        attributes.put("last_login",lastLogin);
        attributes.put("birthday",birthday);
        attributes.put("gender",gender);
        attributes.put("phone",phone);
        attributes.put("language",language);
        attributes.put("about",about);
        attributes.put("img",img);
        attributes.put("user_action_logs",userActionLogs);
        attributes.put("two_factor_auth",twoFactorAuth);
        attributes.put("created_date",createdDate);
        attributes.put("updated_date",updatedDate);
        return attributes;
    }

}
