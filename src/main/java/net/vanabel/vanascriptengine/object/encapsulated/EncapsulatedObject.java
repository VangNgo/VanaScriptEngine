package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.attribute.Attributable;
import net.vanabel.vanascriptengine.attribute.Attribute;
import net.vanabel.vanascriptengine.attribute.AttributeHandler;
import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.Downgradeable;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

import java.util.Map;

public abstract class EncapsulatedObject extends AbstractObject implements Attributable {

    public static class EncapsulatedAttributeHandler<T extends EncapsulatedObject> extends AttributeHandler<T> {
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

    public abstract String getObjectTypeName();

    public abstract String getObjectTypeNamePlural();

    public Map<String, AbstractObject> getCustomData() {
        throw new IllegalStateException(StringUtils.capitalize(getObjectTypeNamePlural()) + " do not support" +
                "custom data!");
    }
}
