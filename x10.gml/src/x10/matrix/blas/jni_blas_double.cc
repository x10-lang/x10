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

// Intended to be included from jni_blas.cc

//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
  
extern "C" {
  // public static native void scale(int n,  double alpha, double[] x);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_scale
  (JNIEnv *env, jclass cls, jint n, jdouble alpha, jdoubleArray x) {
	jboolean isCopy;
	jdouble* xmat = env->GetDoubleArrayElements(x, &isCopy);

	scale(n, alpha, xmat);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(x, xmat, 0);
	}	
  }

  //-------------------------------------------------------------
  // public static native void copy(int n, double[] x, double[] y);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_copy
  (JNIEnv *env, jclass cls, jint n, jdoubleArray x, jdoubleArray y) {
	jboolean isCopy;
	jdouble* xmat = env->GetDoubleArrayElements(x, NULL);
	jdouble* ymat = env->GetDoubleArrayElements(y, &isCopy);
	copy(n, xmat, ymat);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(y, ymat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native double dotProd(int n, double[] x, double[] y);
  JNIEXPORT double JNICALL Java_x10_matrix_blas_WrapBLAS_dotProd
  (JNIEnv *env, jclass cls, jint n, jdoubleArray x, jdoubleArray y) {
	jdouble* xmat = env->GetDoubleArrayElements(x, NULL);
	jdouble* ymat = env->GetDoubleArrayElements(y, NULL);
	return dot_prod(n, xmat, ymat);
  }

  //-------------------------------------------------------------
  //public static native double norm2(int n, double[] x);
  JNIEXPORT double JNICALL Java_x10_matrix_blas_WrapBLAS_norm2
  (JNIEnv *env, jclass cls, jint n, jdoubleArray x) {
	jdouble* xmat = env->GetDoubleArrayElements(x, NULL);
	return norm2(n, xmat);
  }

  //-------------------------------------------------------------
  //public static native double absSum(int n, double[] x);
  JNIEXPORT double JNICALL Java_x10_matrix_blas_WrapBLAS_absSum
  (JNIEnv *env, jclass cls, jint n, jdoubleArray x) {
	jdouble* xmat = env->GetDoubleArrayElements(x, NULL);
	return abs_sum(n, xmat);
  }

//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------
  //-------------------------------------------------------------
  // public static native void matvecMultOff(double[] A, double[] x, double[] y, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matvecMultOff
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray x, jdouble beta, jdoubleArray y, jlongArray dim, jlong lda, jlongArray off, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* xvec = env->GetDoubleArrayElements(x, NULL);
    jdouble* yvec = env->GetDoubleArrayElements(y, &isCopy);
    jlong dimlist[2];
    jlong offlist[4];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
    env->GetLongArrayRegion(off, 0, 4, offlist);

    matrix_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist, lda, (blas_long*)offlist, tranA);

    if (isCopy == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
       env->ReleaseDoubleArrayElements(y, yvec, 0);
    }
  }


  //-------------------------------------------------------------
  // public static native void matvecMult(float[] A, float[] x, float[] y, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matvecMult
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray x, jdouble beta, jdoubleArray y, jlongArray dim, jint tranA) {
    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* xvec = env->GetDoubleArrayElements(x, NULL);
    jdouble* yvec = env->GetDoubleArrayElements(y, &isCopy);
    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

    // vjtodo: Get the float version
    matrix_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist, tranA);

    if (isCopy == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
       env->ReleaseDoubleArrayElements(y, yvec, 0);
    }
  }

  //-------------------------------------------------------------
  // public static native void symvecMult(double[] A, double[] x, double[] y, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symvecMult
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray x, jdouble beta, jdoubleArray y, jlongArray dim) {
    jboolean isCopy;

    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* xvec = env->GetDoubleArrayElements(x, NULL);
    jdouble* yvec = env->GetDoubleArrayElements(y, &isCopy);
    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

	sym_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(y, yvec, 0);
	}
  }

  //--------------------------------------------------------------
  // public static native void trivecMult(double[] A, double[] bx, int lda, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trivecMult
  (JNIEnv *env, jclass cls, jdoubleArray A, jint uplo, jdoubleArray bx, jint lda, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bxvec = env->GetDoubleArrayElements(bx, &isCopy);
    //jint dimlist[3];

	tri_vector_mult(amat, uplo, bxvec, lda, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(bx, bxvec, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void rankOneUpdate(double[] x, double[] y, double[] A, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_rankOneUpdate
  (JNIEnv *env, jobject obj, jdoubleArray x, jdoubleArray y, jdoubleArray A, jlongArray dim, jlongArray offset, jlongArray inc, jint lda, jdouble alpha) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, &isCopy);
    jdouble* xvec = env->GetDoubleArrayElements(x, NULL);
    jdouble* yvec = env->GetDoubleArrayElements(y, NULL);
    jlong dimlist[2];
    jlong offsetlist[4];
    jlong inclist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
    env->GetLongArrayRegion(offset, 0, 4, offsetlist);
    env->GetLongArrayRegion(inc, 0, 2, inclist);

    rank_one_update(alpha, xvec, yvec, amat, (blas_long*)dimlist, (blas_long*)offsetlist, (blas_long*)inclist, lda);

    if (isCopy == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(A, amat, 0);
    }
  }

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

  //-------------------------------------------------------------
  // public static native void matmatMult(double[] A, double[] B, double[] C, long[] dim, long[] ld, long[] off...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matmatMultOff
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray B, jdouble beta, jdoubleArray C, jlongArray dim, jlongArray ld, jlongArray off, jintArray trans) {

	jboolean isCopy;
	jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bmat = env->GetDoubleArrayElements(B, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[3];
    jlong ldlist[3];
    jlong offlist[6];
	jint trnlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);
    env->GetLongArrayRegion(ld, 0, 3, ldlist);
    env->GetLongArrayRegion(off, 0, 6, offlist);
    env->GetIntArrayRegion(trans, 0, 2, trnlist);

	matrix_matrix_mult(alpha, amat, bmat, beta, cmat, (blas_long*)dimlist, (blas_long*)ldlist, (blas_long*)offlist, trnlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void matmatMult(double[] A, double[] B, double[] C, long[] dim, long[] ld, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matmatMult
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray B, jdouble beta, jdoubleArray C, jlongArray dim, jlongArray ld, jintArray trans) {
	jboolean isCopy;
	jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bmat = env->GetDoubleArrayElements(B, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[3];
    jlong ldlist[3];
	jint trnlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);
    env->GetLongArrayRegion(ld, 0, 3, ldlist);
    env->GetIntArrayRegion(trans, 0, 2, trnlist);

	matrix_matrix_mult(alpha, amat, bmat, beta, cmat, (blas_long*)dimlist, (blas_long*)ldlist, trnlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symRankKUpdate(double[] A, double[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symRankKUpdateOff
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdouble beta, jdoubleArray C, jlongArray dim, jlongArray ld, jlongArray off, jboolean upper, jboolean trans) {

	jboolean isCopy;
	jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[2];
    jlong ldlist[2];
    jlong offlist[4];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
    env->GetLongArrayRegion(ld, 0, 2, ldlist);
    env->GetLongArrayRegion(off, 0, 4, offlist);

	sym_rank_k_update(alpha, amat, beta, cmat, (blas_long*)dimlist, (blas_long*)ldlist, (blas_long*)offlist, upper, trans);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symRankKUpdate(double[] A, double[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symRankKUpdate
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdouble beta, jdoubleArray C, jlongArray dim, jboolean upper, jboolean trans) {

	jboolean isCopy;
	jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

	sym_rank_k_update(alpha, amat, beta, cmat, (blas_long*)dimlist, upper, trans);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symmatMult(double[] A, double[] B, double[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symmatMult
  (JNIEnv *env, jclass cls, jdouble alpha, jdoubleArray A, jdoubleArray B, jdouble beta, jdoubleArray C, jlongArray dim) {
	jboolean isCopy;
	jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bmat = env->GetDoubleArrayElements(B, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
	//printf("Here calling blas matrix mult\n"); fflush(stdout);
	sym_matrix_mult(alpha, amat, bmat, beta, cmat, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}	
  }

  // public static native void matsymMult(double[] A, double[] B, double[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matsymMult
  (JNIEnv *env, jclass cls, jdoubleArray B, jdouble alpha, jdoubleArray A, jdouble beta, jdoubleArray C, jlongArray dim) {
	jboolean isCopy;
	jdouble* bmat = env->GetDoubleArrayElements(B, NULL);
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* cmat = env->GetDoubleArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
	//printf("Here calling blas matrix mult\n"); fflush(stdout);
	matrix_sym_mult(bmat, alpha, amat, beta, cmat, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(C, cmat, 0);
	}	
  }

  //-------------------------------------------------------------
  // public static native void matvecMult2(double[] A, double[] x, double[] y, int m, int n);
/*   JNIEXPORT void JNICALL Java_x10_matrix_WrapBLAS_matvecMult2 */
/*   (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray x, jdoubleArray y, jint m, jint n) { */

/* 	jboolean isCopy; */
/* 	jdouble* amat = env->GetDoubleArrayElements(A, NULL); */
/*     jdouble* xvec = env->GetDoubleArrayElements(x, NULL); */
/*     jdouble* yvec = env->GetDoubleArrayElements(y, &isCopy); */
/* 	matrix_vector_mult(amat, xvec, yvec, m, n); */
/* 	if (isCopy == JNI_TRUE) { */
/* 	  //printf("Copying data from c library back to original data in JVM\n"); */
/* 	  env->ReleaseDoubleArrayElements(y, yvec, 0); */
/* 	} */

/*   } */


  //-------------------------------------------------------------
  // public static native void trimatMult(double[] A, double[] B, int[] dim, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trimatMult
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray B, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bmat = env->GetDoubleArrayElements(B, &isCopy);
    jlong dimlist[3];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	tri_matrix_mult(amat, bmat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(B, bmat, 0);
	}
  }

  // public static native void trimatMult(double[] A, double[] B, int[] dim, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_mattriMult
  (JNIEnv *env, jclass cls, jdoubleArray B, jdoubleArray A, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bmat = env->GetDoubleArrayElements(B, &isCopy);
    jlong dimlist[3];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	matrix_tri_mult(bmat, amat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(B, bmat, 0);
	}
  }	


  //-------------------------------------------------------------
  // Triangular solvers
  //-------------------------------------------------------------
  // public static native void trimatSolve(double[] A, double[] bx, int m, int n);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trivecSolve
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray bx, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bxvec = env->GetDoubleArrayElements(bx, &isCopy);
    jlong dimlist[3];
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	tri_vector_solve(amat, bxvec, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(bx, bxvec, 0);
	}
  }

  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trimatSolve
  (JNIEnv *env, jclass cls, jdoubleArray A, jdoubleArray BX, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bxmat = env->GetDoubleArrayElements(BX, &isCopy);
    jlong dimlist[3];

    env->GetLongArrayRegion(dim, 0, 3, dimlist);

    tri_matrix_solve(amat, bxmat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(BX, bxmat, 0);
	}
  }

  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_mattriSolve
  (JNIEnv *env, jclass cls, jdoubleArray BX, jdoubleArray A, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jdouble* amat = env->GetDoubleArrayElements(A, NULL);
    jdouble* bxmat = env->GetDoubleArrayElements(BX, &isCopy);
    jlong dimlist[3];
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	matrix_tri_solve(bxmat, amat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseDoubleArrayElements(BX, bxmat, 0);
	}
  }
}

