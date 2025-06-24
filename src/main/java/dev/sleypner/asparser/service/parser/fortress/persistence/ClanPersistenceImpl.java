package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Clan;
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
public class ClanPersistenceImpl implements ClanPersistence, PersistenceManager<Clan> {
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public ClanPersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public List<Clan> getAll() {
        TypedQuery<Clan> query = em.createQuery("FROM Clan clan", Clan.class);
        return query.getResultList();
    }

    @Override
    public Clan save(Clan clan) {
        em.persist(clan);
        System.out.println(clan.toString());
        return clan;
    }

    @Override
    public Clan update(Clan clan) {
        return em.merge(clan);
    }

    @Override
    public Clan getById(int id) {
        TypedQuery<Clan> query = em.createQuery("SELECT c FROM Clan c WHERE c.id = :id", Clan.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public Clan getByNameAndServer(String clanName, String server) {
        TypedQuery<Clan> query = em.createQuery("SELECT c FROM Clan c " +
                "JOIN FETCH c.server s " +
                "WHERE c.name = :clanName AND LOWER(CONCAT(s.name, s.rates)) = :server", Clan.class);
        query.setParameter("clanName", clanName);
        query.setParameter("serverName", server);
        query.setMaxResults(1);
        List<Clan> clanList = query.getResultList();
        if (clanList.isEmpty()) {
            return null;
        }
        return query.getResultList().getFirst();
    }

    @Override
    public Set<Clan> save(Set<Clan> set) {
        return Set.of();
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Clan> getEntityClass() {
        return Clan.class;
    }
}
