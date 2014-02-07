/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.core;

import x10.rtt.Type;

/*
 * special dispatchers for method abstract public x10.lang.Arithmetic.operator{+,-,*,/}(T):T
 */
public interface Arithmetic {
    public interface x10$lang$Byte extends x10.lang.Arithmetic<Byte> {
        public byte $plus$B(Object a1, Type t1);
        public byte $minus$B(Object a1, Type t1);
        public byte $times$B(Object a1, Type t1);
        public byte $over$B(Object a1, Type t1);
    }
    public interface x10$lang$Short extends x10.lang.Arithmetic<Short> {
        public short $plus$S(Object a1, Type t1);
        public short $minus$S(Object a1, Type t1);
        public short $times$S(Object a1, Type t1);
        public short $over$S(Object a1, Type t1);
    }
    public interface x10$lang$Int extends x10.lang.Arithmetic<Int> {
        public int $plus$I(Object a1, Type t1);
        public int $minus$I(Object a1, Type t1);
        public int $times$I(Object a1, Type t1);
        public int $over$I(Object a1, Type t1);
    }
    public interface x10$lang$Long extends x10.lang.Arithmetic<Long> {
        public long $plus$J(Object a1, Type t1);
        public long $minus$J(Object a1, Type t1);
        public long $times$J(Object a1, Type t1);
        public long $over$J(Object a1, Type t1);
    }
    public interface x10$lang$Float extends x10.lang.Arithmetic<Float> {
        public float $plus$F(Object a1, Type t1);
        public float $minus$F(Object a1, Type t1);
        public float $times$F(Object a1, Type t1);
        public float $over$F(Object a1, Type t1);
    }
    public interface x10$lang$Double extends x10.lang.Arithmetic<Double> {
        public double $plus$D(Object a1, Type t1);
        public double $minus$D(Object a1, Type t1);
        public double $times$D(Object a1, Type t1);
        public double $over$D(Object a1, Type t1);
    }
    public interface x10$lang$Char extends x10.lang.Arithmetic<Char> {
        public char $plus$C(Object a1, Type t1);
        public char $minus$C(Object a1, Type t1);
        public char $times$C(Object a1, Type t1);
        public char $over$C(Object a1, Type t1);
    }
    public interface x10$lang$Boolean extends x10.lang.Arithmetic<Boolean> {
        public boolean $plus$Z(Object a1, Type t1);
        public boolean $minus$Z(Object a1, Type t1);
        public boolean $times$Z(Object a1, Type t1);
        public boolean $over$Z(Object a1, Type t1);
    }
    public interface x10$lang$UByte extends x10.lang.Arithmetic<UByte> {
        public byte $plus$b(Object a1, Type t1);
        public byte $minus$b(Object a1, Type t1);
        public byte $times$b(Object a1, Type t1);
        public byte $over$b(Object a1, Type t1);
    }
    public interface x10$lang$UShort extends x10.lang.Arithmetic<UShort> {
        public short $plus$s(Object a1, Type t1);
        public short $minus$s(Object a1, Type t1);
        public short $times$s(Object a1, Type t1);
        public short $over$s(Object a1, Type t1);
    }
    public interface x10$lang$UInt extends x10.lang.Arithmetic<UInt> {
        public int $plus$i(Object a1, Type t1);
        public int $minus$i(Object a1, Type t1);
        public int $times$i(Object a1, Type t1);
        public int $over$i(Object a1, Type t1);
    }
    public interface x10$lang$ULong extends x10.lang.Arithmetic<ULong> {
        public long $plus$j(Object a1, Type t1);
        public long $minus$j(Object a1, Type t1);
        public long $times$j(Object a1, Type t1);
        public long $over$j(Object a1, Type t1);
    }
}
