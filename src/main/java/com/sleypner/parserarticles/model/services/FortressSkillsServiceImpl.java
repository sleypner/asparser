package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.FortressSkills;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FortressSkillsServiceImpl implements FortressSkillsService {

    private final EntityManager entityManager;

    @Autowired
    public FortressSkillsServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<FortressSkills> getAll() {
        TypedQuery<FortressSkills> query = entityManager.createQuery("FROM FortressSkills skills", FortressSkills.class);
        return query.getResultList();
    }

    @Transactional
    @Override
    public void save(FortressSkills skill) {
        if (!entityManager.contains(skill)) {
            entityManager.persist(skill);
        }
    }

    @Transactional
    @Override
    public FortressSkills update(FortressSkills skill) {
        return entityManager.merge(skill);
    }

    @Override
    public FortressSkills getById(int id) {
        TypedQuery<FortressSkills> query = entityManager.createQuery("SELECT s FROM FortressSkills s WHERE s.id = :id", FortressSkills.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public FortressSkills getByName(String name) {
        TypedQuery<FortressSkills> query = entityManager.createQuery("SELECT s FROM FortressSkills s WHERE s.name LIKE :name", FortressSkills.class);
        query.setParameter("name", name);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
