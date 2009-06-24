package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Error")
public class Error extends Throwable {
    public native def this(): Error;
    public native def this(message: String): Error;
    public native def this(message: String, cause: Throwable): Error;
    public native def this(cause: Throwable): Error;
}
