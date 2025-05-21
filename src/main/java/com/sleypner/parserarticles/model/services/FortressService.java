package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Fortress;

import java.util.List;

public interface FortressService {
    List<Fortress> getAll();

    Fortress save(Fortress fortress);

    Fortress update(Fortress fortress);

    void delete(Fortress fortress);

    Fortress getById(int id);

    Fortress getByNameAndServer(String fortressName, String serverName);

    long getCount();

}
