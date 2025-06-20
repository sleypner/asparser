package dev.sleypner.asparser.scheduler;

//@Configuration
//@EnableScheduling
//public class SchedulingConfig {
//    Logger logger = LoggerFactory.getLogger(this.getClass());
//    private final Environment env;
//
//    public SchedulingConfig(Processing processing, Environment env) {
//
//        this.processing = processing;
//        this.env = env;
//    }
//    @Scheduled(cron = "${parse.articles.cron}", zone = "${parse.zone}")
//    public void scheduleParsingArticle() {
//        logger.atInfo().addKeyValue("scheduleParsingArticle", processing.processingArticles()).addKeyValue("type", "articles").log();
//    }
//
//    @Scheduled(cron = "${parse.fortress.cron}", zone = "${parse.zone}")
//    public void scheduleParsingFortress() {
//        logger.atInfo().addKeyValue("scheduleParsingFortress", processing.processingFortress()).addKeyValue("type", "fortress").log();
//    }
//
//    @Scheduled(fixedDelayString = "${parse.online.delay}", initialDelayString = "${parse.online.initial-delay}", zone = "${parse.zone}")
//    public void scheduleParsingOnline() {
//        logger.atInfo().addKeyValue("scheduleParsingOnline", processing.processingOnlineArticles()).addKeyValue("type", "online_articles").log();
//    }
//
//    @Scheduled(fixedDelayString = "${parse.events.delay}", initialDelayString = "${parse.events.initial-delay}", zone = "${parse.zone}")
//    public void scheduleParsingEvents() {
//        logger.atInfo().addKeyValue("scheduleParsingEvents", processing.processingEventsAndBosses()).addKeyValue("type", "events").log();
//    }
//}
