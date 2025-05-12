package org.example.yhw.bookstorecrud.utils;

import java.util.Map;

/**
 * This utility class extends org.apache.commons.lang3.StringUtils and provides additional
 * string manipulation and validation methods.
 *
 * @author Zin Ko Win
 */

public final class StringUtils extends org.apache.commons.lang3.StringUtils {
    private StringUtils() {
    }

    /**
     * Converts the first character of the input string to lowercase.
     *
     * @param string The input string.
     * @return The input string with the first character in lowercase, or the original string if empty or null.
     */
    public static String firstCharacterToLowerCase(String string) {
        if (isEmpty(string)) {
            return string;
        }

        char[] chars = string.toCharArray();
        chars[0] = Character.toLowerCase(chars[0]);

        return new String(chars);
    }


    /**
     * Counts the number of lines in the given string.
     *
     * @param str The input string.
     * @return The number of lines in the input string, or 0 if empty or null.
     */
    public static int lines(String str) {
        if (isEmpty(str)) {
            return 0;
        }

        String[] strArray = str.split("\\r?\\n");
        return strArray.length;
    }

    /**
     * Counts the number of lines in the given string, considering a maximum character per line.
     *
     * @param str          The input string.
     * @param maxCharacter The maximum number of characters per line.
     * @return The estimated number of lines based on maxCharacter, or 0 if empty or null.
     */
    public static int lines(String str, int maxCharacter) {
        if (isEmpty(str)) {
            return 0;
        }

        String[] strArray = str.split("\\r?\\n");
        int lines = 0;
        for (String val : strArray) {
            float valLength = val.length();
            lines += (int) Math.ceil(valLength / maxCharacter);
        }
        return lines;
    }

    /**
     * Checks if the value associated with the specified key in the given map is equal to the expected value.
     *
     * @param map         The map containing key-value pairs.
     * @param key         The key whose value is to be compared.
     * @param expectValue The expected value to compare against.
     * @return True if the value in the map matches the expected value, false otherwise.
     */
    public static boolean isEquals(Map<String, Object> map, String key, String expectValue) {
        return map.get(key).equals(expectValue);
    }

    /**
     * Provides a default value if the input string is empty or null.
     *
     * @param str          The input string.
     * @param defaultValue The default value to return if the input string is empty or null.
     * @return The input string or the default value if empty or null.
     */
    public static String defaultIfNullOrEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Checks if a string represents a numeric value.
     *
     * @param string The input string.
     * @return True if the input string can be parsed as a numeric value, false otherwise.
     */
    public static boolean isNumeric(String string) {
        if (isEmpty(string)) {
            return false;
        }

        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }
}
