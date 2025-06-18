package dev.sleypner.asparser.service.parser.bosses.persistence;


import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.domain.model.Server;

import java.util.List;

public interface RaidBossesPersistence {
    RaidBoss getById(int id);

    List<RaidBoss> getAll();

    RaidBoss save(RaidBoss boss);

    RaidBoss getByNameAndServer(String name, Server server);

    List<RaidBoss> getByServer(String server);
}
