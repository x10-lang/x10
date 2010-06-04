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


public class LongType extends RuntimeType<Long> {
    public LongType() {
        super(long.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Long;
    }

    @Override
    public Object makeArray(int length) {
        return new long[length];
    }
    
    @Override
    public Long getArray(Object array, int i) {
        return ((long[]) array)[i];
    }
    
    @Override
    public Long setArray(Object array, int i, Long v) {
        ((long[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((long[]) array).length;
    }
    
    @Override
    public Fun_0_1<Long, Long> absOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return (x > 0 ? x : -x);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_1<Long, Long> scaleOperator(final int k) {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return (x * k);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> addOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x + y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> subOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x - y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> mulOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x * y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> divOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x / y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> modOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x % y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> maxOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x > y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> minOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x < y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> andOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x & y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }

    @Override
    public Fun_0_2<Long, Long, Long> orOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x | y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> xorOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x ^ y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.LONG; return null;}
        };
    }

    @Override
    public Fun_0_1<Long, Long> negOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return -x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Long, Long> posOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return +x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Long, Long> invOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return ~x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.LONG; return null;}
        };
    }
    
    @Override
    public Long minValue() {
        return Long.MIN_VALUE;
    }

    @Override
    public Long maxValue() {
        return Long.MAX_VALUE;
    }
    
    @Override
    public Long zeroValue() {
        return 0L;
    }
    
    @Override
    public Long unitValue() {
        return 1L;
    }
}
