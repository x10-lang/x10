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


public class DoubleType extends RuntimeType<Double> {

	private static final long serialVersionUID = 1L;

    public DoubleType() {
        super(double.class,
              new Type[] {
                  new ParameterizedType(Types.COMPARABLE, UnresolvedType.THIS),
                  Types.STRUCT
              });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Double";
    }

    @Override
    public boolean instanceof$(Object o) {
        return o instanceof java.lang.Double;
    }

    @Override
    public Object makeArray(int length) {
        return new double[length];
    }
    
    @Override
    public Object makeArray(Object... elem) {
        double[] arr = new double[elem.length];
        for (int i = 0; i < elem.length; i++) {
            arr[i] = ((Number)elem[i]).doubleValue();
        }
        return arr;
    }
    
    @Override
    public Double getArray(Object array, int i) {
        return ((double[]) array)[i];
    }
    
    @Override
    public Double setArray(Object array, int i, Double v) {
        return ((double[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((double[]) array).length;
    }
    
}
