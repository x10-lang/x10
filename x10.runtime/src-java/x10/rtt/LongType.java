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


public class LongType extends RuntimeType<Long> {

	private static final long serialVersionUID = 1L;

    public LongType() {
        super(long.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, new UnresolvedType(-1))
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Long";
    }

    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Long;
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
    public Long getArray(Object array, int i) {
        return ((long[]) array)[i];
    }
    
    @Override
    public Long setArray(Object array, int i, Long v) {
        return ((long[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((long[]) array).length;
    }
    
}
