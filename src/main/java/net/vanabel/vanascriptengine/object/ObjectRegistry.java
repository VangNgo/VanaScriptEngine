package net.vanabel.vanascriptengine.object;

import net.vanabel.vanascriptengine.attribute.Attributable;
import net.vanabel.vanascriptengine.attribute.Attribute;
import net.vanabel.vanascriptengine.modifier.Modifiable;
import net.vanabel.vanascriptengine.modifier.Modifier;
import net.vanabel.vanascriptengine.object.annotation.ObjectCacheClearer;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.object.datatype.DataTypeObject;
import net.vanabel.vanascriptengine.util.reflection.ReflectionHelper;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class ObjectRegistry {

    private final static Map<Class<? extends AbstractObject>, ObjType<? extends AbstractObject>> CLASS_TO_OBJECT = new HashMap<>();

    public static <T extends DataTypeObject> boolean registerDataType(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ConstructorMethod<T> c;
        MatcherMethod m;
        try {
            c = getConstrFromClass(objClass);
            m = getMatcherFromClass(objClass);
        }
        catch (Exception e) {
            // TODO: Debug
            return false;
        }
        return CLASS_TO_OBJECT.putIfAbsent(objClass, new DataObjType<T>() {
            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }
        }) == null;
    }

    public static <T extends AbstractObject> boolean registerObject(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ObjType<?> objType = CLASS_TO_OBJECT.get(objClass);
        if (objType != null) {
            if (objType instanceof AttrModType) {
                // TODO: Debug stronger declaration
            }
            else if (objType instanceof ModType) {
                // TODO: Debug stronger declaration
            }
            else if (objType instanceof AttrType) {
                // TODO: Debug stronger declaration
            }
            else if (objType instanceof DataObjType) {
                // TODO: Debug invalid conversion
            }
            // TODO: Debug?
            return false;
        }
        ConstructorMethod<T> c;
        MatcherMethod m;
        try {
            c = getConstrFromClass(objClass);
            m = getMatcherFromClass(objClass);
        }
        catch (Exception e) {
            // TODO: Debug
            return false;
        }
        CLASS_TO_OBJECT.put(objClass, new ObjType<T>() {
            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }
        });
        return true;
    }

    public static <T extends AbstractObject & Attributable> boolean registerAttributableObject(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ObjType<?> objType = CLASS_TO_OBJECT.get(objClass);
        if (objType != null) {
            if (objType instanceof AttrModType) {
                // TODO: Debug stronger declaration
                return false;
            }
            else if (objType instanceof ModType) {
                // TODO: Debug, recommend registerAttributableModifiableObject(Class<?>)
            }
            else if (objType instanceof AttrType) {
                return false;
            }
            else if (objType instanceof DataObjType) {
                // TODO: Debug invalid conversion
                return false;
            }
            // TODO: Debug?
        }
        ConstructorMethod<T> c;
        MatcherMethod m;
        Attribute.Handler<T> aH;
        try {
            c = getConstrFromClass(objClass);
            m = getMatcherFromClass(objClass);
            aH = getAttrHandFromClass(objClass);
        }
        catch (Exception e) {
            // TODO: Debug
            return false;
        }
        CLASS_TO_OBJECT.put(objClass, new AttrType<T>() {
            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public Attribute.Handler<T> aH() {
                return aH;
            }
        });
        return true;
    }

    public static <T extends AbstractObject & Modifiable> boolean registerModifiableObject(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ObjType<?> objType = CLASS_TO_OBJECT.get(objClass);
        if (objType != null) {
            if (objType instanceof AttrModType) {
                // TODO: Debug stronger declaration
                return false;
            }
            else if (objType instanceof AttrType) {
                // TODO: Debug, recommend registerAttributableModifiableObject(Class<?>)
            }
            else if (objType instanceof ModType) {
                return false;
            }
            else if (objType instanceof DataObjType) {
                // TODO: Debug invalid conversion
                return false;
            }
            // TODO: Debug?
        }
        ConstructorMethod<T> c;
        MatcherMethod m;
        Modifier.Handler<T> mH;
        try {
            c = getConstrFromClass(objClass);
            m = getMatcherFromClass(objClass);
            mH = getModHandFromClass(objClass);
        }
        catch (Exception e) {
            // TODO: Debug
            return false;
        }
        CLASS_TO_OBJECT.put(objClass, new ModType<T>() {
            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public Modifier.Handler<T> mH() {
                return mH;
            }
        });
        return true;
    }

    public static <T extends AbstractObject & Attributable & Modifiable> boolean registerAttributableModifiableObject(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ObjType<?> objType = CLASS_TO_OBJECT.get(objClass);
        if (objType != null) {
            if (objType instanceof AttrModType) {
                return false;
            }
            else if (objType instanceof DataObjType) {
                // TODO: Debug invalid conversion
                return false;
            }
            // TODO: Debug, overriding weaker declarations
        }
        ConstructorMethod<T> c;
        MatcherMethod m;
        Attribute.Handler<T> aH;
        Modifier.Handler<T> mH;
        try {
            c = getConstrFromClass(objClass);
            m = getMatcherFromClass(objClass);
            aH = getAttrHandFromClass(objClass);
            mH = getModHandFromClass(objClass);
        }
        catch (Exception e) {
            // TODO: Debug
            return false;
        }
        CLASS_TO_OBJECT.put(objClass, new AttrModType<T>() {
            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public Attribute.Handler<T> aH() {
                return aH;
            }

            @Override
            public Modifier.Handler<T> mH() {
                return mH;
            }
        });
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractObject> Attribute.Handler<?> getAttributeHandlerFor(Class<T> objClass) {
        ObjType<T> objType = (ObjType<T>) CLASS_TO_OBJECT.get(objClass);
        return objType instanceof AttrType ? ((AttrType<?>) objType).aH() : null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractObject> Modifier.Handler<?> getModifierHandlerFor(Class<T> objClass) {
        ObjType<T> objType = (ObjType<T>) CLASS_TO_OBJECT.get(objClass);
        return objType instanceof ModType ? ((ModType<?>) objType).mH() : null;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractObject> T constructForClass(String value, Class<T> objClass) {
        ObjType<T> objType = (ObjType<T>) CLASS_TO_OBJECT.get(objClass);
        if (objType == null) {
            return null;
        }
        return objType.con().construct(value);
    }

    public static <T extends AbstractObject> void clearCacheFor(Class<T> objClass) {
        Method lastMethod = null;
        try {
            for (Method m : ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectCacheClearer.class)) {
                m.setAccessible(true);
                lastMethod = m;
                m.invoke(null, m.getAnnotation(ObjectCacheClearer.class).clearDelay());
                m.setAccessible(false);
            }
        }
        catch (Exception e) {
            // Do nothing.
        }
        finally {
            if (lastMethod != null) {
                lastMethod.setAccessible(false);
            }
        }
    }

    public static void clearCacheForAll() {
        for (Class<? extends AbstractObject> objClss : CLASS_TO_OBJECT.keySet()) {
            clearCacheFor(objClss);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Private helping methods, interfaces, and fields

    private final static String ATTRIBUTE_HANDLER_FIELD_NAME = "ATTRIBUTE_HANDLER";
    private final static String MODIFIER_HANDLER_FIELD_NAME = "MODIFIER_HANDLER";

    @FunctionalInterface
    private interface ConstructorMethod<T extends AbstractObject> {
        T construct(String val);
    }

    @FunctionalInterface
    private interface MatcherMethod {
        boolean matches(String val);
    }

    private interface ObjType<T extends AbstractObject> {
        ConstructorMethod<T> con();
        MatcherMethod mat();
    }

    // Placeholder for data types
    private interface DataObjType<T extends DataTypeObject> extends ObjType<T> {}

    private interface AttrType<T extends AbstractObject & Attributable> extends ObjType<T> {
        Attribute.Handler<T> aH();
    }

    private interface ModType<T extends AbstractObject & Modifiable> extends ObjType<T> {
        Modifier.Handler<T> mH();
    }

    private interface AttrModType<T extends AbstractObject & Attributable & Modifiable> extends AttrType<T>, ModType<T> {}

    private static MatcherMethod getMatcherFromClass(Class<? extends AbstractObject> objClass)
            throws IllegalStateException{
        Method m = null;
        try {
            Method[] mArray = ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectMatcher.class);
            if (mArray.length != 1) {
                throw new IllegalStateException("All AbstractObject implementations must have only one static " +
                        "method with the ObjectMatcher annotation!");
            }

            m = mArray[0];
            m.setAccessible(true);
            String methodName = m.getName();
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType typeForBoolean = MethodType.methodType(Boolean.class, String.class).unwrap();
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "matches",
                    MethodType.methodType(MatcherMethod.class),
                    typeForBoolean,
                    lookup.findStatic(objClass, methodName, typeForBoolean),
                    typeForBoolean
            );
            return (MatcherMethod) site.getTarget().invoke();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Could not fetch a matcher method from " + objClass.getName() + "!");
        }
        finally {
            if (m != null) {
                m.setAccessible(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject> ConstructorMethod<T> getConstrFromClass(Class<T> objClass)
            throws IllegalStateException {
        Method m = null;
        try {
            Method[] mArray = ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectConstructor.class);
            if (mArray.length != 1) {
                throw new IllegalStateException("All AbstractObject implementations must have only one static " +
                        "method with the ObjectConstructor annotation!");
            }

            m = mArray[0];
            m.setAccessible(true);
            String methodName = m.getName();
            final MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodType type = MethodType.methodType(objClass, String.class);
            CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "construct",
                    MethodType.methodType(ConstructorMethod.class),
                    MethodType.methodType(AbstractObject.class, String.class),
                    lookup.findStatic(objClass, methodName, type),
                    type
            );
            return (ConstructorMethod<T>) site.getTarget().invoke();
        }
        catch (Throwable t) {
            throw new IllegalStateException("Could not fetch a construction method from " + objClass.getName() + "!");
        }
        finally {
            if (m != null) {
                m.setAccessible(false);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Attributable> Attribute.Handler<T> getAttrHandFromClass(Class<T> objClass)
            throws IllegalStateException {
        try {
            return (Attribute.Handler<T>) objClass.getDeclaredField(ATTRIBUTE_HANDLER_FIELD_NAME).get(null);
        }
        catch (Exception e) {
            throw new IllegalStateException("Attributable object \"" + objClass.getSimpleName() + "\" does not have " +
                    "a field \"ATTRIBUTE_HANDLER\" with an AttributeHandler!");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Modifiable> Modifier.Handler<T> getModHandFromClass(Class<T> objClass)
            throws IllegalStateException {
        try {
            return (Modifier.Handler<T>) objClass.getDeclaredField(MODIFIER_HANDLER_FIELD_NAME).get(null);
        }
        catch (Exception e) {
            throw new IllegalStateException("Modifiable object \"" + objClass.getSimpleName() + "\" does not have a " +
                    "field \"MODIFIER_HANDLER\" with a ModifierHandler!");
        }
    }
}
