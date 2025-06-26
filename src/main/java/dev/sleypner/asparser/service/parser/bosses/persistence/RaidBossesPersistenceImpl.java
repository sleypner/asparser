package dev.sleypner.asparser.service.parser.bosses.persistence;

import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.domain.model.Server;
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
import java.util.stream.Collectors;


@Repository
@Transactional
public class RaidBossesPersistenceImpl implements RaidBossesPersistence, RepositoryManager<RaidBoss>, DateRepository<RaidBoss> {

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
