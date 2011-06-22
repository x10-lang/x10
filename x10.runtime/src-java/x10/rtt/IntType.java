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


public class IntType extends RuntimeType<x10.core.Int> {

    private static final long serialVersionUID = 1L;

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.INT;
    }

    public IntType() {
        super(x10.core.Int.class,
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
        return "x10.lang.Int";
    }

    // for shortcut 
    @Override
    public boolean instanceOf(Object o) {
        return o instanceof x10.core.Int;
    }
    
    @Override
    public Object makeArray(int length) {
        return new int[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        int[] arr = new int[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = x10.core.Int.$unbox(elem[i]);
        }
        return arr;
    }
    
    @Override
    public x10.core.Int getArray(Object array, int i) {
        return x10.core.Int.$box(((int[]) array)[i]);
    }
    
//    @Override
//    public x10.core.Int setArray(Object array, int i, x10.core.Int v) {
//        ((int[]) array)[i] = x10.core.Int.$unbox(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.Int v) {
        ((int[]) array)[i] = x10.core.Int.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((int[]) array).length;
    }
    
}
