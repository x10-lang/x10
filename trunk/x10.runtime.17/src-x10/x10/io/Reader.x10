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

public abstract value Reader {
    public abstract def close(): Void throws IOException;

    public abstract def read(): Byte throws IOException;
    public abstract def available(): Int throws IOException;

    public abstract def skip(Int): Void throws IOException;

    public abstract def mark(Int): Void throws IOException;
    public abstract def reset(): Void throws IOException;
    public abstract def markSupported(): Boolean;
    
    public def readBoolean(): Boolean throws IOException = Marshal.BOOLEAN.read(this);
    public def readByte(): Byte throws IOException = Marshal.BYTE.read(this);
    public def readChar(): Char throws IOException = Marshal.CHAR.read(this);
    public def readShort(): Short throws IOException = Marshal.SHORT.read(this);
    public def readInt(): Int throws IOException = Marshal.INT.read(this);
    public def readLong(): Long throws IOException = Marshal.LONG.read(this);
    public def readFloat(): Float throws IOException = Marshal.FLOAT.read(this);
    public def readDouble(): Double throws IOException = Marshal.DOUBLE.read(this);
    public def readLine(): String throws IOException = Marshal.LINE.read(this);

    public def read[T](m: Marshal[T]): T throws IOException = m.read(this);

    public def read[T](m: Marshal[T], a: Rail[T]): Void  throws IOException = read[T](m, a, 0, a.length);
    public def read[T](m: Marshal[T], a: Rail[T], off: Int, len: Int): Void throws IOException = read[T](m, a, off..off+len);
    public def read[T](m: Marshal[T], a: Rail[T], region: Region{rank==1 /*,self in (0..a.length-1)*/}): Void throws IOException {
        for ((i) in region) {
            a(i) = read[T](m);
        }
    }
    
    public def read[T](m: Marshal[T], a: Array[T]): Void throws IOException {
        read[T](m, a, a.region);
    }

    public def read[T](m: Marshal[T], a: Array[T], region: Region{rank==a.rank}/*{self in a.region}*/): Void throws IOException {
        try {
            finish for (p: Point{rank==a.rank} in region) {
                async (a.dist(p)) {
                    try {
                        a(p) = read(m);
                    }
                    catch (e: IOException) {
                        throw new IORuntimeException(e.getMessage()); // HACK !!   Code gen can't throw exceptions from closure.
                    }
                }
            }
        }
        catch (e: MultipleExceptions) {
            throw new IOException();
        }
    }
     
    public def lines(): ReaderIterator[String] = new ReaderIterator[String](Marshal.LINE, this);
    public def chars(): ReaderIterator[Char] = new ReaderIterator[Char](Marshal.CHAR, this);
    public def bytes(): ReaderIterator[Byte] = new ReaderIterator[Byte](Marshal.BYTE, this);
    
}
