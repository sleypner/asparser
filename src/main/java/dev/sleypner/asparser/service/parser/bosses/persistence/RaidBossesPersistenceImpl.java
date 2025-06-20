package dev.sleypner.asparser.service.parser.bosses.persistence;

import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.PersistenceManager;
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
import java.util.stream.Collectors;


@Repository
@Transactional
public class RaidBossesPersistenceImpl implements RaidBossesPersistence, PersistenceManager<RaidBoss> {

    Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public RaidBossesPersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public RaidBoss getById(int id) {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b WHERE b.id = :id", RaidBoss.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public List<RaidBoss> getAll() {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b ORDER BY b.date DESC", RaidBoss.class);
        return query.getResultList();
    }

    @Override
    public RaidBoss save(RaidBoss boss) {
        return em.merge(boss);
    }

    @Override
    public RaidBoss getByNameAndServer(String name, Server server) {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b WHERE b.name = :name AND b.server = :server", RaidBoss.class);
        query.setParameter("name", name);
        query.setParameter("server", server);

        List<RaidBoss> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.getFirst();
        } else {
            return null;
        }
    }

    @Override
    public List<RaidBoss> getByServer(String server) {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b WHERE b.server = :server ORDER BY b.date DESC", RaidBoss.class);
        query.setParameter("server", server);

        return query.getResultList();
    }

    @Override
    public Set<RaidBoss> save(Set<RaidBoss> bosses) {
        List<RaidBoss> dbBosses = getAll();

        Set<RaidBoss> newBosses = bosses.stream()
                .filter(rb -> !dbBosses.contains(rb))
                .collect(Collectors.toSet());

        Set<RaidBoss> updateBosses = bosses.stream()
                .filter(dbBosses::contains)
                .collect(Collectors.toSet());

        if (!newBosses.isEmpty()) {
            try {
                for (RaidBoss rb : newBosses) {
                    em.persist(rb);
                }
                em.flush();
            } catch (Exception e) {
                log.error("Failed to save new RaidBoss", e);
            }
        }
        if (!updateBosses.isEmpty()) {
            try {
                for (RaidBoss rb : newBosses) {
                    RaidBoss saved = getByNameAndServer(rb.getName(), rb.getServer());
                    saved.setDate(rb.getDate())
                            .setCountKilling(saved.getCountKilling() + 1)
                            .setLastKillersClan(rb.getLastKillersClan())
                            .setAttackersCount(rb.getAttackersCount())
                            .setLastKiller(rb.getLastKiller());
                    em.merge(saved);
                }
                em.flush();
            } catch (Exception e) {
                log.error("Failed to save new RaidBoss", e);
            }
        }
        newBosses.addAll(updateBosses);
        return newBosses;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<RaidBoss> getEntityClass() {
        return RaidBoss.class;
    }
}
