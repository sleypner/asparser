package dev.sleypner.asparser.http;

import dev.sleypner.asparser.domain.model.Image;
import dev.sleypner.asparser.util.EnvHolder;
import dev.sleypner.asparser.util.HtmlDocument;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;
import reactor.util.retry.Retry;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

public class CustomWebClient {

    Environment env;
    Logger log = LoggerFactory.getLogger(CustomWebClient.class);

    private final WebClient webClient;
    private final RateLimiter rateLimiter = RateLimiter.of("api-rate-limiter",
            RateLimiterConfig.custom()
                    .limitForPeriod(2)
                    .limitRefreshPeriod(Duration.ofSeconds(1))
                    .timeoutDuration(Duration.ofSeconds(10))
                    .build()
    );

    public CustomWebClient(String baseUrl) {
        Environment env = EnvHolder.getEnv();

        boolean proxyEnabled = Boolean.parseBoolean(env.getProperty("proxy.enabled", "false"));
        String proxyHost = env.getProperty("proxy.host", "172.17.0.1");
        int proxyPort = Integer.parseInt(env.getProperty("proxy.port", "1081"));

        HttpClient httpClient = HttpClient.create(ConnectionProvider.builder("custom")
                .maxConnections(1)
                .build());

        if (proxyEnabled) {
            httpClient = httpClient.proxy(proxy -> proxy
                    .type(ProxyProvider.Proxy.SOCKS5)
                    .host(proxyHost)
                    .port(proxyPort)
            );
        }

        httpClient = httpClient.responseTimeout(Duration.ofSeconds(20));

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
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

    public Mono<Image> fetchImages(Image image) {
        String uri = image.getExternalUri();
        Path dir = Path.of(image.getDir());
        Path path = Path.of(image.getPath());

        Mono<Void> createDirsMono = Mono.fromCallable(() -> {
            Files.createDirectories(dir);
            return null;
        }).subscribeOn(Schedulers.boundedElastic()).then();

        String pattern = "*" + image.getExternalName();

        return fileExists(dir, pattern)
                .flatMap(exists -> {
                    if (exists) {
                        log.info("File already exists: {}", path);
                        return Mono.just(image);
                    } else {
                        return Mono.defer(() ->
                                webClient.get()
                                        .uri(uri)
                                        .retrieve()
                                        .onStatus(
                                                status -> !status.is2xxSuccessful(),
                                                response -> {
                                                    log.warn("Error: {}", response.statusCode());
                                                    return response.createException();
                                                }
                                        )
                                        .bodyToMono(DataBuffer.class)
                                        .flatMap(dataBuffer ->
                                                createDirsMono.then(DataBufferUtils.write(Mono.just(dataBuffer), path))
                                                        .thenReturn(image)
                                        )
                                        .timeout(Duration.ofSeconds(20))
                                        .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                                        .onErrorResume(throwable -> {
                                            log.error("Failed to fetch '{}': {}", uri, throwable.toString());
                                            return Mono.empty();
                                        })
                        ).transformDeferred(RateLimiterOperator.of(rateLimiter));
                    }
                });
    }

    private Mono<String> fetchHtmlPage(String uri) {

        return Mono.defer(() ->
                webClient.get()
                        .uri(uri)
                        .retrieve()
                        .onStatus(
                                status -> !status.is2xxSuccessful(),
                                response -> {
                                    log.warn("Error: {}", response.statusCode());
                                    return response.createException();
                                }
                        )
                        .bodyToMono(String.class)
                        .timeout(Duration.ofSeconds(20))
                        .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)))
                        .onErrorResume(throwable -> {
                            log.error("Failed to fetch '{}': {}", uri, throwable.toString());
                            return Mono.empty();
                        })
        ).transformDeferred(RateLimiterOperator.of(rateLimiter));
    }

    public Mono<Boolean> fileExists(Path dir, String pattern) {
        return Mono.fromCallable(() -> {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, pattern)) {
                return stream.iterator().hasNext();
            }
        }).subscribeOn(Schedulers.boundedElastic());
    }
}