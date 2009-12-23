package x10.lang;

public final class Cell[T] {

    public var value:T;

    public def this (x:T) { value = x; }

    public global safe def toString () = "Cell(" + hashCode()+")"; // at (this) value.toString();

    public global def apply () = at (this) value;
    public global def apply (x:T) { at (this) value = x; }

    public static operator[T] (x:Cell[T]) = x();
    public static operator[T] (x:T) = new Cell[T](x);

}
