package x10.io;

import x10.compiler.NativeRep;
import x10.compiler.Native;

public class OutputStreamWriter extends Writer {
    @NativeRep("java", "java.io.OutputStream", null, null)
    protected abstract static class OutputStream {
        @Native("java", "#0.close()")
        public native def close(): Void throws IOException;

        @Native("java", "#0.flush()")
        public native def flush(): Void throws IOException;
        
        @Native("java", "#0.write(#1)")
        public native def write(Int): Void throws IOException;
        
        @Native("java", "#0.write(#1.getByteArray())")
        public native def write(Rail[Byte]): Void throws IOException;
        
        @Native("java", "#0.write(#1.getByteArray())")
        public native def write(ValRail[Byte]): Void throws IOException;
        
        @Native("java", "#0.write(#1.getByteArray(), #2, #3)")
        public native def write(Rail[Byte], Int, Int): Void throws IOException;
        
        @Native("java", "#0.write(#1.getByteArray(), #2, #3)")
        public native def write(ValRail[Byte], Int, Int): Void throws IOException;
    }

    val out: OutputStream;
    
    def stream(): OutputStream = out;
    
    public def this(out: OutputStream) {
        this.out = out;
    }
    
    public def flush(): Void throws IOException = out.close();

    public def close(): Void throws IOException = out.close();
    
    public def write(x: Byte): Void throws IOException = out.write(x to Int);
    
    public def write(buf: ValRail[Byte]): Void throws IOException {
        out.write(buf);
    }

    public def write(buf: Rail[Byte]): Void throws IOException {
        out.write(buf);
    }

    public def write(buf: (Int) => Byte, off: Int, len: Int): Void throws IOException {
        if (buf instanceof Rail[Byte]) {
            out.write(buf as Rail[Byte], off, len);
        }
        else if (buf instanceof ValRail[Byte]) {
            out.write(buf as ValRail[Byte], off, len);
        }
        else {
            super.write(buf, off, len);
        }
    }
}
