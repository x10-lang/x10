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

import x10.serialization.X10JavaDeserializer;
import x10.serialization.X10JavaSerializable;
import x10.serialization.X10JavaSerializer;

import java.io.IOException;
import java.lang.reflect.Array;

public final class ByteType extends RuntimeType<x10.core.Byte> {
//public final class ByteType extends RuntimeType<x10.core.Byte> implements X10JavaSerializable {

    private static final long serialVersionUID = 1L;
    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.BYTE;
    }
//    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ByteType.class.getName());

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
//    public static X10JavaSerializable $_deserialize_body(ByteType t, X10JavaDeserializer deserializer) throws IOException {
//        ByteType byteType = (ByteType) Types.BYTE;
//        deserializer.record_reference(byteType);
//        return byteType;
//    }
}
