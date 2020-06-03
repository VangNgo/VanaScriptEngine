package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;

public final class BooleanDataType extends DataTypeObject {

    public final static BooleanDataType TRUE = new BooleanDataType(true);
    public final static BooleanDataType FALSE = new BooleanDataType(false);

    public static BooleanDataType getForBoolean(boolean b) {
        return b ? TRUE : FALSE;
    }

    @ObjectConstructor
    public static BooleanDataType construct(String val) {
        if (val == null) {
            return null;
        }
        switch (val) {
            case "true":
            case "1":
                return TRUE;
            case "false":
            case "0":
                return FALSE;
        }
        return null;
    }

    @ObjectMatcher
    public static boolean matches(String val) {
        return val != null && val.matches("^true|false|0|1$");
    }

    private boolean val;

    BooleanDataType(boolean b) {
        val = b;
    }

    public boolean getValue() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}
