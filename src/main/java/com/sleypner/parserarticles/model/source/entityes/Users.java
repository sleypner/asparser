package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name = "enabled", length = 1)
    public boolean enabled;
    @Column(name = "password", length = 68)
    public String password;
    @Column(name = "username", length = 50)
    public String username;
    @Column(name = "name", length = 50)
    public String name;
    @Column(name = "email", length = 50)
    public String email;
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Roles> roles;
    @Column(name = "oauth", length = 1)
    public Integer oauth;
    @Column(name = "token")
    public String token;
    @Column(name = "external_id")
    public String externalId;
    @Column(name = "provider")
    public String provider;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

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

}
