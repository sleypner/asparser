package dev.sleypner.asparser.util;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static String getMatchedString(String regex, String input) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

}
