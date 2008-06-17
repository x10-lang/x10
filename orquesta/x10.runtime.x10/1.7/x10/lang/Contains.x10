package x10.lang;

/**
   The compiler accepts the syntax 'x in s' if x is of type T
   and if s implements Contains[T].
 */

public interface Contains[T] {
        public def contains(T): boolean;
}
