package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.service.parser.shared.DateRepository;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
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
public class FortressPersistenceImpl implements FortressPersistence, RepositoryManager<Fortress>, DateRepository<Fortress> {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public FortressPersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public List<Fortress> getAll() {
        TypedQuery<Fortress> query = em.createQuery("SELECT f FROM Fortress f JOIN FETCH f.skills", getEntityClass());
        return query.getResultList();
    }

    @Override
    public Fortress save(Fortress fortress) {
        return em.merge(fortress);
    }

    @Override
    public Fortress update(Fortress fortress) {
        return em.merge(fortress);
    }

    @Override
    public void delete(Fortress fortress) {
        em.remove(fortress);
    }

    @Override
    public Fortress getById(Integer id) {
        return em.find(getEntityClass(), id);
    }

    @Override
    public Fortress getById(int id) {
        TypedQuery<Fortress> query = em.createQuery(
                "SELECT f FROM Fortress f " +
                        "JOIN FETCH f.skills " +
                        "WHERE f.id = :id", getEntityClass());
        query.setParameter("id", id);
        return query.getResultList().get(0);
    }

    @Override
    public Fortress getByNameAndServer(String fortressName, String serverName) {
        TypedQuery<Fortress> query = em.createQuery("SELECT f FROM Fortress f WHERE f.name = :fortressName AND f.server = :serverName", getEntityClass());
        query.setParameter("fortressName", fortressName);
        query.setParameter("serverName", serverName);
        return query.getResultList().get(0);
    }

    @Override
    public long getCount() {
        return em.createQuery("SELECT COUNT(f) FROM Fortress f", Long.class).getSingleResult();
    }

    @Override
    public Set<Fortress> save(Set<Fortress> set) {
        return em.merge(set);
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Fortress> getEntityClass() {
        return Fortress.class;
    }
}
