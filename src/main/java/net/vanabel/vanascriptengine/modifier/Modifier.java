package net.vanabel.vanascriptengine.modifier;

import net.vanabel.vanascriptengine.util.conversion.StringUtils;

public class Modifier {

    @FunctionalInterface
    public interface Processor<T extends Modifiable> {
        void process(T object, Modifier modifier);
    }

    private final String name;
    private final String unprocessedValue;
    // TODO: Context

    public Modifier(String name, String value) {
        this.name = name;
        this.unprocessedValue = StringUtils.emptyAsNull(value);
    }

    public String getName() {
        return name;
    }

    public boolean hasValue() {
        return unprocessedValue != null;
    }

    public String getUnprocessedValue() {
        return unprocessedValue;
    }
}
