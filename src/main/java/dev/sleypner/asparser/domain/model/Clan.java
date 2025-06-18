package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "clans")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class Clan extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "image", columnDefinition = "BLOB")
    private byte[] image;
    @Column(name = "server")
    private String server;
    @Column(name = "level")
    private short level;
    @Column(name = "leader")
    private String leader;
    @Column(name = "players_count")
    private short playersCount;
    @Column(name = "castle")
    private String castle;
    @Column(name = "reputation")
    private int reputation;
    @Column(name = "alliance")
    private String alliance;

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
        Clan clan = (Clan) o;
        return getId() != null && Objects.equals(getId(), clan.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
