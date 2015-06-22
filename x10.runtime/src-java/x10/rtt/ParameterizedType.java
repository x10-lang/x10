/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.rtt;

import x10.core.Any;
import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public final class ParameterizedType<T> implements Type<T>, X10JavaSerializable {

    public RuntimeType<T> rawType;
    public Type<?>[] actualTypeArguments;
    
    RuntimeType<T> getRawType() {
        return rawType;
    }
    
    Type<?>[] getActualTypeArguments() {
        return actualTypeArguments;
    }

    private ParameterizedType(RuntimeType<T> rawType, Type<?>... actualTypeArguments) {
        this.rawType = rawType;
        this.actualTypeArguments = actualTypeArguments;
    }
    private static final boolean useCache = true;
    private static final ConcurrentHashMap<RuntimeType<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>> typeCache1 = new ConcurrentHashMap<RuntimeType<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>>();
    public static <T> ParameterizedType/*<T>*/ make(RuntimeType<T> rawType, Type<?> actualTypeArgument0) {
        if (useCache && rawType != null && actualTypeArgument0 != null
            // N.B. guard from NPE with recursive type. see XTENLANG_423.W[U], which extends Z[W[U]], causes NPE for hashCode.
            && (!(actualTypeArgument0 instanceof ParameterizedType) || ((ParameterizedType<?>) actualTypeArgument0).rawType != null)
            ) {
            ConcurrentHashMap<Type<?>, ParameterizedType<?>> typeCache10 = typeCache1.get(rawType);
            if (typeCache10 == null) {
                ConcurrentHashMap<Type<?>, ParameterizedType<?>> typeCache10_;
                typeCache10_ = new ConcurrentHashMap<Type<?>, ParameterizedType<?>>();
                typeCache10 = typeCache1.putIfAbsent(rawType, typeCache10_);
                if (typeCache10 == null) typeCache10 = typeCache10_;
            }
            ParameterizedType type = typeCache10.get(actualTypeArgument0);
            if (type == null) {
                ParameterizedType type_;
                type_ = new ParameterizedType<T>(rawType, actualTypeArgument0);
                type = typeCache10.putIfAbsent(actualTypeArgument0, type_);
                if (type == null) type = type_;
            }
            return type;
        } else {
            return new ParameterizedType<T>(rawType, actualTypeArgument0);
        }
    }
    private static final ConcurrentHashMap<RuntimeType<?>, ConcurrentHashMap<Type<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>>> typeCache2 =
        new ConcurrentHashMap<RuntimeType<?>, ConcurrentHashMap<Type<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>>>();
    public static <T> ParameterizedType/*<T>*/ make(RuntimeType<T> rawType, Type<?> actualTypeArgument0, Type<?> actualTypeArgument1) {
        if (useCache && rawType != null && actualTypeArgument0 != null && actualTypeArgument1 != null
            // N.B. guard from NPE with recursive type. see XTENLANG_423.W[U], which extends Z[W[U]], causes NPE for hashCode.
            && (!(actualTypeArgument0 instanceof ParameterizedType) || ((ParameterizedType<?>) actualTypeArgument0).rawType != null)
            && (!(actualTypeArgument1 instanceof ParameterizedType) || ((ParameterizedType<?>) actualTypeArgument1).rawType != null)
            ) {
            ConcurrentHashMap<Type<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>> typeCache20 = typeCache2.get(rawType);
            if (typeCache20 == null) {
                ConcurrentHashMap<Type<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>> typeCache20_;
                typeCache20_ = new ConcurrentHashMap<Type<?>, ConcurrentHashMap<Type<?>, ParameterizedType<?>>>();
                typeCache20 = typeCache2.putIfAbsent(rawType, typeCache20_);
                if (typeCache20 == null) typeCache20 = typeCache20_;
            }
            ConcurrentHashMap<Type<?>, ParameterizedType<?>> typeCache21 = typeCache20.get(actualTypeArgument0);
            if (typeCache21 == null) {
                ConcurrentHashMap<Type<?>, ParameterizedType<?>> typeCache21_;
                typeCache21_ = new ConcurrentHashMap<Type<?>, ParameterizedType<?>>();
                typeCache21 = typeCache20.putIfAbsent(actualTypeArgument0, typeCache21_);
                if (typeCache21 == null) typeCache21 = typeCache21_;
            }
            ParameterizedType type = typeCache21.get(actualTypeArgument1);
            if (type == null) {
                ParameterizedType type_;
                type_ = new ParameterizedType<T>(rawType, actualTypeArgument0, actualTypeArgument1);
                type = typeCache21.putIfAbsent(actualTypeArgument1, type_);
                if (type == null) type = type_;
            }
            return type;
        } else {
            return new ParameterizedType<T>(rawType, actualTypeArgument0, actualTypeArgument1);
        }
    }
    public static <T> ParameterizedType/*<T>*/ make(RuntimeType<T> rawType, Type<?>... actualTypeArguments) {
        return new ParameterizedType<T>(rawType, actualTypeArguments);
    }

    // Constructor just for allocation
    public ParameterizedType() {
    }

    public final boolean isAssignableTo(Type<?> superType) {
        if (this == superType) return true;
        if (superType == Types.ANY) return true;
        if (!superType.getJavaClass().isAssignableFrom(rawType.getJavaClass())) {
            return false;
        }
        if (superType instanceof ParameterizedType) {
            ParameterizedType<?> pt = (ParameterizedType<?>) superType;
            if (pt.getRawType().isAssignableFrom(pt.actualTypeArguments, rawType, actualTypeArguments)) {
                return true;
            }
        }
        else if (superType instanceof RuntimeType) {
            if (((RuntimeType<?>) superType).isAssignableFrom(null, rawType, actualTypeArguments)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasZero() {
        return rawType.hasZero();
    }

    public boolean isref() {
        return rawType.isref();
    }

    public final boolean isInstance(Object o) {
        return rawType.isInstance(o, actualTypeArguments);
    }
    
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ParameterizedType<?>) {
            ParameterizedType<?> t = (ParameterizedType<?>) o;
            if (!rawType.equals(t.rawType)) {
                return false;
            }
            Type<?>[] t_actualTypeArguments = t.actualTypeArguments;
            if (actualTypeArguments.length != t_actualTypeArguments.length) {
                return false;
            }
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (!actualTypeArguments[i].equals(t_actualTypeArguments[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public final int hashCode() {
        return rawType.hashCode();
    }

    public final int arrayLength(Object array) {
        return rawType.arrayLength(array);
    }

    public final T getArray(Object array, int i) {
        return rawType.getArray(array, i);
    }

    public final Class<?> getJavaClass() {
        return rawType.getJavaClass();
    }

    public final Object makeArray(int dim0) {
        return rawType.makeArray(dim0);
    }

    public final Object makeArray(int dim0, int dim1) {
    	return rawType.makeArray(dim0, dim1);
    }

    public final Object makeArray(int dim0, int dim1, int dim2) {
    	return rawType.makeArray(dim0, dim1, dim2);
    }
    
    public final Object makeArray(int dim0, int dim1, int dim2, int dim3) {
    	return rawType.makeArray(dim0, dim1, dim2, dim3);
    }
    
    public final Object makeArray(int... dims) {
        return rawType.makeArray(dims);
    }

    public final void setArray(Object array, int i, T v) {
    	rawType.setArray(array, i, v);
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
        if (rawType instanceof FunType) {
            return typeNameForFun(o);
        } else if (rawType instanceof VoidFunType) {
            return typeNameForVoidFun(o);
        } else {
            return typeNameForOthers(o);
        }
    }

    // called from Static{Void}FunType.typeName(Object)
    public final String typeNameForFun(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        int i;
        for (i = 0; i < actualTypeArguments.length - 1; i++) {
            if (i != 0) sb.append(",");
            sb.append(printType(actualTypeArguments[i], o));
        }
        sb.append(")=>");
        sb.append(printType(actualTypeArguments[i], o));
        return sb.toString();
    }

    public final String typeNameForVoidFun(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            for (int i = 0; i < actualTypeArguments.length; i++) {
                if (i != 0) sb.append(",");
                sb.append(printType(actualTypeArguments[i], o));
            }
        }
        sb.append(")=>void");
        return sb.toString();
    }
    
    public final String typeNameForOthers(Object o) {
        StringBuilder sb = new StringBuilder();
        sb.append(rawType.typeName());
        sb.append("[");
        for (int i = 0; i < actualTypeArguments.length; i ++) {
            if (i != 0) sb.append(",");
            sb.append(printType(actualTypeArguments[i], o));
        }
        sb.append("]");
        return sb.toString();
    }

    public void $_serialize(X10JavaSerializer serializer) throws IOException {
        serializer.write(rawType);
        serializer.write(actualTypeArguments.length);
        for (Type<?> at : actualTypeArguments) {
            serializer.write(at);
        }
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
        ParameterizedType pt = new ParameterizedType();
        deserializer.record_reference(pt);
        return $_deserialize_body(pt, deserializer);
    }

    public static X10JavaSerializable $_deserialize_body(ParameterizedType pt, X10JavaDeserializer deserializer) throws IOException {
        RuntimeType rawType = (RuntimeType) deserializer.readObject();
        pt.rawType = rawType;
        int length = deserializer.readInt();
        Type<?>[] actualTypeArguments = new Type<?>[length];
        for (int i = 0; i < length; i++) {
            actualTypeArguments[i] = (Type<?>) deserializer.readObject();
        }
        pt.actualTypeArguments = actualTypeArguments;
        return pt;
    }
}
