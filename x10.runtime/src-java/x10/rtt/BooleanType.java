/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.rtt;


import java.lang.reflect.Array;

import x10.serialization.SerializationConstants;

public final class BooleanType extends RuntimeType<x10.core.Boolean> {
    
    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_BOOLEAN_ID;
    }

    public BooleanType() {
        super(x10.core.Boolean.class,
              new Type[] {
                  ParameterizedType.make(Types.COMPARABLE, UnresolvedType.THIS),
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
    public boolean[] makeArray(int dim0) {
        return new boolean[dim0];
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
    public Object makeArray(int... dims) {
        return Array.newInstance(boolean.class, dims);
    }
    
    @Override
    public x10.core.Boolean getArray(Object array, int i) {
        return x10.core.Boolean.$box(((boolean[]) array)[i]);
    }
    
    @Override
    public void setArray(Object array, int i, x10.core.Boolean v) {
        ((boolean[]) array)[i] = x10.core.Boolean.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((boolean[]) array).length;
    }

    @Override
    public boolean isref() {
        return false;
    }
}
