package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.ArrayIndexOutOfBoundsException")
public class ArrayIndexOutOfBoundsException extends RuntimeException {
    public native def this(): ArrayIndexOutOfBoundsException;
    public native def this(message: String): ArrayIndexOutOfBoundsException;
}
