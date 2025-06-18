package dev.sleypner.asparser.service.parser.event.persistence;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.service.parser.shared.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class EventPersistenceImpl implements EventPersistence, PersistenceManager<Event> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public EventPersistenceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Event getById(int id) {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.id = :id", Event.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public List<Event> getAll() {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e ORDER BY e.date DESC", Event.class);
        return query.getResultList();
    }

    @Override
    public Event save(Event event) {
        var i = 0;
        return em.merge(event);
    }

    @Override
    public List<Event> getByServer(String server) {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e WHERE e.server = :server ORDER BY e.date DESC", Event.class);
        query.setParameter("server", server);
        return query.getResultList();
    }

    @Override
    public Set<Event> save(Set<Event> events) {

        try {
            for (Event event : events) {
                em.persist(event);
            }
            em.flush();
        } catch (Exception e) {
            log.error("Failed to save events", e);
        }
        return events;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Event> getEntityClass() {
        return Event.class;
    }

    @Override
    public LocalDateTime getLastDate(String dateFieldName) {
        return PersistenceManager.super.getLastDate(dateFieldName);
    }
}
