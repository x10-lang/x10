/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.io;

/**
 * Usage:
 *
 * try {
 *    val input = new File(inputFileName);
 *    val output = new File(outputFileName);
 *    val p = output.printer();
 *    for (line in input.lines()) {
 *       p.println(line);
 *    }
 *    p.flush();
 * } catch (IOException) { }
 */    
public abstract class Reader {
    /**
     * Close the input stream
     */
    public abstract def close():void;

    /**
     * Read the next Byte from the input; throws IOException if none.
     */
    public abstract def read():Byte;

    /**
     * Fill len bytes of the argument rail starting at off.
     * Throws IOException if not enough elements.
     * This is significantly faster than read(Marshal.BYTE,r,off,len)
     * since this reads multiple bytes at once if possible.
     */
    public abstract def read(r:Rail[Byte], off:Long, len:Long):void;

    /**
     * How many bytes can be read from this stream without blocking?
     *
     * NOTE: there may actually be more bytes than this available when
     * the read is made, in particular this method may return 0 even
     * if a non-zero number of bytes are actually available.
     */
    public abstract def available():Long;

    public abstract def skip(v:Long):void;

    public abstract def mark(m:Long):void;
    public abstract def reset():void;
    public abstract def markSupported(): Boolean;
    
    /**
     * Read the next Boolean from the input; throws IOException if none.
     */
    public def readBoolean():Boolean = Marshal.BOOLEAN.read(this);

    /**
     * Read the next Byte from the input; throws IOException if none.
     */
    public def readByte():Byte = Marshal.BYTE.read(this);

    /**
     * Read the next UByte from the input; throws IOException if none.
     */
    public def readUByte():UByte = Marshal.UBYTE.read(this);

    /**
     * Read the next Char from the input; throws IOException if none.
     */
    public def readChar():Char = Marshal.CHAR.read(this);

    /**
     * Read the next Short from the input; throws IOException if none.
     */
    public def readShort():Short = Marshal.SHORT.read(this);

    /**
     * Read the next UShort from the input; throws IOException if none.
     */
    public def readUShort():UShort = Marshal.USHORT.read(this);

    /**
     * Read the next Int from the input; throws IOException if none.
     */
    public def readInt():Int = Marshal.INT.read(this);

    /**
     * Read the next UInt from the input; throws IOException if none.
     */
    public def readUInt():UInt = Marshal.UINT.read(this);

    /**
     * Read the next Long from the input; throws IOException if none.
     */
    public def readLong():Long = Marshal.LONG.read(this);

    /**
     * Read the next ULong from the input; throws IOException if none.
     */
    public def readULong():ULong = Marshal.ULONG.read(this);

    /**
     * Read the next Float from the input; throws IOException if none.
     */
    public def readFloat():Float = Marshal.FLOAT.read(this);

    /**
     * Read the next Double from the input; throws IOException if none.
     */
    public def readDouble():Double = Marshal.DOUBLE.read(this);

    /**
     * Read the next line from the input; throws IOException if none.
     */
    public def readLine():String = Marshal.LINE.read(this);

    /**
     * Read the next value from the input; throws IOException if none.
     */
    public final def read[T](m:Marshal[T]):T = m.read(this);

    /**
     * Fill the argument rail by reading the next a.size elements. 
     * Throws IOException if not enough elements.
     */
    public final def read[T](m:Marshal[T], a:Rail[T]):void { 
        read[T](m, a, 0, a.size); 
    }

    /**
     * Fill len elements of the argument rail starting at off.
     * Throws IOException if not enough elements.
     */
    public final def read[T](m: Marshal[T], a:Rail[T], off:Long, len:Long):void {
        for (var i: Long = off; i < off+len; i++) {
            a(i) = read[T](m);
        }
    }
    
    public def lines(): ReaderIterator[String] = new ReaderIterator[String](Marshal.LINE, this);
    public def chars(): ReaderIterator[Char] = new ReaderIterator[Char](Marshal.CHAR, this);
    public def bytes(): ReaderIterator[Byte] = new ReaderIterator[Byte](Marshal.BYTE, this);
}
