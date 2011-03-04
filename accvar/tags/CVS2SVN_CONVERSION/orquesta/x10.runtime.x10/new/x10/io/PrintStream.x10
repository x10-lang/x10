package x10.io;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.io.PrintStream")
public class PrintStream extends OutputStream {
    @Native("java", "#0.print(#1)")
    public native def write(Int): void;
    
    @Native("java", "#0.write(#1, #2, #3)")
    public native def write(Rail[Byte], Int, Int): void;
    
    @Native("java", "#0.print(#1)")
    public native def print(Object): void;
    
    @Native("java", "#0.println(#1)")
    public native def println(Object): void;
}
