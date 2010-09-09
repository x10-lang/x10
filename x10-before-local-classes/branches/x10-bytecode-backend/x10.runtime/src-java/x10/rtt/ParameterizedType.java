package x10.rtt;

import java.util.Arrays;
import java.util.List;

import x10.constraint.XConstraint;
import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;

public final class ParameterizedType<T> implements Type<T>{

    private RuntimeType<T> rtt;
    private Type<?>[] params;
    
    RuntimeType<T> getRuntimeType() {
        return rtt;
    }
    
    Type[] getParams() {
        return params;
    }

    public final List<Type<?>> getTypeParameters() {
        return Arrays.asList(params);
    }

    public ParameterizedType(RuntimeType<T> rtt, Type<?>... params) {
        this.rtt = rtt;
        this.params = params;
    }
    
    public final boolean isSubtype(Type<?> o) {
        if (!o.getJavaClass().isAssignableFrom(rtt.base)) {
            return false;
        }
        if (rtt.variances == null) {return true;} 
        if (o instanceof ParameterizedType) {
            Type<?>[] parameters = ((ParameterizedType) o).params;
            for (int i = 0; i < rtt.variances.length ; i ++) {
                switch (rtt.variances[i]) {
                case INVARIANT:
                    if (!params[i].equals(parameters[i])) {
                        return false;
                    }
                    break;
                case COVARIANT:
                    if (!params[i].isSubtype(parameters[i])) {
                        return false;
                    }
                    break;
                case CONTRAVARIANT: 
                    if (!parameters[i].isSubtype(params[i])) {
                        return false;
                    }
                    break;
                }
            }
            return true;
        }
        return false;
    }

    public final boolean instanceof$(Object o) {
        return rtt.instanceof$(o, params);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ParameterizedType) {
            ParameterizedType t = (ParameterizedType) o;
            if (!rtt.base.equals(t.getJavaClass())) {
                return false;
            }
            Type[] parameters = t.params;
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals(parameters[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final Fun_0_1<T, T> absOperator() {
        return rtt.absOperator();
    }

    public final Fun_0_2<T, T, T> addOperator() {
        return rtt.addOperator();
    }

    public final Fun_0_2<T, T, T> andOperator() {
        return rtt.andOperator();
    }

    public final int arrayLength(Object array) {
        return rtt.arrayLength(array);
    }

    public final Fun_0_2<T, T, T> divOperator() {
        return rtt.divOperator();
    }

    public final T getArray(Object array, int i) {
        return rtt.getArray(array, i);
    }

    public final XConstraint getConstraint() {
        return rtt.getConstraint();
    }

    public final Class<?> getJavaClass() {
        return rtt.getJavaClass();
    }

    public final int hashCode() {
        return rtt.hashCode();
    }

    public final Fun_0_1<T, T> invOperator() {
        return rtt.invOperator();
    }

    public final Object makeArray(int length) {
        return rtt.makeArray(length);
    }

    public final Fun_0_2<T, T, T> maxOperator() {
        return rtt.maxOperator();
    }

    public final T maxValue() {
        return rtt.maxValue();
    }

    public final Fun_0_2<T, T, T> minOperator() {
        return rtt.minOperator();
    }

    public final T minValue() {
        return rtt.minValue();
    }

    public final Fun_0_2<T, T, T> modOperator() {
        return rtt.modOperator();
    }

    public final Fun_0_2<T, T, T> mulOperator() {
        return rtt.mulOperator();
    }

    public final Fun_0_1<T, T> negOperator() {
        return rtt.negOperator();
    }

    public final Fun_0_1<T, T> notOperator() {
        return rtt.notOperator();
    }

    public final Fun_0_2<T, T, T> orOperator() {
        return rtt.orOperator();
    }

    public final Fun_0_1<T, T> posOperator() {
        return rtt.posOperator();
    }

    public final Type<T> reinstantiate(List<Type<?>> parms) {
        return rtt.reinstantiate(parms);
    }

    public final Fun_0_1<T, T> scaleOperator(int k) {
        return rtt.scaleOperator(k);
    }

    public final T setArray(Object array, int i, T v) {
        return rtt.setArray(array, i, v);
    }

    public final Fun_0_2<T, T, T> subOperator() {
        return rtt.subOperator();
    }

    public final String toString() {
        return rtt.toString();
    }

    public final T unitValue() {
        return rtt.unitValue();
    }

    public final Fun_0_2<T, T, T> xorOperator() {
        return rtt.xorOperator();
    }

    public final T zeroValue() {
        return rtt.zeroValue();
    }
    
    public final String typeName() {
        String str = rtt.base.toString().substring(6);
        str += "[";
        for (int i = 0; i < params.length; i ++) {
            if (i != 0) str += ",";
            str += params[i].typeName();
        }
        str += "]";
        return str;
    }
}
