package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "fortresses")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class Fortress extends AuditableEntity implements Comparable<Fortress> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @JoinColumn(name = "image_id")
    @ManyToOne
    @ToString.Exclude
    private Image image;
    @JoinColumn(name = "server_id")
    @ManyToOne()
    @ToString.Exclude
    private Server server;
    @ManyToMany
    @JoinTable(
            name = "fortress_and_skills",
            joinColumns = @JoinColumn(name = "fortress_id"),
            inverseJoinColumns = @JoinColumn(name = "fortress_skills_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"fortress_id", "fortress_skills_id"})
    )
    @ToString.Exclude
    private Set<FortressSkill> skills;

    @PrePersist
    private void onCreate() {
        super.setCreatedAt();
    }

    @PreUpdate
    private void onUpdate() {
        super.setUpdatedAt();
    }

    @Override
    public int compareTo(Fortress o) {
        return getUpdatedDate().compareTo(o.getUpdatedDate());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Fortress fortress = (Fortress) o;
        return getId() != null && Objects.equals(getId(), fortress.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

