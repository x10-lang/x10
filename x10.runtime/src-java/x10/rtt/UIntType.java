/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.rtt;


import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

import java.io.IOException;

public class UIntType extends RuntimeType<x10.core.UInt> {
//public class UIntType extends RuntimeType<x10.core.UInt> implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
//    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UIntType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.UINT;
    }

    public UIntType() {
        super(x10.core.UInt.class,
            new Type[] {
                new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
        		new ParameterizedType(x10.lang.Arithmetic.$RTT, UnresolvedType.THIS),
        		new ParameterizedType(x10.lang.Bitwise.$RTT, UnresolvedType.THIS),
        		new ParameterizedType(x10.util.Ordered.$RTT, UnresolvedType.THIS),
                Types.STRUCT
            });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.UInt";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.UInt;
    }
    
    @Override
    public int[] makeArray(int length) {
        return new int[length];
    }
    
    @Override
	public int[][] makeArray(int dim0, int dim1) {
        return new int[dim0][dim1];
	}

	@Override
	public int[][][] makeArray(int dim0, int dim1, int dim2) {
        return new int[dim0][dim1][dim2];
	}

	@Override
	public int[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        return new int[dim0][dim1][dim2][dim3];
	}

	@Override
    public int[] makeArray(Object... elem) {
        int[] arr = new int[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = x10.core.UInt.$unbox(elem[i]);
        }
        return arr;
    }
    
    @Override
    public x10.core.UInt getArray(Object array, int i) {
        return x10.core.UInt.$box(((int[]) array)[i]);
    }
    
//    @Override
//    public x10.core.UInt setArray(Object array, int i, x10.core.UInt v) {
//        // avoid boxing again
//        ((int[]) array)[i] = x10.core.UInt.$unbox(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.UInt v) {
        ((int[]) array)[i] = x10.core.UInt.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((int[]) array).length;
    }

//    public void $_serialize(X10JavaSerializer serializer) throws IOException {
//    }
//
//    public short $_get_serialization_id() {
//        return _serialization_id;
//    }
//
//    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws IOException {
//		return $_deserialize_body(null, deserializer);
//	}
//
//    public static X10JavaSerializable $_deserialize_body(UIntType t, X10JavaDeserializer deserializer) throws IOException {
//        UIntType uIntType = (UIntType) Types.UINT;
//        deserializer.record_reference(uIntType);
//        return uIntType;
//    }
    
}
