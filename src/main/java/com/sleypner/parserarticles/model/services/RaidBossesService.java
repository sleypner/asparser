package com.sleypner.parserarticles.model.services;


import com.sleypner.parserarticles.model.source.entityes.RaidBosses;

import java.util.List;

public interface RaidBossesService {
    RaidBosses getById(int id);

    List<RaidBosses> getAll();

    RaidBosses save(RaidBosses boss);

    RaidBosses getByNameAndServer(String name, String server);

    List<RaidBosses> getByServer(String server);
}
