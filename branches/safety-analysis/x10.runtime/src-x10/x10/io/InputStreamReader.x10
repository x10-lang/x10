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

/** Note: package scope */
class InputStreamReader extends Reader {
    global val stream: InputStream;

    @NativeRep("java", "java.io.InputStream", null, null)
    @NativeRep("c++", "x10aux::ref<x10::io::InputStreamReader__InputStream>", "x10::io::InputStreamReader__InputStream", null)
    protected abstract static class InputStream {
        @Native("java", "#0.close()")
        @Native("c++", "(#0)->close()")
        public native global def close(): Void throws IOException;

        @Native("java", "#0.read()")
        @Native("c++", "(#0)->read()")
        public native global def read(): Int throws IOException;

        @Native("java", "#0.read((#1).getByteArray(), #2, #3)")
        @Native("c++", "(#0)->read(#1,#2,#3)")
        public native global def read(r:Rail[Byte], off: Int, len: Int): Void throws IOException;

        @Native("java", "#0.available()")
        @Native("c++", "(#0)->available()")
        public native global def available(): Int throws IOException;

        @Native("java", "#0.skip(#1)")
        @Native("c++", "(#0)->skip(#1)")
        public native global def skip(Int): Void throws IOException;

        @Native("java", "#0.mark(#1)")
        @Native("c++", "(#0)->mark(#1)")
        public native global def mark(Int): Void throws IOException;

        @Native("java", "#0.reset()")
        @Native("c++", "(#0)->reset()")
        public native global def reset(): Void throws IOException;

        @Native("java", "#0.markSupported()")
        @Native("c++", "(#0)->markSupported()")
        public native global def markSupported(): Boolean;
    }

    public def this(stream: InputStream) {
        this.stream = stream;
    }

    protected global def stream(): InputStream = stream;

    public global def close(): Void throws IOException { stream.close(); }

    public global def read(): Byte throws IOException {
        val n: Int = stream.read();
        if (n == -1) throw new EOFException();
        return n as Byte;
    }
    
    public global def available(): Int throws IOException = stream.available();

    public global def skip(off: Int): Void throws IOException = stream.skip(off);

    public global def mark(off: Int): Void throws IOException = stream.mark(off);
    public global def reset(): Void throws IOException = stream.reset();
    public global def markSupported() = stream.markSupported();
}
