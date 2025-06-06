package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.RaidBosses;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public class RaidBossesServiceImpl implements RaidBossesService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public RaidBossesServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public RaidBosses getById(int id) {
        TypedQuery<RaidBosses> query = entityManager.createQuery("SELECT b FROM RaidBosses b WHERE b.id = :id", RaidBosses.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public List<RaidBosses> getAll() {
        TypedQuery<RaidBosses> query = entityManager.createQuery("SELECT b FROM RaidBosses b ORDER BY b.date DESC", RaidBosses.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public RaidBosses save(RaidBosses boss) {
        return entityManager.merge(boss);
    }

    @Override
    public RaidBosses getByNameAndServer(String name, String server) {
        TypedQuery<RaidBosses> query = entityManager.createQuery("SELECT b FROM RaidBosses b WHERE b.name = :name AND b.server = :server", RaidBosses.class);
        query.setParameter("name", name);
        query.setParameter("server", server);

        List<RaidBosses> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public List<RaidBosses> getByServer(String server) {
        TypedQuery<RaidBosses> query = entityManager.createQuery("SELECT b FROM RaidBosses b WHERE b.server = :server ORDER BY b.date DESC", RaidBosses.class);
        query.setParameter("server", server);

        return query.getResultList();
    }

}
