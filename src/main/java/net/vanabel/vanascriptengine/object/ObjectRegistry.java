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

    private final static String ATTRIBUTE_HANDLER_FIELD_NAME = "ATTRIBUTE_HANDLER";
    private final static String MODIFIER_HANDLER_FIELD_NAME = "MODIFIER_HANDLER";

    private static MatcherMethod getMatcherFromClass(Class<? extends AbstractObject> objClass) {
        Method m = null;
        boolean mAccess = true;
        try {
            Method[] mArray = ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectMatcher.class);
            if (mArray.length == 0) {
                return null;
            }

            m = mArray[0];
            mAccess = m.isAccessible();
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
            return null;
        }
        finally {
            if (m != null) {
                m.setAccessible(mAccess);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject> ConstructorMethod<T> getConstrFromClass(Class<T> objClass) {
        Method m = null;
        boolean mAccess = true;
        try {
            Method[] mArray = ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectConstructor.class);
            if (mArray.length == 0) {
                return null;
            }

            m = mArray[0];
            mAccess = m.isAccessible();
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
            return null;
        }
        finally {
            if (m != null) {
                m.setAccessible(mAccess);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Attributable> Attribute.Handler<T> getAttrHandFromClass(Class<T> objClass) {
        try {
            return (Attribute.Handler<T>) objClass.getDeclaredField(ATTRIBUTE_HANDLER_FIELD_NAME).get(null);
        }
        catch (Exception e) {
            throw new IllegalStateException("Attributable object \"" + objClass.getSimpleName() + "\" does not have " +
                                            "a field \"ATTRIBUTE_HANDLER\" with an AttributeHandler!");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Modifiable> Modifier.Handler<T> getModHandFromClass(Class<T> objClass) {
        try {
            return (Modifier.Handler<T>) objClass.getDeclaredField(MODIFIER_HANDLER_FIELD_NAME).get(null);
        }
        catch (Exception e) {
            throw new IllegalStateException("Modifiable object \"" + objClass.getSimpleName() + "\" does not have a " +
                                            "field \"MODIFIER_HANDLER\" with a ModifierHandler!");
        }
    }



    private final static Map<Class<? extends AbstractObject>, ObjType<? extends AbstractObject>> CLASS_TO_OBJECT = new HashMap<>();

    public static <T extends DataTypeObject> boolean registerDataType(Class<T> objClass) {
        if (objClass == null) {
            return false;
        }
        ObjType<?> objType = CLASS_TO_OBJECT.get(objClass);
        if (objType != null) {
            // TODO: Debug invalid state for data types, data types are neither attributable nor modifiable
            return false;
        }
        return CLASS_TO_OBJECT.putIfAbsent(objClass, new DataObjType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);

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
        CLASS_TO_OBJECT.put(objClass, new ObjType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);

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
        CLASS_TO_OBJECT.put(objClass, new AttrType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final Attribute.Handler<T> aH = getAttrHandFromClass(objClass);

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
        CLASS_TO_OBJECT.put(objClass, new ModType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final Modifier.Handler<T> mH = getModHandFromClass(objClass);

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
        CLASS_TO_OBJECT.put(objClass, new AttrModType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final Attribute.Handler<T> aH = getAttrHandFromClass(objClass);
            final Modifier.Handler<T> mH = getModHandFromClass(objClass);

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
        try {
            for (Method m : ReflectionHelper.getStaticMethodsForAnnotation(objClass, ObjectCacheClearer.class)) {
                m.setAccessible(true);
                m.invoke(null, m.getAnnotation(ObjectCacheClearer.class).clearDelay());
            }
        }
        catch (Exception e) {
            // Do nothing.
        }
    }

    public static void clearCacheForAll() {
        for (Class<? extends AbstractObject> objClss : CLASS_TO_OBJECT.keySet()) {
            clearCacheFor(objClss);
        }
    }
}
