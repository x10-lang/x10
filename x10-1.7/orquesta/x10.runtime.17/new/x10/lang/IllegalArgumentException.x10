package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.IllegalArgumentException")
public class IllegalArgumentException extends RuntimeException {
    public native def this(): IllegalArgumentException;
    public native def this(message: String): IllegalArgumentException;
    public native def this(message: String, cause: Throwable): IllegalArgumentException;
    public native def this(cause: Throwable): IllegalArgumentException;
}
