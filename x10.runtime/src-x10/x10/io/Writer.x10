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

import x10.compiler.Native;

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
public abstract class Writer {
    public abstract def close():void;
    public abstract def flush():void;

    public abstract def write(x:Byte):void;
    public abstract def write(x:String):void;
    public abstract def write(x:Rail[Byte], off:Long, len:Long):void;

    public def write(buf:Rail[Byte]):void {
        write(buf, 0, buf.size);
    }

    public def writeByte(x:Byte):void { 
        Marshal.BYTE.write(this, x); 
    }
    public def writeUByte(x:UByte):void { 
        Marshal.UBYTE.write(this, x); 
    }
    public def writeChar(x:Char):void {  
        Marshal.CHAR.write(this, x); 
    }
    public def writeShort(x:Short):void { 
        Marshal.SHORT.write(this, x); 
    }
    public def writeUShort(x:UShort):void { 
        Marshal.USHORT.write(this, x); 
    }
    public def writeInt(x:Int):void {
        Marshal.INT.write(this, x); 
    }
    public def writeUInt(x:UInt):void { 
        Marshal.UINT.write(this, x); 
    }
    public def writeLong(x:Long):void { 
        Marshal.LONG.write(this, x); 
    }
    public def writeULong(x:ULong):void { 
        Marshal.ULONG.write(this, x); 
    }
    public def writeFloat(x:Float):void { 
        Marshal.FLOAT.write(this, x); 
    }
    public def writeDouble(x:Double):void { 
        Marshal.DOUBLE.write(this, x); 
    }
    public def writeBoolean(x:Boolean):void {
        Marshal.BOOLEAN.write(this, x); 
    }
    
    // made final to satisfy the restrictions on genric instance methods for Native X10
    public final def write[T](m:Marshal[T], x:T):void { 
        m.write(this, x); 
    }

    // DO NOT CALL from X10 code -- only used in @Native annotations
    @Native("java", "x10.core.io.OutputStream.getNativeOutputStream(#this)")
    public final def getNativeOutputStream(): OutputStreamWriter.OutputStream = null;
}
