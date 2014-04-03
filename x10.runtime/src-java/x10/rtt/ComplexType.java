/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2014.
 */

package x10.rtt;

import java.lang.reflect.Array;
import java.util.Arrays;

import x10.lang.Complex;
import x10.serialization.SerializationConstants;

public final class ComplexType extends RuntimeType<x10.lang.Complex> {

    @Override
    public short $_get_serialization_id() {
        return SerializationConstants.RTT_COMPLEX_ID;
    }

    public ComplexType() {
        super(Complex.class,
            new Type[] {
                ParameterizedType.make(x10.lang.Arithmetic.$RTT, UnresolvedType.THIS),
                Types.STRUCT
            });
    }
    
    @Override
    public String typeName() {
        return "x10.lang.Complex";
    }

    // for shortcut
    @Override
    public boolean isInstance(Object o) {
        return o instanceof Complex;
    }
    
    @Override
    public Complex[] makeArray(int dim0) {
        Complex[] arr = new Complex[dim0];
        Arrays.fill(arr, new Complex((java.lang.System) null));
        return arr;
    }
    
    @Override
    public Complex[][] makeArray(int dim0, int dim1) {
        Complex[][] arr = new Complex[dim0][dim1];
        for(Complex[] a0 : arr) {
            Arrays.fill(a0, new Complex((java.lang.System) null));
        }
        return arr;
    }
    
    @Override
    public Complex[][][] makeArray(int dim0, int dim1, int dim2) {
        Complex[][][] arr = new Complex[dim0][dim1][dim2];
        for(Complex[][] a0 : arr) {
            for(Complex[] a1 : a0) {
                Arrays.fill(a1, new Complex((java.lang.System) null));
            }
        }
        return arr;
    }
    
    @Override
    public Complex[][][][] makeArray(int dim0, int dim1, int dim2, int dim3) {
        Complex[][][][] arr = new Complex[dim0][dim1][dim2][dim3];
        for(Complex[][][] a0 : arr) {
            for(Complex[][] a1 : a0) {
                for(Complex[] a2 : a1) {
                    Arrays.fill(a2, new Complex((java.lang.System) null));
                }
            }
        }
        return arr;
    }
    
    @Override
    public Object makeArray(int... dims) {
        Object arr = Array.newInstance(Complex.class, dims);
        // TODO fill with Complex XTENLANG-3383
        return arr;
    }

    @Override
    public Complex getArray(Object array, int i) {
        return ((Complex[]) array)[i];
    }
    
    @Override
    public void setArray(Object array, int i, Complex v) {
        ((Complex[]) array)[i] = v;
    }
    
    @Override
    public int arrayLength(Object array) {
    	return ((long[]) array).length;
    }

    @Override
    public boolean isref() {
        return false;
    }
}
