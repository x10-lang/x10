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

public class BooleanType extends RuntimeType<x10.core.Boolean>{
//public class BooleanType extends RuntimeType<x10.core.Boolean> implements X10JavaSerializable{

	private static final long serialVersionUID = 1L;
//    private static final short _serialization_id = x10.x10rt.DeserializationDispatcher.addDispatcher(DeserializationDispatcher.ClosureKind.CLOSURE_KIND_NOT_ASYNC, BooleanType.class.getName());

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.BOOLEAN;
    }

    public BooleanType() {
        super(x10.core.Boolean.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Boolean";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.Boolean;
    }

    @Override
    public boolean[] makeArray(int length) {
        return new boolean[length];
    }
    
    @Override
	public boolean[][] makeArray(int dim0, int dim1) {
        return new boolean[dim0][dim1];
	}

	@Override
	public boolean[][][] makeArray(int dim0, int dim1, int dim2) {
        return new boolean[dim0][dim1][dim2];
	}

	@Override
	public boolean[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        return new boolean[dim0][dim1][dim2][dim3];
	}

	@Override
    public boolean[] makeArray(Object... elem) {
        boolean[] arr = new boolean[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = x10.core.Boolean.$unbox(elem[i]);
        }
        return arr;
    }
    
    @Override
    public x10.core.Boolean getArray(Object array, int i) {
        return x10.core.Boolean.$box(((boolean[]) array)[i]);
    }
    
//    @Override
//    public x10.core.Boolean setArray(Object array, int i, x10.core.Boolean v) {
//        ((boolean[]) array)[i] = x10.core.Boolean.$unbox(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.Boolean v) {
        ((boolean[]) array)[i] = x10.core.Boolean.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((boolean[]) array).length;
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
//    public static X10JavaSerializable $_deserialize_body(BooleanType t, X10JavaDeserializer deserializer) throws IOException {
//        BooleanType booleanType = (BooleanType) Types.BOOLEAN;
//        deserializer.record_reference(booleanType);
//        return booleanType;
//    }
}
