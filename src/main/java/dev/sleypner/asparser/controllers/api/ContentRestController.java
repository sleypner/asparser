package dev.sleypner.asparser.controllers.api;

import dev.sleypner.asparser.domain.model.Clan;
import dev.sleypner.asparser.domain.model.FortressSkill;
import dev.sleypner.asparser.service.parser.fortress.persistence.ClanPersistence;
import dev.sleypner.asparser.service.parser.fortress.persistence.FortressSkillsPersistence;
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

    private final Logger logger = LoggerFactory.getLogger(ContentRestController.class);

    private final FortressSkillsPersistence fortressSkillsPersistence;
    private final ClanPersistence clanPersistence;

    @GetMapping(value = "/fortress-skills/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] showFortressSkillsImage(@PathVariable Integer id) throws IOException {
        FortressSkill skill = fortressSkillsPersistence.getById(id);
        if (skill.getImage() != null) {
            InputStream is = new ByteArrayInputStream(skill.getImage());
            return is.readAllBytes();
        }
        return null;
    }

    @GetMapping(value = "/clans/image/{id}", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] showClanImage(@PathVariable Integer id) throws IOException {
        Clan clan = clanPersistence.getById(id);
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
