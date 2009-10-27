package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public class Box[+T](value: T) {
    public def this(x: T) { property(x); }

    public def hashCode(): int = value.hashCode();

    public def toString(): String = value.toString();

    public def equals(x:Object): Boolean {
        if (x == null) {
            return false;
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
