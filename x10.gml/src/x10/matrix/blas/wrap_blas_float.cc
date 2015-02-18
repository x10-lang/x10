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

// Intended to be included from wrap_blas.cc

#ifdef __cplusplus
extern "C"  {
#endif

  // Rotate (c,s,r) <- rot(a, b)
  // void cblas_drot(blasint N, double *X, blasint incX, double *Y, blasint incY, double c, double  s);
  
  // for setup Givens rotation
  // void cblas_drotg(double *a, double *b, double *c, double *s);

  //for	setup modified Givens rotation
  // void cblas_drotmg(double *d1, double *d2, double *b1, double b2, double *P);

  // for apply modified Givens rotation
  //void cblas_drotm(blasint N, double *X, blasint incX, double *Y, blasint incY, double *P);

  // for swap x and y
  // Checkout void cblas_dswap(blasint n, double *x, blasint incx, double *Y, blasint incy);

  // for	x = a*x
  // Reciprocal Scale: x <-x/alpha
#if defined(__essl__)
  void sscal(blas_long* N, float* alpha, float *X, blas_long* incX);
#else
  void sscal_(blas_long* N, float* alpha, float *X, blas_long* incX);
#endif

  // Vector Copy: y <-x
#if defined(__essl__)
  void scopy(blas_long* N, float *X, blas_long* incx, float *Y, blas_long* incy);
#else
  void scopy_(blas_long* N, float *X, blas_long* incx, float *Y, blas_long* incy);
#endif

  // for dot product
  // DOT Production: r <- beta * r + alpha * x^T * y
#if defined(__essl__)
  float sdot(blas_long* N, float *X, blas_long* incx, float *Y, blas_long* incy);
#else
  float sdot_(blas_long* N, float *X, blas_long* incx, float *Y, blas_long* incy);
#endif
	
  // for Euclidean norm
#if defined(__essl__)
  float snrm2(blas_long* N, float *X, blas_long* incx);
#else
  float snrm2_(blas_long* N, float *X, blas_long* incx);
#endif

  // SUM: for sum of absolute values
#if defined(__essl__)
  float sasum(blas_long* N, float *X, blas_long* incx);
#else
  float sasum_(blas_long* N, float *X, blas_long* incx);
#endif
	
  // MAX, for index of max abs value
  // CBLAS_INDEX cblas_idamax(blasint n, double *x, blasint incx)
  
  // SUBROUTINE DAXPY(N,DA,DX,INCX,DY,INCY)
#if defined(__essl__)
  void  saxpy(blas_long*N, blas_long*da, float*X, blas_long*incx, float*Y,blas_long* incy);
#else
  void  saxpy_(blas_long*N, blas_long*da, float*X, blas_long*incx, float*Y,blas_long* incy); 
#endif

  //------------------------------------------------------------------------
  // Level Two
  //------------------------------------------------------------------------

  /**
   * SUBROUTINE DGEMV(TRANS,M,N,ALPHA,A,LDA,X,INCX,BETA,Y,INCY)
   *
   * DGEMV  performs one of the matrix-vector operations
   *
   *     y := alpha*A*x + beta*y,   or   y := alpha*A'*x + beta*y,
   *
   *  where alpha and beta are scalars, x and y are vectors and A is an
   *  m by n matrix.
   *
   *  Arguments
   *  ==========
   *
   *  TRANS  - CHARACTER*1.
   *           On entry, TRANS specifies the operation to be performed as
   *           follows:
   *              TRANS = 'N' or 'n'   y := alpha*A*x + beta*y.
   *              TRANS = 'T' or 't'   y := alpha*A'*x + beta*y.
   *              TRANS = 'C' or 'c'   y := alpha*A'*x + beta*y.
   *           Unchanged on exit.
   *
   *  M      - INTEGER.
   *           On entry, M specifies the number of rows of the matrix A.
   *           M must be at least zero.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the number of columns of the matrix A.
   *           N must be at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry, ALPHA specifies the scalar alpha.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, n ).
   *           Before entry, the leading m by n part of the array A must
   *           contain the matrix of coefficients.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program. LDA must be at least
   *           max( 1, m ).
   *           Unchanged on exit.
   *
   *  X      - DOUBLE PRECISION array of DIMENSION at least
   *           ( 1 + ( n - 1 )*abs( INCX ) ) when TRANS = 'N' or 'n'
   *           and at least
   *           ( 1 + ( m - 1 )*abs( INCX ) ) otherwise.
   *           Before entry, the incremented array X must contain the
   *           vector x.
   *           Unchanged on exit.
   *
   *  INCX   - INTEGER.
   *           On entry, INCX specifies the increment for the elements of
   *           X. INCX must not be zero.
   *           Unchanged on exit.
   *
   *  BETA   - DOUBLE PRECISION.
   *           On entry, BETA specifies the scalar beta. When BETA is
   *           supplied as zero then Y need not be set on input.
   *           Unchanged on exit.
   *
   *  Y      - DOUBLE PRECISION array of DIMENSION at least
   *           ( 1 + ( m - 1 )*abs( INCY ) ) when TRANS = 'N' or 'n'
   *           and at least
   *           ( 1 + ( n - 1 )*abs( INCY ) ) otherwise.
   *           Before entry with BETA non-zero, the incremented array Y
   *           must contain the vector y. On exit, Y is overwritten by the
   *           updated vector y.
   *
   *  INCY   - INTEGER.
   *           On entry, INCY specifies the increment for the elements of
   *           Y. INCY must not be zero.
   *           Unchanged on exit.
   */
#if defined(__essl__)
  void sgemv(char* trans, blas_long* M, blas_long* N,
			  float* alpha, float  *A, blas_long* lda,
			  float* x, blas_long* incx,
			  float* beta, float* y, blas_long* incy);
#else
  void sgemv_(char* trans, blas_long* M, blas_long* N, 
			  float* alpha, float  *A, blas_long* lda,  
			  float* x, blas_long* incx, 
			  float* beta, float* y, blas_long* incy);
#endif

  //------------------------------------------------------------------------
   

  /*
   * SUBROUTINE DSYMV(UPLO,N,ALPHA,A,LDA,X,INCX,BETA,Y,INCY)
   *
   *  DSYMV  performs the matrix-vector operation
   *     y := alpha*A*x + beta*y,
   *  where alpha and beta are scalars, x and y are n element vectors and
   *  A is an n by n symmetric matrix.
   *
   *  Arguments
   *  ==========
   *  UPLO   - CHARACTER*1.
   *           On entry, UPLO specifies whether the upper or lower
   *           triangular part of the array A is to be referenced as
   *           follows:
   *
   *              UPLO = 'U' or 'u'   Only the upper triangular part of A
   *                                  is to be referenced.
   *              UPLO = 'L' or 'l'   Only the lower triangular part of A
   *                                  is to be referenced.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the order of the matrix A.
   *           N must be at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry, ALPHA specifies the scalar alpha.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, n ).
   *           Before entry with  UPLO = 'U' or 'u', the leading n by n
   *           upper triangular part of the array A must contain the upper
   *           triangular part of the symmetric matrix and the strictly
   *           lower triangular part of A is not referenced.
   *           Before entry with UPLO = 'L' or 'l', the leading n by n
   *           lower triangular part of the array A must contain the lower
   *           triangular part of the symmetric matrix and the strictly
   *           upper triangular part of A is not referenced.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program. LDA must be at least max( 1, n ).
   *           Unchanged on exit.
   *
   *  X      - DOUBLE PRECISION array of dimension at least
   *           ( 1 + ( n - 1 )*abs( INCX ) ).
   *           Before entry, the incremented array X must contain the n
   *           element vector x.
   *           Unchanged on exit.
   *
   *  INCX   - INTEGER.
   *           On entry, INCX specifies the increment for the elements of
   *           X. INCX must not be zero.
   *           Unchanged on exit.
   *
   *  BETA   - DOUBLE PRECISION.
   *           On entry, BETA specifies the scalar beta. When BETA is
   *           supplied as zero then Y need not be set on input.
   *           Unchanged on exit.
   *
   *  Y      - DOUBLE PRECISION array of dimension at least
   *           ( 1 + ( n - 1 )*abs( INCY ) ).
   *           Before entry, the incremented array Y must contain the n
   *           element vector y. On exit, Y is overwritten by the updated
   *           vector y.
   *
   *  INCY   - INTEGER.
   *           On entry, INCY specifies the increment for the elements of
   *           Y. INCY must not be zero.
   *           Unchanged on exit.
   */
#if defined(__essl__)
  void ssymv(char* uplo, blas_long* N, float* alpha, float* A, blas_long* lda,
			  float* X, blas_long* incx, float* beta, float* Y, blas_long* incy);
#else
  void ssymv_(char* uplo, blas_long* N, float* alpha, float* A, blas_long* lda,
			  float* X, blas_long* incx, float* beta, float* Y, blas_long* incy);
#endif

  //----------------------------------------------------------------------

   /*
	* SUBROUTINE DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
	*
	*  DTRMV  performs one of the matrix-vector operations
	*
	*     x := A*x,   or   x := A**T*x,
	*
	*  where x is an n element vector and  A is an n by n unit, or non-unit,
	*  upper or lower triangular matrix.
	*
	*  Arguments
	*  ==========
	*
	*  UPLO   - CHARACTER*1.
	*           On entry, UPLO specifies whether the matrix is an upper or
	*           lower triangular matrix as follows:
	*
	*              UPLO = 'U' or 'u'   A is an upper triangular matrix.
	*
	*              UPLO = 'L' or 'l'   A is a lower triangular matrix.
	*
	*           Unchanged on exit.
	*
	*  TRANS  - CHARACTER*1.
	*           On entry, TRANS specifies the operation to be performed as
	*           follows:
	*
	*              TRANS = 'N' or 'n'   x := A*x.
	*
	*              TRANS = 'T' or 't'   x := A**T*x.
	*
	*              TRANS = 'C' or 'c'   x := A**T*x.
	*
	*           Unchanged on exit.
	*
	*  DIAG   - CHARACTER*1.
	*           On entry, DIAG specifies whether or not A is unit
	*           triangular as follows:
	*
	*              DIAG = 'U' or 'u'   A is assumed to be unit triangular.
	*
	*              DIAG = 'N' or 'n'   A is not assumed to be unit
	*                                  triangular.
	*
	*           Unchanged on exit.
	*
	*  N      - INTEGER.
	*           On entry, N specifies the order of the matrix A.
	*           N must be at least zero.
	*           Unchanged on exit.
	*
	*  A      - DOUBLE PRECISION array of DIMENSION ( LDA, n ).
	*           Before entry with  UPLO = 'U' or 'u', the leading n by n
	*           upper triangular part of the array A must contain the upper
	*           triangular matrix and the strictly lower triangular part of
	*           A is not referenced.
	*           Before entry with UPLO = 'L' or 'l', the leading n by n
	*           lower triangular part of the array A must contain the lower
	*           triangular matrix and the strictly upper triangular part of
	*           A is not referenced.
	*           Note that when  DIAG = 'U' or 'u', the diagonal elements of
	*           A are not referenced either, but are assumed to be unity.
	*           Unchanged on exit.
	*
	*  LDA    - INTEGER.
	*           On entry, LDA specifies the first dimension of A as declared
	*           in the calling (sub) program. LDA must be at least
	*           max( 1, n ).
	*           Unchanged on exit.
	*
	*  X      - DOUBLE PRECISION array of dimension at least
	*           ( 1 + ( n - 1 )*abs( INCX ) ).
	*           Before entry, the incremented array X must contain the n
	*           element vector x. On exit, X is overwritten with the
	*           tranformed vector x.
	*
	*  INCX   - INTEGER.
	*           On entry, INCX specifies the increment for the elements of
	*           X. INCX must not be zero.
	*           Unchanged on exit.
	*/
#if defined(__essl__)
  void strmv(char* uplo, char* trans, char* diag, blas_long* N, float* A, blas_long* lda, float* X, blas_long* incx);
#else
  void strmv_(char* uplo, char* trans, char* diag, blas_long* N, float* A, blas_long* lda, float* X, blas_long* incx);
#endif

   /*
    * SUBROUTINE SGER(M,N,ALPHA,X,INCX,Y,INCY,A,LDA)
    *
    *  SGER   performs the rank 1 operation
    *
    *     A := alpha*x*y**T + A,
    *
    *  where alpha is a scalar, x is an m element vector, y is an n element
    *  vector and A is an m by n matrix.
    *
    *  Arguments
    *  ==========
    *
    *  M      - INTEGER.
    *           On entry, M specifies the number of rows of the matrix A.
    *           M must be at least zero.
    *           Unchanged on exit.
    *
    *  N      - INTEGER.
    *           On entry, N specifies the number of columns of the matrix A.
    *           N must be at least zero.
    *           Unchanged on exit.
    *
    *  ALPHA  - REAL            .
    *           On entry, ALPHA specifies the scalar alpha.
    *           Unchanged on exit.
    *
    *  X      - REAL             array of dimension at least
    *           ( 1 + ( m - 1 )*abs( INCX ) ).
    *           Before entry, the incremented array X must contain the m
    *           element vector x.
    *           Unchanged on exit.
    *
    *  INCX   - INTEGER.
    *           On entry, INCX specifies the increment for the elements of
    *           X. INCX must not be zero.
    *           Unchanged on exit.
    *
    *  Y      - REAL             array of dimension at least
    *           ( 1 + ( n - 1 )*abs( INCY ) ).
    *           Before entry, the incremented array Y must contain the n
    *           element vector y.
    *           Unchanged on exit.
    *
    *  INCY   - INTEGER.
    *           On entry, INCY specifies the increment for the elements of
    *           Y. INCY must not be zero.
    *           Unchanged on exit.
    *
    *  A      - REAL             array of DIMENSION ( LDA, n ).
    *           Before entry, the leading m by n part of the array A must
    *           contain the matrix of coefficients. On exit, A is
    *           overwritten by the updated matrix.
    *
    *  LDA    - INTEGER.
    *           On entry, LDA specifies the first dimension of A as declared
    *           in the calling (sub) program. LDA must be at least
    *           max( 1, m ).
    *           Unchanged on exit.
   */
#if defined(__essl__)
  void sger(blas_long* M, blas_long* N,
			  float* alpha, float* x, blas_long* incX,
			  float* Y, blas_long* incY,
			  float* A, blas_long* lda);
#else
  void sger_(blas_long* M, blas_long* N,
			  float* alpha, float* x, blas_long* incX,
			  float* Y, blas_long* incY,
			  float* A, blas_long* lda);
#endif

  //------------------------------------------------------------------------

  /**
   * SUBROUTINE DTRSV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
   *
   *  DTRSV  solves one of the systems of equations
   *
   *     A*x = b,   or   A'*x = b,
   *
   *  where b and x are n element vectors and A is an n by n unit, or
   *  non-unit, upper or lower triangular matrix.
   *
   *  No test for singularity or near-singularity is included in this
   *  routine. Such tests must be performed before calling this routine.
   *
   *  Arguments
   *  ==========
   *
   *  UPLO   - CHARACTER*1.
   *           On entry, UPLO specifies whether the matrix is an upper or
   *           lower triangular matrix as follows:
   *              UPLO = 'U' or 'u'   A is an upper triangular matrix.
   *              UPLO = 'L' or 'l'   A is a lower triangular matrix.
   *           Unchanged on exit.
   *
   *  TRANS  - CHARACTER*1.
   *           On entry, TRANS specifies the equations to be solved as
   *           follows:
   *              TRANS = 'N' or 'n'   A*x = b.
   *              TRANS = 'T' or 't'   A'*x = b.
   *              TRANS = 'C' or 'c'   A'*x = b.
   *           Unchanged on exit.
   *
   *  DIAG   - CHARACTER*1.
   *           On entry, DIAG specifies whether or not A is unit
   *           triangular as follows:
   *              DIAG = 'U' or 'u'   A is assumed to be unit triangular.
   *              DIAG = 'N' or 'n'   A is not assumed to be unit
   *                                  triangular.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the order of the matrix A.
   *           N must be at least zero.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, n ).
   *           Before entry with  UPLO = 'U' or 'u', the leading n by n
   *           upper triangular part of the array A must contain the upper
   *           triangular matrix and the strictly lower triangular part of
   *           A is not referenced.
   *           Before entry with UPLO = 'L' or 'l', the leading n by n
   *           lower triangular part of the array A must contain the lower
   *           triangular matrix and the strictly upper triangular part of
   *           A is not referenced.
   *           Note that when  DIAG = 'U' or 'u', the diagonal elements of
   *           A are not referenced either, but are assumed to be unity.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program. LDA must be at least max( 1, n ).
   *           Unchanged on exit.
   *
   *  X      - DOUBLE PRECISION array of dimension at least
   *           ( 1 + ( n - 1 )*abs( INCX ) ).
   *           Before entry, the incremented array X must contain the n
   *           element right-hand side vector b. On exit, X is overwritten
   *           with the solution vector x.
   *
   *  INCX   - INTEGER.
   *           On entry, INCX specifies the increment for the elements of
   *           X. INCX must not be zero.
   *           Unchanged on exit.
   */
#if defined(__essl__)
  void strsv(char* uplo, char* trans, char* diag,
			  blas_long* N, float* A, blas_long* lda,
			  float* X, blas_long* incx);
#else
  void strsv_(char* uplo, char* trans, char* diag, 
			  blas_long* N, float* A, blas_long* lda, 
			  float* X, blas_long* incx);
#endif

	   
  // for performs the symmetric rank 1 operation A := alpha*x*x' + A
  // void cblas_dsyr(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, blasint N, double alpha, 
  //		           double *X, blasint incX, double *A, blasint lda);
  
  // for	performs the symmetric rank 2 operation A := alpha*x*y' + alpha*y*x' + A
  // void cblas_dsyr2(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, blasint N, double alpha, double *X,
  //            blasint incX, double *Y, blasint incY, double *A, blasint lda);



  //------------------------------------------------------------------------
  // Level Three
  //------------------------------------------------------------------------

  /**
   * SUBROUTINE DGEMM(TRANSA,TRANSB,M,N,K,ALPHA,A,LDA,B,LDB,BETA,C,LDC)
   *
   *  DGEMM performs one of the matrix-matrix operations
   *
   *     C := alpha*op( A )*op( B ) + beta*C,
   *
   *  where  op( X ) is one of
   *
   *     op( X ) = X   or   op( X ) = X',
   *
   *  alpha and beta are scalars, and A, B and C are matrices, with op( A )
   *  an m by k matrix,  op( B )  a  k by n matrix and  C an m by n matrix.
   *
   *  Arguments
   *  ==========
   *
   *  TRANSA - CHARACTER*1.
   *           On entry, TRANSA specifies the form of op( A ) to be used in
   *           the matrix multiplication as follows:
   *              TRANSA = 'N' or 'n',  op( A ) = A.
   *              TRANSA = 'T' or 't',  op( A ) = A'.
   *              TRANSA = 'C' or 'c',  op( A ) = A'.
   *           Unchanged on exit.
   *
   *  TRANSB - CHARACTER*1.
   *           On entry, TRANSB specifies the form of op( B ) to be used in
   *           the matrix multiplication as follows:
   *              TRANSB = 'N' or 'n',  op( B ) = B.
   *              TRANSB = 'T' or 't',  op( B ) = B'.
   *              TRANSB = 'C' or 'c',  op( B ) = B'.
   *           Unchanged on exit.
   *
   *  M      - INTEGER.
   *           On entry,  M  specifies  the number  of rows  of the  matrix
   *           op( A )  and of the  matrix  C.  M  must  be at least  zero.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry,  N  specifies the number  of columns of the matrix
   *           op( B ) and the number of columns of the matrix C. N must be
   *           at least zero.
   *           Unchanged on exit.
   *
   *  K      - INTEGER.
   *           On entry,  K  specifies  the number of columns of the matrix
   *           op( A ) and the number of rows of the matrix op( B ). K must
   *           be at least  zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry, ALPHA specifies the scalar alpha.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, ka ), where ka is
   *           k  when  TRANSA = 'N' or 'n',  and is  m  otherwise.
   *           Before entry with  TRANSA = 'N' or 'n',  the leading  m by k
   *           part of the array  A  must contain the matrix  A,  otherwise
   *           the leading  k by m  part of the array  A  must contain  the
   *           matrix A.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program. When  TRANSA = 'N' or 'n' then
   *           LDA must be at least  max( 1, m ), otherwise  LDA must be at
   *           least  max( 1, k ).
   *           Unchanged on exit.
   *
   *  B      - DOUBLE PRECISION array of DIMENSION ( LDB, kb ), where kb is
   *           n  when  TRANSB = 'N' or 'n',  and is  k  otherwise.
   *           Before entry with  TRANSB = 'N' or 'n',  the leading  k by n
   *           part of the array  B  must contain the matrix  B,  otherwise
   *           the leading  n by k  part of the array  B  must contain  the
   *           matrix B.
   *           Unchanged on exit.
   *
   *  LDB    - INTEGER.
   *           On entry, LDB specifies the first dimension of B as declared
   *           in the calling (sub) program. When  TRANSB = 'N' or 'n' then
   *           LDB must be at least  max( 1, k ), otherwise  LDB must be at
   *           least  max( 1, n ).
   *           Unchanged on exit.
   *
   *  BETA   - DOUBLE PRECISION.
   *           On entry,  BETA  specifies the scalar  beta.  When  BETA  is
   *           supplied as zero then C need not be set on input.
   *           Unchanged on exit.
   *
   *  C      - DOUBLE PRECISION array of DIMENSION ( LDC, n ).
   *           Before entry, the leading  m by n  part of the array  C must
   *           contain the matrix  C,  except when  beta  is zero, in which
   *           case C need not be set on entry.
   *           On exit, the array  C  is overwritten by the  m by n  matrix
   *           ( alpha*op( A )*op( B ) + beta*C ).
   *
   *  LDC    - INTEGER.
   *           On entry, LDC specifies the first dimension of C as declared
   *           in  the  calling  (sub)  program.   LDC  must  be  at  least
   *           max( 1, m ).
   *           Unchanged on exit.
   */
#if defined(__essl__)
  void sgemm(char* transA, char* transB, blas_long* M, blas_long* N, blas_long* K,
			  float* alpha, float* A, blas_long* lda, 
			                 float* B, blas_long* ldb, 
			  float* beta,  float* C, blas_long* ldc);
#else
   void sgemm_(char* transA, char* transB, blas_long* M, blas_long* N, blas_long* K,
 			  float* alpha, float* A, blas_long* lda,
 			                 float* B, blas_long* ldb,
 			  float* beta,  float* C, blas_long* ldc);
#endif

  /**
   * SUBROUTINE DSYMM(SIDE,UPLO,M,N,ALPHA,A,LDA,B,LDB,BETA,C,LDC)
   *
   *  DSYMM  performs one of the matrix-matrix operations
   *     C := alpha*A*B + beta*C,
   *  or
   *     C := alpha*B*A + beta*C,
   *
   *  where alpha and beta are scalars,  A is a symmetric matrix and  B and
   *  C are  m by n matrices.
   *
   *  Arguments
   *  ==========
   *  SIDE   - CHARACTER*1.
   *           On entry,  SIDE  specifies whether  the  symmetric matrix  A
   *           appears on the  left or right  in the  operation as follows:
   *              SIDE = 'L' or 'l'   C := alpha*A*B + beta*C,
   *              SIDE = 'R' or 'r'   C := alpha*B*A + beta*C,
   *           Unchanged on exit.
   *
   *  UPLO   - CHARACTER*1.
   *           On  entry,   UPLO  specifies  whether  the  upper  or  lower
   *           triangular  part  of  the  symmetric  matrix   A  is  to  be
   *           referenced as follows:
   *              UPLO = 'U' or 'u'   Only the upper triangular part of the
   *                                  symmetric matrix is to be referenced.
   *              UPLO = 'L' or 'l'   Only the lower triangular part of the
   *                                  symmetric matrix is to be referenced.
   *           Unchanged on exit.
   *
   *  M      - INTEGER.
   *           On entry,  M  specifies the number of rows of the matrix  C.
   *           M  must be at least zero.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the number of columns of the matrix C.
   *           N  must be at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry, ALPHA specifies the scalar alpha.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, ka ), where ka is
   *           m  when  SIDE = 'L' or 'l'  and is  n otherwise.
   *           Before entry  with  SIDE = 'L' or 'l',  the  m by m  part of
   *           the array  A  must contain the  symmetric matrix,  such that
   *           when  UPLO = 'U' or 'u', the leading m by m upper triangular
   *           part of the array  A  must contain the upper triangular part
   *           of the  symmetric matrix and the  strictly  lower triangular
   *           part of  A  is not referenced,  and when  UPLO = 'L' or 'l',
   *           the leading  m by m  lower triangular part  of the  array  A
   *           must  contain  the  lower triangular part  of the  symmetric
   *           matrix and the  strictly upper triangular part of  A  is not
   *           referenced.
   *           Before entry  with  SIDE = 'R' or 'r',  the  n by n  part of
   *           the array  A  must contain the  symmetric matrix,  such that
   *           when  UPLO = 'U' or 'u', the leading n by n upper triangular
   *           part of the array  A  must contain the upper triangular part
   *           of the  symmetric matrix and the  strictly  lower triangular
   *           part of  A  is not referenced,  and when  UPLO = 'L' or 'l',
   *           the leading  n by n  lower triangular part  of the  array  A
   *           must  contain  the  lower triangular part  of the  symmetric
   *           matrix and the  strictly upper triangular part of  A  is not
   *           referenced.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program.  When  SIDE = 'L' or 'l'  then
   *           LDA must be at least  max( 1, m ), otherwise  LDA must be at
   *           least  max( 1, n ).
   *           Unchanged on exit.
   *
   *  B      - DOUBLE PRECISION array of DIMENSION ( LDB, n ).
   *           Before entry, the leading  m by n part of the array  B  must
   *           contain the matrix B.
   *           Unchanged on exit.
   *
   *  LDB    - INTEGER.
   *           On entry, LDB specifies the first dimension of B as declared
   *           in  the  calling  (sub)  program.   LDB  must  be  at  least
   *           max( 1, m ).
   *           Unchanged on exit.
   *
   *  BETA   - DOUBLE PRECISION.
   *           On entry,  BETA  specifies the scalar  beta.  When  BETA  is
   *           supplied as zero then C need not be set on input.
   *           Unchanged on exit.
   *
   *  C      - DOUBLE PRECISION array of DIMENSION ( LDC, n ).
   *           Before entry, the leading  m by n  part of the array  C must
   *           contain the matrix  C,  except when  beta  is zero, in which
   *           case C need not be set on entry.
   *           On exit, the array  C  is overwritten by the  m by n updated
   *           matrix.
   *
   *  LDC    - INTEGER.
   *           On entry, LDC specifies the first dimension of C as declared
   *           in  the  calling  (sub)  program.   LDC  must  be  at  least
   *           max( 1, m ).
   *           Unchanged on exit.
   */
#if defined(__essl__)
   void ssymm(char* side, char* uplo, blas_long* M, blas_long* N,
 			  float* alpha, float* A, blas_long* lda,
 			  float* B, blas_long* ldb,
 			  float* beta, float* C, blas_long* ldc);
#else
   void ssymm_(char* side, char* uplo, blas_long* M, blas_long* N,
 			  float* alpha, float* A, blas_long* lda,
 			  float* B, blas_long* ldb,
 			  float* beta, float* C, blas_long* ldc);
#endif

  /*
   * SUBROUTINE DSYRK(UPLO,TRANS,N,K,ALPHA,A,LDA,BETA,C,LDC)
   *
   *  DSYRK  performs one of the symmetric rank k operations
   *
   *     C := alpha*A*A**T + beta*C,
   *
   *  or
   *
   *     C := alpha*A**T*A + beta*C,
   *
   *  where  alpha and beta  are scalars, C is an  n by n  symmetric matrix
   *  and  A  is an  n by k  matrix in the first case and a  k by n  matrix
   *  in the second case.
   *
   *  Arguments
   *  ==========
   *
   *  UPLO   - CHARACTER*1.
   *           On  entry,   UPLO  specifies  whether  the  upper  or  lower
   *           triangular  part  of the  array  C  is to be  referenced  as
   *           follows:
   *
   *              UPLO = 'U' or 'u'   Only the  upper triangular part of  C
   *                                  is to be referenced.
   *
   *              UPLO = 'L' or 'l'   Only the  lower triangular part of  C
   *                                  is to be referenced.
   *
   *           Unchanged on exit.
   *
   *  TRANS  - CHARACTER*1.
   *           On entry,  TRANS  specifies the operation to be performed as
   *           follows:
   *
   *              TRANS = 'N' or 'n'   C := alpha*A*A**T + beta*C.
   *
   *              TRANS = 'T' or 't'   C := alpha*A**T*A + beta*C.
   *
   *              TRANS = 'C' or 'c'   C := alpha*A**T*A + beta*C.
   *
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry,  N specifies the order of the matrix C.  N must be
   *           at least zero.
   *           Unchanged on exit.
   *
   *  K      - INTEGER.
   *           On entry with  TRANS = 'N' or 'n',  K  specifies  the number
   *           of  columns   of  the   matrix   A,   and  on   entry   with
   *           TRANS = 'T' or 't' or 'C' or 'c',  K  specifies  the  number
   *           of rows of the matrix  A.  K must be at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry, ALPHA specifies the scalar alpha.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, ka ), where ka is
   *           k  when  TRANS = 'N' or 'n',  and is  n  otherwise.
   *           Before entry with  TRANS = 'N' or 'n',  the  leading  n by k
   *           part of the array  A  must contain the matrix  A,  otherwise
   *           the leading  k by n  part of the array  A  must contain  the
   *           matrix A.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in  the  calling  (sub)  program.   When  TRANS = 'N' or 'n'
   *           then  LDA must be at least  max( 1, n ), otherwise  LDA must
   *           be at least  max( 1, k ).
   *           Unchanged on exit.
   *
   *  BETA   - DOUBLE PRECISION.
   *           On entry, BETA specifies the scalar beta.
   *           Unchanged on exit.
   *
   *  C      - DOUBLE PRECISION array of DIMENSION ( LDC, n ).
   *           Before entry  with  UPLO = 'U' or 'u',  the leading  n by n
   *           upper triangular part of the array C must contain the upper
   *           triangular part  of the  symmetric matrix  and the strictly
   *           lower triangular part of C is not referenced.  On exit, the
   *           upper triangular part of the array  C is overwritten by the
   *           upper triangular part of the updated matrix.
   *           Before entry  with  UPLO = 'L' or 'l',  the leading  n by n
   *           lower triangular part of the array C must contain the lower
   *           triangular part  of the  symmetric matrix  and the strictly
   *           upper triangular part of C is not referenced.  On exit, the
   *           lower triangular part of the array  C is overwritten by the
   *           lower triangular part of the updated matrix.
   *
   *  LDC    - INTEGER.
   *           On entry, LDC specifies the first dimension of C as declared
   *           in  the  calling  (sub)  program.   LDC  must  be  at  least
   *           max( 1, n ).
   *           Unchanged on exit.
   *
   */
#if defined(__essl__)
   void ssyrk(char* uplo, char* trans, blas_long* N, blas_long* K, 
              float* alpha, float* A, blas_long* lda, float* beta, float* C, blas_long* ldc);
#else
   void ssyrk_(char* uplo, char* trans, blas_long* N, blas_long* K, 
              float* alpha, float* A, blas_long* lda, float* beta, float* C, blas_long* ldc);
#endif

  
  /*     
   * SUBROUTINE DTRMM(SIDE,UPLO,TRANSA,DIAG,M,N,ALPHA,A,LDA,B,LDB)
   *
   *  DTRMM  performs one of the matrix-matrix operations
   *
   *     B := alpha*op( A )*B,   or   B := alpha*B*op( A ),
   *
   *  where  alpha  is a scalar,  B  is an m by n matrix,  A  is a unit, or
   *  non-unit,  upper or lower triangular matrix  and  op( A )  is one  of
   *
   *     op( A ) = A   or   op( A ) = A'.
   *
   *  Arguments
   *  ==========
   *
   *  SIDE   - CHARACTER*1.
   *           On entry,  SIDE specifies whether  op( A ) multiplies B from
   *           the left or right as follows:
   *
   *              SIDE = 'L' or 'l'   B := alpha*op( A )*B.
   *
   *              SIDE = 'R' or 'r'   B := alpha*B*op( A ).
   *
   *           Unchanged on exit.
   *
   *  UPLO   - CHARACTER*1.
   *           On entry, UPLO specifies whether the matrix A is an upper or
   *           lower triangular matrix as follows:
   *
   *              UPLO = 'U' or 'u'   A is an upper triangular matrix.
   *
   *              UPLO = 'L' or 'l'   A is a lower triangular matrix.
   *
   *           Unchanged on exit.
   *
   *  TRANSA - CHARACTER*1.
   *           On entry, TRANSA specifies the form of op( A ) to be used in
   *           the matrix multiplication as follows:
   *
   *              TRANSA = 'N' or 'n'   op( A ) = A.
   *
   *              TRANSA = 'T' or 't'   op( A ) = A'.
   *
   *              TRANSA = 'C' or 'c'   op( A ) = A'.
   *
   *           Unchanged on exit.
   *
   *  DIAG   - CHARACTER*1.
   *           On entry, DIAG specifies whether or not A is unit triangular
   *           as follows:
   *
   *              DIAG = 'U' or 'u'   A is assumed to be unit triangular.
   *
   *              DIAG = 'N' or 'n'   A is not assumed to be unit
   *                                  triangular.
   *
   *           Unchanged on exit.
   *
   *  M      - INTEGER.
   *           On entry, M specifies the number of rows of B. M must be at
   *           least zero.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the number of columns of B.  N must be
   *           at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry,  ALPHA specifies the scalar  alpha. When  alpha is
   *           zero then  A is not referenced and  B need not be set before
   *           entry.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, k ), where k is m
   *           when  SIDE = 'L' or 'l'  and is  n  when  SIDE = 'R' or 'r'.
   *           Before entry  with  UPLO = 'U' or 'u',  the  leading  k by k
   *           upper triangular part of the array  A must contain the upper
   *           triangular matrix  and the strictly lower triangular part of
   *           A is not referenced.
   *           Before entry  with  UPLO = 'L' or 'l',  the  leading  k by k
   *           lower triangular part of the array  A must contain the lower
   *           triangular matrix  and the strictly upper triangular part of
   *           A is not referenced.
   *           Note that when  DIAG = 'U' or 'u',  the diagonal elements of
   *           A  are not referenced either,  but are assumed to be  unity.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program.  When  SIDE = 'L' or 'l'  then
   *           LDA  must be at least  max( 1, m ),  when  SIDE = 'R' or 'r'
   *           then LDA must be at least max( 1, n ).
   *           Unchanged on exit.
   *
   *  B      - DOUBLE PRECISION array of DIMENSION ( LDB, n ).
   *           Before entry,  the leading  m by n part of the array  B must
   *           contain the matrix  B,  and  on exit  is overwritten  by the
   *           transformed matrix.
   *
   *  LDB    - INTEGER.
   *           On entry, LDB specifies the first dimension of B as declared
   *           in  the  calling  (sub)  program.   LDB  must  be  at  least
   *           max( 1, m ).
   *           Unchanged on exit.
   */
#if defined(__essl__)
   void strmm(char* side, char* uplo, char* transA, char* diag,
 			  blas_long* M, blas_long* N, float* alpha, float* A, blas_long* lda, float* B, blas_long* ldb);
#else
   void strmm_(char* side, char* uplo, char* transA, char* diag,
 			  blas_long* M, blas_long* N, float* alpha, float* A, blas_long* lda, float* B, blas_long* ldb);
#endif


  /*
   * SUBROUTINE DTRSM(SIDE,UPLO,TRANSA,DIAG,M,N,ALPHA,A,LDA,B,LDB)
   *
   *  DTRSM  solves one of the matrix equations
   *     op( A )*X = alpha*B,   or   X*op( A ) = alpha*B,
   *
   *  where alpha is a scalar, X and B are m by n matrices, A is a unit, or
   *  non-unit,  upper or lower triangular matrix  and  op( A )  is one  of
   *
   *     op( A ) = A   or   op( A ) = A'.
   *
   *  The matrix X is overwritten on B.
   *
   *  Arguments
   *  ==========
   *  SIDE   - CHARACTER*1.
   *           On entry, SIDE specifies whether op( A ) appears on the left
   *           or right of X as follows:
   *              SIDE = 'L' or 'l'   op( A )*X = alpha*B.
   *              SIDE = 'R' or 'r'   X*op( A ) = alpha*B.
   *           Unchanged on exit.
   *
   *  UPLO   - CHARACTER*1.
   *           On entry, UPLO specifies whether the matrix A is an upper or
   *           lower triangular matrix as follows:
   *              UPLO = 'U' or 'u'   A is an upper triangular matrix.
   *              UPLO = 'L' or 'l'   A is a lower triangular matrix.
   *           Unchanged on exit.
   *
   *  TRANSA - CHARACTER*1.
   *           On entry, TRANSA specifies the form of op( A ) to be used in
   *           the matrix multiplication as follows:
   *              TRANSA = 'N' or 'n'   op( A ) = A.
   *              TRANSA = 'T' or 't'   op( A ) = A'.
   *              TRANSA = 'C' or 'c'   op( A ) = A'.
   *           Unchanged on exit.
   *
   *  DIAG   - CHARACTER*1.
   *           On entry, DIAG specifies whether or not A is unit triangular
   *           as follows:
   *              DIAG = 'U' or 'u'   A is assumed to be unit triangular.
   *              DIAG = 'N' or 'n'   A is not assumed to be unit
   *                                  triangular.
   *           Unchanged on exit.
   *
   *  M      - INTEGER.
   *           On entry, M specifies the number of rows of B. M must be at
   *           least zero.
   *           Unchanged on exit.
   *
   *  N      - INTEGER.
   *           On entry, N specifies the number of columns of B.  N must be
   *           at least zero.
   *           Unchanged on exit.
   *
   *  ALPHA  - DOUBLE PRECISION.
   *           On entry,  ALPHA specifies the scalar  alpha. When  alpha is
   *           zero then  A is not referenced and  B need not be set before
   *           entry.
   *           Unchanged on exit.
   *
   *  A      - DOUBLE PRECISION array of DIMENSION ( LDA, k ), where k is m
   *           when  SIDE = 'L' or 'l'  and is  n  when  SIDE = 'R' or 'r'.
   *           Before entry  with  UPLO = 'U' or 'u',  the  leading  k by k
   *           upper triangular part of the array  A must contain the upper
   *           triangular matrix  and the strictly lower triangular part of
   *           A is not referenced.
   *           Before entry  with  UPLO = 'L' or 'l',  the  leading  k by k
   *           lower triangular part of the array  A must contain the lower
   *           triangular matrix  and the strictly upper triangular part of
   *           A is not referenced.
   *           Note that when  DIAG = 'U' or 'u',  the diagonal elements of
   *           A  are not referenced either,  but are assumed to be  unity.
   *           Unchanged on exit.
   *
   *  LDA    - INTEGER.
   *           On entry, LDA specifies the first dimension of A as declared
   *           in the calling (sub) program.  When  SIDE = 'L' or 'l'  then
   *           LDA  must be at least  max( 1, m ),  when  SIDE = 'R' or 'r'
   *           then LDA must be at least max( 1, n ).
   *           Unchanged on exit.
   *
   *  B      - DOUBLE PRECISION array of DIMENSION ( LDB, n ).
   *           Before entry,  the leading  m by n part of the array  B must
   *           contain  the  right-hand  side  matrix  B,  and  on exit  is
   *           overwritten by the solution matrix  X.
   *
   *  LDB    - INTEGER.
   *           On entry, LDB specifies the first dimension of B as declared
   *           in  the  calling  (sub)  program.   LDB  must  be  at  least
   *           max( 1, m ).
   *           Unchanged on exit.
   */
#if defined(__essl__)
   void strsm(char* side, char* uplo, char* transA, char* diag, blas_long* M, blas_long* N,
 			  float* alpha, float* A, blas_long* lda,
 			  float* B, blas_long* ldb);
#else
   void strsm_(char* side, char* uplo, char* transA, char* diag, blas_long* M, blas_long* N,
 			  float* alpha, float* A, blas_long* lda,
 			  float* B, blas_long* ldb);
#endif

#ifdef __cplusplus          
}
#endif

