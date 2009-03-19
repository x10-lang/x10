package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.NumberFormatException")
public class NumberFormatException extends IllegalArgumentException {
    public native def this(): NumberFormatException;
    public native def this(message: String): NumberFormatException;
}
