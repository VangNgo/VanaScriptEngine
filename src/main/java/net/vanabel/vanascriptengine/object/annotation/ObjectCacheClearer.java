package net.vanabel.vanascriptengine.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method declaration is intended to clear a cache for a particular
 * {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObject}. Not all
 * {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObjects} will require a cache.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectCacheClearer {
    int clearDelay() default 300 * 1000;
    long customCheckDelay() default 300 * 1000;
}
