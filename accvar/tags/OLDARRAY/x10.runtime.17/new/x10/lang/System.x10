/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;
import x10.io.*;

public class System {

    @NativeRep("java", "java.lang.System")
    private static class NativeSystem {
      // Hide the constructor.
      private def this() = { }
    
      private static incomplete def fakeIt[T](): T;
    
      @Native("java", "java.lang.System.out")
      public const out: PrintStream.NativePrintStream = fakeIt[x10.io.PrintStream.NativePrintStream]();
    
      @Native("java", "java.lang.System.err")
      public const err: PrintStream.NativePrintStream = fakeIt[x10.io.PrintStream.NativePrintStream]();
    
      @Native("java", "java.lang.System.in")
      public const input: InputStream.NativeInputStream = fakeIt[x10.io.InputStream.NativeInputStream]();
    
      @Native("java", "java.lang.System.exit(#1)")
      public static native def exit(code: Int): void;
    
      @Native("java", "java.lang.System.currentTimeMillis()")
      public static native def currentTimeMillis(): Long;
    
      @Native("java", "java.lang.System.nanoTime()")
      public static native def nanoTime(): Long;
    
      @Native("java", "java.lang.System.exit(-1)")
      public static native def exit():Void;
    }
    
    private def this() = {}
    public const err = new PrintStream(NativeSystem.err);
    public const input = new InputStream(NativeSystem.input);
    public const out = new PrintStream(NativeSystem.out);
    public static def exit(code: Int) { NativeSystem.exit();}
    public static def currentTimeMillis() = NativeSystem.currentTimeMillis();
    public static def nanoTime() = NativeSystem.nanoTime();
}
