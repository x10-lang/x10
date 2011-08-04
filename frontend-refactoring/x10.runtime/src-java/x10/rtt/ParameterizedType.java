package x10.rtt;

import java.util.Arrays;
import java.util.List;


public final class ParameterizedType<T> implements Type<T>{

    private RuntimeType<T> rtt;
    private Type<?>[] params;
    
    RuntimeType<T> getRuntimeType() {
        return rtt;
    }
    
    Type<?>[] getParams() {
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
        if (this == o) return true;
        if (o == Types.ANY) return true;
        if (o == Types.OBJECT) return !Types.isStructType(this);
        if (!o.getJavaClass().isAssignableFrom(rtt.base)) {
            return false;
        }
        if (o instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) o;
            if (pt.getRuntimeType().isSuperType(pt.params, (RuntimeType<?>) rtt, params)) {
                return true;
            }
        }
        else if (o instanceof RuntimeType) {
            if (((RuntimeType<?>) o).isSuperType(null, (RuntimeType<?>) rtt, params)) {
                return true;
            }
        }
        return false;
    }

    public final boolean instanceof$(Object o) {
        return rtt.instanceof$(o, params);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ParameterizedType<?>) {
            ParameterizedType<?> t = (ParameterizedType<?>) o;
            if (!rtt.base.equals(t.getJavaClass())) {
                return false;
            }
            Type<?>[] parameters = t.params;
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals(parameters[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public final int arrayLength(Object array) {
        return rtt.arrayLength(array);
    }

    public final T getArray(Object array, int i) {
        return rtt.getArray(array, i);
    }

    public final Class<?> getJavaClass() {
        return rtt.getJavaClass();
    }

    public final int hashCode() {
        return rtt.hashCode();
    }

    public final Object makeArray(int length) {
        return rtt.makeArray(length);
    }

    public final Object makeArray(Object... elems) {
        return rtt.makeArray(elems);
    }

    public final Type<T> reinstantiate(List<Type<?>> parms) {
        return rtt.reinstantiate(parms);
    }

    public final T setArray(Object array, int i, T v) {
        return rtt.setArray(array, i, v);
    }

    public final String toString() {
        return rtt.toString();
    }

    public final String typeName() {
        String str = rtt.typeName();
        str += "[";
        for (int i = 0; i < params.length; i ++) {
            if (i != 0) str += ",";
            str += params[i].typeName();
        }
        str += "]";
        return str;
    }
    
}