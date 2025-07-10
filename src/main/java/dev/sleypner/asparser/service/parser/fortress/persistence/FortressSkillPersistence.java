package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.FortressSkill;

import java.util.List;
import java.util.Optional;

public interface FortressSkillPersistence {

    List<FortressSkill> getAll();

    FortressSkill save(FortressSkill skill);

    FortressSkill update(FortressSkill skill);

    FortressSkill getById(int id);

    Optional<FortressSkill> getByName(String name);
}
