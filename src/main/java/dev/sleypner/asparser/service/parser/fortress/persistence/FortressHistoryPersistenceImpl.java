package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.FortressHistory;
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
public class FortressHistoryPersistenceImpl implements FortressHistoryPersistence, RepositoryManager<FortressHistory>, DateRepository<FortressHistory> {
    @PersistenceContext
    private final EntityManager em;
    private final FortressPersistence fortressService;

    @Autowired
    public FortressHistoryPersistenceImpl(EntityManager em, FortressPersistence fortressService) {
        this.em = em;
        this.fortressService = fortressService;
    }

    @Override
    public List<FortressHistory> getAll() {
        TypedQuery<FortressHistory> query = em.createQuery("SELECT f FROM FortressHistory f", getEntityClass());
        return query.getResultList();
    }

    @Override
    public FortressHistory save(FortressHistory fh) {
        em.persist(fh);
        return fh;
    }

    @Override
    public FortressHistory update(FortressHistory fh) {
        return em.merge(fh);
    }

    @Override
    public FortressHistory getById(Integer id) {
        return em.find(getEntityClass(), id);
    }

    @Override
    public FortressHistory getById(int id) {
        TypedQuery<FortressHistory> query = em.createQuery(
                "SELECT f FROM FortressHistory f WHERE f.id = :id", getEntityClass());
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public FortressHistory getByFortressId(String fortressName) {
        TypedQuery<FortressHistory> query = em.createQuery(
                "SELECT f FROM FortressHistory f WHERE f.fortressId = :id", getEntityClass());
        return null;
    }

    @Override
    public List<FortressHistory> getCurrentStatusOfForts() {
        TypedQuery<FortressHistory> query = em.createQuery(
                "SELECT f FROM FortressHistory f ORDER BY f.createdDate DESC", getEntityClass());
        query.setMaxResults((int) fortressService.getCount());//5x21
        return query.getResultList();
    }

    @Override
    public List<FortressHistory> getByServer(String server) {
        TypedQuery<FortressHistory> query = em.createQuery(
                "SELECT fh FROM FortressHistory fh " +
                        "LEFT JOIN Fortress f ON fh.fortressId=f.id " +
                        "WHERE f.server = :server", getEntityClass());
        query.setParameter("server", server);
        List<FortressHistory> fortressHistoryList = query.getResultList();
        if (fortressHistoryList.isEmpty()) {
            return null;
        }
        return fortressHistoryList;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<FortressHistory> getEntityClass() {
        return FortressHistory.class;
    }

    @Override
    public void delete(FortressHistory fortress) {
        RepositoryManager.super.delete(fortress);
    }

    @Override
    public Set<FortressHistory> save(Set<FortressHistory> set) {
        return RepositoryManager.super.save(set);
    }
}
