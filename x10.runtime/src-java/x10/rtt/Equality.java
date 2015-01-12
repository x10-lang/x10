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

package x10.rtt;


public class Equality {
    public static boolean equalsequals(boolean a, boolean b) { return a == b; }
    public static boolean equalsequals(boolean a, Object b) { return equalsequals(x10.core.Boolean.$box(a), b); }
    public static int compareTo(boolean a, boolean b) { return a == b ? 0 : (b ? -1 : 1); }
    public static int compareTo(boolean a, Comparable b) { return compareTo(x10.core.Boolean.$box(a), b); }
    
    public static boolean equalsequals(char a, char b) { return a == b; }
    public static boolean equalsequals(char a, Object b) { return equalsequals(x10.core.Char.$box(a), b); }
    public static int compareTo(char a, char b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(char a, Comparable b) { return compareTo(x10.core.Char.$box(a), b); }
    
    public static boolean equalsequals(byte a, byte b) { return a == b; }
    public static boolean equalsequals(byte a, Object b) { return equalsequals(x10.core.Byte.$box(a), b); }
    public static int compareTo(byte a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(byte a, Comparable b) { return compareTo(x10.core.Byte.$box(a), b); }

    public static boolean equalsequals(short a, short b) { return a == b; }
    public static boolean equalsequals(short a, Object b) { return equalsequals(x10.core.Short.$box(a), b); }
    public static int compareTo(short a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(short a, Comparable b) { return compareTo(x10.core.Short.$box(a), b); }
    
    public static boolean equalsequals(int a, int b) { return a == b; }
    public static boolean equalsequals(int a, Object b) { return equalsequals(x10.core.Int.$box(a), b); }
    public static int compareTo(int a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(int a, Comparable b) { return compareTo(x10.core.Int.$box(a), b); }
    
    public static boolean equalsequals(long a, long b) { return a == b; }
    public static boolean equalsequals(long a, Object b) { return equalsequals(x10.core.Long.$box(a), b); }
    public static int compareTo(long a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(long a, Comparable b) { return compareTo(x10.core.Long.$box(a), b); }
    
    public static boolean equalsequals(float a, float b) { return a == b; }
    public static boolean equalsequals(float a, Object b) { return equalsequals(x10.core.Float.$box(a), b); }
    public static int compareTo(float a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(float a, Comparable b) { return compareTo(x10.core.Float.$box(a), b); }
    
    public static boolean equalsequals(double a, double b) { return a == b; }
    public static boolean equalsequals(double a, Object b) { return equalsequals(x10.core.Double.$box(a), b); }
    public static int compareTo(double a, byte b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, short b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, int b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, long b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, float b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, double b) { return a == b ? 0 : (a < b ? -1 : 1); }
    public static int compareTo(double a, Comparable b) { return compareTo(x10.core.Double.$box(a), b); }
    
    public static boolean equalsequals(Object a, boolean b) { return equalsequals(a, x10.core.Boolean.$box(b)); }
    public static boolean equalsequals(Object a, byte b) { return equalsequals(a, x10.core.Byte.$box(b)); }
    public static boolean equalsequals(Object a, short b) { return equalsequals(a, x10.core.Short.$box(b)); }
    public static boolean equalsequals(Object a, char b) { return equalsequals(a, x10.core.Char.$box(b)); }
    public static boolean equalsequals(Object a, int b) { return equalsequals(a, x10.core.Int.$box(b)); }
    public static boolean equalsequals(Object a, long b) { return equalsequals(a, x10.core.Long.$box(b)); }
    public static boolean equalsequals(Object a, float b) { return equalsequals(a, x10.core.Float.$box(b)); }
    public static boolean equalsequals(Object a, double b) { return equalsequals(a, x10.core.Double.$box(b)); }
    public static int compareTo(Comparable a, boolean b) { return compareTo(a, x10.core.Boolean.$box(b)); }
    public static int compareTo(Comparable a, byte b) { return compareTo(a, x10.core.Byte.$box(b)); }
    public static int compareTo(Comparable a, short b) { return compareTo(a, x10.core.Short.$box(b)); }
    public static int compareTo(Comparable a, char b) { return compareTo(a, x10.core.Char.$box(b)); }
    public static int compareTo(Comparable a, int b) { return compareTo(a, x10.core.Int.$box(b)); }
    public static int compareTo(Comparable a, long b) { return compareTo(a, x10.core.Long.$box(b)); }
    public static int compareTo(Comparable a, float b) { return compareTo(a, x10.core.Float.$box(b)); }
    public static int compareTo(Comparable a, double b) { return compareTo(a, x10.core.Double.$box(b)); }

    public static boolean equalsequals(Object a, Object b) {
        // Ref equality is pointer equality.
        // This also handles "null == null" and serves as a short cut for other types.
        if (a == b) return true;
        
        // Struct equality is value equality that implys non-null.
        if (a == null || b == null) return false;

        // N.B. this is shortcut that can be removed safely
        // if we need to know the referenceness of values at runtime in some other context,
        // we may want to introduce isref() in RTT, which comes from Type.isReference(), and use it. 
//        if (a instanceof x10.core.RefI || b instanceof x10.core.RefI) return false;
        
        // equality of structs are follows
        // short cuts for primitives
        if (a instanceof x10.core.Boolean && b instanceof x10.core.Boolean)
            return x10.core.Boolean.$unbox((x10.core.Boolean) a) == x10.core.Boolean.$unbox((x10.core.Boolean) b);
        if (a instanceof x10.core.Char && b instanceof x10.core.Char)
            return x10.core.Char.$unbox((x10.core.Char) a) == x10.core.Char.$unbox((x10.core.Char) b);
        if (a instanceof Number && b instanceof Number)
            return equalsNumbers((Number) a, (Number) b);
        // for general structs. it also works with primitives.
        if (a instanceof x10.core.StructI) return ((x10.core.StructI) a)._struct_equals$O(b);
        
        return false;
    }
    public static int compareTo(Comparable a, Comparable b) { return a.compareTo(b); }
    
    private static boolean equalsNumbers(Number a, Number b) {
        if (a instanceof x10.core.Double && b instanceof x10.core.Double) {
            return x10.core.Double.$unbox((x10.core.Double)a) == x10.core.Double.$unbox((x10.core.Double)b);
        }
        
        if (a instanceof x10.core.Float && b instanceof x10.core.Float) {
            return x10.core.Float.$unbox((x10.core.Float)a) == x10.core.Float.$unbox((x10.core.Float)b);
        }
        
        if (a instanceof x10.core.Long && b instanceof x10.core.Long) {
            return x10.core.Long.$unbox((x10.core.Long)a) == x10.core.Long.$unbox((x10.core.Long)b);
        }

        if (a instanceof x10.core.Int && b instanceof x10.core.Int) {
            return x10.core.Int.$unbox((x10.core.Int)a) == x10.core.Int.$unbox((x10.core.Int)b);
        }
        
        if (a instanceof x10.core.Short && b instanceof x10.core.Short) {
            return x10.core.Short.$unbox((x10.core.Short)a) == x10.core.Short.$unbox((x10.core.Short)b);
        }
        
        if (a instanceof x10.core.Byte && b instanceof x10.core.Byte) {
            return x10.core.Byte.$unbox((x10.core.Byte)a) == x10.core.Byte.$unbox((x10.core.Byte)b);
        }
        
        if (a instanceof x10.core.ULong && b instanceof x10.core.ULong) {
            return x10.core.ULong.$unbox((x10.core.ULong)a) == x10.core.ULong.$unbox((x10.core.ULong)b);
        }

        if (a instanceof x10.core.UInt && b instanceof x10.core.UInt) {
            return x10.core.UInt.$unbox((x10.core.UInt)a) == x10.core.UInt.$unbox((x10.core.UInt)b);
        }
        
        if (a instanceof x10.core.UShort && b instanceof x10.core.UShort) {
            return x10.core.UShort.$unbox((x10.core.UShort)a) == x10.core.UShort.$unbox((x10.core.UShort)b);
        }
        
        if (a instanceof x10.core.UByte && b instanceof x10.core.UByte) {
            return x10.core.UByte.$unbox((x10.core.UByte)a) == x10.core.UByte.$unbox((x10.core.UByte)b);
        }
        
        return false;
    }
}
