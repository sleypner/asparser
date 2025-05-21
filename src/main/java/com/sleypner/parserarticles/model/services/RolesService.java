package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Roles;

import java.util.List;
import java.util.Set;

public interface RolesService {
    List<Roles> getAll();

    Roles save(Roles roles);

    Set<Roles> saveAll(Set<Roles> roles);

    List<Roles> getByUserId(int id);

    void delete(Roles roles);

    void deleteByUserId(int id);
}
