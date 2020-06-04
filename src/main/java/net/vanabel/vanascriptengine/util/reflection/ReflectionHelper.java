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

        return fetchAnnotatedFields(clss, aClss, ANNOTATED_STATIC_FIELD_CACHE, true, onlyPublic);
    }

    public static Field[] getInstanceFieldsForAnnotation(Object obj, Class<? extends Annotation> aClss) {
        return getInstanceFieldsForAnnotation(obj, aClss, false);
    }

    public static Field[] getInstanceFieldsForAnnotation(Object obj, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (obj == null || aClss == null) {
            return null; // Quietly fail
        }

        return fetchAnnotatedFields(obj.getClass(), aClss, ANNOTATED_INSTANCE_FIELD_CACHE, false, onlyPublic);
    }

    public static Method[] getStaticMethodsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss) {
        return getStaticMethodsForAnnotation(clss, aClss, false);
    }

    public static Method[] getStaticMethodsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (clss == null || aClss == null) {
            return null; // Quietly fail
        }

        return fetchAnnotatedMethods(clss, aClss, ANNOTATED_STATIC_METHOD_CACHE, true, onlyPublic);
    }

    public static Method[] getInstanceMethodsForAnnotation(Object obj, Class<? extends Annotation> aClss) {
        return getInstanceMethodsForAnnotation(obj, aClss, false);
    }

    public static Method[] getInstanceMethodsForAnnotation(Object obj, Class<? extends Annotation> aClss, boolean onlyPublic) {
        if (obj == null || aClss == null) {
            return null; // Quietly fail
        }

        return fetchAnnotatedMethods(obj.getClass(), aClss, ANNOTATED_INSTANCE_METHOD_CACHE, false, onlyPublic);
    }

    private static Field[] fetchAnnotatedFields(Class<?> clss, Class<? extends Annotation> aClss, Map<Class<?>,
            Map<Class<? extends Annotation>, Set<Field>>> cache, boolean isStatic, boolean onlyPublic) {
        Map<Class<? extends Annotation>, Set<Field>> fieldCache = cache.computeIfAbsent(clss, k -> new HashMap<>());
        Set<Field> fieldSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous class to the cache!
        if (!clss.isAnonymousClass()) {
            if (fieldCache.containsKey(aClss)) {
                fieldSet = fieldCache.get(aClss);
                isFilled = true;
            }
            else {
                fieldCache.put(aClss, fieldSet);
            }
        }

        if (!isFilled) {
            Field lastField = null;
            try {
                for (Field f : clss.getDeclaredFields()) {
                    lastField = f;
                    f.setAccessible(true);
                    int mod = f.getModifiers();
                    boolean fStatic = Modifier.isStatic(f.getModifiers());
                    if (((isStatic && fStatic) || (!isStatic && !fStatic)) &&
                            (!onlyPublic || Modifier.isPublic(mod)) &&
                            f.isAnnotationPresent(aClss)) {
                        fieldSet.add(f);
                    }
                    f.setAccessible(false);
                }
            }
            // If an exception is encountered, don't cache the improperly-populated set and quietly fail
            catch (Exception e) {
                if (lastField != null) {
                    lastField.setAccessible(false);
                }
                fieldCache.remove(aClss);
                return null;
            }
        }
        return fieldSet.toArray(new Field[0]);
    }

    private static Method[] fetchAnnotatedMethods(Class<?> clss, Class<? extends Annotation> aClss,
            Map<Class<?>, Map<Class<? extends Annotation>, Set<Method>>> cache, boolean isStatic, boolean onlyPublic) {
        Map<Class<? extends Annotation>, Set<Method>> methodCache = cache.computeIfAbsent(clss, k -> new HashMap<>());
        Set<Method> methodSet = new HashSet<>();
        boolean isFilled = false;

        // Never send an anonymous object to the cache!
        if (!clss.isAnonymousClass()) {
            if (methodCache.containsKey(aClss)) {
                methodSet = methodCache.get(aClss);
                isFilled = true;
            }
            else {
                methodCache.put(aClss, methodSet);
            }
        }

        if (!isFilled) {
            Method lastMethod = null;
            try {
                for (Method m : clss.getMethods()) {
                    lastMethod = m;
                    m.setAccessible(true);
                    int mod = m.getModifiers();
                    boolean mStatic = Modifier.isStatic(m.getModifiers());
                    if (((isStatic && mStatic) || (!isStatic && !mStatic)) &&
                            (!onlyPublic || Modifier.isPublic(mod)) &&
                            m.isAnnotationPresent(aClss)) {
                        methodSet.add(m);
                    }
                    m.setAccessible(false);
                }
            }
            // If an exception is encountered, don't cache the improperly-populated set and quietly fail
            catch (Exception e) {
                if (lastMethod != null) {
                    lastMethod.setAccessible(false);
                }
                methodCache.remove(aClss);
                return null;
            }
        }
        return methodSet.toArray(new Method[0]);
    }
}
