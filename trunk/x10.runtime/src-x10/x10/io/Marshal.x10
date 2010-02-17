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

import x10.util.StringBuilder;

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
public interface Marshal[T] {
    public global def read(r: Reader): T throws IOException;
    public global def write(w: Writer, T): Void throws IOException;
    
    public const BOOLEAN = new BooleanMarshal();
    public const BYTE = new ByteMarshal();
    public const CHAR = new CharMarshal();
    public const SHORT = new ShortMarshal();
    public const INT = new IntMarshal();
    public const LONG = new LongMarshal();
    public const FLOAT = new FloatMarshal();
    public const DOUBLE = new DoubleMarshal();
    public const LINE = new LineMarshal();
    
    public static class LineMarshal implements Marshal[String] {
        public global def read(r: Reader): String throws IOException {
            val sb = new StringBuilder();
            var ch: Char;
            do {
                ch = CHAR.read(r);
            	sb.add(ch);
            } while (ch != '\n');
            return sb.result();
        }
        public global def write(w: Writer, s: String): Void throws IOException {
            for (var i: int = 0; i < s.length(); i++) {
                val ch = s(i);
                CHAR.write(w, ch);
            }
            // BROKEN code gen
            /*
            for (ch: Char in s.chars()) {
                CHAR.write(w, ch);
            }
            */
        }
    }
    
    public static class BooleanMarshal implements Marshal[Boolean] {
        public global def read(r: Reader): Boolean throws IOException = r.read() != 0;
        public global def write(w: Writer, b: Boolean): Void throws IOException = w.write((b ? 0 : 1) as Byte);
    }
    
    public static class ByteMarshal implements Marshal[Byte] {
        public global def read(r: Reader): Byte throws IOException = r.read();
        public global def write(w: Writer, b: Byte): Void throws IOException = w.write(b);
    }

    public static class CharMarshal implements Marshal[Char] {
        public global def read(r: Reader): Char throws IOException {
            val b1 = r.read();
            if (b1 == -1) throw new EOFException();
            if ((b1 & 0xf8) == 0xf0) {
                val b2 = r.read();
                val b3 = r.read();
                val b4 = r.read();
                return Char.chr(((b1 & 0x03) << 18) | ((b2 & 0x3f) << 12) | ((b3 & 0x3f) << 6) | (b4 & 0x3f));
            }
            if ((b1 & 0xf0) == 0xe0) {
                val b2 = r.read();
                val b3 = r.read();
                return Char.chr(((b1 & 0x1f) << 12) | ((b2 & 0x3f) << 6) | (b3 & 0x3f));
            }
            if ((b1 & 0xe0) == 0xc0) {
                val b2 = r.read();
                return Char.chr(((b1 & 0x1f) << 6) | (b2 & 0x3f));
            }
//            if ((b1 & 0x80) == 0)
                return Char.chr(b1);
        }

        public global def write(w: Writer, c: Char): Void throws IOException {
            val i = c.ord();
            if ((i & 0xffffff80) == 0) {
                w.write(i as Byte);
                return;
            }
            if ((i & 0xfffff800) == 0) {
                w.write(((i >>> 6) & 0x0000001f) | 0x000000c0 as Byte);
                w.write((i & 0x0000003f) | 0x00000080 as Byte);
                return;
            }
            if ((i & 0xffff0000) == 0) {
                w.write(((i >>> 12) & 0x0000000f) | 0x000000e0 as Byte);
                w.write(((i >>> 6) & 0x0000003f) | 0x00000080 as Byte);
                w.write((i & 0x0000003f) | 0x00000080 as Byte);
                return;
            }
            if ((i & 0xffe00000) == 0) {
                w.write(((i >>> 18) & 0x00000007) | 0x000000f0 as Byte);
                w.write(((i >>> 12) & 0x0000003f) | 0x00000080 as Byte);
                w.write(((i >>> 6) & 0x0000003f) | 0x00000080 as Byte);
                w.write((i & 0x0000003f) | 0x00000080 as Byte);
                return;
            }
        }
    }
    
    public static class ShortMarshal implements Marshal[Short] {
        public global def read(r: Reader): Short throws IOException {
            val b1 = r.read();
            val b2 = r.read();
            return (((b1 & 0xff) << 8) | b2) as Short;
        }

        public global def write(w: Writer, s: Short): Void throws IOException {
            val i = s as Int;
            val b1 = ((i >>> 8) & 0xff) as Byte;
            val b2 = (i & 0xff) as Byte;
            w.write(b1);
            w.write(b2);
        }
    }
    
    public static class IntMarshal implements Marshal[Int] {
        public global def read(r: Reader): Int throws IOException {
            val b1 = r.read();
            val b2 = r.read();
            val b3 = r.read();
            val b4 = r.read();
            return (((b1 & 0xff) << 24) | ((b2 & 0xff) << 16) | ((b3 & 0xff) << 8) | (b4 & 0xff)) as Int;
        }
        
        public global def write(w: Writer, i: Int): Void throws IOException {
            val b1 = ((i >>> 24) & 0xff) as Byte;
            val b2 = ((i >>> 16) & 0xff) as Byte;
            val b3 = ((i >>> 8) & 0xff) as Byte;
            val b4 = (i & 0xff) as Byte;
            w.write(b1);
            w.write(b2);
            w.write(b3);
            w.write(b4);
        }
    }
    
    public static class LongMarshal implements Marshal[Long] {
        public global def read(r: Reader): Long throws IOException {
            var l: Long = 0l;
            for (var i: Int = 0; i < 8; i++) {
                val b = r.read();
                l = (l << 8) | (b & 0xff);
            }
            return l;
        }
        
        public global def write(w: Writer, l: Long): Void throws IOException {
            var shift: int = 64;
            while (shift > 0) {
                shift -= 8;
                val b = ((l >>> shift) & 0xffL) as Byte;
                w.write(b);
            }
        }
    }

    public static class FloatMarshal implements Marshal[Float] {
        public global def read(r: Reader): Float throws IOException {
            val i = INT.read(r);
            return Float.fromIntBits(i);
        }
        public global def write(w: Writer, f: Float): Void throws IOException {
            val i = f.toIntBits();
            INT.write(w, i);
        }
    }
    
    public static class DoubleMarshal implements Marshal[Double] {
        public global def read(r: Reader): Double throws IOException {
            val l = LONG.read(r);
            return Double.fromLongBits(l);
        }
        public global def write(w: Writer, d: Double): Void throws IOException {
            val l = d.toLongBits();
            LONG.write(w, l);
        }
    }
}
