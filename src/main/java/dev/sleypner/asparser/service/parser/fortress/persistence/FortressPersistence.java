package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Fortress;

import java.util.List;

public interface FortressPersistence {
    List<Fortress> getAll();

    Fortress save(Fortress fortress);

    Fortress update(Fortress fortress);

    void delete(Fortress fortress);

    Fortress getById(int id);

    Fortress getByNameAndServer(String fortressName, String serverName);

    long getCount();

}
