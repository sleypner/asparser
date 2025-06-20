package dev.sleypner.asparser.service.core.auth.roles;

import dev.sleypner.asparser.domain.model.Role;

import java.util.List;
import java.util.Set;

public interface RolesService {
    List<Role> getAll();

    Role save(Role role);

    Set<Role> saveAll(Set<Role> roles);

    List<Role> getByUserId(int id);

    void delete(Role role);

    void deleteByUserId(int id);
}
