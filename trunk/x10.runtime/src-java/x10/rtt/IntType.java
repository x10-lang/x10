/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.rtt;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class IntType extends RuntimeType<Integer> {
    public IntType() {
        super(int.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Integer;
    }
    
    @Override
    public Object makeArray(int length) {
        return new int[length];
    }
    
    @Override
    public Integer getArray(Object array, int i) {
        return ((int[]) array)[i];
    }
    
    @Override
    public Integer setArray(Object array, int i, Integer v) {
        ((int[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((int[]) array).length;
    }
    
    @Override
    public Fun_0_1<Integer, Integer> absOperator() {
        return new Fun_0_1<Integer, Integer>() {
            public Integer apply(Integer x) {
                return (x > 0 ? x : -x);
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_1<Integer, Integer> scaleOperator(final int k) {
        return new Fun_0_1<Integer, Integer>() {
            public Integer apply(Integer x) {
                return (x * k);
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
        };
    }
    
    @Override
    public Fun_0_2<Integer, Integer, Integer> addOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x + y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> subOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x - y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> mulOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x * y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> divOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x / y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> modOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x % y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> maxOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x > y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    @Override
    public Fun_0_2<Integer, Integer, Integer> minOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x < y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    
    @Override
    public Fun_0_2<Integer, Integer, Integer> andOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x & y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }

    @Override
    public Fun_0_2<Integer, Integer, Integer> orOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x | y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }
    
    @Override
    public Fun_0_2<Integer, Integer, Integer> xorOperator() {
        return new Fun_0_2<Integer, Integer, Integer>() {
            public Integer apply(Integer x, Integer y) {
                return (x ^ y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.INT; }
        };
    }

    @Override
    public Fun_0_1<Integer, Integer> negOperator() {
        return new Fun_0_1<Integer, Integer>() {
            public Integer apply(Integer x) {
                return -x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
        };
    }
    
    @Override
    public Fun_0_1<Integer, Integer> posOperator() {
        return new Fun_0_1<Integer, Integer>() {
            public Integer apply(Integer x) {
                return +x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
        };
    }
    
    @Override
    public Fun_0_1<Integer, Integer> invOperator() {
        return new Fun_0_1<Integer, Integer>() {
            public Integer apply(Integer x) {
                return ~x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.INT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
        };
    }
    
    @Override
    public Integer minValue() {
        return Integer.MIN_VALUE;
    }

    @Override
    public Integer maxValue() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public Integer zeroValue() {
        return 0;
    }
    
    @Override
    public Integer unitValue() {
        return 1;
    }
}
