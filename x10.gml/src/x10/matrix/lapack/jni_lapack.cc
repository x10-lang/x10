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
  //int comp_eigenvalues(double* A, double* W, double* WORK, int* IWORK, int* dim)

  JNIEXPORT int JNICALL Java_x10_matrix_lapack_WrapLAPACK_compEigenvalues
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray W, jdoubleArray WORK, jintArray IWORK, jintArray dim) {

    jboolean isCopyW;
    jboolean isCopyDim;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* wvec = env->GetDoubleArrayElements(W, &isCopyW);
    jdouble* work = env->GetDoubleArrayElements(WORK, NULL);
    jint* iwork = env->GetIntArrayElements(IWORK, NULL);
    jint* dimlist = env->GetIntArrayElements(dim, &isCopyDim);

    jint info = comp_eigenvalues(amat, wvec, work, iwork, dimlist);

    if (isCopyW == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(W, wvec, 0);
    }

    if (isCopyDim == JNI_TRUE) {
       env->ReleaseIntArrayElements(dim, dimlist, 0);
    }

    return info;
  }

  //-------------------------------------------------------------
  //int comp_eigenvectors(double* A, double* W, double* Z, double* WORK, int* IWORK, int* dim)

  JNIEXPORT int JNICALL Java_x10_matrix_lapack_WrapLAPACK_compEigenvectors
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray W, jdoubleArray Z, jdoubleArray WORK, jintArray IWORK, jintArray IFAIL, jintArray dim) {

	jboolean isCopyA;
	jboolean isCopyW;
    jboolean isCopyZ;
    jboolean isCopyDim;
    jdouble* amat = env->GetDoubleArrayElements(A, &isCopyA);
    jdouble* wvec = env->GetDoubleArrayElements(W, &isCopyW);
    jdouble* zvec = env->GetDoubleArrayElements(Z, &isCopyZ);
    jdouble* work = env->GetDoubleArrayElements(WORK, NULL);
    jint* iwork = env->GetIntArrayElements(IWORK, NULL);
    jint* ifail = env->GetIntArrayElements(IFAIL, NULL);
    jint* dimlist = env->GetIntArrayElements(dim, &isCopyDim);

    jint info = comp_eigenvectors(amat, wvec, zvec, work, iwork, ifail, dimlist);

    if (isCopyW == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(W, wvec, 0);
    }

    if (isCopyZ == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(Z, zvec, 0);
    }

    if (isCopyA == JNI_TRUE) {
       env->ReleaseDoubleArrayElements(A, amat, 0);
    }

    if (isCopyDim == JNI_TRUE) {
       env->ReleaseIntArrayElements(dim, dimlist, 0);
    }

    return info;
  }

}
// vim:tabstop=4:shiftwidth=4:expandtab
