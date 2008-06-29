package x10.lang;

public interface Comparable[T]{T <: Comparable[T]} extends Equatable[T] {
    def lt(y: T): Boolean;
    def gt(y: T): Boolean;
    def le(y: T): Boolean;
    def ge(y: T): Boolean;
}
