package x10.rtt;

import x10.core.Any;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;


public final class ParameterizedType<T> implements Type<T>, X10JavaSerializable{

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(ParameterizedType.class.getName());

    private final RuntimeType<T> rtt;
    private final Type<?>[] params;
    
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
        if (!o.getImpl().isAssignableFrom(rtt.getImpl())) {
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
            if (!rtt.getImpl().equals(t.getImpl())) {
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

    public final Class<?> getImpl() {
        return rtt.getImpl();
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

//    public final T setArray(Object array, int i, T v) {
//        return rtt.setArray(array, i, v);
//    }
    public final void setArray(Object array, int i, T v) {
    	rtt.setArray(array, i, v);
    }

    public final String toString() {
        return typeName();
    }

    // Note: this method does not resolve UnresolvedType at runtime
    public final String typeName() {
        return typeName(null);
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
        if (rtt instanceof FunType) {
            return typeNameForFun(o);
        } else if (rtt instanceof VoidFunType) {
            return typeNameForVoidFun(o);
        } else {
            return typeNameForOthers(o);
        }
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
    
    public final String typeNameForOthers(Object o) {
        String str = rtt.typeName();
        str += "[";
        for (int i = 0; i < params.length; i ++) {
            if (i != 0) str += ",";
            str += printType(params[i], o);
        }
        str += "]";
        return str;
    }

	public void _serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(rtt);
        serializer.write(params);
	}

	public static X10JavaSerializable _deserializer( X10JavaDeserializer deserializer) throws IOException {
        ParameterizedType pt = new ParameterizedType(null);
        deserializer.record_reference(pt);
        return _deserialize_body(pt, deserializer);
	}

    public static X10JavaSerializable _deserialize_body(ParameterizedType pt, X10JavaDeserializer deserializer) throws IOException {
		RuntimeType rt = (RuntimeType) deserializer.deSerialize();
        int length = deserializer.readInt();
        Type[] ps = new Type[length];
        deserializer.readArray(ps);
        pt = new ParameterizedType(rt, ps);
        return pt;
    }

	public int _get_serialization_id() {
		return _serialization_id;
	}
}
