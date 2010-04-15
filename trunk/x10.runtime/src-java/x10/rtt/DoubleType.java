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


public class DoubleType extends RuntimeType<Double> {
    public DoubleType() {
        super(double.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Double;
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
    public Double setArray(Object array, int i, Double v) {
        ((double[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((double[]) array).length;
    }
    
    @Override
    public Fun_0_1<Double, Double> absOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) Math.abs(x);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_1<Double, Double> scaleOperator(final int k) {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) (x * k);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.DOUBLE; return null;}
        };
    }
    
    @Override
    public Fun_0_2<Double, Double, Double> addOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x + y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> subOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x - y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> mulOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x * y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> divOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x / y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> modOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (double) (x % y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}

        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> maxOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (x > y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    @Override
    public Fun_0_2<Double, Double, Double> minOperator() {
        return new Fun_0_2<Double, Double, Double>() {
            public Double apply(Double x, Double y) {
                return (x < y ? x : y);
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.DOUBLE; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Double, Double> negOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) -x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.DOUBLE; return null;}
        };
    }
    
    @Override
    public Fun_0_1<Double, Double> posOperator() {
        return new Fun_0_1<Double, Double>() {
            public Double apply(Double x) {
                return (double) +x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.DOUBLE; return null;}
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
