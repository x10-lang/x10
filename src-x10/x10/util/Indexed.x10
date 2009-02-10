package x10.util;

public interface Indexed[T] extends Container[T] {
    public def apply(Int): T;
}
