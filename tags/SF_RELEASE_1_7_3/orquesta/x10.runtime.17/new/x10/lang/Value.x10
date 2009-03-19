package x10.lang;

/**
 * Base class of all value classes.
 */
public value Value {
    public native def equals(Object): boolean;
    public native def hashCode(): int;
    public native def toString(): String;
}
