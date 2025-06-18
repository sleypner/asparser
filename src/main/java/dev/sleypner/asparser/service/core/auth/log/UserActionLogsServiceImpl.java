package dev.sleypner.asparser.service.core.auth.log;

import dev.sleypner.asparser.domain.model.UserActionLog;
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
    public UserActionLog save(UserActionLog userActionLog) {
        return entityManager.merge(userActionLog);
    }
}
