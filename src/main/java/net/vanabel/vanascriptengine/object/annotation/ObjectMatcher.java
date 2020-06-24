package net.vanabel.vanascriptengine.object.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a method declaration will return whether a String can be constructed as a
 * particular {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObject}. All
 * {@link net.vanabel.vanascriptengine.object.AbstractObject AbstractObjects} will require one
 * static method that uses this annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ObjectMatcher {
}
