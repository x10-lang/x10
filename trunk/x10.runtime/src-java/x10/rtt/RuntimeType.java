/**
 * 
 */
/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.rtt;

import java.util.List;

import x10.core.Ref;
import x10.core.fun.Fun_0_1;
import x10.core.fun.Fun_0_2;
import x10.constraint.XConstraint;

public class RuntimeType<T> implements Type<T> {
    Class<?> c;

    public RuntimeType(Class<?> c) {
        this.c = c;
    }
    
    public String toString() {
    	return c.getName();
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof RuntimeType) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (c.equals(rt.c)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isSubtype(Type<?> o) {
        if (this == o) return true;
        if (o instanceof RuntimeType) {
            RuntimeType<?> rt = (RuntimeType<?>) o;
            if (rt.c.isAssignableFrom(c)) {
                return true;
            }
        }
        return false;
    }
    
    public XConstraint getConstraint() {
        return null;
    }
    
    public List<Type<?>> getTypeParameters() {
        return null;
    }

    public Type<T> reinstantiate(List<Type<?>> parms) {
        return this;
    }

    public boolean instanceof$(Object o) {
        return c.isInstance(o);
    }
    
    public T minValue() {
        throw new UnsupportedOperationException();
    }
    
    public T maxValue() {
        throw new UnsupportedOperationException();
    }

    public T zeroValue() {
        // null for ref types, otherwise complain
       /* if (!x10.runtime.impl.java.Configuration.NULL_DEFAULT_VALUE 
              && Value.class.isAssignableFrom(c)) {
            throw new UnsupportedOperationException();
        }*/
        return null;
    }
    
    public T unitValue() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> absOperator() {
        throw new UnsupportedOperationException();
    }
    
    public Fun_0_1<T, T> scaleOperator(int k) {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> addOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> andOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> divOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> maxOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> minOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> modOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> mulOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> negOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> notOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> orOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> subOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_2<T, T, T> xorOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> invOperator() {
        throw new UnsupportedOperationException();
    }

    public Fun_0_1<T, T> posOperator() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getJavaClass() {
        return c;
    }
    
    public Object makeArray(int length) {
        return new Object[length];
    }

    public T getArray(Object array, int i) {
        return (T) ((Object[]) array)[i];
    }

    public T setArray(Object array, int i, T v) {
        ((Object[]) array)[i] = v;
        return v;
    }
    
    public int arrayLength(Object array) {
		return ((Object[]) array).length;
    }
}
