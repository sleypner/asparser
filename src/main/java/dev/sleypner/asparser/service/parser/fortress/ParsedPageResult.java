package dev.sleypner.asparser.service.parser.fortress;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class ParsedPageResult {
    private Set<FortressData> fortressData = new HashSet<>();

    public ParsedPageResult addFortressData(FortressData fd) {
        fortressData.add(fd);
        return this;
    }
}
