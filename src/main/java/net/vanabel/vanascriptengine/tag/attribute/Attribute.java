package net.vanabel.vanascriptengine.tag.attribute;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.ObjectRegistry;
import net.vanabel.vanascriptengine.object.encapsulated.TextObject;
import net.vanabel.vanascriptengine.tag.TagParser;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;
import net.vanabel.vanascriptengine.util.validator.ArrayValidator;
import net.vanabel.vanascriptengine.util.validator.NumberValidator;
import net.vanabel.vanascriptengine.util.validator.ObjectValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Attribute implements Cloneable {

    @FunctionalInterface
    public interface Processor<T> {
        AbstractObject process(T object, Attribute attribute);
    }

    @FunctionalInterface
    public interface DirectProcessor<T> extends Processor<T> {}

    public abstract static class Handler<T extends Attributable> {

        protected final Map<String, Processor<T>> attributes = new HashMap<>();

        protected static void checkForNames(String... n) {
            if (n.length == 0) {
                throw new IllegalArgumentException("No valid attribute names were given!");
            }
        }

        public void registerAttributes(Processor<T> processor, String... names) {
            ObjectValidator.objectIsNonNull(processor, "An attribute processor must be provided!");
            names = StringUtils.enforceValidNames(names);
            checkForNames(names);

            for (String name : names) {
                if (attributes.containsKey(name)) {
                    // TODO: Debug
                    continue;
                }
                attributes.put(name, processor);
            }
        }

        public void extendAttributes(Processor<T> newProcessor, String... names) {
            ObjectValidator.objectIsNonNull(newProcessor, "An attribute processor must be provided!");
            names = StringUtils.enforceValidNames(names);
            checkForNames(names);

            for (String name : names) {
                if (!attributes.containsKey(name)) {
                    // TODO: Debug
                    registerAttributes(newProcessor, name);
                }
                else {
                    Processor<T> oldP = attributes.get(name);
                    // Only preserve DirectProcessor type if both processors are DirectProcessors
                    if (oldP instanceof DirectProcessor && newProcessor instanceof DirectProcessor) {
                        attributes.replace(name, (DirectProcessor<T>) (object, attribute) -> {
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

        public Processor<T> getProcessorFor(String name) {
            return attributes.get(name);
        }

        public abstract AbstractObject processAttribute(T object, Attribute attribute);
    }

    public static class Component {

        public class Context {
            private final AbstractObject val;
            private final String raw;
            private Map<Class<? extends AbstractObject>, AbstractObject> rawObjCache;
            private final Map<String, String> map;
            private Map<String, Map<Class<? extends AbstractObject>, AbstractObject>> mapObjCache;

            public Context(AbstractObject context, String rawContext, Map<String, String> mappedContext) {
                raw = StringUtils.emptyAsNull(rawContext);
                val = context == null ?
                        (raw == null ? null : TextObject.construct(rawContext)) :
                        context;
                this.map = mappedContext.isEmpty() ? null : mappedContext;
            }

            public boolean exists() {
                return raw != null;
            }

            public AbstractObject get() {
                return val;
            }

            public String getRaw() {
                return raw;
            }

            @SuppressWarnings("unchecked")
            public <T extends AbstractObject> T getRawAsType(Class<T> toType) {
                if (rawObjCache == null) {
                    rawObjCache = new HashMap<>();
                }
                return (T) rawObjCache.computeIfAbsent(toType, k -> ObjectRegistry.constructForClass(raw, toType));
            }

            public boolean isMap() {
                return map != null;
            }

            public Set<String> getMapKeys() {
                return map == null ? null : map.keySet();
            }

            public String getKeyValue(String key) {
                return map == null ? null : map.get(key);
            }

            public AbstractObject getKeyValue(String key, Class<? extends AbstractObject> asType) {
                if (map == null) {
                    return null;
                }
                String val = map.get(key);
                if (val == null) {
                    return null;
                }
                Map<Class<? extends AbstractObject>, AbstractObject> cacheMap =
                        mapObjCache.computeIfAbsent(key, k -> new HashMap<>());
                return cacheMap.computeIfAbsent(asType, k -> ObjectRegistry.constructForClass(val, asType));
            }
        }

        private final String name;
        private final Context context;

        public Component(String name, AbstractObject context, String rawContext, Map<String, String> mappedContext) {
            if (StringUtils.emptyAsNull(name) == null) {
                throw new IllegalArgumentException("Cannot have a nameless attribute!");
            }
            this.name = name;
            this.context = new Context(context, rawContext, mappedContext);
        }

        public String getName() {
            return name;
        }

        public boolean hasContext() {
            return context.exists();
        }

        public Context getContext() {
            return context;
        }

        @Override
        public String toString() {
            return name + (context.raw == null ? "" : "(" + context.raw + ")");
        }
    }

    private final Component[] comps;
    private final String rawVal;

    private int fulfilled = 0;

    public Attribute() {
        comps = new Component[0];
        rawVal = "";
    }

    public Attribute(String val) {
        this(val, TagParser.getComponentsFromAttributeString(val));
    }

    public Attribute(Component[] c) {
        comps = c;

        StringBuilder sb = new StringBuilder();
        for (Component cPart : c) {
            sb.append(cPart.toString()).append('.');
        }
        rawVal = sb.substring(0, sb.length() - 1);
    }

    Attribute(String val, Component[] c) {
        if (val == null) {
            throw new IllegalArgumentException("Cannot have pass a null or empty string as the raw value of an attribute!");
        }
        if (c == null) {
            c = new Component[0];
        }
        for (Component comp : c) {
            if (comp == null) {
                throw new IllegalArgumentException("Cannot have a null component!");
            }
        }
        comps = c;
        rawVal = val;
    }

    public Component getComponent() {
        return getComponent(0, false);
    }

    public Component getComponent(int offset) {
        return getComponent(offset, false);
    }

    public Component getComponent(int offset, boolean ignoreFulfilled) {
        int index = NumberValidator.numberIsNonNegative(offset) + (ignoreFulfilled ? 0 : fulfilled);
        if (index >= comps.length) {
            throw new IndexOutOfBoundsException("There are only " + comps.length + " attribute components! Requested " +
                    "attribute component was " + index + (!ignoreFulfilled ? " (offset=" + offset + "; fulfilled=" +
                    fulfilled + ")" : ""));
        }
        return comps[index];
    }

    public String getName() {
        return getName(0, false);
    }

    public String getName(int offset) {
        return getName(offset, false);
    }

    public String getName(int offset, boolean ignoreFulfilled) {
        return getComponent(offset, ignoreFulfilled).name;
    }

    public boolean hasContext() {
        return hasContext(0, false);
    }

    public boolean hasContext(int offset) {
        return hasContext(offset, false);
    }

    public boolean hasContext(int offset, boolean ignoreFulfilled) {
        return getComponent(offset, ignoreFulfilled).context.exists();
    }

    public Component.Context getContext() {
        return getContext(0, false);
    }

    public Component.Context getContext(int offset) {
        return getContext(offset, false);
    }

    public Component.Context getContext(int offset, boolean ignoreFulfilled) {
        return getComponent(offset, ignoreFulfilled).context;
    }

    public boolean startsWith(String... vals) {
        return startsWith(0, vals);
    }

    private boolean startsWithDotSplit(int offset, String s) {
        String[] split = s.split("\\.");
        if (split.length > comps.length - (offset + fulfilled)) {
            return false;
        }
        for (int i = 0; i < split.length; i++) {
            if (!getName(i + offset).equals(split[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean startsWith(int offset, String... vals) {
        ArrayValidator.isNotEmpty(vals);
        if (isComplete()) {
            return false;
        }

        for (String name : vals) {
            if (getName(offset).equals(name) || startsWithDotSplit(offset, name)) {
                return true;
            }
        }
        return false;
    }

    public void fulfill() {
        fulfilled += fulfilled < comps.length ? 1 : 0;
    }

    public void resetFulfilled() {
        fulfilled = 0;
    }

    public void setFulfilled(int attributes) {
        if (attributes < 0 || attributes > comps.length) {
            throw new IllegalArgumentException("Cannot fulfill " + attributes + " of " + comps.length + " attributes!");
        }
        fulfilled = attributes;
    }

    public int getFulfilledCount() {
        return fulfilled;
    }

    public int length() {
        return comps.length;
    }

    public boolean isComplete() {
        return fulfilled >= comps.length;
    }

    @Override
    public String toString() {
        return rawVal;
    }

    /**
     * Returns a clone of this Attribute without
     * @return
     */
    public Attribute imperfectClone() {
        return new Attribute(rawVal, comps);
    }

    @Override
    public Attribute clone() {
        try {
            return (Attribute) super.clone();
        }
        catch (Exception e) {
            return null;
        }
    }
}
