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


public class ULongType extends RuntimeType<x10.core.ULong> {

    private static final long serialVersionUID = 1L;

    // make sure deserialized RTT object is not duplicated
    private Object readResolve() throws java.io.ObjectStreamException {
        return Types.ULONG;
    }

    public ULongType() {
        super(x10.core.ULong.class,
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
        return "x10.lang.ULong";
    }

    // for shortcut 
    @Override
    public boolean instanceof$(Object o) {
        return o instanceof x10.core.ULong;
    }
    
    @Override
    public Object makeArray(int length) {
        return new long[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        long[] arr = new long[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).longValue();
        }
        return arr;
    }
    
    @Override
    public x10.core.ULong getArray(Object array, int i) {
        return x10.core.ULong.$box(((long[]) array)[i]);
    }
    
//    @Override
//    public x10.core.ULong setArray(Object array, int i, x10.core.ULong v) {
//        // avoid boxing again
//        ((int[]) array)[i] = x10.core.ULong.$unbox(v);
//        return v;
//    }
    @Override
    public void setArray(Object array, int i, x10.core.ULong v) {
        ((long[]) array)[i] = x10.core.ULong.$unbox(v);
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((long[]) array).length;
    }
    
}
