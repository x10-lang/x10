package x10.lang;

/**
   The compiler accepts the syntax 'x1 in x2' if x1 and x2 are of type T.
   This is used for, e.g., subset tests for regions.
   
   x1 in x2 works if x1 is T and x2 implements Contains[T]
   x1 in x2 works if x1 is T and x2 implements ContainsAll[T]
   x1 in x2 works if x1 implements Iterable[T] and x2 implements Contains[T]
 */

public interface ContainsAll[ContainsAllT] {
        public def containsAll(ContainsAllT): boolean;
}
