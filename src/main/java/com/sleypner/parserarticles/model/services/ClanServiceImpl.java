package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Clan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ClanServiceImpl implements ClanService {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public ClanServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Clan> getAll() {
        TypedQuery<Clan> query = entityManager.createQuery("FROM Clan clan", Clan.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Clan save(Clan clan) {
        entityManager.persist(clan);
        return getByNameAndServer(clan.getName(), clan.getServer());
    }

    @Transactional
    @Override
    public Clan update(Clan clan) {
        return entityManager.merge(clan);
    }

    @Override
    public Clan getById(int id) {
        TypedQuery<Clan> query = entityManager.createQuery("SELECT c FROM Clan c WHERE c.id = :id", Clan.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Clan getByNameAndServer(String clanName, String serverName) {
        TypedQuery<Clan> query = entityManager.createQuery("SELECT c FROM Clan c WHERE c.name = :clanName AND c.server = :serverName", Clan.class);
        query.setParameter("clanName", clanName);
        query.setParameter("serverName", serverName);
        query.setMaxResults(1);
        List<Clan> clanList = query.getResultList();
        if (clanList.isEmpty()) {
            return null;
        }
        return query.getResultList().getFirst();
    }
}
