package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.ArithmeticException")
public class ArithmeticException extends RuntimeException {
    public native def this(): ArithmeticException;
    public native def this(message: String): ArithmeticException;
}
