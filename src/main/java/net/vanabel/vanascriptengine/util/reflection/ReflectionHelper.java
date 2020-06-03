package net.vanabel.vanascriptengine.util.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ReflectionHelper {

    private final static Map<Class<?>, Map<Class<? extends Annotation>, Set<Method>>> ANNOTATED_METHOD_CACHE = new HashMap<>();

    public static Method[] getMethodsForAnnotation(Class<?> clss, Class<? extends Annotation> aClss) {
        if (clss == null || aClss == null) {
            return null;
        }
        Map<Class<? extends Annotation>, Set<Method>> methodCache = ANNOTATED_METHOD_CACHE.computeIfAbsent(clss, k -> new HashMap<>());
        Set<Method> methodSet = methodCache.get(aClss);
        if (methodSet == null) {
            methodSet = methodCache.computeIfAbsent(aClss, k -> new HashSet<>());
            for (Method m : clss.getDeclaredMethods()) {
                if (m.isAnnotationPresent(aClss)) {
                    methodSet.add(m);
                }
            }
        }
        return methodSet.toArray(new Method[0]);
    }
}
