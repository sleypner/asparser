package com.sleypner.parserarticles.special;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.regex.RegExp;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;
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
        return "{bcrypt}" + encodeBCrypt(input);
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static String capitalizeFirstLetter(String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }
    public static boolean isEmailValid(String email) {
        String regexp = "/^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/";
        return email.matches(regexp);
    }
}
