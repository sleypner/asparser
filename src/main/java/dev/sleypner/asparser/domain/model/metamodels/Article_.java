package dev.sleypner.asparser.domain.model.metamodels;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.domain.model.OnlineStatus;
import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.util.Date;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Article.class)
public class Article_ {
    public static volatile SingularAttribute<OnlineStatus, Integer> id;
    public static volatile SingularAttribute<OnlineStatus, String> link;
    public static volatile SingularAttribute<OnlineStatus, String> title;
    public static volatile SingularAttribute<OnlineStatus, String> subtitle;
    public static volatile SingularAttribute<OnlineStatus, String> description;
    public static volatile SingularAttribute<OnlineStatus, Date> createOn;

    public static final String ID = "id";
    public static final String LINK = "link";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String DESCRIPTION = "description";
    public static final String CREATE_ON = "createOn";

    public Article_() {
    }

}

