package net.vanabel.vanascriptengine.util.validator;

public final class NumberValidator {

    public static int numberIsNonNegative(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("This number cannot be zero or negative! Input received was: " + i);
        }
        return i;
    }
}
