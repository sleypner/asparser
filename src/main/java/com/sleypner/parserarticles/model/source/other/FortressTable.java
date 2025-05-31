package com.sleypner.parserarticles.model.source.other;

import com.sleypner.parserarticles.model.source.entityes.Clan;
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
    private Clan clan;
    private long coffer;
    private int holdTime;

    public FortressTable(String name, String server,
                         List<FortressSkills> skills, LocalDateTime updatedDate,
                         Clan clan, long coffer, int holdTime) {
        this.name = name;
        this.server = server;
        this.skills = skills;
        this.updatedDate = updatedDate;
        this.clan = clan;
        this.coffer = coffer;
        this.holdTime = holdTime;
    }
}
