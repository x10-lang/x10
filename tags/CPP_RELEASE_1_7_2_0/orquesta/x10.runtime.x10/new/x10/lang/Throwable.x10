package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("java", "java.lang.Throwable")
public class Throwable {

    public native def this(): Throwable;
    public native def this(message: String): Throwable;
    public native def this(message: String, cause: Throwable): Throwable;
    public native def this(cause: Throwable): Throwable;

    @Native("java", "#0.getMessage()")
    public native def getMessage(): String;
    
    @Native("java", "#0.getLocalizedMessage()")
    public native def getLocalizedMessage(): String;
    
    @Native("java", "#0.getCause()")
    public native def getCause(): Throwable;

    @Native("java", "#0.toString()")
    public native def toString(): String;
    
    @Native("java", "#0.printStackTrace()")
    public native def printStackTrace(): void;
    
    /*
    public void printStackTrace();
    public void printStackTrace(java.io.PrintStream);
    public void printStackTrace(java.io.PrintWriter);
    public synchronized native java.lang.Throwable fillInStackTrace();
    public java.lang.StackTraceElement[] getStackTrace();
    public void setStackTrace(java.lang.StackTraceElement[]);
*/
}
