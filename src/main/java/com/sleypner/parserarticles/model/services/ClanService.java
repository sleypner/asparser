package com.sleypner.parserarticles.model.services;

import com.sleypner.parserarticles.model.source.entityes.Clan;

import java.util.List;

public interface ClanService {
    List<Clan> getAll();

    Clan save(Clan clan);

    Clan update(Clan clan);

    Clan getById(int id);

    Clan getByNameAndServer(String clanName, String serverName);

}
