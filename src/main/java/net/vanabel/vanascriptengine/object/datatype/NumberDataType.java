package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectCacheClearer;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.util.DuoNode;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a general numeric data type. Encompasses both decimals and integers.
 * This object's numerical range is bounded by Java's double primitive data type.
 */
public class NumberDataType extends DataTypeObject {

    private final static Map<String, DuoNode<Long, NumberDataType>> CONSTRUCT_CACHE = new HashMap<>();

    @ObjectCacheClearer( customCheckDelay = 1000 )
    public static void clearCache(long delay) {
        long time = System.currentTimeMillis();
        for (String key : CONSTRUCT_CACHE.keySet()) {
            DuoNode<Long, NumberDataType> node = CONSTRUCT_CACHE.get(key);
            if (time - node.getLeft() > delay * 1000) {
                CONSTRUCT_CACHE.remove(key);
            }
        }
    }

    @ObjectConstructor
    public static NumberDataType construct(String val) {
        if (val == null || !matches(val)) {
            return null;
        }
        long time = System.currentTimeMillis();
        DuoNode<Long, NumberDataType> node = CONSTRUCT_CACHE.computeIfAbsent(val, k -> new DuoNode<>(time, new NumberDataType(val)));
        if (node.getLeft() != time) {
            node.setLeft(time);
        }
        return node.getRight();
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

    /**
     * Returns an {@link IntegerDataType} representation of this number, if possible.
     * @throws ArithmeticException if this number is not a valid integer
     */
    public IntegerDataType toIntegerType() throws ArithmeticException {
        double down = Math.rint(val);
        if (Double.compare(down, val) != 0) {
            throw new ArithmeticException(this + " cannot be converted to an integer.");
        }
        return new IntegerDataType((long) down);
    }

    public double getValue() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }
}