/*----------------------------------------------------------------*/

void error_missing_blas() {
  printf("GML was built without BLAS library.\n");
  printf("Check build settings in system_setting.mk, including definition of BLASLIB variable\n");
  fflush(stdout);
  exit(1);
}

//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
  
void scale(blas_long n, ElemType alpha, ElemType* x) 
{
#ifdef ENABLE_BLAS
  blas_long incx = 1;
#if defined(__essl__)
  sscal(&n, &alpha, x, &incx);
#else
  sscal_(&n, &alpha, x, &incx);
#endif
#else
  error_missing_blas();
#endif
}

// Copy: Y <- X
void copy(blas_long n, ElemType* x, ElemType* y)
{
#ifdef ENABLE_BLAS
  blas_long incx = 1;
  blas_long incy = 1;
#if defined(__essl__)
  scopy(&n, x, &incx, y, &incy);
#else
  scopy_(&n, x, &incx, y, &incy);
#endif
#else
  error_missing_blas();
#endif
}

// Dot product
ElemType dot_prod(blas_long n, ElemType* x, ElemType* y)
{
#ifdef ENABLE_BLAS
  blas_long incx = 1;
  blas_long incy = 1;
#if defined(__essl__)
  return sdot(&n, x, &incx, y, &incy);
#else
  return sdot_(&n, x, &incx, y, &incy);
#endif
#else
  error_missing_blas();
#endif
}

