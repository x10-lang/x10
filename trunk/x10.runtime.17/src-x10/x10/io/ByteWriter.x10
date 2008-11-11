package x10.io;

public class ByteWriter[T] extends Writer {
    val b: Builder[Byte,T];

    public def this(b: Builder[Byte,T]) { this.b = b; }

    public def write(x: Byte): Void { b.add(x); }
    public def size() = b.size();
    public def toString() = b.toString();
    public def result() = b.result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

