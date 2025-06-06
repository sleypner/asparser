package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Roles;
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
    public List<Roles> getAll() {
        TypedQuery<Roles> query = entityManager.createQuery("FROM Roles roles", Roles.class);
        return query.getResultList();
    }

    @Override
    public Roles save(Roles roles) {
        return entityManager.merge(roles);
    }

    @Override
    public Set<Roles> saveAll(Set<Roles> roles) {
        Set<Roles> savedRoles = new HashSet<Roles>();
        roles.forEach(role -> {
            savedRoles.add(save(role));
        });
        return savedRoles;
    }

    @Override
    public List<Roles> getByUserId(int id) {
        TypedQuery<Roles> query = entityManager.createQuery(
                "SELECT r FROM Roles r " +
                        "WHERE r.user.id = :id", Roles.class);
        query.setParameter("id", id);
        try {
            return query.getResultList();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public void delete(Roles roles) {
        entityManager.remove(roles);
    }

    @Override
    public void deleteByUserId(int id) {
        entityManager.createQuery("DELETE FROM Roles r WHERE r.user.id = :id");
    }
}
