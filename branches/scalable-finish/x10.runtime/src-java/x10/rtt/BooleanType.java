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
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.BOOLEAN; return null;}
        };
    }

    @Override
    public Fun_0_2<Boolean, Boolean, Boolean> orOperator() {
        return new Fun_0_2<Boolean, Boolean, Boolean>() {
            public Boolean apply(Boolean x, Boolean y) {
                return x | y;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1 || i == 2) return Types.BOOLEAN; return null;}
        };
    }

    @Override
    public Fun_0_1<Boolean, Boolean> notOperator() {
        return new Fun_0_1<Boolean, Boolean>() {
            public Boolean apply(Boolean x) {
                return !x;
            }
            public RuntimeType<?> getRTT() { return _RTT; }
            public Type<?> getParam(int i) {if (i == 0 || i == 1) return Types.BOOLEAN; return null;}
        };
    }
}
