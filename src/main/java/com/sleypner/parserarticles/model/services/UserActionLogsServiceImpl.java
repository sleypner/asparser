package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserActionLogsServiceImpl implements UserActionLogsService {

    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public UserActionLogsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Transactional
    @Override
    public UserActionLogs save(UserActionLogs userActionLogs) {
        return entityManager.merge(userActionLogs);
    }
}
