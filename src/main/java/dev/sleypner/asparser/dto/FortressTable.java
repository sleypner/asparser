package dev.sleypner.asparser.dto;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.FortressSkill;
import dev.sleypner.asparser.domain.model.Server;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
    private Server server;
    private List<FortressSkill> skills;
    private LocalDateTime updatedDate;
    private Clan clan;
    private long coffer;
    private int holdTime;

}
