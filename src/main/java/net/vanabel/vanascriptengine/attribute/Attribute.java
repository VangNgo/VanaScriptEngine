package net.vanabel.vanascriptengine.attribute;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;
import net.vanabel.vanascriptengine.util.validator.ArrayValidator;
import net.vanabel.vanascriptengine.util.validator.NumberValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Attribute implements Cloneable {

    @FunctionalInterface
    public interface Processor<T> {
        AbstractObject process(T object, Attribute attribute);
    }

    @FunctionalInterface
    public interface DirectProcessor<T> extends Processor<T> {}

    public static class Component {
        private final String name, context;
        private Map<Class<? extends AbstractObject>, AbstractObject> contextObjCache;

        public Component(String name, String context) {
            if (StringUtils.emptyStringAsNull(name) == null) {
                throw new IllegalArgumentException("Cannot have a nameless attribute!");
            }
            this.name = name;
            this.context = context;
        }

        public String getName() {
            return name;
        }

        public String getContext() {
            return context;
        }

        public AbstractObject getContextAsType(Class<? extends AbstractObject> toClass) {
            if (!contextObjCache.containsKey(toClass)) {
                // TODO: This
            }
            return contextObjCache.get(toClass);
        }

        @Override
        public String toString() {
            return name + (context == null ? "" : "(" + context + ")");
        }
    }

    private final static Map<String, Component[]> COMPONENTS_CACHE = new HashMap<>();

    private static void throwIllegalArgumentForSyntax(String atr, int index, String msg) {
        throw new IllegalArgumentException("Invalid syntax in the attribute string \"" + atr + "\" at index " + index +
                ": " + msg);
    }

    public static Component[] getAttributeComponentsFromString(String str) {
        Component[] finalResult = COMPONENTS_CACHE.get(str);
        if (finalResult != null) {
            return finalResult;
        }

        ArrayList<Component> compList = new ArrayList<>(64);
        int start = 0, end = -1, parens = 0;
        String name = null, context = null;
        boolean quoted = false, isDoubleQuote = false, hadContext = false;
        Map<String, AbstractObject> cValFull = new HashMap<>();

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            switch (c) {
                case '<':
                case '>':
                    if (!quoted && parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray tag mark found.");
                    }
                    break;
                case '(':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        if (hadContext) {
                            throwIllegalArgumentForSyntax(str, i, "Malformed attribute context.");
                        }
                        name = str.substring(start, i);
                        hadContext = true;
                    }
                    parens++;
                    break;
                case ')':
                    if (quoted) {
                        continue;
                    }
                    if (parens <= 0) {
                        throwIllegalArgumentForSyntax(str, i, "Imbalanced parentheses.");
                    }
                    parens--;
                    break;
                case '=':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray equals symbol [=] found.");
                    }
                    // TODO: This
                    break;
                case ';':
                    if (quoted) {
                        continue;
                    }
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray semicolon [;] found.");
                    }
                    // TODO: This
                    break;
                case '"':
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray quote [\"] found.");
                    }
                    if (!quoted) {
                        quoted = true;
                        isDoubleQuote = true;
                    }
                    else if (isDoubleQuote) {
                        quoted = false;
                    }
                    break;
                case '\'':
                    if (parens == 0) {
                        throwIllegalArgumentForSyntax(str, i, "Stray quote ['] found.");
                    }
                    if (!quoted) {
                        quoted = true;
                        isDoubleQuote = false;
                    }
                    else if (!isDoubleQuote) {
                        quoted = false;
                    }
                    break;
                case '.':
                    if (quoted || parens > 0) {
                        continue;
                    }
                    // TODO: This
                    break;
            }
        }

        finalResult = new Component[0];
        compList.trimToSize();
        compList.toArray(finalResult);

        COMPONENTS_CACHE.put(str, finalResult);
        return finalResult;
    }

    private final Component[] comps;
    private final String rawVal;

    private int fulfilled = 0;

    public Attribute() {
        comps = new Component[0];
        rawVal = "";
    }

    public Attribute(String val) {
        comps = getAttributeComponentsFromString(val);
        rawVal = val;
    }

    public Attribute(Component... c) {
        comps = c;

        StringBuilder sb = new StringBuilder();
        for (Component cPart : c) {
            sb.append(cPart.toString()).append('.');
        }
        rawVal = sb.substring(0, sb.length() - 1);
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
        return getComponent(offset, ignoreFulfilled).context != null;
    }

    public String getContext() {
        return getContext(0, false);
    }

    public String getContext(int offset) {
        return getContext(offset, false);
    }

    public String getContext(int offset, boolean ignoreFulfilled) {
        return getComponent(offset, ignoreFulfilled).context;
    }

    public AbstractObject getContextAsType(Class<? extends AbstractObject> typeClass) {
        return getContextAsType(typeClass, 0, false);
    }

    public AbstractObject getContextAsType(Class<? extends AbstractObject> typeClass, int offset) {
        return getContextAsType(typeClass, offset, false);
    }

    public AbstractObject getContextAsType(Class<? extends AbstractObject> typeClass, int offset, boolean ignoreFulfilled) {
        return getComponent(offset, ignoreFulfilled).getContextAsType(typeClass);
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
