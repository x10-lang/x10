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


public class BooleanType extends RuntimeType<Boolean> {
    public BooleanType() {
        super(boolean.class);
    }
    
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Boolean;
    }

    @Override
    public Object makeArray(int length) {
        return new boolean[length];
    }
    
    @Override
    public Boolean getArray(Object array, int i) {
        return ((boolean[]) array)[i];
    }
    
    @Override
    public Boolean setArray(Object array, int i, Boolean v) {
        return ((boolean[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((boolean[]) array).length;
    }
    
    @Override
    public Boolean zeroValue() {
    	return Boolean.FALSE;
    }
    
    @Override
    public Boolean unitValue() {
    	return Boolean.TRUE;
    }
    
    @Override
    public Fun_0_2<Boolean, Boolean, Boolean> andOperator() {
        return new Fun_0_2<Boolean, Boolean, Boolean>() {
            public Boolean apply(Boolean x, Boolean y) {
                return x & y;
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.BOOLEAN; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.BOOLEAN; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.BOOLEAN; }
        };
    }

    @Override
    public Fun_0_2<Boolean, Boolean, Boolean> orOperator() {
        return new Fun_0_2<Boolean, Boolean, Boolean>() {
            public Boolean apply(Boolean x, Boolean y) {
                return x | y;
            }
            public Type<?> rtt_x10$lang$Fun_0_2_U() { return Types.BOOLEAN; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z1() { return Types.BOOLEAN; }
            public Type<?> rtt_x10$lang$Fun_0_2_Z2() { return Types.BOOLEAN; }
        };
    }

    @Override
    public Fun_0_1<Boolean, Boolean> notOperator() {
        return new Fun_0_1<Boolean, Boolean>() {
            public Boolean apply(Boolean x) {
                return !x;
            }
            public Type<?> rtt_x10$lang$Fun_0_1_U() { return Types.BOOLEAN; }
            public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.BOOLEAN; }
        };
    }
}
