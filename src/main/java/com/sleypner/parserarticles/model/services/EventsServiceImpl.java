package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Events;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class EventsServiceImpl implements EventsService {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public EventsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Events getById(int id) {
        TypedQuery<Events> query = entityManager.createQuery("SELECT e FROM Events e WHERE e.id = :id", Events.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public List<Events> getAll() {
        TypedQuery<Events> query = entityManager.createQuery("SELECT e FROM Events e ORDER BY e.date DESC", Events.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public Events save(Events event) {
        var i = 0;
        return entityManager.merge(event);
    }

    @Override
    public LocalDateTime getLastEntryDate() {
        TypedQuery<Events> query = entityManager.createQuery("SELECT e FROM Events e ORDER BY e.date DESC", Events.class);
        List<Events> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.getFirst().getDate();
        } else {
            return null;
        }
    }

    @Override
    public List<Events> getByServer(String server) {
        TypedQuery<Events> query = entityManager.createQuery("SELECT e FROM Events e WHERE e.server = :server ORDER BY e.date DESC", Events.class);
        query.setParameter("server", server);
        return query.getResultList();
    }
}
