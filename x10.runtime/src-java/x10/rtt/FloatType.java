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

public class FloatType extends RuntimeType<Float> implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
    private static final int _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(FloatType.class.getName());

    public FloatType() {
        super(Float.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Float";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Float;
    }

    @Override
    public Object makeArray(int length) {
        return new float[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        float[] arr = new float[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).floatValue();
        }
        return arr;
    }
    
    @Override
    public Float getArray(Object array, int i) {
        return ((float[]) array)[i];
    }
    
//    @Override
//    public Float setArray(Object array, int i, Float v) {
//        // avoid boxing again
////        return ((float[]) array)[i] = v;
//        ((float[]) array)[i] = v;
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, Float v) {
        ((float[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((float[]) array).length;
    }

    public void _serialize(X10JavaSerializer serializer) throws IOException {
    }

    public int _get_serialization_id() {
        return _serialization_id;
    }

    public static X10JavaSerializable _deserializer(X10JavaDeserializer deserializer) throws IOException {
		return _deserialize_body(null, deserializer);
	}

    public static X10JavaSerializable _deserialize_body(FloatType t, X10JavaDeserializer deserializer) throws IOException {
        return new FloatType();
    }
}
