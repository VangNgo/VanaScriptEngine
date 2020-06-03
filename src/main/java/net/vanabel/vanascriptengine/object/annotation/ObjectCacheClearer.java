package net.vanabel.vanascriptengine.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectCacheClearer {
    int clearDelay() default 300 * 1000;
    long customCheckDelay() default 300 * 1000;
}
