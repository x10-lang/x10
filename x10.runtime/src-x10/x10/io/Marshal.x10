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

import x10.util.GrowableRail;

public interface Marshal[T] {
    public def read(r:Reader):T;
    public def write(w:Writer, v:T):void;
    
    public static BOOLEAN = new BooleanMarshal();
    public static BYTE = new ByteMarshal();
    public static UBYTE = new UByteMarshal();
    public static CHAR = new CharMarshal();
    public static SHORT = new ShortMarshal();
    public static USHORT = new UShortMarshal();
    public static INT = new IntMarshal();
    public static UINT = new UIntMarshal();
    public static LONG = new LongMarshal();
    public static ULONG = new ULongMarshal();
    public static FLOAT = new FloatMarshal();
    public static DOUBLE = new DoubleMarshal();
    public static LINE = new LineMarshal();
    
    public final static class LineMarshal implements Marshal[String] {
        public def read(r:Reader):String {
            val buf = new GrowableRail[Byte](32);
            try {
                while (true) {
                    val b = r.read();
                    if ((b as Char) == '\n') break;
		    buf.add(b);
                };
            } catch (e:IOException) {
                if (buf.size() == 0) {
                    throw e;
                }
            }
	    return new String(buf);
        }
        public def write(w:Writer, s:String):void {
	    w.write(s.bytes());
        }
    }
    
    public final static class BooleanMarshal implements Marshal[Boolean] {
        public def read(r: Reader):Boolean = r.read() != 0y;
        public def write(w: Writer, b: Boolean):void {
            w.write((b ? 0 : 1) as Byte); 
        }
    }
    
    public final static class ByteMarshal implements Marshal[Byte] {
        public def read(r:Reader):Byte = r.read();
        public def write(w:Writer, b:Byte):void {
            w.write(b); 
        }
    }

    public final class UByteMarshal implements Marshal[UByte] {
        public def read(r:Reader):UByte = (r.readByte() as UByte);
        public def write(w:Writer, ub:UByte):void {
            w.write(ub as Byte); 
        }
    }

    public final static class CharMarshal implements Marshal[Char] {
        public def read(r:Reader):Char {
            val b1 = r.read();
            if (b1 == -1y) throw new EOFException();
            if ((b1 & 0xf8) == 0xf0) {
                val b2 = r.read();
                val b3 = r.read();
                val b4 = r.read();
                return Char.chr(((b1 & 0x03n) << 18n) | ((b2 & 0x3fn) << 12n) | ((b3 & 0x3fn) << 6n) | (b4 & 0x3fn));
            }
            if ((b1 & 0xf0) == 0xe0) {
                val b2 = r.read();
                val b3 = r.read();
                return Char.chr(((b1 & 0x1fn) << 12n) | ((b2 & 0x3fn) << 6n) | (b3 & 0x3fn));
            }
            if ((b1 & 0xe0) == 0xc0) {
                val b2 = r.read();
                return Char.chr(((b1 & 0x1fn) << 6n) | (b2 & 0x3fn));
            }
//          if ((b1 & 0x80) == 0)
                return Char.chr(b1);
        }

        public def write(w:Writer, c:Char):void {
            val i = c.ord();
            if ((i & 0xffffff80) == 0) {
                w.write(i as Byte);
                return;
            }
            if ((i & 0xfffff800) == 0) {
                w.write((((i >> 6n) & 0x0000001f) | 0x000000c0) as Byte);
                w.write(((i & 0x0000003f) | 0x00000080) as Byte);
                return;
            }
            if ((i & 0xffff0000) == 0) {
                w.write((((i >> 12n) & 0x0000000f) | 0x000000e0) as Byte);
                w.write((((i >> 6n) & 0x0000003f) | 0x00000080) as Byte);
                w.write(((i & 0x0000003f) | 0x00000080) as Byte);
                return;
            }
            if ((i & 0xffe00000) == 0) {
                w.write((((i >> 18n) & 0x00000007) | 0x000000f0) as Byte);
                w.write((((i >> 12n) & 0x0000003f) | 0x00000080) as Byte);
                w.write((((i >> 6n) & 0x0000003f) | 0x00000080) as Byte);
                w.write(((i & 0x0000003f) | 0x00000080) as Byte);
                return;
            }
        }
    }
    
    public final static class ShortMarshal implements Marshal[Short] {
        public def read(r:Reader):Short {
            val b1 = r.read();
            val b2 = r.read();
            return (((b1 & 0xffn) << 8n) | (b2 & 0xffn)) as Short;
        }

        public def write(w:Writer, s:Short):void {
            val i = s as Int;
            val b1 = (i >> 8n) as Byte;
            val b2 = i as Byte;
            w.write(b1);
            w.write(b2);
        }
    }

    public final class UShortMarshal implements Marshal[UShort] {
        public def read(r:Reader):UShort = (r.readShort() as UShort);
        public def write(w:Writer, us:UShort):void { 
            w.writeShort(us as Short); 
        }
    }    
 
     public final static class IntMarshal implements Marshal[Int] {
        public def read(r:Reader):Int {
            val b1 = r.read();
            val b2 = r.read();
            val b3 = r.read();
            val b4 = r.read();
            return (((b1 & 0xffn) << 24n) | ((b2 & 0xffn) << 16n) | ((b3 & 0xffn) << 8n) | (b4 & 0xffn)) as Int;
        }
        
        public def write(w:Writer, i:Int):void {
            val b1 = (i >> 24n) as Byte;
            val b2 = (i >> 16n) as Byte;
            val b3 = (i >> 8n) as Byte;
            val b4 = i as Byte;
            w.write(b1);
            w.write(b2);
            w.write(b3);
            w.write(b4);
        }
    }

    public final class UIntMarshal implements Marshal[UInt] {
        public def read(r:Reader):UInt = (r.readInt() as UInt);
        public def write(w:Writer, ui:UInt):void {
            w.writeInt(ui as Int); 
        }
    }
    
    public final static class LongMarshal implements Marshal[Long] {
        public def read(r:Reader):Long {
            var l: Long = 0;
            for (var i: Int = 0n; i < 8n; i++) {
                val b = r.read();
                l = (l << 8n) | (b & 0xffn);
            }
            return l;
        }
        
        public def write(w:Writer, l:Long):void {
            var shift: Int = 64n;
            while (shift > 0) {
                shift -= 8;
                val b = (l >> shift) as Byte;
                w.write(b);
            }
        }
    }

    public final class ULongMarshal implements Marshal[ULong] {
        public def read(r:Reader):ULong = (r.readLong() as ULong);
        public def write(w:Writer, ul:ULong):void { 
            w.writeLong(ul as Long); 
        }
    }

    public final static class FloatMarshal implements Marshal[Float] {
        public def read(r:Reader):Float {
            val i = INT.read(r);
            return Float.fromIntBits(i);
        }
        public def write(w:Writer, f:Float):void {
            val i = f.toIntBits();
            INT.write(w, i);
        }
    }
    
    public final static class DoubleMarshal implements Marshal[Double] {
        public def read(r:Reader):Double {
            val l = LONG.read(r);
            return Double.fromLongBits(l);
        }
        public def write(w:Writer, d:Double):void {
            val l = d.toLongBits();
            LONG.write(w, l);
        }
    }
}
