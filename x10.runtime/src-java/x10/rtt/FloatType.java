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

public final class FloatType extends RuntimeType<x10.core.Float> {

    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_FLOAT_ID;
    }


    public FloatType() {
        super(x10.core.Float.class,
              new Type[] {
                  ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS),
                  ParameterizedType.make(x10.lang.Arithmetic.$RTT, UnresolvedType.THIS),
                  ParameterizedType.make(x10.util.Ordered.$RTT, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Float";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof x10.core.Float;
    }

    @Override
    public float[] makeArray(int dim0) {
        return new float[dim0];
    }
    
    @Override
    public float[][] makeArray(int dim0, int dim1) {
        return new float[dim0][dim1];
    }
    
    @Override
    public float[][][] makeArray(int dim0, int dim1, int dim2) {
        return new float[dim0][dim1][dim2];
    }
    
    @Override
    public float[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        return new float[dim0][dim1][dim2][dim3];
    }
    
    @Override
    public Object makeArray(int... dims) {
        return Array.newInstance(float.class, dims);
    }
    
    @Override
    public x10.core.Float getArray(Object array, int i) {
        return x10.core.Float.$box(((float[]) array)[i]);
    }
    
    @Override
    public void setArray(Object array, int i, x10.core.Float v) {
        ((float[]) array)[i] = x10.core.Float.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((float[]) array).length;
    }

    @Override
    public boolean isref() {
        return false;
    }
}
