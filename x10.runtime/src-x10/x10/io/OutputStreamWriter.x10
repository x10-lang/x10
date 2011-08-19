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
    @NativeRep("java", "x10.core.io.OutputStream", null, "x10.core.io.OutputStream.$RTT")
    @NativeRep("c++", "x10aux::ref<x10::io::OutputStreamWriter__OutputStream>", "x10::io::OutputStreamWriter__OutputStream", null)
    protected abstract static class OutputStream {
        @Native("java", "#this.close()")
        @Native("c++", "(#this)->close()")
        public native def close(): void; //throws IOException;

        @Native("java", "#this.flush()")
        @Native("c++", "(#this)->flush()")
        public native def flush(): void; //throws IOException;
        
        @Native("java", "#this.write(#v)")
        @Native("c++", "(#this)->write(#v)")
        public native def write(v:Int): void; //throws IOException
        
        @Native("java", "#this.write((#r).raw().getByteArray())")
        @Native("c++", "(#this)->write((#r)->raw())")
        public native def write(r:Array[Byte](1)): void; //throws IOException
        
        @Native("java", "#this.write((#r).raw().getByteArray(), #off, #len)")
        @Native("c++", "(#this)->write((#r)->raw())")
        public native def write(r:Array[Byte](1), off:Int, len:Int): void; //throws IOException
    }

    val out: OutputStream;
    
    def stream(): OutputStream = out;
    
    public def this(out: OutputStream) {
        this.out = out;
    }
    
    public def flush(): void //throws IOException 
    { out.flush(); }

    public def close(): void //throws IOException 
    { out.close(); }
    
    public def write(x: Byte): void //throws IOException 
    { out.write(x); }
    
    public def write(buf:Array[Byte](1)): void //throws IOException 
    {
        out.write(buf);
    }

    public def write(buf:Array[Byte](1), off: Int, len: Int): void //throws IOException 
    {
        out.write(buf, off, len);
    }
}
