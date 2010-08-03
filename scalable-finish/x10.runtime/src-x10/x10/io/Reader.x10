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

/**
 * Usage:
 *
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val i = input.openRead();
 *    val p = output.printer();
 *    for (line in i.lines()) {
 *       line = line.chop();
 *       p.println(line);
 *    }
 * }
 * catch (IOException) { }
 */    
public abstract class Reader {
    public abstract global def close(): Void throws IOException;

    public abstract global def read(): Byte throws IOException;
    public abstract global def available(): Int throws IOException;

    public abstract global def skip(Int): Void throws IOException;

    public abstract global def mark(Int): Void throws IOException;
    public abstract global def reset(): Void throws IOException;
    public abstract global def markSupported(): Boolean;
    
    public global def readBoolean(): Boolean throws IOException = Marshal.BOOLEAN.read(this);
    public global def readByte(): Byte throws IOException = Marshal.BYTE.read(this);
    public global def readChar(): Char throws IOException = Marshal.CHAR.read(this);
    public global def readShort(): Short throws IOException = Marshal.SHORT.read(this);
    public global def readInt(): Int throws IOException = Marshal.INT.read(this);
    public global def readLong(): Long throws IOException = Marshal.LONG.read(this);
    public global def readFloat(): Float throws IOException = Marshal.FLOAT.read(this);
    public global def readDouble(): Double throws IOException = Marshal.DOUBLE.read(this);
    public global def readLine(): String throws IOException = Marshal.LINE.read(this);

    public final global def read[T](m: Marshal[T]): T throws IOException = m.read(this);

    public final global def read[T](m: Marshal[T], a: Rail[T]!): Void  throws IOException 
	= read[T](m, a, 0, a.length);
    public final global def read[T](m: Marshal[T], a: Rail[T]!, off: Int, len: Int): Void throws IOException {
        for (var i: Int = off; i < off+len; i++) {
            a(i) = read[T](m);
        }
    }
    
    /*
    public final global def read[T](m: Marshal[T], a: Rail[T], region: Region{rank==1 /*,self in (0..a.length-1)* /}): Void throws IOException {
        for ((i) in region) {
            a(i) = read[T](m);
        }
    }
    
    public final global def read[T](m: Marshal[T], a: Array[T]): Void throws IOException {
        read[T](m, a, a.region);
    }

    public final global def read[T](m: Marshal[T], a: Array[T], region: Region{rank==a.rank}/*{self in a.region}* /): Void throws IOException {
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
    */
     
    public global def lines(): ReaderIterator[String]! = new ReaderIterator[String](Marshal.LINE, this);
    public global def chars(): ReaderIterator[Char]! = new ReaderIterator[Char](Marshal.CHAR, this);
    public global def bytes(): ReaderIterator[Byte]! = new ReaderIterator[Byte](Marshal.BYTE, this);
    
}
