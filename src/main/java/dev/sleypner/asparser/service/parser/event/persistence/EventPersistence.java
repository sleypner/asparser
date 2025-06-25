package dev.sleypner.asparser.service.parser.event.persistence;

import dev.sleypner.asparser.domain.model.Event;

import java.util.List;

public interface EventPersistence {
    Event getById(Integer id);

    List<Event> getAll();

    Event save(Event event);

    List<Event> getByServer(String server);
}
