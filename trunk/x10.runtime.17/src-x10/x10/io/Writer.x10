/**
 * Usage:
 *
 * try {
 *   val in = new File(inputFileName);
 *   val out = new File(outputFileName);
 *   val p = out.printer();
 *   for (line in in.lines()) {
 *      line = line.chop();
 *      p.println(line);
 *   }
 * }
 * catch (IOException e) { }
 */    
package x10.io;

import x10.compiler.NativeRep;

public abstract class Writer {
    public abstract def close(): Void throws IOException;
    public abstract def flush(): Void throws IOException;
    public abstract def write(x: Byte): Void throws IOException;

    public def writeByte(x: Byte): Void throws IOException = Marshal.BYTE.write(this, x);
    public def writeChar(x: Char): Void throws IOException =  Marshal.CHAR.write(this, x);
    public def writeShort(x: Short): Void throws IOException = Marshal.SHORT.write(this, x);
    public def writeInt(x: Int): Void throws IOException = Marshal.INT.write(this, x);
    public def writeLong(x: Long): Void throws IOException = Marshal.LONG.write(this, x);
    public def writeFloat(x: Float): Void throws IOException = Marshal.FLOAT.write(this, x);
    public def writeDouble(x: Double): Void throws IOException = Marshal.DOUBLE.write(this, x);
    public def writeBoolean(x: Boolean): Void throws IOException = Marshal.BOOLEAN.write(this, x);
    
    public def write[T](m: Marshal[T], x: T): Void throws IOException = m.write(this, x);

    public def write(buf: ValRail[Byte]): Void throws IOException {
        write(buf, 0, buf.length);
    }

    public def write(buf: Rail[Byte]): Void throws IOException {
        write(buf, 0, buf.length);
    }

    public def write(buf: (Int) => Byte, off: Int, len: Int): Void throws IOException {
        for ((i) in off..off+len) {
            write(buf(i));
        }
    }
}
