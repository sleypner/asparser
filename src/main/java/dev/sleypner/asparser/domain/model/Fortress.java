package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
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
    @Column(name = "server")
    private String server;
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "fortress_and_skills",
            joinColumns = @JoinColumn(name = "fortress_id"),
            inverseJoinColumns = @JoinColumn(name = "fortress_skills_id"))
    @Builder.Default
    @ToString.Exclude
    private Set<FortressSkill> skills = new HashSet<>();

    public void setSkill(FortressSkill skills) {
        if (this.skills == null) {
            this.skills = new HashSet<>();
        }
        this.skills.add(skills);
    }

    public void setSkillAll(Set<FortressSkill> skills) {
        if (this.skills == null) {
            this.skills = new HashSet<>();
        }
        this.skills.addAll(skills);
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

