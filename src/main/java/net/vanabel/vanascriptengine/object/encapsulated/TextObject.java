package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.attribute.Attribute;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.object.datatype.BooleanDataType;
import net.vanabel.vanascriptengine.object.datatype.IntegerDataType;
import net.vanabel.vanascriptengine.util.conversion.StringUtils;

public class TextObject extends EncapsulatedObject {

    public final static AttributeHandler<TextObject> ATTRIBUTE_HANDLER = new AttributeHandler<>();
    public final static TextObject EMPTY = new TextObject("");

    public static void registerAttributes(Attribute.Processor<TextObject> processor, String... names) {
        ATTRIBUTE_HANDLER.registerAttributes(processor, names);
    }

    static {
        registerAttributes(
                (Attribute.DirectProcessor<TextObject>) (object, attribute) ->
                        BooleanDataType.getForBoolean(object.value.isEmpty()),
                "is_empty", "isEmpty"
        );
        registerAttributes(
                (Attribute.DirectProcessor<TextObject>) (object, attribute) ->
                        new IntegerDataType(object.value.length()),
                "length"
        );
        registerAttributes(
                (Attribute.DirectProcessor<TextObject>) (object, attribute) -> {
                        if (!attribute.hasContext()) {
                            // TODO: Debug
                            return null;
                        }
                        Integer start = StringUtils.toInteger(attribute.getContext().getRaw());
                        Integer end = object.value.length();
                        if (attribute.getContext().isMap()) {
                            start = StringUtils.toInteger(attribute.getContext().getKeyValue("start"));
                            end = StringUtils.toInteger(attribute.getContext().getKeyValue("end"));
                        }
                        if (start == null || end == null) {
                            // TODO: Debug
                            return null;
                        }
                        return new TextObject(object.value.substring(start, end));
                }, "substring", "substr"
        );
        registerAttributes(
                (Attribute.DirectProcessor<TextObject>) (object, attribute) -> new TextObject(StringUtils.toUpperCase(object.value)),
                "to_uppercase", "toUppercase"
        );
        registerAttributes(
                (Attribute.DirectProcessor<TextObject>) (object, attribute) -> new TextObject(StringUtils.toLowerCase(object.value)),
                "to_lowercase", "toLowercase"
        );
        // TODO: Attributes
    }

    @ObjectConstructor
    public static TextObject construct(String val) {
        if (val == null) {
            throw new IllegalArgumentException("Cannot use a null string!");
        }
        return val.isEmpty() ? EMPTY : new TextObject(val);
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
    public String getObjectTypeName() {
        return "text";
    }

    @Override
    public String getObjectTypeNamePlural() {
        return "texts";
    }

    @Override
    public String toString() {
        return value;
    }
}
