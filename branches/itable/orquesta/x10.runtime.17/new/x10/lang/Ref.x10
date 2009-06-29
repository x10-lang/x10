package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The base class for all reference classes.
 */
@NativeRep("java", "x10.core.Ref")
public class Ref(
    @Native("java", "x10.runtime.Runtime.location(#0)")
    location: Place
    ) {
    
    public native def this();
    
    @Native("java", "(#1).equals(#2)")
    public native def equals(Object): boolean;
    
    @Native("java", "(#1).hashCode()")
    public native def hashCode(): int;
    
    @Native("java", "(#1).toString()")
    public native def toString(): String;
}
