package com.sleypner.parserarticles.controller.api;

import com.sleypner.parserarticles.model.services.ClanService;
import com.sleypner.parserarticles.model.services.FortressSkillsService;
import com.sleypner.parserarticles.model.source.entityes.Clan;
import com.sleypner.parserarticles.model.source.entityes.FortressSkills;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ContentRestController {
    private final FortressSkillsService fortressSkillsService;
    private final ClanService clanService;
    private final Logger logger = LoggerFactory.getLogger(ContentRestController.class);

    @GetMapping(value = "/fortress-skills/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] showFortressSkillsImage(@PathVariable Integer id) throws IOException {
        FortressSkills skill = fortressSkillsService.getById(id);
        if (skill.getImage() != null) {
            InputStream is = new ByteArrayInputStream(skill.getImage());
            return is.readAllBytes();
        }
        return null;
    }

    @GetMapping(value = "/clans/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] showClanImage(@PathVariable Integer id) throws IOException {
        Clan clan = clanService.getById(id);
        if (clan.getImage() != null) {
            InputStream is = new ByteArrayInputStream(clan.getImage());
            return is.readAllBytes();
        }
        return null;
    }

    @GetMapping("/roulette")
    public boolean playRoulette(@RequestParam(name = "number") int number) {
        var ran = new Random().ints(36);
        var num = ran.findAny();
        if (num.isPresent()) {
            return num.getAsInt() == number;
        } else {
            logger.atError()
                    .addKeyValue("exception_class", this.getClass().getSimpleName())
                    .addKeyValue("error_message", "Something wrong with playRoulette")
                    .log();
            return false;
        }
    }
}
