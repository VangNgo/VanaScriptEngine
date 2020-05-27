package net.vanabel.vanascriptengine.attribute;

/**
 * Represents an attributable object within this script engine.
 * If a class implements this interface, it should have a static field containing an {@link AttributeHandler} that
 * {@link #getAttributeHandler()} returns.
 */
public interface Attributable {

    /**
     * Gets this attributable object's {@link AttributeHandler}.
     * @return This object's AttributeHandler.
     */
    AttributeHandler<?> getAttributeHandler();
}
