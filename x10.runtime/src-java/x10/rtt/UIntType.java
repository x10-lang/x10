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


public class UIntType extends RuntimeType<x10.core.UInt> {

	private static final long serialVersionUID = 1L;

    public UIntType() {
//        super(int.class,
        super(x10.core.UInt.class,
            new Type[] {
                new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                Types.STRUCT
            });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.UInt";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof x10.core.UInt;
    }
    
    @Override
    public Object makeArray(int length) {
        return new int[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        int[] arr = new int[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).intValue();
        }
        return arr;
    }
    
    @Override
    public x10.core.UInt getArray(Object array, int i) {
        return x10.core.UInt.make(((int[]) array)[i]);
    }
    
//    @Override
//    public x10.core.UInt setArray(Object array, int i, x10.core.UInt v) {
//        // avoid boxing again
//        ((int[]) array)[i] = x10.core.UInt.getValue(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.UInt v) {
        ((int[]) array)[i] = x10.core.UInt.getValue(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((int[]) array).length;
    }
    
}
