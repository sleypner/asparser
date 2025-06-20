package dev.sleypner.asparser.controllers.pages;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.Fortress;
import dev.sleypner.asparser.domain.model.FortressHistory;
import dev.sleypner.asparser.dto.FortressTable;
import dev.sleypner.asparser.service.parser.fortress.persistence.ClanPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.FortressHistoryPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.FortressPersistence;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class FortressHistoryPageController {

    private final FortressHistoryPersistence fortressHistoryPersistence;
    private final FortressPersistence fortressPersistence;
    private final ClanPersistence clanPersistence;

    @GetMapping("/fortress-history")
    String fortressHistory(Model model) {
        List<FortressTable> fortressTableList = new ArrayList<>();
        List<FortressHistory> fortressHistoryList = fortressHistoryPersistence.getAll().stream().sorted().toList();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressPersistence.getById(fortressHistory.getFortressId());
            Clan clan = clanPersistence.getById(fortressHistory.getClanId());

            FortressTable fortressTable = FortressTable.builder()
                    .name(fortress.getName())
                    .server(fortress.getServer())
                    .skills(fortress.getSkills().stream().toList())
                    .updatedDate(fortress.getUpdatedDate())
                    .clan(clan)
                    .coffer(fortressHistory.getCoffer())
                    .holdTime(fortressHistory.getHoldTime())
                    .build();
            if (fortressTable.getSkills() == null || fortressTable.getSkills().isEmpty()) {
                fortressTable.setSkills(new ArrayList<>());
            }
            fortressTableList.add(fortressTable);
        }
        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Server", new ArrayList<>(List.of("Asterios x5", "Medea x3", "Prime x1", "Hunter x55", "Phoenix x7", "all"))),
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("formOptions", formOptions)
                .addAttribute("fortressTable", fortressTableList)
                .addAttribute("loc", "fortress-history");

        return "layouts/fortress-history";
    }

}
