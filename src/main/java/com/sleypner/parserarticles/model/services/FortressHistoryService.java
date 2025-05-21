package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.FortressHistory;

import java.util.List;

public interface FortressHistoryService {
    List<FortressHistory> getAll();

    void save(FortressHistory fortress);

    FortressHistory update(FortressHistory fortress);

    void delete(FortressHistory fortress);

    FortressHistory getById(int id);

    FortressHistory getByFortressId(String fortressName);

    List<FortressHistory> getCurrentStatusOfForts();
}
