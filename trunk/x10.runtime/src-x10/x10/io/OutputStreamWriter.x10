/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public value OutputStreamWriter extends Writer {
    @NativeRep("java", "java.io.OutputStream", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::OutputStreamWriter__OutputStream>", "x10::io::OutputStreamWriter__OutputStream", null)
    protected abstract static value OutputStream {
        @Native("java", "#0.close()")
        @Native("c++", "(#0)->close()")
        public native def close(): Void throws IOException;

        @Native("java", "#0.flush()")
        @Native("c++", "(#0)->flush()")
        public native def flush(): Void throws IOException;
        
        @Native("java", "#0.write(#1)")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Int): Void throws IOException;
        
        @Native("java", "#0.write((#1).getByteArray())")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Rail[Byte]): Void throws IOException;
        
        @Native("java", "#0.write((#1).getByteArray())")
        @Native("c++", "(#0)->write(#1)")
        public native def write(ValRail[Byte]): Void throws IOException;
        
        @Native("java", "#0.write((#1).getByteArray(), #2, #3)")
        @Native("c++", "(#0)->write(#1)")
        public native def write(Rail[Byte], Int, Int): Void throws IOException;
        
        @Native("java", "#0.write((#1).getByteArray(), #2, #3)")
        @Native("c++", "(#0)->write(#1)")
        public native def write(ValRail[Byte], Int, Int): Void throws IOException;
    }

    val out: OutputStream;
    
    def stream(): OutputStream = out;
    
    public def this(out: OutputStream) {
        this.out = out;
    }
    
    public def flush(): Void throws IOException = out.flush();

    public def close(): Void throws IOException = out.close();
    
    public def write(x: Byte): Void throws IOException = out.write(x);
    
    public def write(buf: ValRail[Byte]): Void throws IOException {
        out.write(buf);
    }

    public def write(buf: Rail[Byte]): Void throws IOException {
        out.write(buf);
    }

    public def write(buf: Rail[Byte], off: Int, len: Int): Void throws IOException {
        out.write(buf, off, len);
    }

    public def write(buf: ValRail[Byte], off: Int, len: Int): Void throws IOException {
        out.write(buf, off, len);
    }
}
