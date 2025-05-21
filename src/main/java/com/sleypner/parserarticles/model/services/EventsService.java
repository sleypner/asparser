package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Events;

import java.time.LocalDateTime;
import java.util.List;

public interface EventsService {
    Events getById(int id);

    List<Events> getAll();

    Events save(Events event);

    LocalDateTime getLastEntryDate();

    List<Events> getByServer(String server);
}
