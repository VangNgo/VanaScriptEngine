package net.vanabel.vanascriptengine.object.encapsulated;

import net.vanabel.vanascriptengine.object.AbstractObject;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class SetObject<T extends AbstractObject> extends EncapsulatedObject implements Set<T> {

    @ObjectConstructor
    public static <S extends AbstractObject> SetObject<S> construct(String value) {
        // TODO: Parse?
        return new SetObject<>();
    }

    @ObjectMatcher
    public static boolean matches(String value) {
        // TODO: This
        return false;
    }

    private Set<T> data;

    public SetObject() {
        this(new HashSet<>());
    }

    public SetObject(Set<T> set) {
        data = set;
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
        return null;
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
    public Iterator<T> iterator() {
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
    public boolean add(T o) {
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
    public boolean addAll(Collection<? extends T> c) {
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
