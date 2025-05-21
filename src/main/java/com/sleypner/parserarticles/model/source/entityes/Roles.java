package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Roles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column(name = "username", length = 50, nullable = false)
    public String username;
    @Column(name = "role", length = 50, nullable = false)
    public String role;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public Roles(String username, String role, Users user) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.username = username.toLowerCase(Locale.ROOT);
        this.role = role;
        this.user = user;
    }
    public Roles(String role) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.role = role;
    }
    public Roles() {
        this.createdDate = LocalDateTime.now().withNano(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Roles)) {
            return false;
        }
        Roles rolesObj = (Roles) obj;
        return Objects.equals(rolesObj.getRole(), this.getRole()) &&
                Objects.equals(rolesObj.getUsername(), this.getUsername());
    }

    public void setUsername(String username) {
        this.username = username.toLowerCase(Locale.ROOT);
    }
}
