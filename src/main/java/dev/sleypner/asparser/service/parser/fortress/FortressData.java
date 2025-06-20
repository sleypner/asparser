package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.FortressHistory;
import dev.sleypner.asparser.domain.model.FortressSkill;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class FortressData {
    private Fortress fortress;
    private FortressHistory fortressHistory;
    private Clan clan;
    private Set<FortressSkill> fortressSkills = new HashSet<>();
    private String clanUrl;
}

