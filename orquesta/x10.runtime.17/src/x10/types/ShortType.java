package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class ShortType extends RuntimeType<Short> {
    public ShortType() {
        super(short.class);
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
    public void setArray(Object array, int i, Short v) {
        ((short[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Short, Short> absOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) (x > 0 ? x : -x);
            }
        };
    }
    @Override
    public Fun_0_1<Short, Short> scaleOperator(final int k) {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> addOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> subOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> mulOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> divOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> modOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> maxOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Short, Short, Short> minOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> andOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x & y);
            }
        };
    }

    @Override
    public Fun_0_2<Short, Short, Short> orOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x | y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Short, Short, Short> xorOperator() {
        return new Fun_0_2<Short, Short, Short>() {
            public Short apply(Short x, Short y) {
                return (short) (x ^ y);
            }
        };
    }

    @Override
    public Fun_0_1<Short, Short> negOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Short, Short> posOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) +x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Short, Short> invOperator() {
        return new Fun_0_1<Short, Short>() {
            public Short apply(Short x) {
                return (short) ~x;
            }
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
