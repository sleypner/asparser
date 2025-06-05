package com.sleypner.parserarticles.controller.pages;

import com.sleypner.parserarticles.model.services.EventsService;
import com.sleypner.parserarticles.model.source.entityes.Events;
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
public class EventsPageController {

    private final EventsService eventsService;

    @GetMapping("/events")
    String events(Model model) {
        List<Events> eventsList = eventsService.getAll().stream().sorted().toList().reversed();

        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Server", new ArrayList<>(List.of("Asterios x5", "Medea x3", "Prime x1", "Hunter x55", "Phoenix x7", "all"))),
                Map.entry("Type", new ArrayList<>(List.of("all", "Territory Wars", "Sieges", "Epic Bosses", "Key Bosses"))),
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("events", eventsList)
                .addAttribute("formOptions", formOptions)
                .addAttribute("loc", "events");

        return "layouts/events";
    }

}
