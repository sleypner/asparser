package dev.sleypner.asparser.scheduler;

import dev.sleypner.asparser.domain.model.*;
import dev.sleypner.asparser.service.parser.shared.OrchestrationService;
import dev.sleypner.asparser.service.parser.shared.RepositoryManager;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.*;
import java.util.function.Supplier;

@Configuration
public class SchedulerConfig {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final TaskQueue taskQueue;
    private final ZoneId zoneId;
    private final OrchestrationService<Server> serverService;
    private final OrchestrationService<Fortress> fortressService;
    private final OrchestrationService<Article> articleService;
    private final OrchestrationService<Event> eventService;
    private final OrchestrationService<OnlineStatus> onlineService;
    private final RepositoryManager<Server> serverRepository;
    private final Environment env;

    public SchedulerConfig(TaskQueue taskQueue,
                           OrchestrationService<Server> serverService,
                           OrchestrationService<Fortress> fortressService,
                           OrchestrationService<Article> articleService,
                           OrchestrationService<Event> eventService,
                           OrchestrationService<OnlineStatus> onlineService,
                           Environment env, RepositoryManager<Server> serverRepository) {
        this.taskQueue = taskQueue;
        this.zoneId = ZoneId.of(env.getProperty("parse.zone", "Europe/Helsinki"));
        this.serverService = serverService;
        this.fortressService = fortressService;
        this.articleService = articleService;
        this.eventService = eventService;
        this.onlineService = onlineService;
        this.env = env;
        this.serverRepository = serverRepository;
    }


    @PostConstruct
    public void scheduleAll() {

        runServerInitIfEmpty();

        scheduleCron(
                env.getProperty("parse.servers.cron"),
                () -> serverService.processList(),
                "Servers Parser"
        );

        scheduleCron(
                env.getProperty("parse.fortresses.cron"),
                () -> fortressService.processList(),
                "Fortresses Parser"
        );
        scheduleCron(
                env.getProperty("parse.articles.cron"),
                () -> articleService.processList(),
                "Articles Parser"
        );
        scheduleFixedDelay(
                Duration.ofMillis(Long.parseLong(env.getProperty("parse.events.delay","600000"))),
                Duration.ofMillis(Long.parseLong(env.getProperty("parse.events.initial-delay","60000"))),
                () -> eventService.processList(),
                "Events Parser"
        );

        scheduleFixedDelay(
                Duration.ofMillis(Long.parseLong(env.getProperty("parse.online.delay", "600000"))),
                Duration.ofMillis(Long.parseLong(env.getProperty("parse.online.initial-delay", "60000"))),
                () -> onlineService.processList(),
                "Online Parser"
        );
    }

    private void scheduleCron(String cron, Supplier<Mono<Void>> task, String name) {
        Trigger trigger = new CronTrigger(cron);

        Mono.defer(() -> {
                    TriggerContext context = new SimpleTriggerContext();
                    Instant nextExecution = trigger.nextExecution(context);

                    if (nextExecution == null) {
                        log.info("Cron {}: nextExecution is null. Stopping schedule.", name);
                        return Mono.empty();
                    }

                    Duration delay = Duration.between(
                            ZonedDateTime.now(zoneId),
                            ZonedDateTime.ofInstant(nextExecution, zoneId));

                    log.info("[{}] next execution at {}", name, nextExecution);

                    return Mono.delay(delay)
                            .flatMap(tick -> {
                                log.info("[{}] starting scheduled task", name);
                                taskQueue.submit(task);
                                return Mono.empty();
                            });
                })
                .repeat()
                .subscribe();
    }

    private void scheduleFixedDelay(Duration delay,
                                    Duration initialDelay,
                                    Supplier<Mono<Void>> task,
                                    String name) {


        Flux.interval(initialDelay, delay)
                .doOnNext(i -> {
                    LocalTime nextExecution = LocalDateTime.now().plus(delay).withNano(0).toLocalTime();
                    log.info("[{}] Scheduled run at: {}", name, nextExecution);
                    taskQueue.submit(task);
                })
                .subscribe();
    }
    private void runServerInitIfEmpty(){
        Mono.fromCallable(serverRepository::count)
                .subscribeOn(Schedulers.boundedElastic())
                .filter(count -> count == 0)
                .flatMap(count -> {
                    log.info("Server table is empty. Triggering initial population...");
                    return serverService.processList();
                })
                .doOnSuccess(v -> log.info("Initial server load complete."))
                .doOnError(err -> log.error("Failed to initialize servers", err))
                .subscribe();
    }
}
