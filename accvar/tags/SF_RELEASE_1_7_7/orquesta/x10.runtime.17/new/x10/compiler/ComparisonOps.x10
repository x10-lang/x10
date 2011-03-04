package x10.compiler;

public interface ComparisonOps[T] {
    def $eq(that: T): boolean;
    def $lt(that: T): boolean;
    def $gt(that: T): boolean;
    def $le(that: T): boolean;
    def $ge(that: T): boolean;
    def $ne(that: T): boolean;
}
