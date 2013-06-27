/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

#include <stdio.h>
#include <stdlib.h>

//using namespace std;
#include "wrap_blas.h"

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
#if defined(__bgp__)
  void dscal(long* N, double* alpha, double *X, long* incX);
#else
  void dscal_(long* N, double* alpha, double *X, long* incX);
#endif

  // Vector Copy: y <-x
#if defined(__bgp__)
  void dcopy(long* N, double *X, long* incx, double *Y, long* incy);
#else
  void dcopy_(long* N, double *X, long* incx, double *Y, long* incy);
#endif

  // for dot product
  // DOT Production: r <- beta * r + alpha * x^T * y
#if defined(__bgp__)
  double ddot(long* N, double *X, long* incx, double *Y, long* incy);
#else
  double ddot_(long* N, double *X, long* incx, double *Y, long* incy);
#endif
	
  // for Euclidean norm
#if defined(__bgp__)
  double dnrm2(long* N, double *X, long* incx);
#else
  double dnrm2_(long* N, double *X, long* incx);
#endif

  // SUM: for sum of absolute values
#if defined(__bgp__)
  double dasum(long* N, double *X, long* incx);
#else
  double dasum_(long* N, double *X, long* incx);
#endif
	
  // MAX, for index of max abs value
  // CBLAS_INDEX cblas_idamax(blasint n, double *x, blasint incx)
  
  // SUBROUTINE DAXPY(N,DA,DX,INCX,DY,INCY)
#if defined(__bgp__)
  void  daxpy(long*N, long*da, double*X, long*incx, double*Y,long* incy);
#else
  void  daxpy_(long*N, long*da, double*X, long*incx, double*Y,long* incy); 
#endif

  //------------------------------------------------------------------------
  // Level Two
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  // for matrix vector multiply
  /**
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
#if defined(__bgp__)
  void dgemv(char* trans, long* M, long* N,
			  double* alpha, double  *A, long* lda,
			  double* x, long* incx,
			  double* beta, double* y, long* incy);
#else
  void dgemv_(char* trans, long* M, long* N, 
			  double* alpha, double  *A, long* lda,  
			  double* x, long* incx, 
			  double* beta, double* y, long* incy);
#endif

  //------------------------------------------------------------------------
   

  //------------------------------------------------------------------------
  //void cblas_dsymv(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, blasint N, double alpha, double *A,
  //             blasint lda, double *X, blasint incX, double beta, double *Y, blasint incY);
  // for symmetric matrix vector multiply
  /*  =======
   *
   *  DSYMV  performs the matrix-vector  operation
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
#if defined(__bgp__)
  void dsymv(char* uplo, long* N, double* alpha, double* A, long* lda,
			  double* X, long* incx, double* beta, double* Y, long* incy);
#else
  void dsymv_(char* uplo, long* N, double* alpha, double* A, long* lda,
			  double* X, long* incx, double* beta, double* Y, long* incy);
#endif

  //----------------------------------------------------------------------

  /*
	  SUBROUTINE DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
	*     .. Scalar Arguments ..
	   INTEGER INCX,LDA,N
	   CHARACTER DIAG,TRANS,UPLO
	*     ..
	*     .. Array Arguments ..
	   DOUBLE PRECISION A(LDA,*),X(*)
	*     ..
	*
	*  Purpose
	*  =======
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
	*
	*/
  //DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
#if defined(__bgp__)
  void dtrmv(char* uplo, char* trans, char* diag, long* N, double* A, long* lda, double* X, long* incx);
#else
  void dtrmv_(char* uplo, char* trans, char* diag, long* N, double* A, long* lda, double* X, long* incx);
