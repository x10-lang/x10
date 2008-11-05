/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public class PrintStream extends OutputStream {

    
    @NativeRep("java", "java.io.PrintStream", null, null)
    public static class NativePrintStream extends NativeOutputStream {
    
        // this is fake.
        public def this() = {}
      
        @Native("java", "#0.print(#1)")
        public native def write(Int): void;
    
        @Native("java", "#0.printf(#1)")
        public native def printf(String): void;

        @Native("java", "#0.printf(#1, #2)")
        public native def printf(String, Object): void;
  
        @Native("java", "#0.printf(#1, #2, #3)")
        public native def printf(String, Object, Object): void;

        @Native("java", "#0.printf(#1, #2, #3, #4)")
        public native def printf(String, Object, Object, Object): void;

        @Native("java", "#0.write(#1, #2, #3)")
        public native def write(Rail[Byte], Int, Int): void;
    
        @Native("java", "#0.print(#1)")
        public native def print(Object): void;
    
        @Native("java", "#0.println(#1)")
        public native def println(Object): void;
    
        @Native("java", "#0.println()")
        public native def println(): void;

        public native def this(NativeOutputStream);
    }
    
    public def this(o:OutputStream) {
        super(new NativePrintStream(o.n));
    }
    
    public def this(n:NativePrintStream) { super(n); }
    public def write(a:Int) { (n as NativePrintStream).print(a);}
    public def println() { (n as NativePrintStream).println();}
    public def println(o:Object) { (n as NativePrintStream).println(o);}
    public def print(o:Object) { (n as NativePrintStream).print(o);}
    public def printf(f:String) { (n as NativePrintStream).printf(f); }
    public def printf(f:String, o0:Object) { (n as NativePrintStream).printf(f,o0); }
    public def printf(f:String, o0:Object, o1:Object) { (n as NativePrintStream).printf(f,o0,o1); }
    public def printf(f:String, o0:Object, o1:Object, o2:Object) { (n as NativePrintStream).printf(f,o0,o1,o2); }
 }

