package net.vanabel.vanascriptengine.object;

import net.vanabel.vanascriptengine.attribute.Attributable;
import net.vanabel.vanascriptengine.attribute.AttributeHandler;
import net.vanabel.vanascriptengine.modifier.Modifiable;
import net.vanabel.vanascriptengine.modifier.ModifierHandler;
import net.vanabel.vanascriptengine.object.annotation.ObjectConstructor;
import net.vanabel.vanascriptengine.object.annotation.ObjectMatcher;
import net.vanabel.vanascriptengine.object.datatype.DataTypeObject;

import java.lang.annotation.Annotation;
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
        AttributeHandler<T> aH();
    }

    private interface ModType<T extends AbstractObject & Modifiable> extends ObjType<T> {
        ModifierHandler<T> mH();
    }

    private interface AttrModType<T extends AbstractObject & Attributable & Modifiable> extends AttrType<T>, ModType<T> {}

    private final static String ATTRIBUTE_HANDLER_FIELD_NAME = "ATTRIBUTE_HANDLER";
    private final static String MODIFIER_HANDLER_FIELD_NAME = "MODIFIER_HANDLER";

    private static MatcherMethod getMatcherFromClass(Class<? extends AbstractObject> objClass) {
        try {
            String methodName = null;
            for (Method m : objClass.getDeclaredMethods()) {
                for (Annotation a : m.getAnnotations()) {
                    if (a.annotationType() == ObjectMatcher.class) {
                        methodName = m.getName();
                        break;
                    }
                }
            }

            if (methodName == null) {
                return null;
            }

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
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject> ConstructorMethod<T> getConstrFromClass(Class<T> objClass) {
        try {
            String methodName = null;
            for (Method m : objClass.getDeclaredMethods()) {
                for (Annotation a : m.getAnnotations()) {
                    if (a.annotationType() == ObjectConstructor.class) {
                        methodName = m.getName();
                        break;
                    }
                }
            }

            if (methodName == null) {
                return null;
            }

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
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Attributable> AttributeHandler<T> getAttrHandFromClass(Class<T> objClass) {
        try {
            return (AttributeHandler<T>) objClass.getDeclaredField(ATTRIBUTE_HANDLER_FIELD_NAME).get(null);
        }
        catch (Exception e) {
            throw new IllegalStateException("Attributable object \"" + objClass.getSimpleName() + "\" does not have " +
                                            "a field \"ATTRIBUTE_HANDLER\" with an AttributeHandler!");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T extends AbstractObject & Modifiable> ModifierHandler<T> getModHandFromClass(Class<T> objClass) {
        try {
            return (ModifierHandler<T>) objClass.getDeclaredField(MODIFIER_HANDLER_FIELD_NAME).get(null);
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
            if (objType instanceof ModType) {
                // TODO: Debug, recommend registerAttributableModifiableObject(Class<?>)
                return false;
            }
            if (objType instanceof AttrType) {
                return false;
            }
            // TODO: Debug?
        }
        CLASS_TO_OBJECT.put(objClass, new AttrType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final AttributeHandler<T> aH = getAttrHandFromClass(objClass);

            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public AttributeHandler<T> aH() {
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
            if (objType instanceof AttrType) {
                // TODO: Debug, recommend registerAttributableModifiableObject(Class<?>)
            }
            if (objType instanceof ModType) {
                return false;
            }
            // TODO: Debug?
        }
        CLASS_TO_OBJECT.put(objClass, new ModType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final ModifierHandler<T> mH = getModHandFromClass(objClass);

            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public ModifierHandler<T> mH() {
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
            // TODO: Debug, overriding weaker declarations
        }
        CLASS_TO_OBJECT.put(objClass, new AttrModType<T>() {
            final ConstructorMethod<T> c = getConstrFromClass(objClass);
            final MatcherMethod m = getMatcherFromClass(objClass);
            final AttributeHandler<T> aH = getAttrHandFromClass(objClass);
            final ModifierHandler<T> mH = getModHandFromClass(objClass);

            @Override
            public ConstructorMethod<T> con() {
                return c;
            }

            @Override
            public MatcherMethod mat() {
                return m;
            }

            @Override
            public AttributeHandler<T> aH() {
                return aH;
            }

            @Override
            public ModifierHandler<T> mH() {
                return mH;
            }
        });
        return true;
    }
}
