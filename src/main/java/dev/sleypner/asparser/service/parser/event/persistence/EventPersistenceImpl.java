package dev.sleypner.asparser.service.parser.event.persistence;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.service.parser.shared.DateRepository;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import dev.sleypner.asparser.util.StringExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class EventPersistenceImpl implements EventPersistence, RepositoryManager<Event>, DateRepository<Event> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public EventPersistenceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Event> getAll() {
        TypedQuery<Event> query = em.createQuery("SELECT e FROM Event e ORDER BY e.date DESC", getEntityClass());
        return query.getResultList();
    }

    @Override
    public Event save(Event event) {
        var i = 0;
        return em.merge(event);
    }

    @Override
    public List<Event> getByServer(String server) {
        TypedQuery<Event> query = em.createQuery(
                "SELECT e FROM Event e " +
                        "JOIN FETCH e.server s " +
                        "WHERE LOWER(CONCAT(s.name, s.rates)) = :server " +
                        "ORDER BY e.date DESC", getEntityClass()
        );
        String serverProcess = StringExtension.trimAll(server.toLowerCase());
        query.setParameter("server", serverProcess);
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
    public void delete(Event entity) {
        em.remove(entity);
    }

    @Override
    public Event getById(Integer id) {
        return em.find(Event.class, id);
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Event> getEntityClass() {
        return Event.class;
    }

}
