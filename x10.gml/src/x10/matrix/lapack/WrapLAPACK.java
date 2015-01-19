/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2015.
 */

package x10.matrix.lapack;

import x10.core.Rail;
import x10.rtt.DoubleType;
import x10.rtt.FloatType;

/**
 * JNI wrappers for corresponding functions in wrap_lapack.cc
 */
public class WrapLAPACK {
    static { 
        String lib = "jlapack";
        String ext = System.getenv("GML_ELEM_TYPE");
        if (ext != null) lib += "_" + ext;
        System.out.println("Loading " + lib);

        System.loadLibrary(lib);
    }

    static float getFloat(Object o) { return x10.core.Float.$unbox((x10.core.Float) o);}
    static double getDouble(Object o) { return x10.core.Double.$unbox((x10.core.Double) o);}

    public static native int solveLinearEquation(float[] A, float[] BX, int[] ipiv, int[] dim);
    public static native int solveLinearEquation(double[] A, double[] BX, int[] ipiv, int[] dim);

    public static <T> int solveLinearEquationET(Rail<T> A, Rail<T> BX, int[] ipiv, int[] dim) {
        if (A.$getParam(0) instanceof DoubleType) {
            return solveLinearEquation(A.getDoubleArray(), BX.getDoubleArray(), ipiv, dim);
        }
        return solveLinearEquation(A.getFloatArray(), BX.getFloatArray(), ipiv, dim);
    }

    public static native int compEigenvalues(double[] A, double[] W, double[] WORK, int[] IWORK, int[] dim);
    public static native int compEigenvalues(float[] A, float[] W, float[] WORK, int[] IWORK, int[] dim);
    public static <T> int compEigenvaluesET(Rail<T> A, Rail<T> W, Rail<T> WORK, int[] IWORK, int[] dim) {
        if (A.$getParam(0) instanceof DoubleType) {
            return compEigenvalues(A.getDoubleArray(), W.getDoubleArray(), WORK.getDoubleArray(), IWORK, dim);
        }
        return compEigenvalues(A.getFloatArray(), W.getFloatArray(), WORK.getFloatArray(), IWORK, dim);
    }

    public static native int compEigenvectors(double[] A, double[] W, double[] Z, double[] WORK, int[] IWORK, int[] IFAIL, int[] dim);
    public static native int compEigenvectors(float[] A, float[] W, float[] Z, float[] WORK, int[] IWORK, int[] IFAIL, int[] dim);
    public static <T> int compEigenvectorsET(Rail<T> A, Rail<T> W, Rail<T> Z, Rail<T> WORK, int[] IWORK, int[] IFAIL, int[] dim) {
        if (A.$getParam(0) instanceof DoubleType) {
            return compEigenvectors(A.getDoubleArray(), W.getDoubleArray(), Z.getDoubleArray(), WORK.getDoubleArray(), IWORK, IFAIL, dim);
        }
        return compEigenvectors(A.getFloatArray(), W.getFloatArray(), Z.getFloatArray(), WORK.getFloatArray(), IWORK, IFAIL, dim);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
