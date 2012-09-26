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
 * special dispatchers for method abstract public x10.lang.Bitwise.operator{&,|,^}(T):T
 */
public interface Bitwise {
    public interface x10$lang$Byte extends x10.lang.Bitwise<Byte> {
        public byte $ampersand$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $bar$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $caret$B(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Short extends x10.lang.Bitwise<Short> {
        public short $ampersand$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $bar$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $caret$S(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Int extends x10.lang.Bitwise<Int> {
        public int $ampersand$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $bar$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $caret$I(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Long extends x10.lang.Bitwise<Long> {
        public long $ampersand$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $bar$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $caret$J(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Float extends x10.lang.Arithmetic<Float> {
        public float $ampersand$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $bar$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $caret$F(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Double extends x10.lang.Arithmetic<Double> {
        public double $ampersand$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $bar$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $caret$D(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Char extends x10.lang.Reducible<Char> {
        public char $ampersand$C(java.lang.Object a1, x10.rtt.Type t1);
        public char $bar$C(java.lang.Object a1, x10.rtt.Type t1);
        public char $caret$C(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$Boolean extends x10.lang.Reducible<Boolean> {
        public boolean $ampersand$Z(java.lang.Object a1, x10.rtt.Type t1);
        public boolean $bar$Z(java.lang.Object a1, x10.rtt.Type t1);
        public boolean $caret$Z(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UByte extends x10.lang.Bitwise<UByte> {
        public byte $ampersand$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $bar$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $caret$b(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UShort extends x10.lang.Bitwise<UShort> {
        public short $ampersand$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $bar$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $caret$s(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$UInt extends x10.lang.Bitwise<UInt> {
        public int $ampersand$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $bar$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $caret$i(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface x10$lang$ULong extends x10.lang.Bitwise<ULong> {
        public long $ampersand$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $bar$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $caret$j(java.lang.Object a1, x10.rtt.Type t1);
    }
}
