package x10.io;

public value FilterReader extends Reader {
    val r: Reader;
    
    protected def inner(): Reader = r;

    public def this(r: Reader) { this.r = r; }
    public def close(): Void throws IOException = r.close();
    public def read(): Byte throws IOException = r.read();
    
    public def available(): Int throws IOException = r.available();
    
    public def skip(off: Int): Void throws IOException = r.skip(off);

    public def mark(off: Int): Void throws IOException = r.mark(off);
    public def reset(): Void throws IOException = r.reset();
    public def markSupported(): Boolean = r.markSupported();
    
}
