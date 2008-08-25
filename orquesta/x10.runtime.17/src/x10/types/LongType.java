package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class LongType extends RuntimeType<Long> {
    public LongType() {
        super(long.class);
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
    public void setArray(Object array, int i, Long v) {
        ((long[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Long, Long> absOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return (x > 0 ? x : -x);
            }
        };
    }
    @Override
    public Fun_0_1<Long, Long> scaleOperator(final int k) {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> addOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> subOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> mulOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> divOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> modOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> maxOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Long, Long, Long> minOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> andOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x & y);
            }
        };
    }

    @Override
    public Fun_0_2<Long, Long, Long> orOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x | y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Long, Long, Long> xorOperator() {
        return new Fun_0_2<Long, Long, Long>() {
            public Long apply(Long x, Long y) {
                return (x ^ y);
            }
        };
    }

    @Override
    public Fun_0_1<Long, Long> negOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Long, Long> posOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return +x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Long, Long> invOperator() {
        return new Fun_0_1<Long, Long>() {
            public Long apply(Long x) {
                return ~x;
            }
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