// Euclidean norm2
ElemType norm2(blas_long n, ElemType* x)
{
#ifdef ENABLE_BLAS
  blas_long incx = 1;
#if defined(__essl__)
  return snrm2(&n, x, &incx);
#else
  return snrm2_(&n, x, &incx);
#endif
#else
  error_missing_blas();
#endif
}

// Sum of absolute value
ElemType abs_sum(blas_long n, ElemType* x)
{
#ifdef ENABLE_BLAS
  blas_long incx = 1;
#if defined(__essl__)
  return sasum(&n, x, &incx);
#else
  return sasum_(&n, x, &incx);
#endif
#else
  error_missing_blas();
#endif
}


//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------
//y = alpha*op(A)*x + beta * y
void matrix_vector_mult(ElemType alpha, ElemType* A, ElemType* x, ElemType beta, ElemType* y, blas_long* dim, blas_long lda, blas_long* offset, int transA)
{
#ifdef ENABLE_BLAS
  char tA = transA?'T':'N';
  blas_long m   = dim[0];
  blas_long n   = dim[1];
  blas_long incx = 1;
  blas_long incy = 1;
  blas_long offsetA = offset[0] + offset[1]*lda;
  blas_long offsetX = offset[2];
  blas_long offsetY = offset[3];
#if defined(__essl__)
  sgemv(&tA, &m, &n,
		 &alpha, A+offsetA, &lda,
		         x+offsetX, &incx,
		 &beta,  y+offsetY, &incy);
#else
  sgemv_(&tA, &m, &n,
		 &alpha, A+offsetA, &lda,
		         x+offsetX, &incx,
		 &beta,  y+offsetY, &incy);
#endif
#else
  error_missing_blas();
#endif
}

