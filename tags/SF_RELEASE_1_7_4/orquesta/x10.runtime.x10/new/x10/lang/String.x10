package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.String")
public final value String implements (nat) => Char {
    // TODO: constructors
    public native def this(): String;
    public native def this(String): String;
    
    @Native("java", "#0.length()")
    public native def length(): Int;
    
    @Native("java", "#0.charAt(#1)")
    public native def apply(index: nat): Char;
    
    @Native("java", "#0.substring(#1, #2)")
    public native def substring(fromIndex: nat, toIndex: nat): String;
}
