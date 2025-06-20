package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.FortressHistory;

import java.util.List;

public interface FortressHistoryPersistence {
    List<FortressHistory> getAll();

    FortressHistory save(FortressHistory fortress);

    FortressHistory update(FortressHistory fortress);

    void delete(FortressHistory fortress);

    FortressHistory getById(int id);

    FortressHistory getByFortressId(String fortressName);

    List<FortressHistory> getCurrentStatusOfForts();

    List<FortressHistory> getByServer(String server);
}
