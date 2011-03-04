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

import x10.compiler.Native;
import x10.compiler.NativeRep;

/** Note: package scope */
public class InputStreamReader extends Reader {
    val stream: InputStream;

    @NativeRep("java", "java.io.InputStream", null, "x10.rtt.Types.INPUT_STREAM")
    @NativeRep("c++", "x10aux::ref<x10::io::InputStreamReader__InputStream>", "x10::io::InputStreamReader__InputStream", null)
    protected abstract static class InputStream {
        // @Native("java", "#0.close()")
        @Native("java", "try { #0.close(); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); }")
        @Native("c++", "(#0)->close()")
        public native def close(): Void; //throws IOException;

        // @Native("java", "#0.read()")
        @Native("java", "new Object() { int eval(java.io.InputStream f) { try { return f.read(); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); } } }.eval(#0)")
        @Native("c++", "(#0)->read()")
        public native def read(): Int; //throws IOException;

        // @Native("java", "#0.read((#1).getByteArray(), #2, #3)")
        @Native("java", "new Object() { void eval(java.io.InputStream f, byte[] b, int off, int len) { try {  return f.read(b, off, len); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); } } }.eval(#0, (#1).getByteArray(), #2, #3)")
        @Native("c++", "(#0)->read(#1,#2,#3)")
        public native def read(r:Rail[Byte], off: Int, len: Int): Void; //throws IOException;

        // @Native("java", "#0.available()")
        @Native("java", "new Object() { int eval(java.io.InputStream f) { try { return f.available(); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); } } }.eval(#0)")
        @Native("c++", "(#0)->available()")
        public native def available(): Int; //throws IOException;

        // @Native("java", "#0.skip(#1)")
        @Native("java", "try { #0.skip(#1); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); }")
        @Native("c++", "(#0)->skip(#1)")
        public native def skip(Int): Void; //throws IOException;

        @Native("java", "#0.mark(#1)")
        @Native("c++", "(#0)->mark(#1)")
        public native def mark(Int): Void;

        // @Native("java", "#0.reset()")
        @Native("java", "try { #0.reset(); } catch (java.io.IOException e) { throw new x10.io.IOException(e.getMessage()); }")
        @Native("c++", "(#0)->reset()")
        public native def reset(): Void; //throws IOException;

        @Native("java", "#0.markSupported()")
        @Native("c++", "(#0)->markSupported()")
        public native def markSupported(): Boolean;
    }

    public def this(stream: InputStream) {
        this.stream = stream;
    }

    protected def stream(): InputStream = stream;

    public def close(): Void //throws IOException 
    { stream.close(); }

    public def read(): Byte //throws IOException 
    {
        val n: Int = stream.read();
        if (n == -1) throw new EOFException();
        return n as Byte;
    }
    
    public def available(): Int //throws IOException 
    = stream.available();

    public def skip(off: Int): Void //throws IOException 
    = stream.skip(off);

    public def mark(off: Int): Void //throws IOException 
    = stream.mark(off);
    public def reset(): Void //throws IOException 
    = stream.reset();
    public def markSupported() = stream.markSupported();
}
