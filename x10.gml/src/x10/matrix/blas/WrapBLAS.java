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

package x10.matrix.blas;

/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

import x10.core.Rail;
import x10.rtt.Type;
import x10.rtt.DoubleType;
import x10.rtt.FloatType;

/**
 * This Java class provides JNI wrappers for BLAS library routines.
 * vj: Updated to deal with ElemType, i.e. to deal with both float and
   double matrices and vectors. 

   <p> The challenge we face is to make a single WrapBLAS.java class
   work when ElemType is declared as float or as double. We were able
   to make this work at the C/C++ level by using a typedef and
   conditionally including the _float_ or the _double_ variant of the
   code. We make this work in X10 by having application code use the
   type ElemType; this gets typedef'd to Float or Double. 

   <p> Ultimately native BLAS methods must be called, in
   single-precision or double-precision, depending on how the library
   is built. Thus we have to make the X10 compiler, the Java compiler
   and the C/C++ compiler all understand that the same piece of code
   must work for Float or for Double. We use different techniques in
   the three different languages. 
  
   <p> In Java we also have to use generic methods. We pass into the *ET methods arguments
   of type Rail<T> (where in X10 we would see Rail[ElemType]) and T
   (where in X10 we would see ElemType), where T is a type parameter
   for the method. At the X10 level, we pass the actual argument type
   (ElemType) for T. If the Java method is supposed to
   return ElemType, we make it return T. We use the trick of boxing the result
   (double or float), and casting to T (i.e. to java.lang.Object) to
   get the same code body return a value that can be either a double
   or a float at runtime (depending on how the library was compiled). 
   Java knows how to automatically unbox a value (e.g. Double --> double,
   and Float --> float), so we do not have to write unboxing code explicitly. 

   <p> Note that the jblas library will define only the ElemType
   version of the native methods. E.g. if GML is being built with
   ElemType=Float, then the jblas library will only define the float
   versions of the native methods (eg scale). We dynamically check
   whether the rail being passed into the *ET methods is a double or
   float rail, and call the corresponding native method. 

 */
public class WrapBLAS {

    static { 
        String lib = "jblas";
        String ext = System.getenv("GML_ELEM_TYPE");
        if (ext != null) lib += "_" + ext;
        System.out.println("Loading " + lib);

        System.loadLibrary(lib);
    }

    static float getFloat(Object o) { return x10.core.Float.$unbox((x10.core.Float) o);}
    static double getDouble(Object o) { return x10.core.Double.$unbox((x10.core.Double) o);}

    static native void scale(long n, double alpha, double[] x);
    static native void scale(long n, float alpha, float[] x);
    public static <T> void scaleET(long n, T alpha, Rail<T> x) {
	if (x.$getParam(0) instanceof DoubleType) {
	    scale(n, getDouble(alpha), x.getDoubleArray());
	    return;
	}
	scale(n, getFloat(alpha), x.getFloatArray());
    }

    static native void copy(long n, double[] x, double[] y);
    static native void copy(long n, float[] x, float[] y);
    public static <T> void copyET(long n, Rail<T> x, Rail<T> y) {
	if (x.$getParam(0) instanceof DoubleType) {
	    copy(n, x.getDoubleArray(), y.getDoubleArray());
	    return;
	}
	copy(n, x.getFloatArray(), y.getFloatArray());
    }


    // FOr methods returning a value of type ElemType, the trick is to 
    // have the method return a value of type T. The native methods will
    // return a double or float, this needs to be boxed so it can be cast
    // to T (i.e. Object). The returned value will typically be stored in
    // a variable with the unboxed type, and Java will automatically do the
    // unboxing. 
           static native double dotProd(long n, double[] x, double[] y);
           static native float  dotProd(long n,  float[] x,  float[] y);
    public static <T> T dotProdET(long n, Rail<T> x, Rail<T> y) {
	if (x.$getParam(0) instanceof DoubleType) {
	    return (T) x10.core.Double.$box(dotProd(n, x.getDoubleArray(), y.getDoubleArray()));
	}
	return (T) x10.core.Float.$box(dotProd(n, x.getFloatArray(), y.getFloatArray()));
    }


