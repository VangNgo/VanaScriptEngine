package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.attribute.Attributable;
import net.vanabel.vanascriptengine.attribute.Attribute;
import net.vanabel.vanascriptengine.modifier.Modifiable;
import net.vanabel.vanascriptengine.modifier.Modifier;
import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.Downgradeable;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

import java.util.Map;

/**
 * Represents an encapsulated object type. All objects of this type are cloneable.
 */
public abstract class EncapsulatedObject extends AbstractObject implements Attributable, Cloneable {

    public static class AttributeHandler<T extends EncapsulatedObject> extends Attribute.Handler<T> {
        // TODO: Universal attributes?

        @Override
        @SuppressWarnings("unchecked")
        public AbstractObject processAttribute(T object, Attribute attribute) {
            if (object == null || attribute == null) {
                // TODO: Debug or exception?
                return null;
            }
            if (attribute.isComplete()) {
                // TODO: Debug
                return object;
            }

            int previousFulfilled = attribute.getFulfilledCount();
            String aName = attribute.getName();
            AbstractObject result = null;
            Attribute.Processor<T> processor = getProcessorForAttribute(aName);

            if (processor != null) {
                if (!(processor instanceof Attribute.DirectProcessor)) {
                    object = (T) object.clone();
                }
                result = processor.process(object, attribute);
            }
            if (result != null) {
                attribute.fulfill();
                return result;
            }

            if (object instanceof Downgradeable) {
                AbstractObject downgrade = ((Downgradeable) object).downgrade();
                if (downgrade instanceof Attributable) {
                    Attributable atr = (Attributable) downgrade;
                    // TODO: Downgrade object and re-process using downgraded object's attribute handler
                    return null;
                }
            }
            return null;
        }
    }

    public static class ModifierHandler<T extends EncapsulatedObject & Modifiable> extends Modifier.Handler<T> {

        @Override
        public boolean processModifier(T object, Modifier modifier) {
            if (object == null || modifier == null) {
                // TODO: Debug or exception?
                return false;
            }

            String mName = modifier.getName();
            Modifier.Processor<T> processor = getProcessorForModifier(mName);

            boolean isProcessed = processor.process(object, modifier);

            if (!isProcessed) {
                // TODO: Debug
            }
            return isProcessed;
        }
    }

    public abstract String getObjectTypeName();

    public abstract String getObjectTypeNamePlural();

    public Map<String, AbstractObject> getCustomData() {
        throw new IllegalStateException(StringUtils.capitalize(getObjectTypeNamePlural()) + " do not support" +
                "custom data!");
    }

    @Override
    public EncapsulatedObject clone() {
        try {
            return (EncapsulatedObject) super.clone();
        }
        catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
}
