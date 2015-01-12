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

import java.lang.reflect.Array;

import x10.serialization.SerializationConstants;

public final class ByteType extends RuntimeType<x10.core.Byte> {

    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_BYTE_ID;
    }

    public ByteType() {
        super(x10.core.Byte.class,
            new Type[] {
                ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS),
                ParameterizedType.make(x10.lang.Arithmetic.$RTT, UnresolvedType.THIS),
                ParameterizedType.make(x10.lang.Bitwise.$RTT, UnresolvedType.THIS),
                ParameterizedType.make(x10.util.Ordered.$RTT, UnresolvedType.THIS),
                Types.STRUCT
            });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Byte";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.Byte;
    }
    
    @Override
    public byte[] makeArray(int dim0) {
        return new byte[dim0];
    }
    
    @Override
    public byte[][] makeArray(int dim0, int dim1) {
        return new byte[dim0][dim1];
    }
    
    @Override
    public byte[][][] makeArray(int dim0, int dim1, int dim2) {
        return new byte[dim0][dim1][dim2];
    }
    
    @Override
    public byte[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        return new byte[dim0][dim1][dim2][dim3];
    }
    
    @Override
    public Object makeArray(int... dims) {
        return Array.newInstance(byte.class, dims);
    }
    
    @Override
    public x10.core.Byte getArray(Object array, int i) {
        return x10.core.Byte.$box(((byte[]) array)[i]);
    }
    
    @Override
    public void setArray(Object array, int i, x10.core.Byte v) {
        ((byte[]) array)[i] = x10.core.Byte.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((byte[]) array).length;
    }

    @Override
    public boolean isref() {
        return false;
    }
}