//y = alpha*op(A)*x + beta * y
void matrix_vector_mult(ElemType alpha, ElemType* A, ElemType* x, ElemType beta, ElemType* y, blas_long* dim, int transA)
{
#ifdef ENABLE_BLAS
  char tA = transA?'T':'N';
  blas_long m   = dim[0];
  blas_long n   = dim[1];
  blas_long incx = 1;
  blas_long incy = 1;
#if defined(__essl__)
  sgemv(&tA, &m, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
#else
  sgemv_(&tA, &m, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
#endif
#else
  error_missing_blas();
#endif
}

//y = alpha*A*x + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(ElemType alpha, ElemType* A, ElemType* x, ElemType beta, ElemType* y, blas_long* dim)
{
#ifdef ENABLE_BLAS
  char uplo = 'L';
  blas_long m   = dim[0];
  blas_long n   = dim[1];
  blas_long incx = 1;
  blas_long incy = 1;

#if defined(__essl__)
  ssymv(&uplo, &n,
 		 &alpha, A, &m,
 		         x, &incx,
 		 &beta,  y, &incy);
#else
  ssymv_(&uplo, &n,
 		 &alpha, A, &m,
 		         x, &incx,
 		 &beta,  y, &incy);
#endif
#else
  error_missing_blas();
#endif
}


//SUBROUTINE DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(ElemType* A, blas_long ul, ElemType* bx, blas_long lda, int transA)
{
#ifdef ENABLE_BLAS
	char uplo = ul?'U':'L';
	char trnA = transA?'T':'N';
	char diag = 'N';
	blas_long  N  = lda;
	blas_long  incx = 1;
#if defined(__essl__)
	strmv(&uplo, &trnA, &diag, &N, A, &lda, bx, &incx);
#else
	strmv_(&uplo, &trnA, &diag, &N, A, &lda, bx, &incx);
#endif
#else
  error_missing_blas();
#endif
}
// = alpha*x*y**T + A
void rank_one_update(ElemType alpha, ElemType* x, ElemType* y, ElemType* A, blas_long* dim, blas_long* offset, blas_long* inc, blas_long lda)
{
#ifdef ENABLE_BLAS
  blas_long m    = dim[0];
  blas_long n    = dim[1];
  blas_long offsetX = offset[0];
  blas_long offsetY = offset[1];
  blas_long offsetA = offset[0] + offset[1]*lda;
  blas_long incx = inc[0];
  blas_long incy = inc[1];
#if defined(__essl__)
  sger(&m, &n,
		 &alpha, x+offsetX, &incx,
		         y+offsetY, &incy,
		         A+offsetA, &lda);
#else
  sger_(&m, &n,
		 &alpha, x+offsetX, &incx,
		         y+offsetY, &incy,
		         A+offsetA, &lda);
#endif
#else
  error_missing_blas();
#endif
}

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

//-----------------------------------------------------------------
// Simplified interface, thread-safe routine
//-----------------------------------------------------------------

// C = alpha*op(A) * op(B) + beta*C
void matrix_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C, blas_long* dim, 
						blas_long* ld, blas_long* offset, int* trans)
{
#ifdef ENABLE_BLAS
  char transA = (trans[0])?'T':'N';
  char transB = (trans[1])?'T':'N';
  blas_long  m = dim[0];
  blas_long  n = dim[1];
  blas_long  k = dim[2];
  blas_long  lda = ld[0];
  blas_long  ldb = ld[1];
  blas_long  ldc = ld[2];
  blas_long  offsetA = offset[0] + offset[1]*lda;
  blas_long  offsetB = offset[2] + offset[3]*ldb;
  blas_long  offsetC = offset[4] + offset[5]*ldc;
  //printf("call dgemm: %c %c, %d %d %d, %d %d %d\n", transA, transB, m, n, k, lda, ldb, ldc); fflush(stdout);
#if defined(__essl__)
  sgemm(&transA, &transB,
		 &m, &n, &k,
		 &alpha, A+offsetA, &lda,
		         B+offsetB, &ldb,
		 &beta,  C+offsetC, &ldc);
#else
  sgemm_(&transA, &transB, 
		 &m, &n, &k,
		 &alpha, A+offsetA, &lda,
		         B+offsetB, &ldb, 
		 &beta,  C+offsetC, &ldc);
#endif
#else
  error_missing_blas();
#endif
}

// C = alpha*op(A) * op(B) + beta*C
void matrix_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C, blas_long* dim, 
						  blas_long* ld, int* trans)
{
#ifdef ENABLE_BLAS
  char transA = (trans[0])?'T':'N';
  char transB = (trans[1])?'T':'N';
  blas_long  m = dim[0];
  blas_long  n = dim[1];
  blas_long  k = dim[2];
  blas_long  lda = ld[0];
  blas_long  ldb = ld[1];
  blas_long  ldc = ld[2];
  //printf("call dgemm: %c %c, %d %d %d, %d %d %d\n", transA, transB, m, n, k, lda, ldb, ldc); fflush(stdout);
#if defined(__essl__)
  sgemm(&transA, &transB,
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb,
		 &beta,  C, &ldc);
#else
  sgemm_(&transA, &transB, 
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb, 
		 &beta,  C, &ldc);
#endif
#else
  error_missing_blas();
#endif
}

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(ElemType alpha, ElemType* A, ElemType beta, ElemType* C, blas_long* dim, 
                       blas_long* ld, blas_long* offset, bool upper, bool trans)
{
#ifdef ENABLE_BLAS
  char uplo = (upper)?'U':'L'; // upper / lower triangular
  char transA = (trans)?'T':'N';
  blas_long  n = dim[0];
  blas_long  k = dim[1];
  blas_long  lda = ld[0];
  blas_long  ldc = ld[1];
  blas_long  offsetA = offset[0] + offset[1]*lda;
  blas_long  offsetC = offset[2] + offset[3]*ldc;
  //printf("call dsyrk: trans=%c, %d %d %d %d\n", transA, n, k, lda, ldc); fflush(stdout);
#if defined(__essl__)
  ssyrk(&uplo, &transA,
        &n, &k,
        &alpha, A+offsetA, &lda,
        &beta,  C+offsetC, &ldc);
#else
  ssyrk_(&uplo, &transA,
         &n, &k,
         &alpha, A+offsetA, &lda,
         &beta,  C+offsetC, &ldc);
#endif
#else
  error_missing_blas();
#endif
}

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(ElemType alpha, ElemType* A, ElemType beta, ElemType* C, 
                       blas_long* dim, bool upper, bool trans)
{
#ifdef ENABLE_BLAS
  char uplo = (upper)?'U':'L'; // upper / lower triangular
  char transA = (trans)?'T':'N';
  blas_long  n = dim[0];
  blas_long  k = dim[1];
  blas_long  lda = (trans)?k:n;
  blas_long  ldc = n;
  //printf("call dsyrk: trans=%c, %d %d %d %d\n", transA, n, k, lda, ldc); fflush(stdout);
#if defined(__essl__)
  ssyrk(&uplo, &transA,
        &n, &k,
        &alpha, A, &lda,
        &beta,  C, &ldc);
#else
  ssyrk_(&uplo, &transA,
         &n, &k,
         &alpha, A, &lda,
         &beta,  C, &ldc);
#endif
#else
  error_missing_blas();
#endif
}

