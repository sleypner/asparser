package dev.sleypner.asparser.service.parser.server.persistence;

import dev.sleypner.asparser.domain.model.Server;
import dev.sleypner.asparser.service.parser.shared.DateRepository;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
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
public class ServerPersistenceImpl implements RepositoryManager<Server>, DateRepository<Server> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public ServerPersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    public List<Server> getAll() {
        TypedQuery<Server> query = em.createQuery("FROM Server s", getEntityClass());
        return query.getResultStream().toList();
    }

    public Server getById(int id) {
        return em.find(getEntityClass(), id);
    }

    @Override
    public Set<Server> save(Set<Server> servers) {
        List<Server> dbServer = getAll();
        Set<String> existingServers = dbServer.stream()
                .map(Server::getName)
                .collect(Collectors.toSet());
        Set<Server> newServers = servers.stream()
                .filter(server -> !existingServers.contains(server.getName()))
                .collect(Collectors.toSet());

        Set<String> onDown = servers.stream()
                .map(Server::getName)
                .collect(Collectors.toSet());
        Set<Server> disabledServers = dbServer.stream()
                .filter(server -> !onDown.contains(server.getName()))
                .collect(Collectors.toSet());

        if (!disabledServers.isEmpty()) {
            try {
                for (Server server : disabledServers) {
                    server.setStatus("down");
                    em.merge(server);
                }
                em.flush();
            } catch (Exception e) {
                log.error("Failed to disable servers", e);
            }
        }
        if (!newServers.isEmpty()) {
            try {
                for (Server server : newServers) {
                    em.merge(server);
                }
                em.flush();
            } catch (Exception e) {
                log.error("Failed to save servers", e);
            }
        }
        return newServers;
    }

    @Override
    public void delete(Server entity) {

    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Server> getEntityClass() {
        return Server.class;
    }
}
