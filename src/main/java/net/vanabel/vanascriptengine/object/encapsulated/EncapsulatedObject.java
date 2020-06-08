package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.modifier.Modifiable;
import net.vanabel.vanascriptengine.modifier.Modifier;
import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.Downgradeable;
import net.vanabel.vanascriptengine.object.ObjectRegistry;
import net.vanabel.vanascriptengine.object.datatype.BooleanDataType;
import net.vanabel.vanascriptengine.tag.attribute.Attributable;
import net.vanabel.vanascriptengine.tag.attribute.Attribute;

/**
 * Represents an encapsulated object type. All objects of this type are cloneable.
 */
public abstract class EncapsulatedObject extends AbstractObject implements Attributable, Cloneable {

    public static class AttributeHandler<T extends EncapsulatedObject> extends Attribute.Handler<T> {

        public AttributeHandler() {
            ////////////////////////////////////////////////////////////////////
            // Universal attributes shared across all objects
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> new TextObject(object.getObjectTypeName()),
                    "object_type", "objectType"
            );
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> object.clone(),
                    "clone"
            );
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> {
                        SetObject<TextObject> names = new SetObject<>();
                        for (String a : getAttributes()) {
                            names.add(new TextObject(a));
                        }
                        return names;
                    },
                    "valid_attributes", "validAttributes"
            );

            ////////////////////////////////////////////////////////////////////
            // Downgrade (equivalent to casting upwards in Java)
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> BooleanDataType.getForBoolean(object instanceof Downgradeable),
                    "downgradeable"
            );
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> {
                        if (object instanceof Downgradeable) {
                            return ((Downgradeable) object).downgrade();
                        }
                        return null;
                    },
                    "downgrade"
            );

            ////////////////////////////////////////////////////////////////////
            // Modifiable
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> BooleanDataType.getForBoolean(object instanceof Modifiable),
                    "modifiable"
            );
            registerAttributes(
                    (Attribute.DirectProcessor<T>) (object, attribute) -> {
                        SetObject<TextObject> names = new SetObject<>();
                        Modifier.Handler<?> modHand = ObjectRegistry.getModifierHandlerFor(object.getClass());
                        if (modHand != null) {
                            for (String m : modHand.getModifiers()) {
                                names.add(new TextObject(m));
                            }
                            return names;
                        }
                        return null;
                    },
                    "valid_modifiers", "validModifiers"
            );
        }

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
            Attribute.Processor<T> processor = getProcessorFor(aName);

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
