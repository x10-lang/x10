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
 * special dispatchers for method abstract public x10.lang.Reducible.operator()(T,T):T
 */
public interface Reducible {
    public interface x10$lang$Byte extends x10.lang.Reducible<Byte> {
        public byte $apply$B(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Short extends x10.lang.Reducible<Short> {
        public short $apply$S(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Int extends x10.lang.Reducible<Int> {
        public int $apply$I(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Long extends x10.lang.Reducible<Long> {
        public long $apply$J(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Float extends x10.lang.Reducible<Float> {
        public float $apply$F(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Double extends x10.lang.Reducible<Double> {
        public double $apply$D(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Char extends x10.lang.Reducible<Char> {
        public char $apply$C(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$Boolean extends x10.lang.Reducible<Boolean> {
        public boolean $apply$Z(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$UByte extends x10.lang.Reducible<UByte> {
        public byte $apply$b(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$UShort extends x10.lang.Reducible<UShort> {
        public short $apply$s(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$UInt extends x10.lang.Reducible<UInt> {
        public int $apply$i(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
    public interface x10$lang$ULong extends x10.lang.Reducible<ULong> {
        public long $apply$j(java.lang.Object a1, x10.rtt.Type t1, java.lang.Object a2, x10.rtt.Type t2);
    }
}
