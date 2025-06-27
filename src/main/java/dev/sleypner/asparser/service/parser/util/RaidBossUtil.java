package dev.sleypner.asparser.service.parser.util;

import dev.sleypner.asparser.domain.model.RaidBoss;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class RaidBossUtil {

    private static boolean matches(RaidBoss a, RaidBoss b) {
        return a.getName() != null &&
                b.getName() != null &&
                a.getName().equalsIgnoreCase(b.getName()) &&
                a.getServer() != null &&
                b.getServer() != null &&
                Objects.equals(a.getServer().getId(), b.getServer().getId());
    }

    public static boolean exists(Collection<RaidBoss> bosses, RaidBoss boss) {
        return bosses.stream().anyMatch(b -> matches(b, boss));
    }

    public static Optional<RaidBoss> findExists(Collection<RaidBoss> bosses, RaidBoss boss) {
        return bosses.stream().filter(b -> matches(b, boss)).findFirst();
    }
}
