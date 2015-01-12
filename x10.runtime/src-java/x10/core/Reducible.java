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

package x10.core;

import x10.rtt.Type;

/*
 * special dispatchers for method abstract public x10.lang.Reducible.operator()(T,T):T
 */
public interface Reducible {
    public interface x10$lang$Byte extends x10.lang.Reducible<Byte> {
        public byte $apply$B(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Short extends x10.lang.Reducible<Short> {
        public short $apply$S(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Int extends x10.lang.Reducible<Int> {
        public int $apply$I(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Long extends x10.lang.Reducible<Long> {
        public long $apply$J(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Float extends x10.lang.Reducible<Float> {
        public float $apply$F(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Double extends x10.lang.Reducible<Double> {
        public double $apply$D(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Char extends x10.lang.Reducible<Char> {
        public char $apply$C(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$Boolean extends x10.lang.Reducible<Boolean> {
        public boolean $apply$Z(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$UByte extends x10.lang.Reducible<UByte> {
        public byte $apply$b(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$UShort extends x10.lang.Reducible<UShort> {
        public short $apply$s(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$UInt extends x10.lang.Reducible<UInt> {
        public int $apply$i(Object a1, Type t1, Object a2, Type t2);
    }
    public interface x10$lang$ULong extends x10.lang.Reducible<ULong> {
        public long $apply$j(Object a1, Type t1, Object a2, Type t2);
    }
}
