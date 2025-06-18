package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.FortressSkill;

import java.util.List;

public interface FortressSkillsPersistence {

    List<FortressSkill> getAll();

    FortressSkill save(FortressSkill skill);

    FortressSkill update(FortressSkill skill);

    FortressSkill getById(int id);

    FortressSkill getByName(String name);
}
