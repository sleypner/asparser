package dev.sleypner.asparser.http;

import dev.sleypner.asparser.util.HtmlDocument;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

public class CustomWebClient {

    Logger log = LoggerFactory.getLogger(CustomWebClient.class);

    private final WebClient webClient;
    private final RateLimiter rateLimiter = RateLimiter.of(
            "api-rate-limiter",
            RateLimiterConfig.custom()
                    .limitForPeriod(2)
                    .limitRefreshPeriod(Duration.ofSeconds(1))
                    .timeoutDuration(Duration.ofMillis(500))
                    .build()
    );

    public CustomWebClient(String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();
    }

    public Mono<HtmlDocument> parsePage(String url) {
        return fetchHtmlPage(url)
                .timeout(Duration.ofSeconds(20))
                .flatMap(html -> Mono.just(new HtmlDocument(Jsoup.parse(html))))
                .onErrorResume(e -> {
                    log.error("Error parsing page: {}", e.getMessage());
                    return Mono.empty();
                });
    }

    private Mono<String> fetchHtmlPage(String url) {
        log.info("Fetching page {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .onStatus(
                        status -> status == HttpStatus.TOO_MANY_REQUESTS,
                        response -> Mono.error(new RuntimeException("Rate limit exceeded"))
                )
                .bodyToMono(String.class)
                .transformDeferred(RateLimiterOperator.of(rateLimiter))
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(10))
                        .filter(ex -> ex instanceof RuntimeException &&
                                ex.getMessage().contains("Rate limit exceeded"))
                );
    }
}