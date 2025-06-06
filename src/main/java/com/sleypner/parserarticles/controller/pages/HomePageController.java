package com.sleypner.parserarticles.controller.pages;

import com.sleypner.parserarticles.model.services.ArticleService;
import com.sleypner.parserarticles.model.source.entityes.Article;
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

    private final ArticleService articleService;

    @GetMapping("/")
    String index(Model model) {
        List<Article> listArticles = articleService.getAll();

        Map<String, List<String>> formOptions = Map.ofEntries(
                Map.entry("Sort", new ArrayList<>(List.of("Date ascending", "Date descending")))
        );

        model.addAttribute("formOptions", formOptions)
                .addAttribute("articles", listArticles)
                .addAttribute("loc", "articles");
        return "home/index";
    }

}
