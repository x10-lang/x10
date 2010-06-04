/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.rtt;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class FloatType extends RuntimeType<Float> {
    public FloatType() {
        super(float.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Float;
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
    public Float setArray(Object array, int i, Float v) {
        ((float[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((float[]) array).length;
    }
    
    @Override
    public Fun_0_1<Float, Float> absOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) Math.abs(x);
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_1<Float, Float> scaleOperator(final int k) {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) (x * k);
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.FLOAT; }
        };
    }
    
    @Override
    public Fun_0_2<Float, Float, Float> addOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x + y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> subOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x - y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> mulOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x * y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> divOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x / y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> modOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (float) (x % y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> maxOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (x > y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    @Override
    public Fun_0_2<Float, Float, Float> minOperator() {
        return new Fun_0_2<Float, Float, Float>() {
            public Float apply(Float x, Float y) {
                return (x < y ? x : y);
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.FLOAT; }
        };
    }
    
    @Override
    public Fun_0_1<Float, Float> negOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) -x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.FLOAT; }
        };
    }
    
    @Override
    public Fun_0_1<Float, Float> posOperator() {
        return new Fun_0_1<Float, Float>() {
            public Float apply(Float x) {
                return (float) +x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.FLOAT; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.FLOAT; }
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
