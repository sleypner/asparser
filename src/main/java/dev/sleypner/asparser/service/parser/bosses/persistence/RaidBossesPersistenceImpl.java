package dev.sleypner.asparser.service.parser.bosses.persistence;

import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import dev.sleypner.asparser.service.parser.util.RaidBossUtil;
import dev.sleypner.asparser.util.StringExtension;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


@Repository
@Transactional
public class RaidBossesPersistenceImpl implements RaidBossesPersistence, RepositoryManager<RaidBoss> {

    Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public RaidBossesPersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public RaidBoss getById(Integer id) {
        return em.find(RaidBoss.class, id);
    }

    @Override
    public List<RaidBoss> getAll() {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b", getEntityClass());
        return query.getResultStream().toList();
    }

    @Override
    public RaidBoss save(RaidBoss boss) {
        return em.merge(boss);
    }

    @Override
    public RaidBoss getByNameAndServer(String name, Server server) {
        TypedQuery<RaidBoss> query = em.createQuery("SELECT b FROM RaidBoss b WHERE b.name = :name AND b.server = :server", getEntityClass());
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
        TypedQuery<RaidBoss> query = em.createQuery(
                "SELECT b FROM RaidBoss b " +
                        "JOIN FETCH b.server s " +
                        "WHERE LOWER(CONCAT(s.name, s.rates)) = :server " +
                        "ORDER BY b.date DESC", getEntityClass()
        );

        String serverProcess = StringExtension.trimAll(server.toLowerCase());
        query.setParameter("server", serverProcess);

        return query.getResultList();
    }

    @Override
    public Set<RaidBoss> save(Set<RaidBoss> bosses) {
        List<RaidBoss> dbBosses = getAll();

        Map<Boolean, List<RaidBoss>> partitioned = bosses.stream()
                .collect(Collectors.partitioningBy(rb -> RaidBossUtil.exists(dbBosses,rb)));

        Set<RaidBoss> newBosses = new HashSet<>(partitioned.get(false));
        Set<RaidBoss> updateBosses = new HashSet<>(partitioned.get(true));

        if (!newBosses.isEmpty()) {
            try {
                bosses.forEach(em::merge);
            } catch (Exception e) {
                log.error("Failed to save RaidBoss", e);
            }
        }
        if (!updateBosses.isEmpty()) {
            try {
                bosses.forEach(rb -> {
                    Optional<RaidBoss> dbBoss = RaidBossUtil.findExists(dbBosses, rb);
                    dbBoss.ifPresent(dbRb->{
                        int countKilling = rb.getCountKilling() + dbRb.getCountKilling();
                        rb.setId(dbRb.getId());
                        rb.setCountKilling(countKilling);
                    });
                    em.merge(rb);
                });
            } catch (Exception e) {
                log.error("Failed to update RaidBoss", e);
            }
        }
        newBosses.addAll(updateBosses);
        return newBosses;
    }

    @Override
    public void delete(RaidBoss entity) {
        em.remove(entity);
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
