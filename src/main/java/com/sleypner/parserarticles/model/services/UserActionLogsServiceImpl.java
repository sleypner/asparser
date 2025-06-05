package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public class UserActionLogsServiceImpl implements UserActionLogsService {

    @PersistenceContext
    private final EntityManager entityManager;
    @Autowired
    public UserActionLogsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    @Override
    public UserActionLogs save(UserActionLogs userActionLogs) {
        return entityManager.merge(userActionLogs);
    }
}
