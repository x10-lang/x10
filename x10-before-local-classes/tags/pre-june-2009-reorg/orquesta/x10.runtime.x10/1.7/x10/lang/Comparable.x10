package x10.lang;

public interface Comparable[ComparableT]{ComparableT <: Comparable[ComparableT]} extends Equatable[ComparableT] {
    def lt(y: ComparableT): Boolean;
    def gt(y: ComparableT): Boolean;
    def le(y: ComparableT): Boolean;
    def ge(y: ComparableT): Boolean;
}
