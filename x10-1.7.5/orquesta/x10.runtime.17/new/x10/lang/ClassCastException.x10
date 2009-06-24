package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.ClassCastException")
public class ClassCastException extends RuntimeException {
    public native def this(): ClassCastException;
    public native def this(message: String): ClassCastException;
}
