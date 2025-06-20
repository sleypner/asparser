package dev.sleypner.asparser.service.core.auth.roles;

import dev.sleypner.asparser.domain.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class RolesServiceImpl implements RolesService {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public RolesServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Role> getAll() {
        TypedQuery<Role> query = entityManager.createQuery("FROM Role roles", Role.class);
        return query.getResultList();
    }

    @Override
    public Role save(Role role) {
        return entityManager.merge(role);
    }

    @Override
    public Set<Role> saveAll(Set<Role> roles) {
        Set<Role> savedRoles = new HashSet<Role>();
        roles.forEach(role -> {
            savedRoles.add(save(role));
        });
        return savedRoles;
    }

    @Override
    public List<Role> getByUserId(int id) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r " +
                        "WHERE r.user.id = :id", Role.class);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void delete(Role role) {
        entityManager.remove(role);
    }

    @Override
    public void deleteByUserId(int id) {
        entityManager.createQuery("DELETE FROM Role r WHERE r.user.id = :id");
    }
}
