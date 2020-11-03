package com.lifeknight.ripears.utilities;

import net.minecraft.util.EnumChatFormatting;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utilities {
    public static List<String> returnStartingEntries(String[] strings, String text, boolean ignoreCase) {
        if (text == null || text.isEmpty()) return Arrays.asList(strings);
        List<String> result = new ArrayList<>();
        if (ignoreCase) text = text.toLowerCase();
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (string.startsWith(text)) result.add(string);
        }
        return result;
    }

    public static List<String> returnStartingEntries(String text, List<String> strings, boolean ignoreCase) {
        if (text == null || text.isEmpty()) return strings;
        List<String> result = new ArrayList<>();
        if (ignoreCase) text = text.toLowerCase();
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (string.startsWith(text)) result.add(string);
        }
        return result;
    }

    public static String removeAllPunctuation(String text) {
        return text.replaceAll("\\W", "");
    }

    public static int countWords(String text) {
        int count = 0;
        for (int x = 0; x < text.length(); x++) {
            if (text.charAt(x) == ' ') {
                count++;
            }
        }
        return ++count;
    }

    public static String removeFormattingCodes(String text) {
        return EnumChatFormatting.getTextWithoutFormattingCodes(text);
    }

    public static String multiplyString(String text, int times) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < times; i++) {
            result.append(text);
        }
        return result.toString();
    }

    public static String formatCapitalization(String text, boolean keepFirstCapitalized) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = text.length() - 1; i > 0; i--) {
            char toInsert;
            char previousChar = text.charAt(i - 1);
            if (previousChar == Character.toUpperCase(previousChar)) {
                toInsert = Character.toLowerCase(text.charAt(i));
            } else {
                toInsert = text.charAt(i);
            }
            stringBuilder.insert(0, toInsert);
        }

        return stringBuilder.insert(0, keepFirstCapitalized ? text.charAt(0) : Character.toLowerCase(text.charAt(0))).toString();
    }

    public static String shortenDouble(double value, int decimalDigits) {
        String asString = String.valueOf(value);
        int wholeDigits = asString.substring(0, asString.indexOf(".")).length();
        return new DecimalFormat(multiplyString("#", wholeDigits) + "." + multiplyString("#", decimalDigits)).format(value);
    }

    public static boolean equalsAny(String text, List<String> strings, boolean ignoreCase, boolean ignorePunctuation) {
        if (ignoreCase) text = text.toLowerCase();
        if (ignorePunctuation) text = removeAllPunctuation(text);
        for (String string : strings) {
            if (ignoreCase) string = string.toLowerCase();
            if (ignorePunctuation) string = removeAllPunctuation(text);
            if (text.equals(string)) return true;
        }
        return false;
    }

    public static boolean isWithinRange(int a, int b, int range) {
        return (b > a - range && b < a + range) || (a > b - range && a < b + range);
    }
}
