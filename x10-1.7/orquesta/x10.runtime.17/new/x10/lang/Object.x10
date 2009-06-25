package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Object")
public interface Object {
    public def equals(Object): boolean;
    public def hashCode(): int;
    public def toString(): String;
}
