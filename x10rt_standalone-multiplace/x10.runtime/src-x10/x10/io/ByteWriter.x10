package x10.io;

import x10.util.Builder;

public class ByteWriter[T] extends Writer {
    global val b: Builder[Byte,T];

    public def this(b: Builder[Byte,T]) { this.b = b; }

    public global def write(x: Byte): Void { b.add(x); }
    public global incomplete def size() : Long;
    public global safe def toString() = b.toString();
    public global def result() = b.result(); 
    
    public global def flush(): Void { }
    public global def close(): Void { }
}

