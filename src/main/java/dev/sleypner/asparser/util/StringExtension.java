package dev.sleypner.asparser.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

public class StringExtension {
    public static String encodeBCrypt(String input) {
        var encoder = new BCryptPasswordEncoder();
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

    public static String extractTextInParentheses(String input) {
        if (input == null || input.isEmpty()) return "";
        int start = input.indexOf('(');
        int end = input.indexOf(')');
        return (start != -1 && end != -1 && start < end)
                ? input.substring(start + 1, end)
                : "";
    }

    public static String trimAll(String input) {
        if (input == null || input.isEmpty()) return "";
        return input.replaceAll("\\s+", "");
    }
}
