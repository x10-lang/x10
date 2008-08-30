package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.Iterator")
public interface Iterator[+T] {
    def hasNext(): boolean;
    def next(): T;
}
