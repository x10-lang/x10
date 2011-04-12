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


public class ShortType extends RuntimeType<Short> {

	private static final long serialVersionUID = 1L;

    public ShortType() {
//        super(short.class,
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
    
    @Override
    public Short setArray(Object array, int i, Short v) {
        // avoid boxing again
//        return ((short[]) array)[i] = v;
        ((short[]) array)[i] = v;
        return v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((short[]) array).length;
    }
    
}
