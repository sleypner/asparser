package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Fortress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FortressServiceImpl implements FortressService {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public FortressServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Fortress> getAll() {
        TypedQuery<Fortress> query = entityManager.createQuery("SELECT f FROM Fortress f JOIN FETCH f.skills", Fortress.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Fortress save(Fortress fortress) {
        entityManager.persist(fortress);
        return getByNameAndServer(fortress.getName(), fortress.getServer());
    }

    @Transactional
    @Override
    public Fortress update(Fortress fortress) {
        return entityManager.merge(fortress);
    }

    @Transactional
    @Override
    public void delete(Fortress fortress) {
        entityManager.remove(fortress);
    }

    @Override
    public Fortress getById(int id) {
        TypedQuery<Fortress> query = entityManager.createQuery(
                "SELECT f FROM Fortress f " +
                        "JOIN FETCH f.skills " +
                        "WHERE f.id = :id", Fortress.class);
        query.setParameter("id", id);
        return query.getResultList().get(0);
    }

    @Override
    public Fortress getByNameAndServer(String fortressName, String serverName) {
        TypedQuery<Fortress> query = entityManager.createQuery("SELECT f FROM Fortress f WHERE f.name = :fortressName AND f.server = :serverName", Fortress.class);
        query.setParameter("fortressName", fortressName);
        query.setParameter("serverName", serverName);
        return query.getResultList().get(0);
    }

    @Override
    public long getCount() {
        return entityManager.createQuery("SELECT COUNT(f) FROM Fortress f", Long.class).getSingleResult();
    }
}
