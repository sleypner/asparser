package com.sleypner.parserarticles.model.source.other;

import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.FortressSkills;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FortressTable implements Serializable {
    private int id;
    private String name;
    private String server;
    private List<FortressSkills> skills;
    private LocalDateTime updatedDate;
    private Clan clan;
    private long coffer;
    private int holdTime;

}
