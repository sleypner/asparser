package com.sleypner.parserarticles.controller.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RoulettePageController {

    @GetMapping("/roulette")
    String roulette(Model model) {

        model.addAttribute("loc", "roulette");

        return "layouts/roulette";
    }
}
