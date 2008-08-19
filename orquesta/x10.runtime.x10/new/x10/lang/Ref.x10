package x10.lang;

import x10.compiler.Native;

/**
 * The base class for all reference classes.
 */
public class Ref(@Native("java", "x10.lang.Runtime.location(#0)") location: Place) {
    public def this() = property(here);
    public native def equals(Object): boolean;
    public native def hashCode(): int;
    public native def toString(): String;
}
