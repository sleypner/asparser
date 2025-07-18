package dev.sleypner.asparser.service.parser.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public interface RepositoryManager<T> {

    @PersistenceContext
    EntityManager getEm();

    Class<T> getEntityClass();

    default T save(T entity) {
        return getEm().merge(entity);
    }

    default Set<T> save(Set<T> set) {
        return set.stream()
                .map(entity -> getEm().merge(entity))
                .collect(Collectors.toSet());
    }

    default void delete(T entity) {
        getEm().remove(entity);
    }

    default T getById(Integer id) {
        return getEm().find(getEntityClass(), id);
    }

    default List<T> getAll() {
        String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
        return getEm().createQuery(jpql, getEntityClass()).getResultList();
    }

    default Optional<T> getByName(String fieldName, String name) {
        String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e WHERE e." + fieldName + " = :name";
        TypedQuery<T> query = getEm()
                .createQuery(jpql, getEntityClass());
        query.setParameter("name", name);
        return query.getResultStream()
                .findFirst();
    }

    default Optional<T> getByName(String name) {
        return getByName("name", name);
    }
    default Long count(){
        return getEm().createQuery("SELECT COUNT(t) FROM "+getEntityClass().getSimpleName()+" t", Long.class)
                        .getSingleResult();
    }

}
