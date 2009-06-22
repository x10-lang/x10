package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

/**
 * The compiler accepts the syntax 'for (x in s)' if x is a formal
 * parameter of type T, and if s implements Iterable[T].
 */
@NativeRep("java", "java.util.Iterable<#1>")
public interface Iterable[+T] {
    def iterator(): Iterator[T];
}
