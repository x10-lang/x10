package x10.compiler;

public interface SetOps[T] {
    def $not(): T;
    def $and(that: T): T;
    def $or(that: T): T;
    def $minus(that: T): T;
}