#endif

  //------------------------------------------------------------------------
  // void cblas_dger(const enum CBLAS_ORDER order, const int M, const int N,
  //              const double alpha, const double *X, const int incX,
  //              const double *Y, const int incY, double *A, const int lda);
  /**
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
#if defined(__bgp__)
  void dger(long* M, long* N,
			  double* alpha, double* x, long* incX,
			  double* Y, long* incY,
			  double* A, long* lda);
#else
  void dger_(long* M, long* N,
			  double* alpha, double* x, long* incX,
			  double* Y, long* incY,
			  double* A, long* lda);
#endif

  //------------------------------------------------------------------------

  //------------------------------------------------------------------------
  // for solving triangular matrix problems
  /**
   * SUBROUTINE DTRSV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
   *
   *  Purpose
   *  =======
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
#if defined(__bgp__)
  void dtrsv(char* uplo, char* trans, char* diag,
			  long* N, double* A, long* lda,
			  double* X, long* incx);
#else
  void dtrsv_(char* uplo, char* trans, char* diag, 
			  long* N, double* A, long* lda, 
			  double* X, long* incx);
#endif

  //void cblas_dtrsv(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, enum CBLAS_TRANSPOSE TransA, 
  //				   enum CBLAS_DIAG Diag, blasint N, double *A, blasint lda, double *X, blasint incX);
  //------------------------------------------------------------------------

	   
  // for performs the symmetric rank 1 operation A := alpha*x*x' + A
  // void cblas_dsyr(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, blasint N, double alpha, 
  //		           double *X, blasint incX, double *A, blasint lda);
  
  // for	performs the symmetric rank 2 operation A := alpha*x*y' + alpha*y*x' + A
  // void cblas_dsyr2(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, blasint N, double alpha, double *X,
  //            blasint incX, double *Y, blasint incY, double *A, blasint lda);




  //------------------------------------------------------------------------
  // Level Three Routings
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  /**
   * SUBROUTINE DGEMM(TRANSA,TRANSB,M,N,K,ALPHA,A,LDA,B,LDB,BETA,C,LDC)
   *
   *  Purpose
   *  =======
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
#if defined(__bgp__)
  void dgemm(char* transA, char* transB, long* M, long* N, long* K,
			  double* alpha, double* A, long* lda, 
			  double* B, long* ldb, 
			  double* beta, double* C, long* ldc);
#else
   void dgemm_(char* transA, char* transB, long* M, long* N, long* K,
 			  double* alpha, double* A, long* lda,
 			  double* B, long* ldb,
 			  double* beta, double* C, long* ldc);
#endif

  //void cblas_dgemm(enum CBLAS_ORDER Order, enum CBLAS_TRANSPOSE TransA, 
  //enum CBLAS_TRANSPOSE TransB, blasint M, blasint N, blasint K,
  //double alpha, double *A, blasint lda, double *B, blasint ldb, 
  //double beta, double *C, blasint ldc);
  //------------------------------------------------------------------------


  //------------------------------------------------------------------------
  /**
   * SUBROUTINE DSYMM(SIDE,UPLO,M,N,ALPHA,A,LDA,B,LDB,BETA,C,LDC)
   *  Purpose
   *  =======
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
#if defined(__bgp__)
   void dsymm(char* side, char* uplo, long* M, long* N,
 			  double* alpha, double* A, long* lda,
 			  double* B, long* ldb,
 			  double* beta, double* C, long* ldc);
#else
   void dsymm_(char* side, char* uplo, long* M, long* N,
 			  double* alpha, double* A, long* lda,
 			  double* B, long* ldb,
 			  double* beta, double* C, long* ldc);
#endif

  // void cblas_dsymm(enum CBLAS_ORDER Order, enum CBLAS_SIDE Side, enum CBLAS_UPLO Uplo, 
  //                  blasint M, blasint N, double alpha, double *A, blasint lda, 
  //                  double *B, blasint ldb, double beta, double *C, blasint ldc);
  //------------------------------------------------------------------------
  
  /*     SUBROUTINE DTRMM(SIDE,UPLO,TRANSA,DIAG,M,N,ALPHA,A,LDA,B,LDB)
   *  Purpose
   *  =======
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
  //DTRMM(SIDE,UPLO,TRANSA,DIAG,M,N,ALPHA,A,LDA,B,LDB)
#if defined(__bgp__)
   void dtrmm(char* side, char* uplo, char* transA, char* diag,
 			  long* M, long* N, double* alpha, double* A, long* lda, double* B, long* ldb);
#else
   void dtrmm_(char* side, char* uplo, char* transA, char* diag,
 			  long* M, long* N, double* alpha, double* A, long* lda, double* B, long* ldb);
#endif


  //------------------------------------------------------------------------
  /*
   * SUBROUTINE DTRSM(SIDE,UPLO,TRANSA,DIAG,M,N,ALPHA,A,LDA,B,LDB)
   *  Purpose
   *  =======
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
#if defined(__bgp__)
   void dtrsm(char* side, char* uplo, char* transA, char* diag, long* M, long* N,
 			  double* alpha, double* A, long* lda,
 			  double* B, long* ldb);
#else
   void dtrsm_(char* side, char* uplo, char* transA, char* diag, long* M, long* N,
 			  double* alpha, double* A, long* lda,
 			  double* B, long* ldb);
#endif

  // for solving triangular matrix with multiple right hand sides
  // void cblas_dtrsm(enum CBLAS_ORDER Order, enum CBLAS_SIDE Side, enum CBLAS_UPLO Uplo, 
  //			 enum CBLAS_TRANSPOSE TransA, enum CBLAS_DIAG Diag, blasint M, blasint N, 
  //			 double alpha, double *A, blasint lda, double *B, blasint ldb);

#ifdef __cplusplus          
}
#endif

/*----------------------------------------------------------------*/


