package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("c++", "x10aux::ref<x10::lang::Box<#1 > >", "x10::lang::Box<#1 >", null)
public class Box[+T](value: T) {
    public def this(x: T) { property(x); }

    @Native("c++", "x10aux::hash_code(#0)")
    public def hashCode(): int = value.hashCode();

    @Native("c++", "x10aux::to_string(#0)")
    public def toString(): String = value.toString();

    @Native("c++", "x10aux::equals(#0,#1)")
    public def equals(x: Value): Boolean {
        if (value instanceof Ref) {
            return (value as Ref).equals(x);
        }
        if (x instanceof T) {
            val y = x as T;
            return value.equals(y);
        }
        return false;
    }

    @Native("c++", "x10aux::equals(#0,#1)")
    public def equals(x: Ref): Boolean {
        if (x == null) {
            return false;
        }
        if (value instanceof Ref) {
            return (value as Ref).equals(x);
        }
        if (x instanceof T) {
            val y = x as T;
            return value.equals(y);
        }
        if (x instanceof Box[T]) {
            val y = (x as Box[T]) as T;
            return value.equals(y);
        }
        return false;
    }
}
