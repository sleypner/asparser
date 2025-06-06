package com.sleypner.parserarticles.controller.pages;

import com.sleypner.parserarticles.model.services.ClanService;
import com.sleypner.parserarticles.model.services.FortressHistoryService;
import com.sleypner.parserarticles.model.services.FortressService;
import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.Fortress;
import com.sleypner.parserarticles.model.source.entityes.FortressHistory;
import com.sleypner.parserarticles.model.source.other.FortressTable;
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
public class FortressPageController {

    private final FortressHistoryService fortressHistoryService;
    private final FortressService fortressService;
    private final ClanService clanService;

    @GetMapping("/fortress")
    String fortress(Model model) {
        List<FortressTable> fortressTableList = new ArrayList<>();
        List<FortressHistory> fortressHistoryList = fortressHistoryService.getCurrentStatusOfForts().stream().sorted().toList();
        for (FortressHistory fortressHistory : fortressHistoryList) {
            Fortress fortress = fortressService.getById(fortressHistory.getFortressId());
            Clan clan = clanService.getById(fortressHistory.getClanId());

            FortressTable fortressTable = FortressTable.builder()
                    .name(fortress.getName())
                    .server(fortress.getServer())
                    .skills(fortress.getSkills().stream().toList())
                    .updatedDate(fortress.getUpdatedDate())
                    .clan(clan)
                    .coffer(fortressHistory.getCoffer())
                    .holdTime(fortressHistory.getHoldTime())
                    .build();
            fortressTableList.add(fortressTable);
        }
        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Server", new ArrayList<>(List.of("Asterios x5", "Medea x3", "Prime x1", "Hunter x55", "Phoenix x7", "all")))
        );

        model.addAttribute("formOptions", formOptions)
                .addAttribute("fortressTable", fortressTableList)
                .addAttribute("loc", "fortress");

        return "layouts/fortress";
    }

}
