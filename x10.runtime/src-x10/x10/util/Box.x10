package x10.util;

public final class Box[+T](value: T) implements ()=> T {
    public def this(x: T) { property(x); }

    public def apply()=value;
    public global safe def hashCode(): int = value.hashCode();

    public global safe def toString(): String = value.toString();

    public global safe def equals(x:Any): Boolean {
        if (x == null) {
            return false;
        }
        if (x instanceof T) {
            val y = x as T;
            return value.equals(y);
        }
        if (x instanceof Box[T]) {
            val y = (x as Box[T]).value;
            return value.equals(y);
        }
        return false;
    }

    public static operator[T](x:T):Box[T] = new Box[T](x);
}
