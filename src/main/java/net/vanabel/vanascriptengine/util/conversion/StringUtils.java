package net.vanabel.vanascriptengine.util.conversion;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {

    public final static Pattern INTEGER_PATTERN = Pattern.compile("^[+\\-]?[0-9]+$");
    public final static Pattern BINARY_PATTERN = Pattern.compile("^0[bB][01]+$");
    public final static Pattern HEXADECIMAL_PATTERN = Pattern.compile("^0[x|X][0-9a-fA-F]+$");
    public final static Pattern DECIMAL_PATTERN = Pattern.compile("^[+\\-]?[0-9]+(?:\\.[0-9]+)?(?:[+\\-]?[eE][0-9]+)?$");

    public final static Pattern INVALID_NAME_CHARS = Pattern.compile(".+[\"'()=;]+.+");

    public final static Pattern DOUBLE_QUOTED_PATTERN = Pattern.compile("^\"[^\"]+\"$");
    public final static Pattern SINGLE_QUOTED_PATTERN = Pattern.compile("^'[^']+'$");
    public final static Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("\\\\(?:\\\\\\\\)*[^\\\\]");

    public static boolean matchesDecimalPattern(String str) {
        return DECIMAL_PATTERN.matcher(str).matches();
    }

    public static boolean isDouble(String str) {
        return toDouble(str) != null;
    }

    public static Double toDouble(String str) {
        try {
            return Double.parseDouble(str);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static boolean matchesIntegerPattern(String str) {
        return INTEGER_PATTERN.matcher(str).matches();
    }

    public static boolean matchesBinaryPattern(String str) {
        return BINARY_PATTERN.matcher(str).matches();
    }

    public static boolean matchesHexadecimalPattern(String str) {
        return HEXADECIMAL_PATTERN.matcher(str).matches();
    }

    public static boolean isInteger(String str) {
        return toInteger(str) != null;
    }

    public static Integer toInteger(String str) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static String enforceValidName(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Cannot have a null or empty string as a name!");
        }
        if (INVALID_NAME_CHARS.matcher(str).matches()) {
            throw new IllegalArgumentException("The string \"" + str + "\" contains at least one of the following " +
                    "illegal characters: \" ' ( ) = ;");
        }
        return str;
    }

    public static String[] enforceValidNames(String... strs) {
        ArrayList<String> strList = new ArrayList<>(strs.length);
        for (String s : strs) {
            strList.add(enforceValidName(s));
        }
        return strList.toArray(new String[0]);
    }

    public static String toUpperCase(String str) {
        return str == null ? null : str.toUpperCase(Locale.ENGLISH);
    }

    public static String toLowerCase(String str) {
        return str == null ? null : str.toLowerCase(Locale.ENGLISH);
    }

    public static String capitalize(String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return str;
        }
        if (str.length() == 1) {
            return toUpperCase(str);
        }
        return toUpperCase(String.valueOf(str.charAt(0))) + str.substring(1);
    }

    public static String emptyAsNull(String str) {
        return str == null || str.isEmpty() ? null : str;
    }
}
