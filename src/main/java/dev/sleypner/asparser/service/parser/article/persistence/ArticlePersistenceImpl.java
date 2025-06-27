package dev.sleypner.asparser.service.parser.article.persistence;


import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.domain.model.metamodels.Article_;
import dev.sleypner.asparser.domain.model.shared.EntityDiffer;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

@Repository
@Transactional
public class ArticlePersistenceImpl implements ArticlePersistence, RepositoryManager<Article> {

    private final Logger log = LoggerFactory.getLogger(getClass());
    @PersistenceContext
    private final EntityManager em;

    @Autowired
    public ArticlePersistenceImpl(EntityManager theentityManager) {
        em = theentityManager;
    }

    @Override
    public Article save(Article article) {
        return em.merge(article);
    }

    @Override
    public List<Article> getByDate(LocalDateTime dateStart, LocalDateTime dateEnd) {
        return em.createQuery(
                        "FROM Article articles WHERE articles.createOn BETWEEN :dateStart AND :dateEnd", Article.class)
                .setParameter("dateStart", dateStart)
                .setParameter("dateEnd", dateEnd)
                .getResultList();
    }

    @Override
    public List<Article> getLastNumbersArticles(int number) {
        List<Article> res = em.createQuery("FROM Article articles ORDER BY articles.createOn DESC LIMIT :number", Article.class)
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

        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
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
        TypedQuery<Article> query = em.createQuery(criteriaQuery);

        return query.getResultList();
    }

    @Override
    public List<Article> getLastArticle() {
        List<Article> res = em.createQuery("FROM Article articles ORDER BY articles.createOn DESC LIMIT 1", Article.class).getResultList();
        if (res.isEmpty()) {
            return null;
        } else {
            return res;
        }
    }

    public List<Article> query() {
        return em.createQuery("FROM Article articles", Article.class).getResultList();
    }

    @Override
    public Article getById(Integer id) {
        return em.find(Article.class, id);
    }

    @Override
    public List<Article> getAll() {
        TypedQuery<Article> query = em.createQuery("FROM Article articles", Article.class);
        return query.getResultList();
    }

    @Override
    public Set<Article> save(Set<Article> parsed) {

        Set<Article> saved = new HashSet<>(getAll());
        Set<String> existingLinks = saved.stream()
                .map(Article::getLink)
                .collect(Collectors.toSet());

        BiPredicate<Article, Article> articleComparator = (a1, a2) ->
                Objects.equals(a1.getTitle(), a2.getTitle()) &&
                        Objects.equals(a1.getSubtitle(), a2.getSubtitle()) &&
                        Objects.equals(a1.getDescription(), a2.getDescription()) &&
                        Objects.equals(a1.getCreateOn(), a2.getCreateOn());


        EntityDiffer<Article, String> articleDiffer = new EntityDiffer<>(articleComparator);

        Set<Article> updatedArticles = articleDiffer.findUpdated(parsed, saved);

        parsed.forEach(parsedItem -> {
            if (!existingLinks.contains(parsedItem.getLink())) {
                updatedArticles.add(parsedItem);
            }
        });

        if (!updatedArticles.isEmpty()) {
            try {
                for (Article article : updatedArticles) {
                    em.merge(article);
                }
            } catch (Exception e) {
                log.error("Failed to save articles", e);
            }
        }
        return updatedArticles;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public Class<Article> getEntityClass() {
        return Article.class;
    }

    @Override
    public Long dataCount() {
        Object res = em.createQuery("SELECT COUNT(*) FROM Article articles").getSingleResult();
        return (Long) res;
    }
}
