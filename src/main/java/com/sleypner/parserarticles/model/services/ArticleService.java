package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Article;

import java.time.LocalDateTime;
import java.util.List;

public interface ArticleService {
    List<Article> query();

    Article getById(int id);

    List<Article> getAll();

    Article save(Article article);

    Article deleteById(int id);

    List<Article> getByDate(LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Article> getByDateAndMore(String title, String subtitle, String description, LocalDateTime dateStart, LocalDateTime dateEnd);

    List<Article> getLastArticle();

    Long dataCount();

    List<Article> getLastNumbersArticles(int number);
}
