package dev.sleypner.asparser.service.core.auth.captcha;

import com.nimbusds.jose.shaded.gson.JsonObject;
import com.nimbusds.jose.shaded.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${google.recaptcha.secret}")
    private String secret;

    @Override
    public boolean checkCaptcha(String captcha) {
        if (captcha == null || captcha.isEmpty()) {
            return false;
        }
        try {
            String url = "https://www.google.com/recaptcha/api/siteverify?" +
                    "secret=" + secret + "&response=" + captcha;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

            HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonObject jsonResponse = JsonParser.parseString(httpResponse.body()).getAsJsonObject();

            return jsonResponse.get("success").getAsBoolean();
        } catch (Exception e) {
            return false;
        }
    }
}
