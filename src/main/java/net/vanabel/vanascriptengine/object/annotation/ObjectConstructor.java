package net.vanabel.vanascriptengine.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method declaration can create a
 * {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObject} from a String. All
 * {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObjects} will require one
 * static method that uses this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectConstructor {
}
