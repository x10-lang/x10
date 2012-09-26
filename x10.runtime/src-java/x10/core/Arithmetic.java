/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

package x10.core;

/*
 * special dispatchers for method abstract public x10.lang.Arithmetic.operator{+,-,*,/}(T):T
 */
public interface Arithmetic {
    public interface x10$lang$Byte extends x10.lang.Arithmetic<Byte> {
        public byte $plus$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $minus$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $times$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $over$B(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Short extends x10.lang.Arithmetic<Short> {
        public short $plus$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $minus$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $times$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $over$S(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Int extends x10.lang.Arithmetic<Int> {
        public int $plus$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $minus$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $times$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $over$I(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Long extends x10.lang.Arithmetic<Long> {
        public long $plus$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $minus$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $times$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $over$J(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Float extends x10.lang.Arithmetic<Float> {
        public float $plus$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $minus$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $times$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $over$F(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Double extends x10.lang.Arithmetic<Double> {
        public double $plus$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $minus$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $times$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $over$D(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Char extends x10.lang.Reducible<Char> {
        public char $plus$C(java.lang.Object a1, x10.rtt.Type t1);
        public char $minus$C(java.lang.Object a1, x10.rtt.Type t1);
        public char $times$C(java.lang.Object a1, x10.rtt.Type t1);
        public char $over$C(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Boolean extends x10.lang.Reducible<Boolean> {
        public boolean $plus$Z(java.lang.Object a1, x10.rtt.Type t1);
        public boolean $minus$Z(java.lang.Object a1, x10.rtt.Type t1);
        public boolean $times$Z(java.lang.Object a1, x10.rtt.Type t1);
        public boolean $over$Z(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UByte extends x10.lang.Arithmetic<UByte> {
        public byte $plus$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $minus$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $times$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $over$b(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UShort extends x10.lang.Arithmetic<UShort> {
        public short $plus$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $minus$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $times$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $over$s(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UInt extends x10.lang.Arithmetic<UInt> {
        public int $plus$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $minus$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $times$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $over$i(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$ULong extends x10.lang.Arithmetic<ULong> {
        public long $plus$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $minus$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $times$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $over$j(java.lang.Object a1, x10.rtt.Type t1);
    }
}
