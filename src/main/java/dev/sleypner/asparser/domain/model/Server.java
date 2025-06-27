package dev.sleypner.asparser.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Table(name = "servers")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(callSuper = true)
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "external_id", unique = true, nullable = false)
    private Integer externalId;
    @Column(nullable = false)
    private String name;
    private String rates;
    private String status;
    @OneToMany(mappedBy = "server")
    @ToString.Exclude
    private Set<OnlineStatus> onlineStatuses;
    @OneToMany(mappedBy = "server")
    @ToString.Exclude
    private Set<Event> events;
    @OneToMany(mappedBy = "server")
    @ToString.Exclude
    private Set<RaidBoss> raidBosses;


}
