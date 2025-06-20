package dev.sleypner.asparser.controllers.pages;

import dev.sleypner.asparser.domain.model.RaidBoss;
import dev.sleypner.asparser.service.parser.bosses.persistence.RaidBossesPersistence;
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
public class RaidBossesPageController {

    private final RaidBossesPersistence raidBossesPersistence;

    @GetMapping("/bosses")
    String bosses(Model model) {
        List<RaidBoss> bossesList = raidBossesPersistence.getAll().stream().sorted().toList().reversed();

        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Server", new ArrayList<>(List.of("Asterios x5", "Medea x3", "Prime x1", "Hunter x55", "Phoenix x7", "all"))),
                Map.entry("Type", new ArrayList<>(List.of("all", "Epic Bosses", "Key Bosses"))),
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("bosses", bossesList)
                .addAttribute("formOptions", formOptions)
                .addAttribute("loc", "bosses");

        return "layouts/bosses";
    }

}
