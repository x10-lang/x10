package x10.types;

import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;


public class BooleanType extends RuntimeType<Boolean> {
    public BooleanType() {
        super(boolean.class);
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
    public void setArray(Object array, int i, Boolean v) {
        ((boolean[]) array)[i] = v;
    }
    
    @Override
    public Fun_0_2<Boolean, Boolean, Boolean> andOperator() {
        return new Fun_0_2<Boolean, Boolean, Boolean>() {
            public Boolean apply(Boolean x, Boolean y) {
                return x & y;
            }
        };
    }

    @Override
    public Fun_0_2<Boolean, Boolean, Boolean> orOperator() {
        return new Fun_0_2<Boolean, Boolean, Boolean>() {
            public Boolean apply(Boolean x, Boolean y) {
                return x | y;
            }
        };
    }

    @Override
    public Fun_0_1<Boolean, Boolean> notOperator() {
        return new Fun_0_1<Boolean, Boolean>() {
            public Boolean apply(Boolean x) {
                return !x;
            }
        };
    }
}
