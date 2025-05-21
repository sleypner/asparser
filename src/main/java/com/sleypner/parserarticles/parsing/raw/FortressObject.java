package com.sleypner.parserarticles.parsing.raw;

import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.Fortress;
import com.sleypner.parserarticles.model.source.entityes.FortressHistory;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FortressObject {
    private Fortress fortress;
    private FortressHistory fortressHistory;
    private Clan clan;

    public FortressObject(Clan clan, FortressHistory fortressHistory, Fortress fortress) {
        this.fortress = fortress;
        this.fortressHistory = fortressHistory;
        this.clan = clan;
    }

    public FortressObject() {
    }

}

