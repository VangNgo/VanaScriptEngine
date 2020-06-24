package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectCacheClearer;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.util.DuoNode;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a general integer data type.
 * This object's numerical range is bounded by Java's long primitive data type.
 */
public class IntegerDataType extends DataTypeObject {

    /**
     * Constructs an IntegerObject from a String. Accepts binary and hexadecimal inputs, as long as
     * they are formatted as Java literals.
     * @param val The String to convert to an IntegerObject.
     * @return The IntegerObject represented by the String.
     */
    @ObjectConstructor
    public static IntegerDataType construct(String val) {
        if (val == null || !matches(val)) {
            return null;
        }
        long time = System.currentTimeMillis();
        DuoNode<Long, IntegerDataType> node = CONSTRUCT_CACHE.computeIfAbsent(val, k -> new DuoNode<>(time, null));
        if (node.getLeft() != time) {
            node.setLeft(time);
        }
        if (node.getRight() == null) {
            if (StringUtils.matchesBinaryPattern(val)) {
                node.setRight(new IntegerDataType(Long.parseLong(val.substring(2), 2)));
            }
            else if (StringUtils.matchesHexadecimalPattern(val)) {
                node.setRight(new IntegerDataType(Long.parseLong(val.substring(2), 16)));
            }
            else {
                node.setRight(new IntegerDataType(val));
            }
        }
        return node.getRight();
    }

    @ObjectMatcher
    public static boolean matches(String val) {
        return StringUtils.matchesIntegerPattern(val);
    }

    private final long val;

    public IntegerDataType(byte b) {
        val = b;
    }

    public IntegerDataType(short s) {
        val = s;
    }

    public IntegerDataType(int i) {
        val = i;
    }

    public IntegerDataType(long l) {
        val = l;
    }

    public IntegerDataType(char c) throws NumberFormatException {
        if (!Character.isDigit(c)) {
            throw new NumberFormatException("Illegal digit");
        }
        val = Character.digit(c, 10);
    }

    public IntegerDataType(String s) throws NumberFormatException {
        val = Long.parseLong(s);
    }

    public NumberDataType toNumberType() {
        return new NumberDataType(val);
    }

    public String toBinary() {
        return "0b" + Long.toBinaryString(val);
    }

    public String toHexadecimal() {
        return "0x" + Long.toHexString(val);
    }

    /**
     * Returns the value stored by this IntegerObject as a primitive long.
     */
    public long getValue() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }



    ////////////////////////////////////////////////////////////////////////////
    // Cache fields and methods

    private final static Map<String, DuoNode<Long, IntegerDataType>> CONSTRUCT_CACHE = new HashMap<>();

    @ObjectCacheClearer( customCheckDelay = 1000 )
    public static void clearCache(long delay) {
        long time = System.currentTimeMillis();
        for (String key : CONSTRUCT_CACHE.keySet()) {
            DuoNode<Long, IntegerDataType> node = CONSTRUCT_CACHE.get(key);
            if (time - node.getLeft() > delay) {
                CONSTRUCT_CACHE.remove(key);
            }
        }
    }
}
