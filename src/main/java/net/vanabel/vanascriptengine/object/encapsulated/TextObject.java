package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.attribute.AttributeHandler;
import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;

public class TextObject extends EncapsulatedObject {

    public final static EncapsulatedAttributeHandler<TextObject> ATTRIBUTE_HANDLER = new EncapsulatedAttributeHandler<>();

    @ObjectConstructor
    public static TextObject construct(String val) {
        if (val == null) {
            throw new IllegalArgumentException("Cannot use a null string!");
        }
        return new TextObject(val);
    }

    @ObjectMatcher
    public static boolean matches(String val) {
        return true;
    }

    private final String value;

    public TextObject(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public AbstractObject clone() {
        return new TextObject(value);
    }
}