     static native double norm2(long n, double[] x);
     static native  float norm2(long n, float[] x);
    public static <T> T  norm2ET(long n, Rail<T> x) {
	if (x.$getParam(0) instanceof DoubleType) {
	    return (T) x10.core.Double.$box(norm2(n, x.getDoubleArray()));
	}
	return (T) x10.core.Float.$box(norm2(n, x.getFloatArray()));
    }


           static native double absSum(long n, double[] x);
           static native  float absSum(long n, float[] x);
    public static <T> T  absSumET(long n, Rail<T> x) {
	if (x.$getParam(0) instanceof DoubleType) {
	    return (T) x10.core.Double.$box(absSum(n, x.getDoubleArray()));
	}
	return (T) x10.core.Float.$box(absSum(n, x.getFloatArray()));
    }

           static native void matmatMultOff(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim, long[] ld, long[] off, int[] trans);
           static native void matmatMultOff( float alpha,  float[] A,  float[] B,  float beta,  float[] C, long[] dim, long[] ld, long[] off, int[] trans);
    public static <T> void matmatMultOffET(T alpha,  Rail<T> A,  Rail<T> B,  T beta,  Rail<T> C, long[] dim, long[] ld, long[] off, int[] trans) {
	if (A.$getParam(0) instanceof DoubleType) {
	    matmatMultOff(getDouble(alpha), A.getDoubleArray(), B.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim, ld, off, trans); 
	    return;
	}
	matmatMultOff(getFloat(alpha), A.getFloatArray(), B.getFloatArray(), getFloat(beta), C.getFloatArray(), dim, ld, off, trans); 
    }


           static native void matmatMult(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim, long[] ld, int[] trans);
           static native void matmatMult( float alpha,  float[] A,  float[] B,  float beta,  float[] C, long[] dim, long[] ld, int[] trans);
    public static <T> void matmatMultET(      T alpha,  Rail<T> A,  Rail<T> B,      T beta,  Rail<T> C, long[] dim, long[] ld, int[] trans) {
	if (A.$getParam(0) instanceof DoubleType) {
	    matmatMult(getDouble(alpha), A.getDoubleArray(), B.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim, ld, trans); 
	    return;
	}
	matmatMult(getFloat(alpha), A.getFloatArray(), B.getFloatArray(), getFloat(beta), C.getFloatArray(), dim, ld, trans); 
    }

            static native void symRankKUpdateOff(double alpha, double[] A, double beta, double[] C, long[] dim, long[] ld, long[] off, boolean upper, boolean trans);
            static native void symRankKUpdateOff( float alpha,  float[] A, float beta,   float[] C, long[] dim, long[] ld, long[] off, boolean upper, boolean trans);
    public static <T> void symRankKUpdateOffET(       T alpha,  Rail<T> A,     T beta,   Rail<T> C, long[] dim, long[] ld, long[] off, boolean upper, boolean trans) {
	if (A.$getParam(0) instanceof DoubleType) {
	    symRankKUpdateOff(getDouble(alpha), A.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim, ld, off, upper, trans); 
	    return;
	}
	symRankKUpdateOff(getFloat(alpha), A.getFloatArray(), getFloat(beta), C.getFloatArray(), dim, ld, off, upper, trans); 

    }

           static native void symRankKUpdate(double alpha, double[] A, double beta, double[] C, long[] dim, boolean upper, boolean trans);
           static native void symRankKUpdate( float alpha,  float[] A,  float beta,  float[] C, long[] dim, boolean upper, boolean trans);
    public static <T> void symRankKUpdateET(      T alpha,  Rail<T> A,     T beta,   Rail<T> C, long[] dim, boolean upper, boolean trans) {
	if (A.$getParam(0) instanceof DoubleType) {
	    symRankKUpdate(getDouble(alpha), A.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim, upper, trans); 
	    return;
	}
	symRankKUpdate(getFloat(alpha), A.getFloatArray(), getFloat(beta), C.getFloatArray(), dim, upper, trans); 

    }

