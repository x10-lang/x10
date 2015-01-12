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

import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;


public final class UnresolvedType implements Type, X10JavaSerializable {

    public static final UnresolvedType THIS = new UnresolvedType(-1);
    private static final UnresolvedType[] params = {
        new UnresolvedType(0),
        new UnresolvedType(1),
        new UnresolvedType(2),
        new UnresolvedType(3),
        new UnresolvedType(4),
        new UnresolvedType(5),
        new UnresolvedType(6),
        new UnresolvedType(7),
        new UnresolvedType(8),
        new UnresolvedType(9),
    };

    private final int index;
    
    public static UnresolvedType PARAM(int index) {
        assert index >= 0;
        if (index < params.length) {
            return params[index];
        }
        return new UnresolvedType(index);
    }
    
    private UnresolvedType(int index) {
        this.index = index;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UnresolvedType) {
            UnresolvedType ut = (UnresolvedType) o;
            if (index != ut.index) {
                return false;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return index;
    }
    
    @Override
    public String toString() {
        return "UnresolvedType(" + index + ")";
    }
    
    public final int getIndex() {
    	return index;
    }
    
    public final int arrayLength(Object array) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final Object getArray(Object array, int i) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final Class<?> getJavaClass() {
        throw new java.lang.UnsupportedOperationException();
    }

    public boolean hasZero() {
        throw new java.lang.UnsupportedOperationException();
    }

    public boolean isref() {
        throw new java.lang.UnsupportedOperationException();
    }

    public final boolean isInstance(Object o) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final boolean isAssignableTo(Type superType) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final Object makeArray(int dim0) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final Object makeArray(int dim0, int dim1) {
    	throw new java.lang.UnsupportedOperationException();
    }
    
    public final Object makeArray(int dim0, int dim1, int dim2) {
    	throw new java.lang.UnsupportedOperationException();
    }
    
    public final Object makeArray(int dim0, int dim1, int dim2, int dim3) {
    	throw new java.lang.UnsupportedOperationException();
    }
    
    public final Object makeArray(int... dims) {
        throw new java.lang.UnsupportedOperationException();
    }
    
    public final void setArray(Object array, int i, Object v) {
        throw new java.lang.UnsupportedOperationException();
    }

    public final String typeName() {
        return toString();
    }

    public void $_serialize(X10JavaSerializer $serializer) {
        throw new java.lang.UnsupportedOperationException();
    }

    public static X10JavaSerializable $_deserializer(X10JavaDeserializer $deserializer) {
        throw new java.lang.UnsupportedOperationException();
    }

    public short $_get_serialization_id() {
        throw new java.lang.UnsupportedOperationException();
    }

    public static X10JavaSerializable $_deserialize_body(UnresolvedType $_obj, X10JavaDeserializer $deserializer) {
        throw new java.lang.UnsupportedOperationException();
    }

}
