package com.sleypner.parserarticles.model.source.other;

import com.sleypner.parserarticles.model.source.entityes.FortressSkills;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class FortressTable implements Serializable {
    private int id;
    private String name;
    private String server;
    private List<FortressSkills> skills;
    private LocalDateTime updatedDate;
    private int clanId;
    private String clanName;
    private short level;
    private String leader;
    private short playersCount;
    private String castle;
    private int reputation;
    private String alliance;
    private long coffer;
    private int holdTime;

    public FortressTable(String name, String server,
                         List<FortressSkills> skills, LocalDateTime updatedDate,
                         int clanId, String clanName,
                         short level, String leader,
                         short playersCount, String castle,
                         int reputation, String alliance,
                         long coffer, int holdTime) {
        this.name = name;
        this.server = server;
        this.skills = skills;
        this.updatedDate = updatedDate;
        this.clanId = clanId;
        this.clanName = clanName;
        this.level = level;
        this.leader = leader;
        this.playersCount = playersCount;
        this.castle = castle;
        this.reputation = reputation;
        this.alliance = alliance;
        this.coffer = coffer;
        this.holdTime = holdTime;
    }
}
