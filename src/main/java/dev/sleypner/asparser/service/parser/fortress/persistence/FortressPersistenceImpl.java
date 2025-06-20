package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.service.parser.shared.PersistenceManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Repository
@Transactional
public class FortressPersistenceImpl implements FortressPersistence, PersistenceManager<Fortress> {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public FortressPersistenceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Fortress> getAll() {
        TypedQuery<Fortress> query = entityManager.createQuery("SELECT f FROM Fortress f JOIN FETCH f.skills", Fortress.class);
        return query.getResultList();
    }

    @Override
    public Fortress save(Fortress fortress) {
        entityManager.persist(fortress);
        System.out.println(fortress);
        return fortress;
    }

    @Override
    public Fortress update(Fortress fortress) {
        return entityManager.merge(fortress);
    }

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

    @Override
    public Set<Fortress> save(Set<Fortress> set) {
        return Set.of();
    }

    @Override
    public EntityManager getEm() {
        return entityManager;
    }

    @Override
    public Class<Fortress> getEntityClass() {
        return Fortress.class;
    }
}
