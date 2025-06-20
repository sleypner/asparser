package dev.sleypner.asparser.controllers.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class WordsPageController {

    @GetMapping("/words")
    String roulette(Model model) {

        model.addAttribute("loc", "words");

        return "words/words";
    }
}
