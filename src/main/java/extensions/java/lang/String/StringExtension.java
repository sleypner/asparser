package extensions.java.lang.String;


import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Random;

@Extension
public class StringExtension {

    public static String encodeBCrypt(@This String input) {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode(input));
        return encoder.encode(input);
    }

    public static String createPassword(@This String input) {
        return "{bcrypt}" + encodeBCrypt(input);
    }

    public static int randomInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static String capitalizeFirstLetter(@This String string) {
        if (string == null || string.isEmpty()) {
            return string;
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static boolean isEmailValid(@This String email) {
        String regexp = "/^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$/";
        return email.matches(regexp);
    }

    public static String extractTextInParentheses(@This String input) {
        if (input == null || input.isEmpty()) return "";
        int start = input.indexOf('(');
        int end = input.indexOf(')');
        return (start != -1 && end != -1 && start < end)
                ? input.substring(start + 1, end)
                : "";
    }
}