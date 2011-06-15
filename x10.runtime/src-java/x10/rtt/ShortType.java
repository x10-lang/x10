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

public class ShortType extends RuntimeType<Short>implements X10JavaSerializable {

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(ShortType.class.getName());

    public ShortType() {
        super(Short.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Short";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Short;
    }

    @Override
    public Object makeArray(int length) {
        return new short[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        short[] arr = new short[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).shortValue();
        }
        return arr;
    }

    @Override
    public Short getArray(Object array, int i) {
        return ((short[]) array)[i];
    }
    
//    @Override
//    public Short setArray(Object array, int i, Short v) {
//        // avoid boxing again
////        return ((short[]) array)[i] = v;
//        ((short[]) array)[i] = v;
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, Short v) {
        ((short[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((short[]) array).length;
    }

    public void _serialize(X10JavaSerializer serializer) throws IOException {
    }

    public int _get_serialization_id() {
        return _serialization_id;
    }

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws IOException {
		return _deserialize_body(null, deserializer);
	}

    public static X10JavaSerializable _deserialize_body(ShortType t, X10JavaDeserializer deserializer) throws IOException {
        ShortType shortType = new ShortType();
        deserializer.record_reference(shortType);
        return shortType;
    }
    
}
