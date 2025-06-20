package dev.sleypner.asparser.controllers.pages;

import dev.sleypner.asparser.domain.model.Article;
import dev.sleypner.asparser.service.parser.article.persistence.ArticlePersistence;
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
public class HomePageController {

    private final ArticlePersistence articlePersistence;

    @GetMapping("/")
    String index(Model model) {
        List<Article> listArticles = articlePersistence.getAll();

        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("formOptions", formOptions)
                .addAttribute("articles", listArticles)
                .addAttribute("loc", "articles");
        return "home/index";
    }

}
