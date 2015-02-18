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
  // public static native void scale(int n,  ElemType alpha, ElemType[] x);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_scale
  (JNIEnv *env, jclass cls, jint n, jfloat alpha, jfloatArray x) {
	jboolean isCopy;
	jfloat* xmat = env->GetFloatArrayElements(x, &isCopy);

	scale(n, alpha, xmat);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(x, xmat, 0);
	}	
  }

  //-------------------------------------------------------------
  // public static native void copy(int n, float[] x, float[] y);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_copy
  (JNIEnv *env, jclass cls, jint n, jfloatArray x, jfloatArray y) {
	jboolean isCopy;
	jfloat* xmat = env->GetFloatArrayElements(x, NULL);
	jfloat* ymat = env->GetFloatArrayElements(y, &isCopy);
	copy(n, xmat, ymat);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(y, ymat, 0);
	}
  }

  //-------------------------------------------------------------

  // public static native float dotProd(int n, float[] x, float[] y);
  JNIEXPORT float JNICALL Java_x10_matrix_blas_WrapBLAS_dotProd
  (JNIEnv *env, jclass cls, jint n, jfloatArray x, jfloatArray y) {
	jfloat* xmat = env->GetFloatArrayElements(x, NULL);
	jfloat* ymat = env->GetFloatArrayElements(y, NULL);
	// vjTODO: the float version
	return dot_prod(n, xmat, ymat);
  }

  //-------------------------------------------------------------
  //public static native float norm2(int n, float[] x);
  JNIEXPORT float JNICALL Java_x10_matrix_blas_WrapBLAS_norm2
  (JNIEnv *env, jclass cls, jint n, jfloatArray x) {
	jfloat* xmat = env->GetFloatArrayElements(x, NULL);

	// vjTODO: the float version
	return norm2(n, xmat);
  }

  //-------------------------------------------------------------
  //public static native float absSum(int n, float[] x);
  JNIEXPORT float JNICALL Java_x10_matrix_blas_WrapBLAS_absSum
  (JNIEnv *env, jclass cls, jint n, jfloatArray x) {
	jfloat* xmat = env->GetFloatArrayElements(x, NULL);

	// vjTODO: the float version
	return abs_sum(n, xmat);
  }

