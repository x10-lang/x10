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

/*
 * special return type for method abstract public x10.lang.Iterator.next():T
 */
public interface Iterator {
    public interface x10$lang$Byte extends x10.lang.Iterator<Byte> {
        public byte next$O();
    }
    public interface x10$lang$Short extends x10.lang.Iterator<Short> {
        public short next$O();
    }
    public interface x10$lang$Int extends x10.lang.Iterator<Int> {
        public int next$O();
    }
    public interface x10$lang$Long extends x10.lang.Iterator<Long> {
        public long next$O();
    }
    public interface x10$lang$Float extends x10.lang.Iterator<Float> {
        public float next$O();
    }
    public interface x10$lang$Double extends x10.lang.Iterator<Double> {
        public double next$O();
    }
    public interface x10$lang$Char extends x10.lang.Iterator<Char> {
        public char next$O();
    }
    public interface x10$lang$Boolean extends x10.lang.Iterator<Boolean> {
        public boolean next$O();
    }
    public interface x10$lang$UByte extends x10.lang.Iterator<UByte> {
        public byte next$O();
    }
    public interface x10$lang$UShort extends x10.lang.Iterator<UShort> {
        public short next$O();
    }
    public interface x10$lang$UInt extends x10.lang.Iterator<UInt> {
        public int next$O();
    }
    public interface x10$lang$ULong extends x10.lang.Iterator<ULong> {
        public long next$O();
    }
}
