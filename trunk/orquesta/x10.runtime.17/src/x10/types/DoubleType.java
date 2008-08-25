package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class DoubleType extends RuntimeType<Double> {
    public DoubleType() {
        super(double.class);
    }
    
    @Override
    public Object makeArray(int length) {
        return new double[length];
    }
    
    @Override
    public Double getArray(Object array, int i) {
        return ((double[]) array)[i];
    }
    
    @Override
    public void setArray(Object array, int i, Double v) {
        ((double[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_1<Double, Double> absOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) Math.abs(x);
            }
        };
    }
    @Override
    public Fun_0_1<Double, Double> scaleOperator(final int k) {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) (x * k);
            }
        };
    }
    
    @Override
    public Fun_0_2<Double, Double, Double> addOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x + y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> subOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x - y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> mulOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x * y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> divOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x / y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> modOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x % y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> maxOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (x > y ? x : y);
            }
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> minOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (x < y ? x : y);
            }
        };
    }
    
    @Override
    public Fun_0_1<Double, Double> negOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) -x;
            }
        };
    }
    
    @Override
    public Fun_0_1<Double, Double> posOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) +x;
            }
        };
    }
    
    @Override
    public Double minValue() {
        return Double.MIN_VALUE;
    }

    @Override
    public Double maxValue() {
        return Double.MAX_VALUE;
    }
    
    @Override
    public Double zeroValue() {
        return 0.0;
    }
    
    @Override
    public Double unitValue() {
        return 1.0;
    }
}
