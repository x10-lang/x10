/*
 *  This file is part of the X10 Applications project.
 *
 *  (C) Copyright IBM Corporation 2011.
 */


#include <stdio.h>
#include <stdlib.h>

#include <jni.h>
#include "wrap_lapack.h"

extern "C" {
//------------------------------------------------------------------------
// Compute solutions to linear equations
//------------------------------------------------------------------------
//int solve_linear_equation(double* A, double* B, int* IPIV, int* dim)

  JNIEXPORT int JNICALL Java_x10_matrix_lapack_WrapLAPACK_solveLinearEquation
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray B, jintArray IPIV, jintArray dim) {
	jboolean isCopyA, isCopyB;
	jdouble* Amat = env->GetDoubleArrayElements(A, &isCopyA);
	jdouble* Bmat = env->GetDoubleArrayElements(B, &isCopyB);
	jint*   ip   = env->GetIntArrayElements(IPIV, NULL);

	jint dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetIntArrayRegion(dim, 0, 2, dimlist);

	jint info = solve_linear_equation(Amat, Bmat, ip, dimlist);

	if (isCopyA == JNI_TRUE) {
		//printf("Copying data from c library back to original data in JVM\n");
		env->ReleaseDoubleArrayElements(A, Amat, 0);
	}

	if (isCopyB == JNI_TRUE) {
		env->ReleaseDoubleArrayElements(B, Bmat, 0);
	}
	return info;
  }

  //-------------------------------------------------------------
  //int comp_engenvalue(double* A, double* W, double* WORK, int* dim)

  JNIEXPORT int JNICALL Java_x10_matrix_lapack_WrapLAPACK_compEigenValue
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray W, jdoubleArray WORK, jintArray dim) {

    jboolean isCopyW;
    jboolean isCopyDim;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* wvec = env->GetDoubleArrayElements(W, &isCopyW);
    jdouble* work = env->GetDoubleArrayElements(WORK, NULL);
    jint* dimlist = env->GetIntArrayElements(dim, &isCopyDim);

    jint info = comp_eigenvalue(amat, wvec, work, dimlist);

    if (isCopyW == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(W, wvec, 0);
    }

    if (isCopyDim == JNI_TRUE) {
       env->ReleaseIntArrayElements(dim, dimlist, 0);
    }

    return info;
  }

  JNIEXPORT int JNICALL Java_x10_matrix_lapack_WrapLAPACK_compEigenVector
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray W, jdoubleArray WORK, jintArray dim) {

	jboolean isCopyA;
	jboolean isCopyW;
    jboolean isCopyDim;
    jdouble* amat = env->GetDoubleArrayElements(A, &isCopyA);
    jdouble* wvec = env->GetDoubleArrayElements(W, &isCopyW);
    jdouble* work = env->GetDoubleArrayElements(WORK, NULL);
    jint* dimlist = env->GetIntArrayElements(dim, &isCopyDim);

    jint info = comp_eigenvector(amat, wvec, work, dimlist);

    if (isCopyW == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(W, wvec, 0);
    }

    if (isCopyA == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
       env->ReleaseDoubleArrayElements(A, amat, 0);
    }

    if (isCopyDim == JNI_TRUE) {
       env->ReleaseIntArrayElements(dim, dimlist, 0);
    }

    return info;
  }

}
//-----------------------------------------------------------------
