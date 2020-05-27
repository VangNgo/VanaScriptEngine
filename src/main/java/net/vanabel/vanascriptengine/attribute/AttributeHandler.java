package net.vanabel.vanascriptengine.attribute;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.util.validator.ObjectValidator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AttributeHandler<T extends Attributable> {

    protected final Map<String, Attribute.Processor<T>> attributes = new HashMap<>();

    private static String[] enforceNames(String... n) {
        Set<String> nameList = new HashSet<>(n.length);
        for (String name : n) {
            if (name == null || name.isEmpty() || name.matches(".*[\"'()=;]+.*")) {
                continue;
            }
            nameList.add(name);
        }
        return nameList.toArray(new String[0]);
    }

    protected static void checkForNames(String... n) {
        if (n.length == 0) {
            throw new IllegalArgumentException("No valid attribute names were given!");
        }
    }

    public void registerAttributes(Attribute.Processor<T> processor, String... names) {
        names = enforceNames(names);
        ObjectValidator.objectIsNonNull(processor, "A processor must be provided!");
        checkForNames(names);

        for (String name : names) {
            if (attributes.containsKey(name)) {
                // TODO: Debug
                continue;
            }
            attributes.put(name, processor);
        }
    }

    public void extendAttributes(Attribute.Processor<T> newProcessor, String... names) {
        names = enforceNames(names);
        ObjectValidator.objectIsNonNull(newProcessor, "A processor must be provided!");
        checkForNames(names);

        for (String name : names) {
            if (!attributes.containsKey(name)) {
                // TODO: Debug
                registerAttributes(newProcessor, name);
            }
            else {
                Attribute.Processor<T> oldP = attributes.get(name);
                // Only preserve DirectProcessor type if both processors are DirectProcessors
                if (oldP instanceof Attribute.DirectProcessor && newProcessor instanceof Attribute.DirectProcessor) {
                    attributes.replace(name, (Attribute.DirectProcessor<T>) (object, attribute) -> {
                        AbstractObject result = newProcessor.process(object, attribute);
                        return result != null ? result : oldP.process(object, attribute);
                    });
                }
                else {
                    attributes.replace(name, (object, attribute) -> {
                        AbstractObject result = newProcessor.process(object, attribute);
                        return result != null ? result : oldP.process(object, attribute);
                    });
                }
            }
        }
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
