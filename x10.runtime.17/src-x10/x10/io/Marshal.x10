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

import x10.util.StringBuilder;

public interface Marshal[T] {
    public def read(r: Reader): T throws IOException;
    public def write(w: Writer, T): Void throws IOException;
    
    public const BOOLEAN = new BooleanMarshal();
    public const BYTE: ByteMarshal = new ByteMarshal();
    public const CHAR: CharMarshal = new CharMarshal();
    public const SHORT: ShortMarshal = new ShortMarshal();
    public const INT: IntMarshal = new IntMarshal();
    public const LONG: LongMarshal = new LongMarshal();
    public const FLOAT: FloatMarshal = new FloatMarshal();
    public const DOUBLE: DoubleMarshal = new DoubleMarshal();
    public const LINE: LineMarshal = new LineMarshal();
    
    public static value LineMarshal implements Marshal[String] {
        public def read(r: Reader): String throws IOException {
            val sb = new StringBuilder();
            var ch: Char;
            do {
                ch = CHAR.read(r);
            	sb.add(ch);
            } while (ch != '\n');
            return sb.result();
        }
        public def write(w: Writer, s: String): Void throws IOException {
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
    
    public static value BooleanMarshal implements Marshal[Boolean] {
        public def read(r: Reader): Boolean throws IOException = r.read() != 0;
        public def write(w: Writer, b: Boolean): Void throws IOException = w.write((b ? 0 : 1) to Byte);
    }
    
    public static value ByteMarshal implements Marshal[Byte] {
        public def read(r: Reader): Byte throws IOException = r.read();
        public def write(w: Writer, b: Byte): Void throws IOException = w.write(b);
    }

    public static value CharMarshal implements Marshal[Char] {
        public def read(r: Reader): Char throws IOException {
            val b1 = r.read();
            if (b1 == -1) throw new EOFException();
            if ((b1 & 0x80) == 0)
                return b1 to Char;
            val b2 = r.read();
            return ((b2 << 8) | b1) to Char;
        }
        public def write(w: Writer, c: Char): Void throws IOException {
            val i = c to Int;
            val b2 = ((i >>> 8) & 0xff) to Byte;
            val b1 = ((i & 0xff) to Byte) to Byte;
            w.write(b1);
            if ((b1 & 0x80) == 0 && b2 == 0)
                return;
            w.write(b2);
        }
    }
    
    public static value ShortMarshal implements Marshal[Short] {
        public def read(r: Reader): Short throws IOException {
            val b1 = r.read();
            val b2 = r.read();
            return ((b1 << 8) | b2) to Short;
        }

        public def write(w: Writer, s: Short): Void throws IOException {
            val i = s to Int;
            val b1 = ((i >>> 8) & 0xff) to Byte;
            val b2 = (i & 0xff) to Byte;
            w.write(b1);
            w.write(b2);
        }
    }
    
    public static value IntMarshal implements Marshal[Int] {
        public def read(r: Reader): Int throws IOException {
            val b1 = r.read();
            val b2 = r.read();
            val b3 = r.read();
            val b4 = r.read();
            return ((b1 << 24) | (b2 << 16) | (b3 << 8) | b4) to Int;
        }
        
        public def write(w: Writer, i: Int): Void throws IOException {
            val b1 = ((i >>> 24) & 0xff) to Byte;
            val b2 = ((i >>> 16) & 0xff) to Byte;
            val b3 = ((i >>> 8) & 0xff) to Byte;
            val b4 = (i & 0xff) to Byte;
            w.write(b1);
            w.write(b2);
            w.write(b3);
            w.write(b4);
        }
    }
    
    public static value LongMarshal implements Marshal[Long] {
        public def read(r: Reader): Long throws IOException {
            var l: Long = 0l;
            for (var i: Int = 0; i < 8; i++) {
                l <<= 8;
                val b = r.read();
                l = (l << 8) | b;
            }
            return l;
        }
        
        public def write(w: Writer, l: Long): Void throws IOException {
            var shift: int = 64;
            while (shift > 0) {
                shift -= 8;
                val b = ((l >>> shift) & 0xffL) to Byte;
                w.write(b);
            }
        }
    }

    public static value FloatMarshal implements Marshal[Float] {
        public def read(r: Reader): Float throws IOException {
            val i = INT.read(r);
            return Float.fromIntBits(i);
        }
        public def write(w: Writer, f: Float): Void throws IOException {
            val i = f.toIntBits();
            INT.write(w, i);
        }
    }
    
    public static value DoubleMarshal implements Marshal[Double] {
        public def read(r: Reader): Double throws IOException {
            val l = LONG.read(r);
            return Double.fromLongBits(l);
        }
        public def write(w: Writer, d: Double): Void throws IOException {
            val l = d.toLongBits();
            LONG.write(w, l);
        }
    }
}
