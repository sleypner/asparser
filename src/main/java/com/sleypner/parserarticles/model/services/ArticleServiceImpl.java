package com.sleypner.parserarticles.model.services;


import com.sleypner.parserarticles.model.source.entityes.Article;
import com.sleypner.parserarticles.model.source.metamodels.Article_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ArticleServiceImpl implements ArticleService {
    @PersistenceContext
    private final EntityManager entityManager;

    @Autowired
    public ArticleServiceImpl(EntityManager theentityManager) {
        entityManager = theentityManager;
    }

    @Transactional
    @Override
    public Article save(Article article) {
        return entityManager.merge(article);
    }

    @Transactional
    @Override
    public Article deleteById(int id) {
        Article elem = entityManager.find(Article.class, id);
        entityManager.remove(elem);
        return elem;
    }

    @Override
    public List<Article> getByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
        return entityManager.createQuery(
                        "FROM Article articles WHERE articles.createOn BETWEEN :dateStart AND :dateEnd", Article.class)
                .setParameter("dateStart", dateStart)
                .setParameter("dateEnd", dateEnd)
                .getResultList();
    }

    @Override
    public List<Article> getLastNumbersArticles(int number) {
        List<Article> res = entityManager.createQuery("FROM Article articles ORDER BY articles.createOn DESC LIMIT :number", Article.class)
                .setParameter("number", number)
                .getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res;
        }
    }

    @Override
    public List<Article> getByDateAndMore(String title, String subtitle, String description, LocalDateTime dateStart, LocalDateTime dateEnd) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Article> criteriaQuery = criteriaBuilder.createQuery(Article.class);
        Root<Article> articleRoot = criteriaQuery.from(Article.class);

        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(criteriaBuilder.like(articleRoot.get(Article_.TITLE), title));
        }

        if (subtitle != null) {
            predicates.add(criteriaBuilder.like(articleRoot.get(Article_.SUBTITLE), subtitle));
        }

        if (description != null) {
            predicates.add(criteriaBuilder.like(articleRoot.get(Article_.DESCRIPTION), description));
        }

        if (dateStart != null || dateEnd != null) {
            LocalDateTime newDateStart = LocalDateTime.parse("2000-01-01 10:00:00");
            LocalDateTime newDateEnd = LocalDateTime.now();
            if (dateStart != null) {
                newDateStart = dateStart;
            }
            if (dateEnd != null) {
                newDateEnd = dateEnd;
            }
            predicates.add(criteriaBuilder.between(articleRoot.get(Article_.CREATE_ON), newDateStart, newDateEnd));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Article> query = entityManager.createQuery(criteriaQuery);

        return query.getResultList();
    }

    @Override
    public List<Article> getLastArticle() {
        List<Article> res = entityManager.createQuery("FROM Article articles ORDER BY articles.createOn DESC LIMIT 1", Article.class).getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res;
        }
    }

    @Override
    public List<Article> query() {
        return entityManager.createQuery("FROM Article articles", Article.class).getResultList();
    }

    @Override
    public Article getById(int id) {
        return entityManager.find(Article.class, id);
    }

    @Override
    public List<Article> getAll() {
        TypedQuery<Article> query = entityManager.createQuery("FROM Article articles", Article.class);
        return query.getResultList();
    }

    @Override
    public Long dataCount() {
        Object res = entityManager.createQuery("SELECT COUNT(*) FROM Article articles").getSingleResult();
        return (Long) res;
    }
}
