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

    @NativeRep("java", "x10.core.io.InputStream", null, "x10.core.io.InputStream.$RTT")
    @NativeRep("c++", "x10::io::InputStreamReader__InputStream*", "x10::io::InputStreamReader__InputStream", null)
    protected abstract static class InputStream {
        @Native("java", "#this.close()")
        @Native("c++", "(#this)->close()")
        public native def close(): void; //throws IOException;

        @Native("java", "#this.read()")
        @Native("c++", "(#this)->read()")
        public native def read(): Int; //throws IOException;

        @Native("java", "#this.read((#r).raw().getByteArray(), #off, #len)")
        @Native("c++", "(#this)->read((#r)->raw(),#off,#len)")
        public native def read(r:Rail[Byte], off: Int, len: Int): void; //throws IOException;

        @Native("java", "#this.available()")
        @Native("c++", "(#this)->available()")
        public native def available(): Int; //throws IOException;

        @Native("java", "#this.skip(#v)")
        @Native("c++", "(#this)->skip(#v)")
        public native def skip(v:Int): void; //throws IOException;

        @Native("java", "#this.mark(#m)")
        @Native("c++", "(#this)->mark(#m)")
        public native def mark(m:Int): void;

        @Native("java", "#this.reset()")
        @Native("c++", "(#this)->reset()")
        public native def reset(): void; //throws IOException;

        @Native("java", "#this.markSupported()")
        @Native("c++", "(#this)->markSupported()")
        public native def markSupported(): Boolean;
    }

    public def this(stream: InputStream) {
        this.stream = stream;
    }

    protected def stream(): InputStream = stream;

    public def close(): void //throws IOException 
    { stream.close(); }

    public def read(): Byte //throws IOException 
    {
        val n: Int = stream.read();
        if (n == -1) throw new EOFException();
        return n as Byte;
    }
    
    public def read(r:Rail[Byte], off:Int, len:Int): void //throws IOException 
    { stream.read(r,off,len); }

    public def available(): Int //throws IOException 
    = stream.available();

    public def skip(off: Int): void //throws IOException 
    { stream.skip(off); }

    public def mark(off: Int): void //throws IOException 
    { stream.mark(off); }
    public def reset(): void //throws IOException 
    { stream.reset(); }
    public def markSupported() = stream.markSupported();
}
