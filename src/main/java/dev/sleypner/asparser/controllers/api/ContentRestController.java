package dev.sleypner.asparser.controllers.api;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/content")
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class ContentRestController {

    private final Logger logger = LoggerFactory.getLogger(ContentRestController.class);

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
