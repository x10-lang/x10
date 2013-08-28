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
 * special return type for method abstract public x10.lang.Sequence.operator()(Int):T
 */
public interface Sequence {
    public interface x10$lang$Byte extends x10.lang.Sequence<Byte> {
        public byte $apply$O(int i);
    }
    public interface x10$lang$Short extends x10.lang.Sequence<Short> {
        public short $apply$O(int i);
    }
    public interface x10$lang$Int extends x10.lang.Sequence<Int> {
        public int $apply$O(int i);
    }
    public interface x10$lang$Long extends x10.lang.Sequence<Long> {
        public long $apply$O(int i);
    }
    public interface x10$lang$Float extends x10.lang.Sequence<Float> {
        public float $apply$O(int i);
    }
    public interface x10$lang$Double extends x10.lang.Sequence<Double> {
        public double $apply$O(int i);
    }
    public interface x10$lang$Char extends x10.lang.Sequence<Char> {
        public char $apply$O(int i);
    }
    public interface x10$lang$Boolean extends x10.lang.Sequence<Boolean> {
        public boolean $apply$O(int i);
    }
    public interface x10$lang$UByte extends x10.lang.Sequence<UByte> {
        public byte $apply$O(int i);
    }
    public interface x10$lang$UShort extends x10.lang.Sequence<UShort> {
        public short $apply$O(int i);
    }
    public interface x10$lang$UInt extends x10.lang.Sequence<UInt> {
        public int $apply$O(int i);
    }
    public interface x10$lang$ULong extends x10.lang.Sequence<ULong> {
        public long $apply$O(int i);
    }
}
