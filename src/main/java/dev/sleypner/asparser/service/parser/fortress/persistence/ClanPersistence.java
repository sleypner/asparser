package dev.sleypner.asparser.service.parser.fortress.persistence;

import dev.sleypner.asparser.domain.model.Clan;

import java.util.List;

public interface ClanPersistence {
    List<Clan> getAll();

    Clan save(Clan clan);

    Clan update(Clan clan);

    Clan getById(int id);

    Clan getByNameAndServer(String clanName, String serverName);

}
