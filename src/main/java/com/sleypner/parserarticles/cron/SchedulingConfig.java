package com.sleypner.parserarticles.cron;

import com.sleypner.parserarticles.parsing.Processing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SchedulingConfig {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Environment env;
    private final Processing processing;

    public SchedulingConfig(Processing processing, Environment env) {

        this.processing = processing;
        this.env = env;
    }
//    @Scheduled(cron = "${parse.articles.cron}", zone = "${parse.zone}")
//    public void scheduleParsingArticle() {
//        logger.atInfo().addKeyValue("parser", processing.processingArticles()).addKeyValue("type", "articles").log();
//    }
//
//    @Scheduled(cron = "${parse.fortress.cron}", zone = "${parse.zone}")
//    public void scheduleParsingFortress() {
//        logger.atInfo().addKeyValue("parser", processing.processingFortress()).addKeyValue("type", "fortress").log();
//    }
//
//    @Scheduled(fixedDelayString = "${parse.online.delay}", initialDelayString = "${parse.online.initial-delay}", zone = "${parse.zone}")
//    public void scheduleParsingOnline() {
//        logger.atInfo().addKeyValue("parser", processing.processingOnlineArticles()).addKeyValue("type", "online_articles").log();
//    }
//
//    @Scheduled(fixedDelayString = "${parse.events.delay}", initialDelayString = "${parse.events.initial-delay}", zone = "${parse.zone}")
//    public void scheduleParsingEvents() {
//        logger.atInfo().addKeyValue("parser", processing.processingEventsAndBosses()).addKeyValue("type", "events").log();
//    }
}
