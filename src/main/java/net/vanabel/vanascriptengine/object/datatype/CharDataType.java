package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;

public class CharDataType extends DataTypeObject {

    @ObjectConstructor
    public static CharDataType construct(String val) {
        if (val == null || val.length() != 1) {
            return null;
        }
        return new CharDataType(val.charAt(0));
    }

    @ObjectMatcher
    public static boolean matches(String val) {
        return val != null && val.length() == 1;
    }

    private final char val;

    public CharDataType(char c) {
        val = c;
    }

    public char getValue() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}
