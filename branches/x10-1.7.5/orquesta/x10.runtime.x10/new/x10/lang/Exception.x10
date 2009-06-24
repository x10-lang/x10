package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Exception")
public class Exception extends Throwable {
    public native def this(): Exception;
    public native def this(message: String): Exception;
    public native def this(message: String, cause: Throwable): Exception;
    public native def this(cause: Throwable): Exception;
}
