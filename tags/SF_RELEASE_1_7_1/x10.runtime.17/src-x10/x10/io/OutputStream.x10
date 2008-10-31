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

/**
Wrapper class that delegates all methods ultimately to methods on
the native NativeOutputStream that it wraps.
*/
public class OutputStream {
    
    @NativeRep("java", "java.io.OutputStream")
    public static class NativeOutputStream {
      @Native("java", "#0.write(#1)")
      public native def write(Byte): void throws IOException;
      
      @Native("java", "(#0).write((#1).getByteArray(), #2,#3)")
      public native def write(r:Rail[Byte], a:Int, b:Int):Void throws IOException;
    
    }
    val n:NativeOutputStream;
    public def this(n:NativeOutputStream) {
       this.n=n;
    }
   
    public  def write(r:Rail[Byte], a:Int, b:Int) throws IOException {
       n.write(r, a, b);
    }
    public def write(b:Byte) throws IOException {
      n.write(b);
    }
}
