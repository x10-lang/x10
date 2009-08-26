package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.System")
public class System {
    // Hide the constructor.
    private def this() = { }
    
    private static incomplete def fakeIt[T](): T;
    
    @Native("java", "java.lang.System.out")
    public const out: x10.io.PrintStream = fakeIt[x10.io.PrintStream]();
    
    @Native("java", "java.lang.System.err")
    public const err: x10.io.PrintStream = fakeIt[x10.io.PrintStream]();
    
    @Native("java", "java.lang.System.in")
    public const input: x10.io.InputStream = fakeIt[x10.io.InputStream]();
    
    @Native("java", "java.lang.System.exit(#1)")
    public static native def exit(code: Int): void;
    
    @Native("java", "java.lang.System.currentTimeMillis()")
    public static native def currentTimeMillis(): Long;
    
    @Native("java", "java.lang.System.nanoTime()")
    public static native def nanoTime(): Long;
}
