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
 * special dispatchers for method abstract public x10.lang.Arithmetic.operator{+,-,*,/}(that:T):T
 */
public interface Arithmetic {
    public interface $B extends x10.lang.Arithmetic<Byte> {
        public byte $plus$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $minus$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $times$B(java.lang.Object a1, x10.rtt.Type t1);
        public byte $over$B(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $S extends x10.lang.Arithmetic<Short> {
        public short $plus$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $minus$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $times$S(java.lang.Object a1, x10.rtt.Type t1);
        public short $over$S(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $I extends x10.lang.Arithmetic<Int> {
        public int $plus$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $minus$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $times$I(java.lang.Object a1, x10.rtt.Type t1);
        public int $over$I(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $J extends x10.lang.Arithmetic<Long> {
        public long $plus$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $minus$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $times$J(java.lang.Object a1, x10.rtt.Type t1);
        public long $over$J(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $F extends x10.lang.Arithmetic<Float> {
        public float $plus$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $minus$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $times$F(java.lang.Object a1, x10.rtt.Type t1);
        public float $over$F(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $D extends x10.lang.Arithmetic<Double> {
        public double $plus$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $minus$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $times$D(java.lang.Object a1, x10.rtt.Type t1);
        public double $over$D(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $b extends x10.lang.Arithmetic<UByte> {
        public byte $plus$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $minus$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $times$b(java.lang.Object a1, x10.rtt.Type t1);
        public byte $over$b(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $s extends x10.lang.Arithmetic<UShort> {
        public short $plus$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $minus$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $times$s(java.lang.Object a1, x10.rtt.Type t1);
        public short $over$s(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $i extends x10.lang.Arithmetic<UInt> {
        public int $plus$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $minus$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $times$i(java.lang.Object a1, x10.rtt.Type t1);
        public int $over$i(java.lang.Object a1, x10.rtt.Type t1);
    }
    public interface $j extends x10.lang.Arithmetic<ULong> {
        public long $plus$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $minus$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $times$j(java.lang.Object a1, x10.rtt.Type t1);
        public long $over$j(java.lang.Object a1, x10.rtt.Type t1);
    }
}
