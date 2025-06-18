package dev.sleypner.asparser.controllers.pages;

import dev.sleypner.asparser.domain.model.Event;
import dev.sleypner.asparser.service.parser.event.persistence.EventPersistence;
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

    private final EventPersistence eventPersistence;

    @GetMapping("/events")
    String events(Model model) {
        List<Event> eventList = eventPersistence.getAll().stream().sorted().toList().reversed();

        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Server", new ArrayList<>(List.of("Asterios x5", "Medea x3", "Prime x1", "Hunter x55", "Phoenix x7", "all"))),
                Map.entry("Type", new ArrayList<>(List.of("all", "Territory Wars", "Sieges", "Epic Bosses", "Key Bosses"))),
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("events", eventList)
                .addAttribute("formOptions", formOptions)
                .addAttribute("loc", "events");

        return "layouts/events";
    }

}
