package com.sleypner.parserarticles.parsing;

import com.sleypner.parserarticles.model.source.entityes.Clan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

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
 }