//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
  
void scale(long n, double alpha, double* x) 
{
#ifdef ENABLE_BLAS
  long incx = 1;
#if defined(__bgp__)
  dscal(&n, &alpha, x, &incx);
#else
  dscal_(&n, &alpha, x, &incx);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}
// Copy: Y <- X
void copy(long n, double* x, double* y)
{
#ifdef ENABLE_BLAS
  long incx = 1;
  long incy = 1;
#if defined(__bgp__)
  dcopy(&n, x, &incx, y, &incy);
#else
  dcopy_(&n, x, &incx, y, &incy);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}
// Dot product
double dot_prod(long n, double* x, double* y)
{
#ifdef ENABLE_BLAS
  long incx = 1;
  long incy = 1;
#if defined(__bgp__)
  return ddot(&n, x, &incx, y, &incy);
#else
  return ddot_(&n, x, &incx, y, &incy);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

// Euclidean norm2
double norm2(long n, double* x)
{
#ifdef ENABLE_BLAS
  long incx = 1;
#if defined(__bgp__)
  return dnrm2(&n, x, &incx);
#else
  return dnrm2_(&n, x, &incx);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

// Sum of absolute value
double abs_sum(long n, double* x)
{
#ifdef ENABLE_BLAS
  long incx = 1;
#if defined(__bgp__)
  return dasum(&n, x, &incx);
#else
  return dasum_(&n, x, &incx);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}


//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------
//y = alpha*op(A)*x + beta * y
void matrix_vector_mult(double* A, double* x, double* y, long* dim, double* scale, int transA)
{
#ifdef ENABLE_BLAS
  char tA = transA?'T':'N';

  double alpha = scale[0];
  double beta  = scale[1];
  long m   = dim[0];
  long n   = dim[1];
  long incx = 1;
  long incy = 1;
#if defined(__bgp__)
  dgemv(&tA, &m, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
#else
  dgemv_(&tA, &m, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

//y = alpha*A*x + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double* A, double* x, double* y, long* dim, double* scale)
{
#ifdef ENABLE_BLAS
  char uplo = 'L';
  double alpha = scale[0];
  double beta  = scale[1];
  long m   = dim[0];
  long n   = dim[1];
  long incx = 1;
  long incy = 1;

  //printf("dim: %i %i\n", m, n);
  //printf("%f %f %f  \n", A[2], A[5], A[7]);
  //printf("%f %f %f %f \n", x[0], x[1], x[2], x[3]);
#if defined(__bgp__)
  dsymv(&uplo, &n,
 		 &alpha, A, &m,
 		         x, &incx,
 		 &beta,  y, &incy);
#else
  dsymv_(&uplo, &n,
 		 &alpha, A, &m,
 		         x, &incx,
 		 &beta,  y, &incy);
#endif
  //printf("%f %f %f %f \n", y[0], y[1], y[2], y[3]);
  //fflush(stdout);
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

//SUBROUTINE DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(double* A, long ul, double* bx, long lda, int transA)
{
#ifdef ENABLE_BLAS
	char uplo = ul?'U':'L';
	char trnA = transA?'T':'N';
	char diag = 'N';
	long  N  = lda;
	long  incx = 1;
#if defined(__bgp__)
	dtrmv(&uplo, &trnA, &diag, &N, A, &lda, bx, &incx);
#else
	dtrmv_(&uplo, &trnA, &diag, &N, A, &lda, bx, &incx);
#endif
	//dtrmv_(char* uplo, char* trans, char* diag, int N, double* A, int lda, double* X, int incx);
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

// A = alpha*x*y**T + A
void rank_one_update(double* A, double* x, double* y, long* dim, long* offset, long* inc, long lda, double alpha)
{
  long m    = dim[0];
  long n    = dim[1];
  long offx = offset[0];
  long offy = offset[1];
  double* xStart = x + offx;
  double* yStart = y + offy;
  long incx = inc[0];
  long incy = inc[1];
#if defined(__bgp__)
  dger(&m, &n,
		 &alpha, xStart, &incx,
		         yStart, &incy,
		         A, &lda);
#else
  dger_(&m, &n,
		 &alpha, xStart, &incx,
		         yStart, &incy,
		         A, &lda);
#endif
}

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

//-----------------------------------------------------------------
// Simplified interface, thread-safe routine
//-----------------------------------------------------------------

// C = alpha*op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, long* dim, 
						  long* ld, double* scale, int* trans)
{
  char transA = (trans[0])?'T':'N';
  char transB = (trans[1])?'T':'N';
  long  m = dim[0];
  long  n = dim[1];
  long  k = dim[2];
  long  lda = ld[0];
  long  ldb = ld[1];
  long  ldc = ld[2];
  double alpha = scale[0];
  double beta  = scale[1];
  //printf("call dgemm: dim %d %d %d, ld %d %d %d, %c %c\n", m, n, k, lda, ldb, ldc, transA, transB); fflush(stdout);
#if defined(__bgp__)
  dgemm(&transA, &transB,
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb,
		 &beta,  C, &ldc);
#else
  dgemm_(&transA, &transB, 
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb, 
		 &beta,  C, &ldc);
#endif
}

// C = alpha*op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, long* dim, 
						  double* scale, int* trans)
{
#ifdef ENABLE_BLAS
  char transA = (trans[0])?'T':'N';
  char transB = (trans[1])?'T':'N';
  long  m = dim[0];
  long  n = dim[1];
  long  k = dim[2];
  long  lda = (trans[0])?k:m;
  long  ldb = (trans[1])?n:k;
  long  ldc = m;
  double alpha = scale[0];
  double beta  = scale[1];
  //printf("call dgemm: %d %d %d, %c %c\n", m, n, k, transA, transB); fflush(stdout);
#if defined(__bgp__)
  dgemm(&transA, &transB,
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb,
		 &beta,  C, &ldc);
#else
  dgemm_(&transA, &transB, 
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb, 
		 &beta,  C, &ldc);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

//-------------------------------------------------------------------------------

// C = alpah*A * B + beta*C
// side = "L", Lower symmetric-triangular data, Non-unit triangular
void sym_matrix_mult(double* A, double* B, double* C, long* dim, double* scale)
{
#ifdef ENABLE_BLAS
  char side = 'L'; // alpha*A*B
  char uplo = 'L'; // Lower triangular
  
  long   m     = dim[0];
  long   n     = dim[1];
  double alpha = scale[0];
  double beta  = scale[1];
#if defined(__bgp__)
  dsymm(&side, &uplo,
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#else
  dsymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n, 
		 &beta,  C, &n);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

void matrix_sym_mult(double* B, double* A, double* C, long* dim, double* scale)
{
#ifdef ENABLE_BLAS
  char side = 'R'; // alpha*B*A
  char uplo = 'L'; // Lower triangular
  
  long   m     = dim[0];
  long   n     = dim[1];
  double alpha = scale[0];
  double beta  = scale[1];
#if defined(__bgp__)
  dsymm(&side, &uplo,
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#else
  dsymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

//-------------------------------------------------------
// B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(double* A, double* B, long* dim, int tranA)
{
#ifdef ENABLE_BLAS
  char side = 'L'; //alpha * op(A) * B;
  char uplo = dim[2]?'U':'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tran = (tranA)?'T':'N';
  double alpha = 1.0;
  long m = dim[0]; //Rows of B
  long n = dim[1]; //Columns of B
  long lda = m;
  long ldb = n;
#if defined(__bgp__)
  dtrmm(&side, &uplo, &tran, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#else
  dtrmm_(&side, &uplo, &tran, &diag, 
		 &m, &n, &alpha, A, &lda, B, &ldb);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}
// B := alpha*A*op( B ), B is lower-non-unit triangular
void matrix_tri_mult(double* B, double* A, long* dim, int tranB)
{
#ifdef ENABLE_BLAS
  char side = 'R'; //alpha * A * op(B);
  char uplo = dim[2]?'U':'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tTri = (tranB)?'T':'N';
  double alpha = 1.0;
  long m = dim[0]; //Rows of B
  long n = dim[1]; //Columns of B
  long lda = n;
  long ldb = m;
#if defined(__bgp__)
  dtrmm(&side, &uplo, &tTri, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#else
  dtrmm_(&side, &uplo, &tTri, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

//------------------------------------------------------------------------
// Solve a lower-triangular, non unit-diagonal triangular matrix equation
// A*x = b
void tri_vector_solve(double* A, double* bx, long* dim, int tranA)
{
#ifdef ENABLE_BLAS
  char uplo = dim[2]?'U':'L';
  char trans= (tranA)?'T':'N';
  char diag = 'N';
  long incx = 1;
  long m = dim[0]; // Leading dimension of A, must be number of rows in A
  long n = dim[1]; // Order of matrix A, must be number of columns in A
#if defined(__bgp__)
  dtrsv(&uplo,  &trans, &diag,
		 &n, A,  &m,
		 bx, &incx);
#else
  dtrsv_(&uplo,  &trans, &diag,
		 &n, A,  &m,
		 bx, &incx);
#endif
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}

void tri_matrix_solve(double* A, double* BX, long* dim, int tranA)
{
#ifdef ENABLE_BLAS
	char side = 'L'; //op(A) X = B
	char uplo = dim[2]?'U':'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	long m   = dim[0]; //number of rows in B
	long n   = dim[1]; //number of column in B
	long lda = m;      //first dimension of A
	long ldb = m;      //first dimension of B
	double alpha = 1.0;
#if defined(__bgp__)
	dtrsm(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#else
	dtrsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#endif
//void dtrsm_(char* side, char* uplo, char* transA, char* diag, long* M, long* N,
//			  double* alpha, double* A, long* lda,
//			  double* B, long* ldb);
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}


void matrix_tri_solve(double* BX, double* A, long* dim, int tranA)
{
#ifdef ENABLE_BLAS
	char side = 'R'; //X op(A) = B
	char uplo = dim[2]?'U':'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	long m   = dim[0]; //number of rows in B
	long n   = dim[1]; //number of column in B
	long lda = m;      //first dimension of A
	long ldb = n;      //first dimension of B
	double alpha = 1.0;
#if defined(__bgp__)
	dtrsm(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#else
	dtrsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
#endif
//void dtrsm_(char* side, char* uplo, char* transA, char* diag, long* M, long* N,
//			  double* alpha, double* A, long* lda,
//			  double* B, long* ldb);
#else
  printf("BLAS is not added in GML build.\n");
  printf("Uncomment the line: add_blas = yes in system_setting.mk, and make sure blas lib and path names are correct\n");
  fflush(stdout);
  exit(1);
#endif
}


//-----------------------------------------------------------------------------
//y = A*x
/* void matrix_vector_mult(double* A, double* x, double* y, int m, int n) */
/* { */
/*   char   transA = 'N'; */
/*   double alpha = 1.0; */
/*   double beta  = 0.0; */
/*   long incx = 1; */
/*   long incy = 1; */

/*   dgemv_(&transA, &m, &n, */
/* 		 &alpha, A, &m, */
/* 		         x, &incx,  */
/* 		 &beta,  y, &incy); */
/* } */


//------------------------------------------------------------------------
// Other tools
//------------------------------------------------------------------------

void print_matrix_data(double* M, long m, long n) 
{
  long r, c;
  printf("Matrix %ld x %ld:\n", m, n);
  for (r=0; r<m; r++)
	{
	  for (c=0; c<n; c++)
		printf("%2.3f \t | ", M[c*n+r]); //data stored in column major
	  printf("\n");
	}
}
//
void print_matrix(char* st, double* M, long m, long n) 
{
  printf("%s", st);
  print_matrix_data(M, m, n);
}
//------------------------------------------------------------------------
void c_mat_mat_mult(double* A, double* B, double* C, long M, long N, long K)
{
  long r, c, k;
 
  for (r=0; r<M; r++)
	{
	  long idx_2 =0;
	  for (c=0; c<N; c++) 
		{
		  double v = 0.0;
		  long idx_1 = r;

		  for (k=0; k<K; k++) 
			{
			  v     += A[idx_1] * B[idx_2];
			  idx_1 += M;
			  idx_2 += 1;
			}
		  C[c*M+r] = v;
		}
	}
}
