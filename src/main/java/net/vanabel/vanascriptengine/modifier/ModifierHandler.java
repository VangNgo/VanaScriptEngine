package net.vanabel.vanascriptengine.modifier;

import net.vanabel.vanascriptengine.util.conversion.StringUtils;
import net.vanabel.vanascriptengine.util.validator.ObjectValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class ModifierHandler<T extends Modifiable> {

    protected final Map<String, Modifier.Processor<T>> modifiers = new HashMap<>();

    private static void checkForNames(String... n) {
        if (n.length == 0) {
            throw new IllegalArgumentException("No valid modifier names were given!");
        }
    }

    public void registerModifiers(Modifier.Processor<T> processor, String... names) {
        ObjectValidator.objectIsNonNull(processor, "A modifier processor must be provided!");
        names = StringUtils.enforceValidNames(names);
        checkForNames(names);

        for (String name : names) {
            if (modifiers.containsKey(name)) {
                // TODO: Debug
                continue;
            }
            modifiers.put(name, processor);
        }
    }

    public void extendModifiers(Modifier.Processor<T> processor, String... names) {
        ObjectValidator.objectIsNonNull(processor, "A modifier processor must be provided!");
        names = StringUtils.enforceValidNames(names);
        checkForNames(names);

        for (String name : names) {
            Modifier.Processor<T> oldP = modifiers.get(name);
            if (oldP == null) {
                registerModifiers(processor, name);
                continue;
            }
            modifiers.replace(name, (object, modifier) -> {
                boolean isProcessed = processor.process(object, modifier);
                return isProcessed || oldP.process(object, modifier);
            });
        }
    }

    public boolean hasModifier(String name) {
        return modifiers.containsKey(name);
    }

    public Set<String> getModifiers() {
        return modifiers.keySet();
    }

    public Modifier.Processor<T> getProcessorForModifier(String name) {
        return modifiers.get(name);
    }

    public abstract boolean processModifier(T object, Modifier modifier);
}
