package dev.sleypner.asparser.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

@Component
public class TaskQueue {

    Logger log = LoggerFactory.getLogger(this.getClass());
    private final Queue<Supplier<Mono<Void>>> taskQueue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    public void submit(final Supplier<Mono<Void>> task) {
        taskQueue.add(task);
        processQueue();
    }

    private void processQueue() {
        if (isRunning.compareAndSet(false, true)) {
            Mono.defer(this::runNext)
                    .repeat(() -> !taskQueue.isEmpty())
                    .doFinally(signal -> isRunning.set(false))
                    .subscribe();
        }
    }

    private Mono<Void> runNext() {
        Supplier<Mono<Void>> task = taskQueue.poll();
        if (task == null) {
            return Mono.empty();
        }
        return task.get()
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(e -> log.error("Task failed", e))
                .onErrorResume(e -> Mono.empty());
    }

}
