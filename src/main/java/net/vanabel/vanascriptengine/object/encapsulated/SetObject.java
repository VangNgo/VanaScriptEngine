package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.ObjectRegistry;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.object.datatype.BooleanDataType;
import net.vanabel.vanascriptengine.object.datatype.IntegerDataType;
import net.vanabel.vanascriptengine.tag.attribute.Attribute;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetObject extends EncapsulatedObject implements Set<AbstractObject> {

    public final static AttributeHandler<SetObject> ATTRIBUTE_HANDLER = new AttributeHandler<>();

    public static void registerAttributes(Attribute.Processor<SetObject> processor, String... names) {
        ATTRIBUTE_HANDLER.registerAttributes(processor, names);
    }

    static {
        registerAttributes(
                (Attribute.DirectProcessor<SetObject>) (object, attribute) -> new IntegerDataType(object.size()),
                "size"
        );
        registerAttributes(
                (Attribute.DirectProcessor<SetObject>) (object, attribute) -> BooleanDataType.getForBoolean(object.isEmpty()),
                "is_empty", "isEmpty"
        );
        registerAttributes(
                (object, attribute) -> {
                    Class<? extends AbstractObject> clss = object.clss;
                    if (clss == AbstractObject.class) {
                        return new TextObject("none");
                    }
                    // TODO: This
                    return null;
                }, "get_object_restriction", "getObjectRestriction"
        );
        registerAttributes(
                (Attribute.DirectProcessor<SetObject>) (object, attribute) -> {
                    if (!attribute.hasContext()) {
                        // TODO: Debug
                        return BooleanDataType.getForBoolean(false);
                    }
                    // TODO: This
                    return null;
                }, "contains"
        );
        registerAttributes(
                (object, attribute) -> {
                    if (!attribute.hasContext()) {
                        // TODO: Debug
                        return null;
                    }
                    AbstractObject addObj = ObjectRegistry.constructForClass(attribute.getContext().getRaw(), object.clss);
                    if (addObj == null) {
                        // TODO: Debug
                        return object;
                    }
                    object.add(addObj);
                    return object;
                }, "add"
        );
    }

    @ObjectConstructor
    public static SetObject construct(String value) {
        // TODO: Parse?
        return new SetObject();
    }

    @ObjectMatcher
    public static boolean matches(String value) {
        // TODO: This
        return false;
    }

    private final Class<? extends AbstractObject> clss;
    private final Set<AbstractObject> data;

    public SetObject() {
        this(AbstractObject.class);
    }

    public SetObject(Class<? extends AbstractObject> clss) {
        this.clss = clss;
        this.data = new HashSet<>();
    }

    @SuppressWarnings("unchecked")
    public SetObject(Set<? extends AbstractObject> set) {
        data = (Set<AbstractObject>) set;
        try {
            clss = (Class<AbstractObject>) Class.forName(getClass().getField("data").getGenericType().getTypeName());
        }
        catch (Exception e) {
            throw new RuntimeException("Unexpected error while constructing a SetObject!");
        }
    }

    @Override
    public String getObjectTypeName() {
        return "set";
    }

    @Override
    public String getObjectTypeNamePlural() {
        return "sets";
    }

    @Override
    public String toString() {
        if (data.isEmpty()) {
            return "set@" + hashCode() + "[]";
        }
        StringBuilder sb = new StringBuilder("set@").append(hashCode()).append('[');
        for (AbstractObject obj : data) {
            sb.append(obj.toString()).append(" , ");
        }
        return sb.substring(0, sb.length() - 3) + "]";
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return data.contains(o);
    }

    @Override
    public Iterator<AbstractObject> iterator() {
        return data.iterator();
    }

    @Override
    public Object[] toArray() {
        return data.toArray();
    }

    @Override
    public <S> S[] toArray(S[] a) {
        return data.toArray(a);
    }

    @Override
    public boolean add(AbstractObject o) {
        return data.add(o);
    }

    @Override
    public boolean remove(Object o) {
        return data.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return data.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends AbstractObject> c) {
        return data.addAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return data.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return data.removeAll(c);
    }

    @Override
    public void clear() {
        data.clear();
    }
}
