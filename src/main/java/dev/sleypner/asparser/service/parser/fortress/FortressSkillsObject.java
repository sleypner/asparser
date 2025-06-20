package dev.sleypner.asparser.service.parser.fortress;

import dev.sleypner.asparser.domain.model.FortressSkill;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
public class FortressSkillsObject {
    private Set<FortressSkill> skills;
    private String server;
    private String fortName;

    public FortressSkillsObject(Set<FortressSkill> skills, String server, String fortName) {
        this.skills = skills;
        this.server = server;
        this.fortName = fortName;
    }
}
