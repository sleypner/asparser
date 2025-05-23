package com.sleypner.parserarticles.special;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class HttpAction {

    Logger logger = LoggerFactory.getLogger(HttpAction.class);

    public HttpAction() {
    }
    public HttpResponse<String> getHttpResponse(URI uri) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(30))
                    .headers("Content-Type", "text/plain;charset=UTF-8")
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return response;
        } catch (InterruptedException | IOException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
            return null;
        }
    }

    public List<HttpResponse<String>> getHttpResponses(List<URI> ListUri) {
        try {
            List<HttpResponse<String>> responses = new ArrayList<>();
            for (URI uri : ListUri) {
                HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                        .timeout(Duration.ofSeconds(30))
                        .headers("Content-Type", "text/plain;charset=UTF-8")
                        .GET()
                        .build();
                HttpResponse<String> response = HttpClient.newBuilder()
                        .build().send(httpRequest, HttpResponse.BodyHandlers.ofString());
                responses.add(response);
                Thread.sleep(300);
            }
            return responses;
        } catch (InterruptedException | IOException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();

            return null;
        }
    }
    public HttpResponse<String> getHttpResponse(URI uri, String user, String password) {
        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(30))
                    .headers(
                            "Content-Type", "text/plain;charset=UTF-8",
                            "Cookie", "JSESSIONID=73C7CD923CF1D98A21B8C42377B09F3F; Path=/; HttpOnly;"
                    )
                    .GET()
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder()
                    .build().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return response;
        } catch (InterruptedException | IOException e) {
            logger.atError()
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();

            return null;
        }
    }


    public Document getDocument(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode == 200) {
            return Jsoup.parse(response.body());
        }
        logger.atError()
                .addKeyValue("exception_class", this.getClass().getSimpleName())
                .addKeyValue("error_message", "Status code " + statusCode)
                .log();
        return null;
    }

    public List<Document> getDocuments(List<HttpResponse<String>> response) {
        List<Document> documents = new ArrayList<>();
        for (HttpResponse<String> resp : response) {
            int statusCode = resp.statusCode();
            if (statusCode == 200) {
                documents.add(Jsoup.parse(resp.body()));
            } else {
                logger.atError()
                        .addKeyValue("exception_class", this.getClass().getSimpleName())
                        .addKeyValue("error_message", "Status code " + statusCode)
                        .log();
                return null;
            }
        }
        return documents;
    }

    public HttpHeaders getHeaders(HttpResponse<String> response) {
        return response.headers();
    }

    public byte[] getImage(String url) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BufferedImage bImage;
        try {
            bImage = ImageIO.read(new URI(url).toURL());
            ImageIO.write(bImage, "jpg", bos);
        } catch (IOException | URISyntaxException e) {
            logger.atError()
                    .setMessage("Image not found!")
                    .addKeyValue("exception_class", e.getClass().getSimpleName())
                    .addKeyValue("error_message", e.getMessage())
                    .log();
        }
        return bos.toByteArray();
    }

}
