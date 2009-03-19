package x10.compiler;

public interface ArithmeticOps[T] {
    def $plus(): T;
    def $minus(): T;

    def $plus(that: T): T;
    def $minus(that: T): T;
    def $times(that: T): T;
    def $over(that: T): T;
    def $percent(that: T): T;
}

