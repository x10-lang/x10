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
 * Wrapper class that delegates all methods ultimately to methods on
 * the native NativeByteArrayOutputStream that it wraps.
 */

public class ByteArrayOutputStream extends OutputStream {
    
    @NativeRep("java", "java.io.ByteArrayOutputStream", null, null)
    public static class NativeByteArrayOutputStream extends NativeOutputStream {
        
        @Native("java", "#0.write(#1)")
        public native def write(Byte): void throws IOException;
      
        @Native("java", "(#0).write((#1).getByteArray(), #2,#3)")
        public native def write(r:Rail[Byte], a:Int, b:Int):Void throws IOException;
    
        public native def this();
    }

    public def this() {
        super(new NativeByteArrayOutputStream());
    }
   
    public  def write(r:Rail[Byte], a:Int, b:Int) throws IOException {
        n.write(r, a, b);
    }

    public def write(b:Byte) throws IOException {
        n.write(b);
    }

    public def toString() = n.toString();
}
