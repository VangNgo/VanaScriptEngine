package net.vanabel.vanascriptengine.attribute;

import net.vanabel.vanascriptengine.object.AbstractObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AttributeHandler<T extends Attributable> {

    protected final Map<String, Attribute.Processor<T>> attributes = new HashMap<>();

    private static String[] enforceNames(String... n) {
        ArrayList<String> nameList = new ArrayList<>(n.length);
        for (String name : n) {
            if (name == null || name.isEmpty() || name.matches(".*[\"'()=;]+.*")) {
                continue;
            }
            nameList.add(name);
        }
        return nameList.toArray(new String[0]);
    }

    public boolean registerAttribute(Attribute.Processor<T> processor, String... names) {
        names = enforceNames(names);
        if (processor == null) {
            throw new IllegalArgumentException("A processor must be provided!");
        }
        if (names.length == 0) {
            throw new IllegalArgumentException("No valid attribute names were given!");
        }

        boolean success = false;
        for (String name : names) {
            if (attributes.containsKey(name)) {
                // TODO: Debug
                continue;
            }
            attributes.put(name, processor);
            success = true;
        }
        return success;
    }

    public boolean hasAttribute(String name) {
        return attributes.containsKey(name);
    }

    public Set<String> getAttributes() {
        return attributes.keySet();
    }

    public Attribute.Processor<T> getProcessorForAttribute(String name) {
        return attributes.get(name);
    }

    public abstract AbstractObject processAttribute(T object, Attribute attribute);
}
