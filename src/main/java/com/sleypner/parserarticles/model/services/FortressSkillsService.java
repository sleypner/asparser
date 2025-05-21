package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.FortressSkills;

import java.util.List;

public interface FortressSkillsService {

    List<FortressSkills> getAll();

    void save(FortressSkills skill);

    FortressSkills update(FortressSkills skill);

    FortressSkills getById(int id);

    FortressSkills getByName(String name);
}
