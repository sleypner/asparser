package dev.sleypner.asparser.service.parser.shared;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Set;

public interface RepositoryManager<T> {

    @PersistenceContext
    EntityManager getEm();

    Class<T> getEntityClass();

    default T save(T entity) {
        return entity;
    }

    Set<T> save(Set<T> set);

    default void delete(T entity){
        getEm().remove(entity);
    }

    default T getById(Integer id){
        return getEm().find(getEntityClass(), id);
    }

    default List<T> getAll(){
        String jpql = "SELECT e FROM " + getEntityClass().getSimpleName() + " e";
        return getEm().createQuery(jpql, getEntityClass()).getResultList();
    }

}
