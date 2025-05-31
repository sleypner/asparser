package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.FortressHistory;
import com.sleypner.parserarticles.model.source.entityes.Users;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FortressHistoryServiceImpl implements FortressHistoryService {
    @PersistenceContext
    private final EntityManager entityManager;
    private final FortressServiceImpl fortressService;

    @Autowired
    public FortressHistoryServiceImpl(EntityManager entityManager, FortressServiceImpl fortressService) {
        this.entityManager = entityManager;
        this.fortressService = fortressService;
    }

    @Override
    public List<FortressHistory> getAll() {
        TypedQuery<FortressHistory> query = entityManager.createQuery("SELECT f FROM FortressHistory f", FortressHistory.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public void save(FortressHistory fh) {
        entityManager.persist(fh);
    }

    @Transactional
    @Override
    public FortressHistory update(FortressHistory fh) {
        return entityManager.merge(fh);
    }

    @Transactional
    @Override
    public void delete(FortressHistory fh) {
        entityManager.remove(fh);
    }

    @Override
    public FortressHistory getById(int id) {
        TypedQuery<FortressHistory> query = entityManager.createQuery(
                "SELECT f FROM FortressHistory f WHERE f.id = :id", FortressHistory.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public FortressHistory getByFortressId(String fortressName) {
        TypedQuery<FortressHistory> query = entityManager.createQuery(
                "SELECT f FROM FortressHistory f WHERE f.fortressId = :id", FortressHistory.class);
        return null;
    }

    @Override
    public List<FortressHistory> getCurrentStatusOfForts() {
        TypedQuery<FortressHistory> query = entityManager.createQuery(
                "SELECT f FROM FortressHistory f ORDER BY f.createdDate DESC", FortressHistory.class);
        query.setMaxResults((int) fortressService.getCount());//5x21
        return query.getResultList();
    }

    @Override
    public List<FortressHistory> getByServer(String server) {
        TypedQuery<FortressHistory> query = entityManager.createQuery(
                "SELECT fh FROM FortressHistory fh " +
                        "LEFT JOIN Fortress f ON fh.fortressId=f.id " +
                        "WHERE f.server = :server", FortressHistory.class);
        query.setParameter("server", server);
        List<FortressHistory> fortressHistoryList = query.getResultList();
        if (fortressHistoryList.isEmpty()) {
            return null;
        }
        return fortressHistoryList;
    }
}
