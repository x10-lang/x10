/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public class OutputStreamWriter extends Writer {
    @NativeRep("java", "x10.core.io.OutputStream", null, "x10.core.io.OutputStream._RTT")
    @NativeRep("c++", "x10aux::ref<x10::io::OutputStreamWriter__OutputStream>", "x10::io::OutputStreamWriter__OutputStream", null)
    protected abstract static class OutputStream {
        @Native("java", "#0.close()")
        @Native("c++", "(#0)->close()")
        public native def close(): Void; //throws IOException;

        @Native("java", "#0.flush()")
        @Native("c++", "(#0)->flush()")
        public native def flush(): Void; //throws IOException;
        
        @Native("java", "#0.write(#1)")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Int): Void; //throws IOException
        
        @Native("java", "#0.write((#1).getByteArray())")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Rail[Byte]): Void; //throws IOException
        
        @Native("java", "#0.write((#1).getByteArray(), #2, #3)")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Rail[Byte], Int, Int): Void; //throws IOException
    }

    val out: OutputStream;
    
    def stream(): OutputStream = out;
    
    public def this(out: OutputStream) {
        this.out = out;
    }
    
    public def flush(): Void //throws IOException 
    = out.flush();

    public def close(): Void //throws IOException 
    = out.close();
    
    public def write(x: Byte): Void //throws IOException 
    = out.write(x);
    
    public def write(buf: Rail[Byte]): Void //throws IOException 
    {
        out.write(buf);
    }

    public def write(buf:Rail[Byte], off: Int, len: Int): Void //throws IOException 
    {
        out.write(buf, off, len);
    }
}
