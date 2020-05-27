package net.vanabel.vanascriptengine.util.validator;

public final class ArrayValidator {

    public static <T> T[] isNotEmpty(T[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Empty or null array provided.");
        }
        return array;
    }
}
