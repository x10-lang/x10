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
 * special dispatchers for method abstract public x10.lang.Bitwise.operator{&,|,^}(that:T):T
 */
public interface Bitwise {
    public interface $B extends x10.lang.Bitwise<Byte> {
        public byte $ampersand$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $bar$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $caret$B(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $S extends x10.lang.Bitwise<Short> {
        public short $ampersand$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $bar$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $caret$S(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $I extends x10.lang.Bitwise<Int> {
        public int $ampersand$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $bar$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $caret$I(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $J extends x10.lang.Bitwise<Long> {
        public long $ampersand$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $bar$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $caret$J(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $b extends x10.lang.Bitwise<UByte> {
        public byte $ampersand$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $bar$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $caret$b(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $s extends x10.lang.Bitwise<UShort> {
        public short $ampersand$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $bar$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $caret$s(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $i extends x10.lang.Bitwise<UInt> {
        public int $ampersand$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $bar$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $caret$i(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $j extends x10.lang.Bitwise<ULong> {
        public long $ampersand$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $bar$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $caret$j(java.lang.Object a1, x10.rtt.Type t1);
    }
}