           static native void symmatMult(double alpha, double[] A, double[] B, double beta, double[] C, long[] dim);
           static native void symmatMult( float alpha,  float[] A,  float[] B,  float beta,  float[] C, long[] dim);
    public static <T> void symmatMultET(      T alpha,  Rail<T> A,  Rail<T> B,      T beta,  Rail<T> C, long[] dim) {
	if (A.$getParam(0) instanceof DoubleType) {
	    symmatMult(getDouble(alpha), A.getDoubleArray(), B.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim); 
	    return;
	}
	symmatMult(getFloat(alpha), A.getFloatArray(), B.getFloatArray(), getFloat(beta), C.getFloatArray(), dim); 
    }

           static native void matsymMult(double[] B, double alpha, double[] A, double beta, double[] C, long[] dim);
           static native void matsymMult( float[] B,  float alpha,  float[] A,  float beta,  float[] C, long[] dim);
    public static <T> void matsymMultET(  Rail<T> B,      T alpha,  Rail<T> A,     T beta,   Rail<T> C, long[] dim) {
	if (A.$getParam(0) instanceof DoubleType) {
	    matsymMult(B.getDoubleArray(), getDouble(alpha), A.getDoubleArray(), getDouble(beta), C.getDoubleArray(), dim); 
	    return;
	}
	matsymMult(B.getFloatArray(), getFloat(alpha), A.getFloatArray(), getFloat(beta), C.getFloatArray(), dim); 
    }

           static native void matvecMultOff(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim, long lda, long[] off, int transA);
           static native void matvecMultOff( float alpha,  float[] A,  float[] x,  float beta,  float[] y, long[] dim, long lda, long[] off, int transA);
    public static <T> void matvecMultOffET(      T alpha,  Rail<T> A,  Rail<T> x,      T beta,  Rail<T> y, long[] dim, long lda, long[] off, int transA) {
	if (A.$getParam(0) instanceof DoubleType) {
	    matvecMultOff(getDouble(alpha), A.getDoubleArray(), x.getDoubleArray(), getDouble(beta), y.getDoubleArray(), dim, lda, off, transA);
	    return;
	}
	matvecMultOff(getFloat(alpha), A.getFloatArray(), x.getFloatArray(), getFloat(beta), y.getFloatArray(), dim, lda, off, transA);
    }

           static native void matvecMult(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim, int transA);
           static native void matvecMult( float alpha,  float[] A,  float[] x,  float beta,  float[] y, long[] dim, int transA);
    public static <T> void matvecMultET(      T alpha,  Rail<T> A,  Rail<T> x,      T beta,  Rail<T> y, long[] dim, int transA) {
	if (A.$getParam(0) instanceof DoubleType) {
	    matvecMult(getDouble(alpha), A.getDoubleArray(), x.getDoubleArray(), getDouble(beta), y.getDoubleArray(), dim, transA);
	    return;
	}
	matvecMult(getFloat(alpha), A.getFloatArray(), x.getFloatArray(), getFloat(beta), y.getFloatArray(), dim, transA);
    }

           static native void symvecMult(double alpha, double[] A, double[] x, double beta, double[] y, long[] dim);
           static native void symvecMult( float alpha,  float[] A,  float[] x,  float beta,  float[] y, long[] dim);
    public static <T> void symvecMultET(      T alpha,  Rail<T> A,  Rail<T> x,      T beta,  Rail<T> y, long[] dim) {
	if (A.$getParam(0) instanceof DoubleType) {
	    symvecMult(getDouble(alpha), A.getDoubleArray(), x.getDoubleArray(), getDouble(beta), y.getDoubleArray(), dim);
	    return;
	}
	symvecMult(getFloat(alpha), A.getFloatArray(), x.getFloatArray(), getFloat(beta), y.getFloatArray(), dim);
    }


