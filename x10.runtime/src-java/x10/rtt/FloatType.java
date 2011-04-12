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


public class FloatType extends RuntimeType<Float> {

	private static final long serialVersionUID = 1L;

    public FloatType() {
//        super(float.class,
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
    
    @Override
    public Float setArray(Object array, int i, Float v) {
        // avoid boxing again
//        return ((float[]) array)[i] = v;
        ((float[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((float[]) array).length;
    }
    
}
