package dev.sleypner.asparser.service.parser.online.persistence;

import dev.sleypner.asparser.domain.model.OnlineStatus;
import dev.sleypner.asparser.dto.OnlineChart;
import dev.sleypner.asparser.service.parser.shared.DateRepository;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
public class OnlinePersistenceImpl implements OnlinePersistence, RepositoryManager<OnlineStatus>, DateRepository<OnlineStatus> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public OnlinePersistenceImpl(EntityManager entityManager) {
        this.em = entityManager;
    }

    @Override
    public OnlineStatus save(OnlineStatus status) {
        return em.merge(status);
    }


    @Override
    public List<OnlineStatus> getAll() {
        TypedQuery<OnlineStatus> query = em.createQuery("FROM OnlineStatus online", getEntityClass());
        return query.getResultList();
    }

    @Override
    public List<OnlineChart> getByTimePeriod(LocalDateTime periodStart, LocalDateTime periodEnd, Integer interval) {
        String nativeQuery = """
                SELECT created_date as date, MIN(online) as min, round(AVG(online),0) as avg, MAX(online) as max, 
                MIN(on_trade) as min_trade, round(AVG(on_trade),0) as avg_trade, MAX(on_trade) as max_trade, server_name as server 
                FROM online_status WHERE created_date BETWEEN :periodStart AND :periodEnd 
                GROUP BY UNIX_TIMESTAMP(created_date) DIV :interval, server_name
                """;

        Query nativeQueryParam = em.createNativeQuery(nativeQuery, "OnlineChartDtoMapping")
                .setParameter("interval", interval == null ? 1800 : (interval < 1800 ? 1800 : interval))
                .setParameter("periodStart", periodStart == null ? LocalDateTime.now().minusYears(50) : periodStart)
                .setParameter("periodEnd", periodEnd == null ? LocalDateTime.now() : periodEnd);

        return nativeQueryParam.getResultList();

    }

    @Override
    public Set<OnlineStatus> save(Set<OnlineStatus> onlineStatuses) {

        if (!onlineStatuses.isEmpty()) {
            try {
                for (OnlineStatus onlineStatus : onlineStatuses) {
                    em.persist(onlineStatus);
                }
                em.flush();
            } catch (Exception e) {
                log.error("Failed to save online", e);
            }
        }
        return onlineStatuses;
    }

    @Override
    public void delete(OnlineStatus entity) {

    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<OnlineStatus> getEntityClass() {
        return OnlineStatus.class;
    }
}
