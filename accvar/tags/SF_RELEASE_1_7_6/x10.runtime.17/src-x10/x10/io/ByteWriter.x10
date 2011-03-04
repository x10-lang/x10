package x10.io;

import x10.util.Builder;

public value ByteWriter[T] extends Writer {
    val b: Builder[Byte,T];

    public def this(b: Builder[Byte,T]) { this.b = b; }

    public def write(x: Byte): Void { b.add(x); }
    public incomplete def size() : Long;
    public def toString() = (b as Object).toString();
    public def result() = b.result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

