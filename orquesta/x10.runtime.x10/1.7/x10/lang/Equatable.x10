package x10.lang;

public interface Equatable[T]{T <: Equatable[T]} {
    def eq(y: T): Boolean;
    def ne(y: T): Boolean;
}
