package com.sleypner.parserarticles.parsing.raw;

import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.Fortress;
import com.sleypner.parserarticles.model.source.entityes.FortressHistory;
import com.sleypner.parserarticles.model.source.entityes.FortressSkills;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Setter
@Getter
public class FortressParser implements EntitiesParser {
    private String name = "fortress";
    private Fortress fortress;
    private FortressHistory fortressHistory;
    private Clan clan;
    private Set<FortressSkills> fortressSkills;
    private String clanUrl;

    public FortressParser(FortressHistory fortressHistory, Fortress sourceObject, Set<FortressSkills> fortressSkills) {
        this.fortressHistory = fortressHistory;
        this.fortress = sourceObject;
        this.fortressSkills = fortressSkills;
    }

    public FortressParser() {
    }

    @Override
    public List<URI> getUris() {
        List<URI> uris = new ArrayList<URI>();
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/3.en.html"));
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/8.en.html"));
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/0.en.html"));
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/2.en.html"));
        uris.add(URI.create("https://asterios.tm/static/ratings/fortress/6.en.html"));
        return uris;
    }
}
