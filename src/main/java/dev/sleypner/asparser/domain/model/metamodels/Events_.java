package dev.sleypner.asparser.domain.model.metamodels;

import dev.sleypner.asparser.domain.model.Event;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

import java.time.LocalDateTime;

@StaticMetamodel(Event.class)
public class Events_ {
    public static volatile SingularAttribute<Event, LocalDateTime> date;
}