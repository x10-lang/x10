package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;


public class ClockUseException extends RuntimeException {
    public def this(): ClockUseException = {}
    public def this(message: String): ClockUseException = { super(message);}
}
