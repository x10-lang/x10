package x10.lang;

/**
 * The base class for all reference classes.
 */
public class Ref(location: Place) {
    public def this() = property(here);
    public native def equals(Object): boolean;
    public native def hashCode(): int;
    public native def toString(): String;
}
