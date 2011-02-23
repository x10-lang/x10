package x10.rtt;

import x10.core.Any;


public final class ParameterizedType<T> implements Type<T>{

    private RuntimeType<T> rtt;
    private Type<?>[] params;
    
    RuntimeType<T> getRuntimeType() {
        return rtt;
    }
    
    Type<?>[] getParams() {
        return params;
    }

    public ParameterizedType(RuntimeType<T> rtt, Type<?>... params) {
        this.rtt = rtt;
        this.params = params;
    }
    
    public final boolean isSubtype(Type<?> o) {
        if (this == o) return true;
        if (o == Types.ANY) return true;
        if (o == Types.OBJECT) return !Types.isStructType(this);
        if (!o.getJavaClass().isAssignableFrom(rtt.getJavaClass())) {
            return false;
        }
        if (o instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) o;
            if (pt.getRuntimeType().isSuperType(pt.params, rtt, params)) {
                return true;
            }
        }
        else if (o instanceof RuntimeType) {
            if (((RuntimeType<?>) o).isSuperType(null, rtt, params)) {
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
            if (!rtt.getJavaClass().equals(t.getJavaClass())) {
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

    public final T setArray(Object array, int i, T v) {
        return rtt.setArray(array, i, v);
    }

    public final String toString() {
        return typeName();
    }

    // Note: this method does not resolve UnresolvedType at runtime
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

    private static final String printType(Type<?> t, Object o) {
        if (t instanceof UnresolvedType) {
            int index = ((UnresolvedType) t).getIndex();
            if (index >= 0) {
                t = ((Any) o).$getParam(index);
            } else {
                t = ((Any) o).$getRTT();
            }
        }
        
        if (t instanceof ParameterizedType) {
            return ((ParameterizedType<?>) t).typeName(o);
        } else {
            return t.typeName();
        }
    }
    
    public final String typeName(Object o) {
        String str = rtt.typeName();
        str += "[";
        for (int i = 0; i < params.length; i ++) {
            if (i != 0) str += ",";
            str += printType(params[i], o);
        }
        str += "]";
        return str;
    }

    // called from Static{Void}FunType.typeName(Object)
    public final String typeNameForFun(Object o) {
        String str = "(";
        int i;
        for (i = 0; i < params.length - 1; i++) {
            if (i != 0) str += ",";
            str += printType(params[i], o);
        }
        str += ")=>";
        str += printType(params[i], o);
        return str;
    }

    public final String typeNameForVoidFun(Object o) {
        String str = "(";
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                if (i != 0) str += ",";
                str += printType(params[i], o);
            }
        }
        str += ")=>void";
        return str;
    }
    
}
