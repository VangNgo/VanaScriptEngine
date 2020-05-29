package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;

public class BooleanDataType extends DataTypeObject {

    @ObjectConstructor
    public static BooleanDataType construct(String val) {
        if (val == null) {
            return null;
        }
        switch (val) {
            case "true":
            case "1":
                return new BooleanDataType(true);
            case "false":
            case "0":
                return new BooleanDataType(false);
        }
        return null;
    }

    private boolean val;

    public BooleanDataType(boolean b) {
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
