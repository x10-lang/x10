package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class ByteType extends RuntimeType<Byte> {
    public ByteType() {
        super(byte.class);
    }
    
    @Override
    public Object makeArray(int length) {
        return new byte[length];
    }
    
    @Override
    public Byte getArray(Object array, int i) {
        return ((byte[]) array)[i];
    }
    
    @Override
    public void setArray(Object array, int i, Byte v) {
        ((byte[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Byte, Byte> absOperator() {
        return new Fun_0_1<Byte, Byte>() {
            public Byte apply(Byte x) {
                return (byte) (x > 0 ? x : -x);
            }
        };
    }
    @Override
    public Fun_0_1<Byte, Byte> scaleOperator(final int k) {
        return new Fun_0_1<Byte, Byte>() {
            public Byte apply(Byte x) {
                return (byte) (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Byte, Byte, Byte> addOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> subOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> mulOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> divOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> modOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> maxOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Byte, Byte, Byte> minOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Byte, Byte, Byte> andOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x & y);
            }
        };
    }

    @Override
    public Fun_0_2<Byte, Byte, Byte> orOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x | y);
            }
        };
    }
    
    @Override
    public Fun_0_2<Byte, Byte, Byte> xorOperator() {
        return new Fun_0_2<Byte, Byte, Byte>() {
            public Byte apply(Byte x, Byte y) {
                return (byte) (x ^ y);
            }
        };
    }

    @Override
    public Fun_0_1<Byte, Byte> negOperator() {
        return new Fun_0_1<Byte, Byte>() {
            public Byte apply(Byte x) {
                return (byte) -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Byte, Byte> posOperator() {
        return new Fun_0_1<Byte, Byte>() {
            public Byte apply(Byte x) {
                return (byte) +x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Byte, Byte> invOperator() {
        return new Fun_0_1<Byte, Byte>() {
            public Byte apply(Byte x) {
                return (byte) ~x;
            }
        };
    }
    
    @Override
    public Byte minValue() {
        return Byte.MIN_VALUE;
    }

    @Override
    public Byte maxValue() {
        return Byte.MAX_VALUE;
    }
    
    @Override
    public Byte zeroValue() {
        return (byte) 0;
    }
    
    @Override
    public Byte unitValue() {
        return (byte) 1;
    }
}
