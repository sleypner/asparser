package com.sleypner.parserarticles.model.source.entityes;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "clans")
public class Clan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "image", columnDefinition="BLOB")
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
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public Clan(String name,String server){
        this.name = name;
        this.server = server;
        this.updatedDate = LocalDateTime.now().withNano(0);
    }

    public Clan(String name,
                byte[] image,
                String server,
                Short level,
                String leader,
                Short playersCount,
                String castle,
                Integer reputation,
                String alliance) {
        this.name = name;
        this.image = image;
        this.server = server;
        this.level = level;
        this.leader = leader;
        this.playersCount = playersCount;
        this.castle = castle;
        this.reputation = reputation;
        this.alliance = alliance;
        this.updatedDate = LocalDateTime.now().withNano(0);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Clan)){
            return false;
        }
        Clan clanObj = (Clan) obj;
        return Objects.equals(clanObj.getName(), this.getName()) &&
                Objects.equals(clanObj.getServer(), this.getServer());
    }
}
