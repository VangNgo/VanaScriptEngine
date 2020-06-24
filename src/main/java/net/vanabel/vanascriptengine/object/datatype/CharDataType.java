package net.vanabel.vanascriptengine.object.datatype;

import net.vanabel.vanascriptengine.object.annotation.ObjectCacheClearer;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.util.DuoNode;

import java.util.HashMap;
import java.util.Map;

public class CharDataType extends DataTypeObject {

    @ObjectConstructor
    public static CharDataType construct(String val) {
        if (val == null || val.length() != 1) {
            return null;
        }
        long time = System.currentTimeMillis();
        DuoNode<Long, CharDataType> node = CONSTRUCT_CACHE.computeIfAbsent(val, k -> new DuoNode<>(time, new CharDataType(val.charAt(0))));
        if (node.getLeft() != time) {
            node.setLeft(time);
        }
        return node.getRight();
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



    ////////////////////////////////////////////////////////////////////////////
    // Cache fields and methods

    private final static Map<String, DuoNode<Long, CharDataType>> CONSTRUCT_CACHE = new HashMap<>();

    @ObjectCacheClearer( customCheckDelay = 1000 )
    public static void clearCache(long delay) {
        long time = System.currentTimeMillis();
        for (String key : CONSTRUCT_CACHE.keySet()) {
            DuoNode<Long, CharDataType> node = CONSTRUCT_CACHE.get(key);
            if (time - node.getLeft() > delay) {
                CONSTRUCT_CACHE.remove(key);
            }
        }
    }
}
