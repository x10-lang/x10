package x10.io;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.io.IOException")
public class IOException extends Exception {
    public native def this(): IOException;
    public native def this(message: String): IOException;
}
