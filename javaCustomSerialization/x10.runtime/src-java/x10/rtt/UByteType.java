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

public class UByteType extends RuntimeType<x10.core.UByte> implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(UByteType.class.getName());

    public UByteType() {
        super(x10.core.UByte.class,
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
        return "x10.lang.UByte";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof x10.core.UByte;
    }
    
    @Override
    public Object makeArray(int length) {
        return new byte[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        byte[] arr = new byte[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).byteValue();
        }
        return arr;
    }
    
    @Override
    public x10.core.UByte getArray(Object array, int i) {
        return x10.core.UByte.$box(((byte[]) array)[i]);
    }
    
//    @Override
//    public x10.core.UByte setArray(Object array, int i, x10.core.UByte v) {
//        // avoid boxing again
//        ((int[]) array)[i] = x10.core.UByte.$unbox(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.UByte v) {
        ((byte[]) array)[i] = x10.core.UByte.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((byte[]) array).length;
    }

    public void _serialize(X10JavaSerializer serializer) throws java.io.IOException {
    }

    public int _get_serialization_id() {
        return _serialization_id;
    }

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws java.io.IOException {
		return _deserialize_body(null, deserializer);
	}

    public static X10JavaSerializable _deserialize_body(UByteType t, X10JavaDeserializer deserializer) throws java.io.IOException {
        UByteType uByteType = new UByteType();
        deserializer.record_reference(uByteType);
        return uByteType;
    }
    
}
