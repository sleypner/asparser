package com.sleypner.parserarticles.special;

import com.sleypner.parserarticles.model.source.entityes.UserActionLogs;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua_parser.Client;
import ua_parser.Parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Special {

    public static String getMatchedString(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
    public static String encodeBCrypt(String input) {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(input));
        return encoder.encode(input);
    }
    public static String createPassword(String input) {
        return "{bcrypt}"+encodeBCrypt(input);
    }

    public static UserActionLogs getAction(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        Parser parser = new Parser();
        Client client = parser.parse(userAgent);

        return new UserActionLogs(
                request.getRemoteAddr(),
                client.userAgent.family,
                client.os.family, client.device.family,
                request.getSession().getId());
    }
 }
