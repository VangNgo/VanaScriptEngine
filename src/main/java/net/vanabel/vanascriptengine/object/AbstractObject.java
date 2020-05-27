package net.vanabel.vanascriptengine.object;

public abstract class AbstractObject implements Cloneable {

    /**
     * For debugging purposes only.
     * @return This object's Java class name.
     */
    public final String getSimpleClassName() {
        return getClass().getSimpleName();
    }

    /**
     * A simplified String representation of this object.
     * @return A simplified String representation of this object. If no such
     *        representation exists, then {@link #toString()} is used instead.
     */
    public String toSimpleString() {
        return toString();
    }

    /**
     * A String representation of this object.
     * @return A String representation of this object.
     */
    @Override
    public abstract String toString();

    /**
     * Returns a clone of this object.
     * @return A clone of this object.
     */
    public abstract AbstractObject clone();
}
