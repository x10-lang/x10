package x10.lang;

public interface Equatable[EquatableT]{EquatableT <: Equatable[EquatableT]} {
    def eq(y: EquatableT): Boolean;
    def ne(y: EquatableT): Boolean;
}
