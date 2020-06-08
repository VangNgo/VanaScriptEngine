package net.vanabel.vanascriptengine.tag.base;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.tag.attribute.Attributable;
import net.vanabel.vanascriptengine.tag.attribute.Attribute;

public abstract class AbstractTagBase {

    protected final String name, context;

    public AbstractTagBase(String name, String context) {
        if (name == null) {
            throw new IllegalStateException("Cannot have a null name for a tag base!");
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

    public boolean requiresContext() {
        return false;
    }

    /**
     * Returns the {@link AbstractObject} that can be created from this tag base.
     * If this tag base implements {@link net.vanabel.vanascriptengine.tag.attribute.Attributable Attributable} and this
     * method returns {@code null}, then the attribute immediately following the tag base will be processed.
     * @return An AbstractObject or null.
     */
    public abstract AbstractObject getObjectResult();

    @Override
    public String toString() {
        return name + (context == null ? "" : "(" + context + ")");
    }

    protected static class AttributeProcessor<T extends AbstractTagBase & Attributable> extends Attribute.Handler<T> {

        @Override
        public AbstractObject processAttribute(T tagBase, Attribute attribute) {
            if (tagBase == null) {
                return null;
            }
            if (attribute == null) {
                throw new IllegalStateException("Cannot process a null attribute!");
            }
            AbstractObject result = null;
            if (attribute.isComplete()) {
                result = tagBase.getObjectResult();
                if (result == null) {
                    // TODO: Debug
                }
                return result;
            }

            Attribute.Processor<T> processor = attributes.get(attribute.getName());
            if (processor != null) {
                result = processor.process(tagBase, attribute);
                if (result != null) {
                    attribute.fulfill();
                    return result;
                }
            }

            // TODO: Debug
            return null;
        }
    }
}
