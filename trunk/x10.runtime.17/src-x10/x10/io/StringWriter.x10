package x10.io;

import x10.util.StringBuilder;

public class StringWriter extends Writer {
    val b: StringBuilder;

    public def this() { this.b = new StringBuilder(); }

    public def write(x: Byte): Void { b.add((x to Byte) to Char); }
    public def size() = b.length();
    public def toString() = b.toString();
    public def result() = b.result(); 
    
    public def flush(): Void { }
    public def close(): Void { }
}

