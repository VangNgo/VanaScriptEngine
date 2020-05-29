package net.vanabel.vanascriptengine.util.conversion;

import java.util.Locale;
import java.util.regex.Pattern;

public final class StringUtils {

    public final static Pattern DOUBLE_QUOTED_PATTERN = Pattern.compile("^\"[^\"]+\"$");
    public final static Pattern SINGLE_QUOTED_PATTERN = Pattern.compile("^'[^']+'$");
    public final static Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("\\\\(?:\\\\\\\\)*[^\\\\]");

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
