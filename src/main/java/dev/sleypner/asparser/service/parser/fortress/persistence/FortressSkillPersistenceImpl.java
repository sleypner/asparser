package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.FortressSkill;
import dev.sleypner.asparser.service.parser.shared.DateRepository;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Transactional
public class FortressSkillPersistenceImpl implements
        FortressSkillPersistence,
        RepositoryManager<FortressSkill>,
        DateRepository<FortressSkill> {

    private final EntityManager em;

    @Autowired
    public FortressSkillPersistenceImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<FortressSkill> getAll() {
        TypedQuery<FortressSkill> query = em.createQuery("FROM FortressSkill skills", getEntityClass());
        return query.getResultList();
    }

    @Override
    public FortressSkill save(FortressSkill skill) {
        return getByName(skill.getName())
                .orElseGet(() -> RepositoryManager.super.save(skill));
    }

    @Override
    public FortressSkill update(FortressSkill skill) {
        return em.merge(skill);
    }

    @Override
    public FortressSkill getById(int id) {
        TypedQuery<FortressSkill> query = em.createQuery("SELECT s FROM FortressSkill s WHERE s.id = :id", getEntityClass());
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public FortressSkill getById(Integer id) {
        return em.find(getEntityClass(), id);
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<FortressSkill> getEntityClass() {
        return FortressSkill.class;
    }

    @Override
    public Optional<FortressSkill> getByName(String name) {
        return RepositoryManager.super.getByName("name", name);
    }

    @Override
    public Set<FortressSkill> save(Set<FortressSkill> set) {
        return set.stream()
                .map(this::save)
                .collect(Collectors.toSet());
    }

    @Override
    public void delete(FortressSkill entity) {
        RepositoryManager.super.delete(entity);
    }
}
