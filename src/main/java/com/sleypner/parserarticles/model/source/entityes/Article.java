package com.sleypner.parserarticles.model.source.entityes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.sleypner.parserarticles.model.source.metamodels.Article_;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article implements Serializable,Comparable<Article> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "link")
    private String link;
    @Column(name = "title")
    private String title;
    @Column(name = "subtitle")
    private String subtitle;
    @Column(columnDefinition = "LONGTEXT",name = "description")
    private String description;
    @Column(name = "create_on", columnDefinition = "TIMESTAMP(0)")
    private LocalDateTime createOn;
    @Column(name = "created_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime createdDate;
    @Column(name = "updated_date", columnDefinition = "TIMESTAMP(0)")
    LocalDateTime updatedDate;

    public Article(String link, String title, String subtitle, String description, LocalDateTime createOn) {
        this.createdDate = LocalDateTime.now().withNano(0);
        this.link = link;
        this.title = title;
        this.subtitle = subtitle;
        this.description = description;
        this.createOn = createOn;
    }

    @Override
    public String toString() {
        return String.format("%s\n %s\n %s\n %s\n %5$tY-%tm-%5$td %5$tT\n", link, title, subtitle, description, createOn);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Article articleObj = (Article) obj;
        if (this.createOn.isEqual(articleObj.createOn)){
            if (!this.title.equals(articleObj.title) ||
                    !this.subtitle.equals(articleObj.subtitle) ||
                    !this.description.equals(articleObj.description) ||
                    !this.link.equals(articleObj.link)){
                return false;
            }
            return true;
        }else {
            return false;
        }
    }

    @Override
    public int compareTo(Article o) {
        return getCreateOn().compareTo(o.getCreateOn());
    }
}
