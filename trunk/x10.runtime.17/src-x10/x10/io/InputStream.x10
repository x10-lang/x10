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


public class InputStream {

    @NativeRep("java", "java.io.InputStream", null, null)
    public static class NativeInputStream {
      @Native("java", "#0.read()")
      public native def read(): Int;
    }
    val n: NativeInputStream;
    public def this(n:NativeInputStream) {
      this.n  = n;
    }
   
    public def read() throws IOException = n.read();
}
