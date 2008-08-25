package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class FloatType extends RuntimeType<Float> {
    public FloatType() {
        super(float.class);
    }
    
    @Override
    public Object makeArray(int length) {
        return new float[length];
    }
    
    @Override
    public Float getArray(Object array, int i) {
        return ((float[]) array)[i];
    }
    
    @Override
    public void setArray(Object array, int i, Float v) {
        ((float[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Float, Float> absOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) Math.abs(x);
            }
        };
    }
    @Override
    public Fun_0_1<Float, Float> scaleOperator(final int k) {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Float, Float, Float> addOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> subOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> mulOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> divOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> modOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> maxOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> minOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_1<Float, Float> negOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Float, Float> posOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) +x;
            }
        };
    }
    
    @Override
    public Float minValue() {
        return Float.MIN_VALUE;
    }

    @Override
    public Float maxValue() {
        return Float.MAX_VALUE;
    }
    
    @Override
    public Float zeroValue() {
        return 0.0F;
    }
    
    @Override
    public Float unitValue() {
        return 1.0F;
    }
}
