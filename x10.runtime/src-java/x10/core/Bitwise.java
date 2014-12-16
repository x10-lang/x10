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
 * special dispatchers for method abstract public x10.lang.Bitwise.operator{&,|,^}(T):T
 */
public interface Bitwise {
    public interface x10$lang$Byte extends x10.lang.Bitwise<Byte> {
        public byte $ampersand$B(Object a1, Type t1);
        public byte $bar$B(Object a1, Type t1);
        public byte $caret$B(Object a1, Type t1);
    }
    public interface x10$lang$Short extends x10.lang.Bitwise<Short> {
        public short $ampersand$S(Object a1, Type t1);
        public short $bar$S(Object a1, Type t1);
        public short $caret$S(Object a1, Type t1);
    }
    public interface x10$lang$Int extends x10.lang.Bitwise<Int> {
        public int $ampersand$I(Object a1, Type t1);
        public int $bar$I(Object a1, Type t1);
        public int $caret$I(Object a1, Type t1);
    }
    public interface x10$lang$Long extends x10.lang.Bitwise<Long> {
        public long $ampersand$J(Object a1, Type t1);
        public long $bar$J(Object a1, Type t1);
        public long $caret$J(Object a1, Type t1);
    }
    public interface x10$lang$Float extends x10.lang.Bitwise<Float> {
        public float $ampersand$F(Object a1, Type t1);
        public float $bar$F(Object a1, Type t1);
        public float $caret$F(Object a1, Type t1);
    }
    public interface x10$lang$Double extends x10.lang.Bitwise<Double> {
        public double $ampersand$D(Object a1, Type t1);
        public double $bar$D(Object a1, Type t1);
        public double $caret$D(Object a1, Type t1);
    }
    public interface x10$lang$Char extends x10.lang.Bitwise<Char> {
        public char $ampersand$C(Object a1, Type t1);
        public char $bar$C(Object a1, Type t1);
        public char $caret$C(Object a1, Type t1);
    }
    public interface x10$lang$Boolean extends x10.lang.Bitwise<Boolean> {
        public boolean $ampersand$Z(Object a1, Type t1);
        public boolean $bar$Z(Object a1, Type t1);
        public boolean $caret$Z(Object a1, Type t1);
    }
    public interface x10$lang$UByte extends x10.lang.Bitwise<UByte> {
        public byte $ampersand$b(Object a1, Type t1);
        public byte $bar$b(Object a1, Type t1);
        public byte $caret$b(Object a1, Type t1);
    }
    public interface x10$lang$UShort extends x10.lang.Bitwise<UShort> {
        public short $ampersand$s(Object a1, Type t1);
        public short $bar$s(Object a1, Type t1);
        public short $caret$s(Object a1, Type t1);
    }
    public interface x10$lang$UInt extends x10.lang.Bitwise<UInt> {
        public int $ampersand$i(Object a1, Type t1);
        public int $bar$i(Object a1, Type t1);
        public int $caret$i(Object a1, Type t1);
    }
    public interface x10$lang$ULong extends x10.lang.Bitwise<ULong> {
        public long $ampersand$j(Object a1, Type t1);
        public long $bar$j(Object a1, Type t1);
        public long $caret$j(Object a1, Type t1);
    }
}
