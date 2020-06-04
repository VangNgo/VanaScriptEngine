package net.vanabel.vanascriptengine.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ReflectionHelper {

    private final static Map<Class<?>, Map<Class<? extends Annotation>, Set<Method>>> ANNOTATED_STATIC_METHOD_CACHE = new HashMap<>();
    private final static Map<Class<?>, Map<Class<? extends Annotation>, Set<Method>>> ANNOTATED_INSTANCE_METHOD_CACHE = new HashMap<>();
    private final static Map<Class<?>, Map<Class<? extends Annotation>, Set<Field>>> ANNOTATED_STATIC_FIELD_CACHE = new HashMap<>();
    private final static Map<Class<?>, Map<Class<? extends Annotation>, Set<Field>>> ANNOTATED_INSTANCE_FIELD_CACHE = new HashMap<>();

    public static Field[] getStaticFieldsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss) {
        return getStaticFieldsForAnnotation(clss, aClss, false);
    }

    public static Field[] getStaticFieldsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (clss == null || aClss == null) {
            return null; // Quietly fail
        }

        Set<Field> fieldSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous class to the cache!
        if (!clss.isAnonymousClass()) {
            Map<Class<? extends Annotation>, Set<Field>> fieldCache = ANNOTATED_STATIC_FIELD_CACHE
                    .computeIfAbsent(clss, k -> new HashMap<>());
            if (fieldCache.containsKey(aClss)) {
                fieldSet = fieldCache.get(aClss);
                isFilled = true;
            }
            else {
                fieldCache.put(aClss, fieldSet);
            }
        }

        if (!isFilled) {
            for (Field f : clss.getDeclaredFields()) {
                f.setAccessible(true);
                int mod = f.getModifiers();
                if (Modifier.isStatic(mod) && (!onlyPublic || Modifier.isPublic(mod)) && f.isAnnotationPresent(aClss)) {
                    fieldSet.add(f);
                }
                f.setAccessible(false);
            }
        }
        return fieldSet.toArray(new Field[0]);
    }

    public static Field[] getInstanceFieldsForAnnotation(Object obj, Class<? extends Annotation> aClss) {
        return getInstanceFieldsForAnnotation(obj, aClss, false);
    }

    public static Field[] getInstanceFieldsForAnnotation(Object obj, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (obj == null || aClss == null) {
            return null; // Quietly fail
        }

        Class<?> clss = obj.getClass();
        Set<Field> fieldSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous class to the cache!
        if (!clss.isAnonymousClass()) {
            Map<Class<? extends Annotation>, Set<Field>> fieldCache = ANNOTATED_INSTANCE_FIELD_CACHE
                    .computeIfAbsent(clss, k -> new HashMap<>());
            if (fieldCache.containsKey(aClss)) {
                fieldSet = fieldCache.get(aClss);
                isFilled = true;
            }
            else {
                fieldCache.put(aClss, fieldSet);
            }
        }

        if (!isFilled) {
            for (Field f : clss.getDeclaredFields()) {
                f.setAccessible(true);
                int mod = f.getModifiers();
                if (!Modifier.isStatic(mod) && (!onlyPublic || Modifier.isPublic(mod)) && f.isAnnotationPresent(aClss)) {
                    fieldSet.add(f);
                }
                f.setAccessible(false);
            }
        }
        return fieldSet.toArray(new Field[0]);
    }

    public static Method[] getStaticMethodsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss) {
        return getStaticMethodsForAnnotation(clss, aClss, false);
    }

    public static Method[] getStaticMethodsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (clss == null || aClss == null) {
            return null; // Quietly fail
        }

        Set<Method> methodSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous class to the cache!
        if (!clss.isAnonymousClass()) {
            Map<Class<? extends Annotation>, Set<Method>> methodCache = ANNOTATED_STATIC_METHOD_CACHE
                    .computeIfAbsent(clss, k -> new HashMap<>());
            if (methodCache.containsKey(aClss)) {
                methodSet = methodCache.get(aClss);
                isFilled = true;
            }
            else {
                methodCache.put(aClss, methodSet);
            }
        }

        if (!isFilled) {
            for (Method m : clss.getDeclaredMethods()) {
                m.setAccessible(true);
                int mod = m.getModifiers();
                if (Modifier.isStatic(mod) && (!onlyPublic || Modifier.isPublic(mod)) && m.isAnnotationPresent(aClss)) {
                    methodSet.add(m);
                }
                m.setAccessible(false);
            }
        }
        return methodSet.toArray(new Method[0]);
    }

    public static Method[] getInstanceMethodsForAnnotation(Object obj, Class<? extends Annotation> aClss) {
        return getInstanceMethodsForAnnotation(obj, aClss, false);
    }

    public static Method[] getInstanceMethodsForAnnotation(Object obj, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (obj == null || aClss == null) {
            return null; // Quietly fail
        }

        Class<?> clss = obj.getClass();
        Set<Method> methodSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous object to the cache!
        if (!clss.isAnonymousClass()) {
            Map<Class<? extends Annotation>, Set<Method>> methodCache = ANNOTATED_INSTANCE_METHOD_CACHE
                    .computeIfAbsent(clss, k -> new HashMap<>());
            if (methodCache.containsKey(aClss)) {
                methodSet = methodCache.get(aClss);
                isFilled = true;
            }
            else {
                methodCache.put(aClss, methodSet);
            }
        }

        if (!isFilled) {
            for (Method m : obj.getClass().getMethods()) {
                m.setAccessible(true);
                int mod = m.getModifiers();
                if (!Modifier.isStatic(mod) && (!onlyPublic || Modifier.isPublic(mod)) && m.isAnnotationPresent(aClss)) {
                    methodSet.add(m);
                }
                m.setAccessible(false);
            }
        }
        return methodSet.toArray(new Method[0]);
    }
}
