/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.rtt;

import x10.core.Any;
import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;


public final class ParameterizedType<T> implements Type<T>, X10JavaSerializable{

	private static final long serialVersionUID = 1L;
    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(x10.x10rt.DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ParameterizedType.class);

    public RuntimeType<T> rtt;
    public Type<?>[] params;
    
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

    // Constructor just for allocation
    public ParameterizedType() {
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

    public final boolean instanceOf(Object o) {
        return rtt.instanceOf(o, params);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ParameterizedType<?>) {
            ParameterizedType<?> t = (ParameterizedType<?>) o;
            if (!rtt.equals(t.rtt)) {
                return false;
            }
            Type<?>[] parameters = t.params;
            if (params.length != parameters.length) {
                return false;
            }
            for (int i = 0; i < params.length; i++) {
                if (!params[i].equals(parameters[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return rtt.hashCode();
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

    public final Object makeArray(int dim0) {
        return rtt.makeArray(dim0);
    }

    public final Object makeArray(int dim0, int dim1) {
    	return rtt.makeArray(dim0, dim1);
    }

    public final Object makeArray(int dim0, int dim1, int dim2) {
    	return rtt.makeArray(dim0, dim1, dim2);
    }
    
    public final Object makeArray(int dim0, int dim1, int dim2, int dim3) {
    	return rtt.makeArray(dim0, dim1, dim2, dim3);
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

    @Override
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

	public void $_serialize(X10JavaSerializer serializer) throws IOException {
		serializer.write(rtt);
        serializer.write(params);
	}

	public static X10JavaSerializable $_deserializer( X10JavaDeserializer deserializer) throws IOException {
        ParameterizedType pt = new ParameterizedType();
        deserializer.record_reference(pt);
        return $_deserialize_body(pt, deserializer);
	}

    public static X10JavaSerializable $_deserialize_body(ParameterizedType pt, X10JavaDeserializer deserializer) throws IOException {
		RuntimeType rt = (RuntimeType) deserializer.readRef();
        pt.rtt = rt;
        int length = deserializer.readInt();
        Type[] ps = new Type[length];
        deserializer.readArray(ps);
        pt.params = ps;
        return pt;
    }

	public short $_get_serialization_id() {
		return _serialization_id;
	}
}
