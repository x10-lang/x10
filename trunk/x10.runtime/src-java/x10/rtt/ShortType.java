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


public class ShortType extends RuntimeType<Short> {
    public ShortType() {
        super(short.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Short;
    }

    @Override
    public Object makeArray(int length) {
        return new short[length];
    }
    
    @Override
    public Short getArray(Object array, int i) {
        return ((short[]) array)[i];
    }
    
    @Override
    public Short setArray(Object array, int i, Short v) {
        ((short[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((short[]) array).length;
    }
    
    @Override
    public Fun_0_1<Short, Short> absOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) (x > 0 ? x : -x);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_1<Short, Short> scaleOperator(final int k) {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) (x * k);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> addOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x + y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> subOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x - y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> mulOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x * y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> divOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x / y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> modOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x % y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> maxOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (x > y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> minOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (x < y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> andOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x & y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }

    @Override
    public Fun_0_2<Short, Short, Short> orOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x | y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> xorOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x ^ y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.SHORT; return null;}
        };
    }

    @Override
    public Fun_0_1<Short, Short> negOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) -x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Short, Short> posOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) +x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Short, Short> invOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) ~x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.SHORT; return null;}
        };
    }
    
    @Override
    public Short minValue() {
        return Short.MIN_VALUE;
    }

    @Override
    public Short maxValue() {
        return Short.MAX_VALUE;
    }
    
    @Override
    public Short zeroValue() {
        return (short) 0;
    }
    
    @Override
    public Short unitValue() {
        return (short) 1;
    }
}