           static native void trivecMult(double[] A, int uplo, double[] bx, long lda, int tranA);
           static native void trivecMult( float[] A, int uplo,  float[] bx, long lda, int tranA);
    public static <T> void trivecMultET(  Rail<T> A, int uplo,  Rail<T> bx, long lda, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	    trivecMult(A.getDoubleArray(), uplo, bx.getDoubleArray(), lda, tranA);
	    return;
	}
	trivecMult(A.getFloatArray(), uplo, bx.getFloatArray(), lda, tranA);
    }

           static native void rankOneUpdate(double alpha, double[] x, double[] y, double[] A, long[] dim, long[] offset, long[] inc, long lda);
           static native void rankOneUpdate( float alpha,  float[] x,  float[] y,  float[] A, long[] dim, long[] offset, long[] inc, long lda);
    public static <T> void rankOneUpdateET(      T alpha,  Rail<T> x,  Rail<T> y,  Rail<T> A, long[] dim, long[] offset, long[] inc, long lda) {
	if (A.$getParam(0) instanceof DoubleType) {
	    rankOneUpdate(getDouble(alpha), x.getDoubleArray(), y.getDoubleArray(), A.getDoubleArray(), dim, offset, inc, lda); 
	    return;
	}
	rankOneUpdate(getFloat(alpha), x.getFloatArray(), y.getFloatArray(), A.getFloatArray(), dim, offset, inc, lda); 

    }

           static native void trimatMult(double[] A, double[] B, long[] dim, int tranA);
           static native void trimatMult( float[] A,  float[] B, long[] dim, int tranA);
    public static <T> void trimatMultET(  Rail<T> A,  Rail<T> B, long[] dim, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	    trimatMult(A.getDoubleArray(), B.getDoubleArray(), dim, tranA);
	    return;
	}
	trimatMult(A.getFloatArray(), B.getFloatArray(), dim, tranA);
    }
    
           static native void mattriMult(double[] B, double[] A, long[] dim, int tranA);
           static native void mattriMult( float[] B,  float[] A, long[] dim, int tranA);
    public static <T> void mattriMultET(  Rail<T> B,  Rail<T> A, long[] dim, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	    mattriMult(B.getDoubleArray(), A.getDoubleArray(), dim, tranA);
	    return;
	}
	mattriMult(B.getFloatArray(), A.getFloatArray(), dim, tranA);
    }
	
           static native void trivecSolve(double[] A, double[] bx, long[] dim, int tranA);
           static native void trivecSolve( float[] A,  float[] bx, long[] dim, int tranA);
    public static <T> void trivecSolveET(  Rail<T> A,  Rail<T> bx, long[] dim, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	   trivecSolve(A.getDoubleArray(), bx.getDoubleArray(), dim, tranA);
	   return;
	}
	trivecSolve(A.getFloatArray(), bx.getFloatArray(), dim, tranA);
    }


            static native void trimatSolve(double[] A, double[] BX, long[] dim, int tranA);
            static native void trimatSolve( float[] A,  float[] BX, long[] dim, int tranA);
    public static <T> void trimatSolveET(  Rail<T> A,  Rail<T> bx, long[] dim, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	   trimatSolve(A.getDoubleArray(), bx.getDoubleArray(), dim, tranA);
	   return;
	}
	trimatSolve(A.getFloatArray(), bx.getFloatArray(), dim, tranA);
    }

           static native void mattriSolve(double[] BX, double[] A, long[] dim, int tranA);
           static native void mattriSolve( float[] BX,  float[] A, long[] dim, int tranA);
    public static <T> void mattriSolveET(  Rail<T> BX,  Rail<T> A, long[] dim, int tranA) {
	if (A.$getParam(0) instanceof DoubleType) {
	   mattriSolve(BX.getDoubleArray(), A.getDoubleArray(), dim, tranA);
	   return;
	}
	mattriSolve(BX.getFloatArray(), A.getFloatArray(), dim, tranA);
    }

}
