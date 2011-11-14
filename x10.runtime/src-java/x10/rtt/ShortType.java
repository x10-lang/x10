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
import java.lang.reflect.Array;

public final class ShortType extends RuntimeType<x10.core.Short> {
//public final class ShortType extends RuntimeType<x10.core.Short> implements X10JavaSerializable {

    private static final long serialVersionUID = 1L;
//    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, ShortType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.SHORT;
    }

    public ShortType() {
        super(x10.core.Short.class,
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
        return "x10.lang.Short";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.Short;
    }
    
    @Override
    public short[] makeArray(int dim0) {
        return new short[dim0];
    }
    
    @Override
    public short[][] makeArray(int dim0, int dim1) {
        return new short[dim0][dim1];
    }
    
    @Override
    public short[][][] makeArray(int dim0, int dim1, int dim2) {
        return new short[dim0][dim1][dim2];
    }
    
    @Override
    public short[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        return new short[dim0][dim1][dim2][dim3];
    }
    
    @Override
    public Object makeArray(int... dims) {
        return Array.newInstance(short.class, dims);
    }
    
    @Override
    public x10.core.Short getArray(Object array, int i) {
        return x10.core.Short.$box(((short[]) array)[i]);
    }
    
    @Override
    public void setArray(Object array, int i, x10.core.Short v) {
        ((short[]) array)[i] = x10.core.Short.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((short[]) array).length;
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
//    public static X10JavaSerializable $_deserialize_body(ShortType t, X10JavaDeserializer deserializer) throws IOException {
//        ShortType shortType = (ShortType) Types.SHORT;
//        deserializer.record_reference(shortType);
//        return shortType;
//    }
    
}