//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------
  //-------------------------------------------------------------
  // public static native void matvecMultOff(float[] A, float[] x, float[] y, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matvecMultOff
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray x, jfloat beta, jfloatArray y, jlongArray dim, jlong lda, jlongArray off, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* xvec = env->GetFloatArrayElements(x, NULL);
    jfloat* yvec = env->GetFloatArrayElements(y, &isCopy);
    jlong dimlist[2];
    jlong offlist[4];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
    env->GetLongArrayRegion(off, 0, 4, offlist);

    matrix_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist, lda, (blas_long*)offlist, tranA);

    if (isCopy == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
       env->ReleaseFloatArrayElements(y, yvec, 0);
    }
  }

  //-------------------------------------------------------------
  // public static native void matvecMult(float[] A, float[] x, float[] y, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matvecMult
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray x, jfloat beta, jfloatArray y, jlongArray dim, jint tranA) {
    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* xvec = env->GetFloatArrayElements(x, NULL);
    jfloat* yvec = env->GetFloatArrayElements(y, &isCopy);
    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

    // vjtodo: Get the float version
    matrix_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist, tranA);

    if (isCopy == JNI_TRUE) {
       //printf("Copying data from c library back to original data in JVM\n");
       env->ReleaseFloatArrayElements(y, yvec, 0);
    }
  }

  //-------------------------------------------------------------
  // public static native void symvecMult(float[] A, float[] x, float[] y, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symvecMult
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray x, jfloat beta, jfloatArray y, jlongArray dim) {
    jboolean isCopy;

    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* xvec = env->GetFloatArrayElements(x, NULL);
    jfloat* yvec = env->GetFloatArrayElements(y, &isCopy);
    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

	sym_vector_mult(alpha, amat, xvec, beta, yvec, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(y, yvec, 0);
	}
  }

  //--------------------------------------------------------------
  // public static native void trivecMult(float[] A, float[] bx, int lda, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trivecMult
  (JNIEnv *env, jclass cls, jfloatArray A, jint uplo, jfloatArray bx, jint lda, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bxvec = env->GetFloatArrayElements(bx, &isCopy);
    //jint dimlist[3];

	tri_vector_mult(amat, uplo, bxvec, lda, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(bx, bxvec, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void rankOneUpdate(float[] x, float[] y, float[] A, ....)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_rankOneUpdate
  (JNIEnv *env, jobject obj, jfloatArray x, jfloatArray y, jfloatArray A, jlongArray dim, jlongArray offset, jlongArray inc, jint lda, jfloat alpha) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, &isCopy);
    jfloat* xvec = env->GetFloatArrayElements(x, NULL);
    jfloat* yvec = env->GetFloatArrayElements(y, NULL);
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
	  env->ReleaseFloatArrayElements(A, amat, 0);
    }
  }

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

  //-------------------------------------------------------------
  // public static native void matmatMult(float[] A, float[] B, float[] C, long[] dim, long[] ld, long[] off...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matmatMultOff
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray B, jfloat beta, jfloatArray C, jlongArray dim, jlongArray ld, jlongArray off, jintArray trans) {

	jboolean isCopy;
	jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bmat = env->GetFloatArrayElements(B, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

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
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void matmatMult(float[] A, float[] B, float[] C, long[] dim, long[] ld, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matmatMult
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray B, jfloat beta, jfloatArray C, jlongArray dim, jlongArray ld, jintArray trans) {
	jboolean isCopy;
	jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bmat = env->GetFloatArrayElements(B, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

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
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symRankKUpdate(float[] A, float[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symRankKUpdateOff
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloat beta, jfloatArray C, jlongArray dim, jlongArray ld, jlongArray off, jboolean upper, jboolean trans) {

	jboolean isCopy;
	jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

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
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symRankKUpdate(float[] A, float[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symRankKUpdate
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloat beta, jfloatArray C, jlongArray dim, jboolean upper, jboolean trans) {

	jboolean isCopy;
	jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);

	sym_rank_k_update(alpha, amat, beta, cmat, (blas_long*)dimlist, upper, trans);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}
  }

  //-------------------------------------------------------------
  // public static native void symmatMult(float[] A, float[] B, float[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_symmatMult
  (JNIEnv *env, jclass cls, jfloat alpha, jfloatArray A, jfloatArray B, jfloat beta, jfloatArray C, jlongArray dim) {
	jboolean isCopy;
	jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bmat = env->GetFloatArrayElements(B, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
	//printf("Here calling blas matrix mult\n"); fflush(stdout);
	sym_matrix_mult(alpha, amat, bmat, beta, cmat, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}	
  }

  // public static native void matsymMult(float[] A, float[] B, float[] C, ...)
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_matsymMult
  (JNIEnv *env, jclass cls, jfloatArray B, jfloat alpha, jfloatArray A, jfloat beta, jfloatArray C, jlongArray dim) {
	jboolean isCopy;
	jfloat* bmat = env->GetFloatArrayElements(B, NULL);
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* cmat = env->GetFloatArrayElements(C, &isCopy);

    jlong dimlist[2];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 2, dimlist);
	//printf("Here calling blas matrix mult\n"); fflush(stdout);
	matrix_sym_mult(bmat, alpha, amat, beta, cmat, (blas_long*)dimlist);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(C, cmat, 0);
	}	
  }

  //-------------------------------------------------------------
  // public static native void matvecMult2(float[] A, float[] x, float[] y, int m, int n);
/*   JNIEXPORT void JNICALL Java_x10_matrix_WrapBLAS_matvecMult2 */
/*   (JNIEnv *env, jclass cls, jfloatArray A, jfloatArray x, jfloatArray y, jint m, jint n) { */

/* 	jboolean isCopy; */
/* 	jfloat* amat = env->GetFloatArrayElements(A, NULL); */
/*     jfloat* xvec = env->GetFloatArrayElements(x, NULL); */
/*     jfloat* yvec = env->GetFloatArrayElements(y, &isCopy); */
/* 	matrix_vector_mult(amat, xvec, yvec, m, n); */
/* 	if (isCopy == JNI_TRUE) { */
/* 	  //printf("Copying data from c library back to original data in JVM\n"); */
/* 	  env->ReleaseFloatArrayElements(y, yvec, 0); */
/* 	} */

/*   } */


  //-------------------------------------------------------------
  // public static native void trimatMult(float[] A, float[] B, int[] dim, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trimatMult
  (JNIEnv *env, jclass cls, jfloatArray A, jfloatArray B, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bmat = env->GetFloatArrayElements(B, &isCopy);
    jlong dimlist[3];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	tri_matrix_mult(amat, bmat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(B, bmat, 0);
	}
  }

  // public static native void trimatMult(float[] A, float[] B, int[] dim, int tranA);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_mattriMult
  (JNIEnv *env, jclass cls, jfloatArray B, jfloatArray A, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bmat = env->GetFloatArrayElements(B, &isCopy);
    jlong dimlist[3];
    // This line is necessary, since Java arrays are not guaranteed 
    // to have a continuous memory layout like C arrays.
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	matrix_tri_mult(bmat, amat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(B, bmat, 0);
	}
  }	


  //-------------------------------------------------------------
  // Triangular solvers
  //-------------------------------------------------------------
  // public static native void trimatSolve(float[] A, float[] bx, int m, int n);
  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trivecSolve
  (JNIEnv *env, jclass cls, jfloatArray A, jfloatArray bx, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bxvec = env->GetFloatArrayElements(bx, &isCopy);
    jlong dimlist[3];
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	tri_vector_solve(amat, bxvec, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(bx, bxvec, 0);
	}
  }

  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_trimatSolve
  (JNIEnv *env, jclass cls, jfloatArray A, jfloatArray BX, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bxmat = env->GetFloatArrayElements(BX, &isCopy);
    jlong dimlist[3];

    env->GetLongArrayRegion(dim, 0, 3, dimlist);

    tri_matrix_solve(amat, bxmat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(BX, bxmat, 0);
	}
  }

  JNIEXPORT void JNICALL Java_x10_matrix_blas_WrapBLAS_mattriSolve
  (JNIEnv *env, jclass cls, jfloatArray BX, jfloatArray A, jlongArray dim, jint tranA) {

    jboolean isCopy;
    jfloat* amat = env->GetFloatArrayElements(A, NULL);
    jfloat* bxmat = env->GetFloatArrayElements(BX, &isCopy);
    jlong dimlist[3];
    env->GetLongArrayRegion(dim, 0, 3, dimlist);

	matrix_tri_solve(bxmat, amat, (blas_long*)dimlist, tranA);

	if (isCopy == JNI_TRUE) {
	  //printf("Copying data from c library back to original data in JVM\n");
	  env->ReleaseFloatArrayElements(BX, bxmat, 0);
	}
  }
}

