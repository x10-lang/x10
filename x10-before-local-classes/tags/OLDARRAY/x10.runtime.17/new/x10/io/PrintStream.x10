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

    
    @NativeRep("java", "java.io.PrintStream")
    public static class NativePrintStream extends NativeOutputStream {
    
      // this is fake.
      public def this() = {}
      
      @Native("java", "#0.print(#1)")
      public native def write(Int): void;
    
      @Native("java", "#0.write(#1, #2, #3)")
      public native def write(Rail[Byte], Int, Int): void;
    
      @Native("java", "#0.print(#1)")
      public native def print(Object): void;
    
      @Native("java", "#0.println(#1)")
      public native def println(Object): void;
    
      @Native("java", "#0.println()")
      public native def println(): void;
    }
    
    public def this(n:NativePrintStream) { super(n); }
    public def write(a:Int) { (n as NativePrintStream).print(a);}
    public def println() { (n as NativePrintStream).println();}
    public def println(o:Object) { (n as NativePrintStream).println(o);}
    public def print(o:Object) { (n as NativePrintStream).print(o);}
 }

