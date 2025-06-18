package dev.sleypner.asparser.service.parser.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.persistence.metamodel.SingularAttribute;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Set;

public interface PersistenceManager<T> {
    default T save(T entity) {
        return entity;
    }

    Set<T> save(Set<T> set);

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
