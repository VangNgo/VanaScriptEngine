package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

/**
 * Represents a general numeric data type. Encompasses both decimals and integers.
 * This object's numerical range is bounded by Java's double primitive data type.
 */
public class NumberDataType extends DataTypeObject {

    @ObjectConstructor
    public static NumberDataType construct(String val) {
        if (val == null || !matches(val)) {
            return null;
        }
        return new NumberDataType(val);
    }

    @ObjectMatcher
    public static boolean matches(String val) {
        return StringUtils.matchesDecimalPattern(val);
    }

    private final double val;

    public NumberDataType(byte b) {
        val = b;
    }

    public NumberDataType(short s) {
        val = s;
    }

    public NumberDataType(int i) {
        val = i;
    }

    public NumberDataType(long l) {
        val = l;
    }

    public NumberDataType(float f) {
        val = f;
    }

    public NumberDataType(double d) {
        val = d;
    }

    public NumberDataType(String val) throws NumberFormatException {
        this.val = Double.parseDouble(val);
    }

    public double getValue() {
        return val;
    }

    @Override
    public String toString() {
        return null;
    }
}
