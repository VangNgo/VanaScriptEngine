package net.vanabel.vanascriptengine.util.conversion;

import java.util.regex.Pattern;

public final class StringUtils {

    public final static Pattern DOUBLE_QUOTED_PATTERN = Pattern.compile("^\"[^\"]+\"$");
    public final static Pattern SINGLE_QUOTED_PATTERN = Pattern.compile("^'[^']+'$");
    public final static Pattern ESCAPED_CHARACTER_PATTERN = Pattern.compile("\\\\(?:\\\\\\\\)*[^\\\\]");

    public static String emptyStringAsNull(String str) {
        return str == null || str.isEmpty() ? null : str;
    }
}