//-------------------------------------------------------------------------------

// C = alpah*A * B + beta*C
// side = "L", Lower symmetric-triangular data, Non-unit triangular
void sym_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C, blas_long* dim)
{
#ifdef ENABLE_BLAS
  char side = 'L'; // alpha*A*B
  char uplo = 'L'; // Lower triangular
  
  blas_long   m     = dim[0];
  blas_long   n     = dim[1];
#if defined(__essl__)
  ssymm(&side, &uplo,
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#else
  ssymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n, 
		 &beta,  C, &n);
#endif
#else
  error_missing_blas();
#endif
}

void matrix_sym_mult(ElemType* B, ElemType alpha, ElemType* A, ElemType beta, ElemType* C, blas_long* dim)
{
#ifdef ENABLE_BLAS
  char side = 'R'; // alpha*B*A
  char uplo = 'L'; // Lower triangular
  
  blas_long   m     = dim[0];
  blas_long   n     = dim[1];
#if defined(__essl__)
  ssymm(&side, &uplo,
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#else
  ssymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#endif
#else
  error_missing_blas();
#endif
}

//-------------------------------------------------------
// B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(ElemType* A, ElemType* B, blas_long* dim, int tranA)
{
#ifdef ENABLE_BLAS
  char side = 'L'; //alpha * op(A) * B;
  char uplo = dim[2]?'U':'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tran = (tranA)?'T':'N';
  ElemType alpha = 1.0;
  blas_long m = dim[0]; //Rows of B
  blas_long n = dim[1]; //Columns of B
  blas_long lda = m;
  blas_long ldb = n;
#if defined(__essl__)
  strmm(&side, &uplo, &tran, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#else
  strmm_(&side, &uplo, &tran, &diag, 
		 &m, &n, &alpha, A, &lda, B, &ldb);
#endif
#else
  error_missing_blas();
#endif
}
// := alpha*A*op( B ), B is lower-non-unit triangular
void matrix_tri_mult(ElemType* B, ElemType* A, blas_long* dim, int tranB)
{
#ifdef ENABLE_BLAS
  char side = 'R'; //alpha * A * op(B);
  char uplo = dim[2]?'U':'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tTri = (tranB)?'T':'N';
  ElemType alpha = 1.0;
  blas_long m = dim[0]; //Rows of B
  blas_long n = dim[1]; //Columns of B
  blas_long lda = n;
  blas_long ldb = m;
#if defined(__essl__)
  strmm(&side, &uplo, &tTri, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#else
  strmm_(&side, &uplo, &tTri, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#endif
#else
  error_missing_blas();
#endif
}

//------------------------------------------------------------------------
// Solve a lower-triangular, non unit-diagonal triangular matrix equation
// A*x = b
void tri_vector_solve(ElemType* A, ElemType* bx, blas_long* dim, int tranA)
{
#ifdef ENABLE_BLAS
  char uplo = dim[2]?'U':'L';
  char trans= (tranA)?'T':'N';
  char diag = 'N';
  blas_long incx = 1;
  blas_long m = dim[0]; // Leading dimension of A, must be number of rows in A
  blas_long n = dim[1]; // Order of matrix A, must be number of columns in A
#if defined(__essl__)
  strsv(&uplo,  &trans, &diag,
		 &n, A,  &m,
		 bx, &incx);
#else
  strsv_(&uplo,  &trans, &diag,
		 &n, A,  &m,
		 bx, &incx);
#endif
#else
  error_missing_blas();
#endif
}


void tri_matrix_solve(ElemType* A, ElemType* BX, blas_long* dim, int tranA)
{
#ifdef ENABLE_BLAS
	char side = 'L'; //op(A) X = B
	char uplo = dim[2]?'U':'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	blas_long m   = dim[0]; //number of rows in B
	blas_long n   = dim[1]; //number of column in B
	blas_long lda = m;      //first dimension of A
	blas_long ldb = m;      //first dimension of B
	ElemType alpha = 1.0;
#if defined(__essl__)
	strsm(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#else
	strsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#endif
#else
  error_missing_blas();
#endif
}

void matrix_tri_solve(ElemType* BX, ElemType* A, blas_long* dim, int tranA)
{
#ifdef ENABLE_BLAS
	char side = 'R'; //X op(A) = B
	char uplo = dim[2]?'U':'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	blas_long m   = dim[0]; //number of rows in B
	blas_long n   = dim[1]; //number of column in B
	blas_long lda = m;      //first dimension of A
	blas_long ldb = n;      //first dimension of B
	ElemType alpha = 1.0;
#if defined(__essl__)
	strsm(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#else
	strsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#endif
#else
  error_missing_blas();
#endif
}



