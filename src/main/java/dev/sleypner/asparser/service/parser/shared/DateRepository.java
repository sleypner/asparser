package dev.sleypner.asparser.service.parser.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.time.LocalDateTime;

public interface DateRepository<T> {
    @PersistenceContext
    EntityManager getEm();

    Class<T> getEntityClass();

    default LocalDateTime getLastDate(String dateFieldName) {
        try {
            CriteriaBuilder cb = getEm().getCriteriaBuilder();
            CriteriaQuery<LocalDateTime> query = cb.createQuery(LocalDateTime.class);
            Root<T> root = query.from(getEntityClass());

            query.select(cb.greatest(root.get(dateFieldName).as(LocalDateTime.class)));

            TypedQuery<LocalDateTime> tq = getEm().createQuery(query);
            return tq.getResultStream().findFirst().orElse(null);
        } catch (Exception e) {
            return null;
        }

    }
}
