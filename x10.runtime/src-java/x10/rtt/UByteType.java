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


import java.lang.reflect.Array;

import x10.x10rt.X10JavaDeserializer;
import x10.x10rt.X10JavaSerializable;
import x10.x10rt.X10JavaSerializer;

public final class UByteType extends RuntimeType<x10.core.UByte> {
//public final class UByteType extends RuntimeType<x10.core.UByte> implements X10JavaSerializable {

    private static final long serialVersionUID = 1L;
//    private static short _serialization_id;x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, UByteType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.UBYTE;
    }

    public UByteType() {
        super(x10.core.UByte.class,
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
        return "x10.lang.UByte";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.UByte;
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
    public x10.core.UByte getArray(Object array, int i) {
        return x10.core.UByte.$box(((byte[]) array)[i]);
    }
    
    @Override
    public void setArray(Object array, int i, x10.core.UByte v) {
        ((byte[]) array)[i] = x10.core.UByte.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((byte[]) array).length;
    }

//    public void $_serialize(X10JavaSerializer serializer) throws java.io.IOException {
//    }
//
//    public short $_get_serialization_id() {
//        return _serialization_id;
//    }
//
//    public static X10JavaSerializable $_deserializer(X10JavaDeserializer deserializer) throws java.io.IOException {
//		return $_deserialize_body(null, deserializer);
//	}
//
//    public static X10JavaSerializable $_deserialize_body(UByteType t, X10JavaDeserializer deserializer) throws java.io.IOException {
//        UByteType uByteType = (UByteType) Types.UBYTE;
//        deserializer.record_reference(uByteType);
//        return uByteType;
//    }
    
}
