package com.stackoverflowcompetitor.util;

public class ValidationUtil {

    private ValidationUtil() {
    }

    public static boolean validateLength(String input, int min, int max) {
        return (input.length() < min || input.length() > max);
    }
}
