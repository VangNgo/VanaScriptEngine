package net.vanabel.vanascriptengine.util.validator;

public final class ObjectValidator {

    public static <T> T objectIsNonNull(T object) {
        return objectIsNonNull(object, "A null object was provided!");
    }

    public static <T> T objectIsNonNull(T object, String msg) {
        if (object == null) {
            throw new IllegalArgumentException(msg);
        }
        return object;
    }
}
