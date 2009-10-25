package x10.util;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.util.Iterator")
public class Iterator[T] {

    @Native("java", "#0.hasNext()")
    public native def hasNext():boolean;

    @Native("java", "#0.next()")
    public native def next():T;
}
