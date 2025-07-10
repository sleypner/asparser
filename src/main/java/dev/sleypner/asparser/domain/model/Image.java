package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "images", uniqueConstraints = @UniqueConstraint(columnNames = "externalName"))
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class Image extends AuditableEntity {

    public static final String UNIQUE_FIELD_NAME = "externalName";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private UUID uuid;
    private String name;
    @Column(unique = true)
    private String externalName;
    private String extension;
    private String path;
    private String dir;
    private String externalUri;
    @OneToOne
    @JoinColumn(name = "fortress_skill_id")
    @ToString.Exclude
    private FortressSkill fortressSkill;
    @OneToOne
    @JoinColumn(name = "clan_id")
    @ToString.Exclude
    private Clan clan;
    @OneToMany(mappedBy = "image")
    @ToString.Exclude
    private Set<Fortress> fortresses;

    @PrePersist
    private void onCreate() {
        super.setCreatedAt();
    }

    @PreUpdate
    private void onUpdate() {
        super.setUpdatedAt();
    }
}
