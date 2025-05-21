package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.OnlineStatus;
import com.sleypner.parserarticles.model.source.other.OnlineChart;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OnlineStatusServiceImpl implements OnlineStatusService {

    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public OnlineStatusServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    @Override
    public OnlineStatus save(OnlineStatus status) {
        return entityManager.merge(status);
    }

    @Override
    public List<OnlineStatus> getAll() {
        TypedQuery<OnlineStatus> query = entityManager.createQuery("FROM OnlineStatus online", OnlineStatus.class);
        return query.getResultList();
    }

    @Override
    public List<OnlineChart> getByTimePeriod(LocalDateTime periodStart, LocalDateTime periodEnd, Integer interval) {
        String nativeString = """
                SELECT created_date as date, MIN(online) as min, round(AVG(online),0) as avg, MAX(online) as max, 
                MIN(on_trade) as min_trade, round(AVG(on_trade),0) as avg_trade, MAX(on_trade) as max_trade, server_name as server 
                FROM online_status WHERE created_date BETWEEN :periodStart AND :periodEnd 
                GROUP BY UNIX_TIMESTAMP(created_date) DIV :interval, server_name
                """;

        Query nativeQuery = entityManager.createNativeQuery(nativeString)
                .setParameter("interval", interval == null ? 1800 : (interval < 1800 ? 1800 : interval))
                .setParameter("periodStart", periodStart == null ? LocalDateTime.now().minusYears(50) : periodStart)
                .setParameter("periodEnd", periodEnd == null ? LocalDateTime.now() : periodEnd);

        List<OnlineChart> newList = new ArrayList<>();
        List<Object[]> results = nativeQuery.getResultList();
        for (Object[] result : results) {
            BigDecimal avg = (BigDecimal) result[2];
            BigDecimal avg_trade = (BigDecimal) result[5];
            newList.add(new OnlineChart((Timestamp) result[0], (Short) result[1], avg.shortValue(), (Short) result[3], (Short) result[4], avg_trade.shortValue(), (Short) result[6], (String) result[7]));
        }
        return newList;
    }

    @Override
    public List getServers() {
        Query query = entityManager.createQuery("select online.serverName as s_name from OnlineStatus online group by s_name order by s_name asc");
        return query.getResultList();
    }
}
