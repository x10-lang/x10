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
  void dscal_(int* N, double* alpha, double *X, int* incX);

  // Vector Copy: y <-x
  void dcopy_(int* N, double *X, int* incx, double *Y, int* incy);

  // for dot product
  // DOT Production: r <- beta * r + alpha * x^T * y
  double ddot_(int* N, double *X, int* incx, double *Y, int* incy);
	
  // for Euclidean norm
  double dnrm2_ (int* N, double *X, int* incx);
	
  // SUM: for sum of absolute values
  double dasum_ (int* N, double *X, int* incx);
	
  // MAX, for index of max abs value
  // CBLAS_INDEX cblas_idamax(blasint n, double *x, blasint incx)
  
  // SUBROUTINE DAXPY(N,DA,DX,INCX,DY,INCY)
  void  daxpy_(int*N, int*da, double*X, int*incx, double*Y,int* incy); 

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
  void dgemv_(char* trans, int* M, int* N, 
			  double* alpha, double  *A, int* lda,  
			  double* x, int* incx, 
			  double* beta, double* y, int* incy);
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
  void dsymv_(char* uplo, int* N, double* alpha, double* A, int* lda,
			  double* X, int* incx, double* beta, double* Y, int* incy);
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
  void dtrmv_(char* uplo, char* trans, char* diag, int* N, double* A, int* lda, double* X, int* incx);

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
  void dtrsv_(char* uplo, char* trans, char* diag, 
			  int* N, double* A, int* lda, 
			  double* X, int* incx);
  //void cblas_dtrsv(enum CBLAS_ORDER order, enum CBLAS_UPLO Uplo, enum CBLAS_TRANSPOSE TransA, 
  //				   enum CBLAS_DIAG Diag, blasint N, double *A, blasint lda, double *X, blasint incX);
  //------------------------------------------------------------------------


  // for performs the rank 1 operation A := alpha*x*y' + A,
  // Checkout void cblas_dger (enum CBLAS_ORDER order, blasint M, blasint N, double  alpha, 
  //				   double *X, blasint incX, double *Y, blasint incY, double *A, blasint lda);
	   
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
  void dgemm_(char* transA, char* transB, int* M, int* N, int* K,
			  double* alpha, double* A, int* lda, 
			  double* B, int* ldb, 
			  double* beta, double* C, int* ldc);
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
  void dsymm_(char* side, char* uplo, int* M, int* N, 
			  double* alpha, double* A, int* lda, 
			  double* B, int* ldb, 
			  double* beta, double* C, int* ldc);
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
  void dtrmm_(char* side, char* uplo, char* transA, char* diag, 
			  int* M, int* N, double* alpha, double* A, int* lda, double* B, int* ldb);


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
  void dtrsm_(char* side, char* uplo, char* transA, char* diag, int* M, int* N,
			  double* alpha, double* A, int* lda, 
			  double* B, int* ldb);
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
  
void scale(int n, double alpha, double* x) 
{
  int incx = 1;
  dscal_(&n, &alpha, x, &incx);
}
// Copy: Y <- X
void copy(int n, double* x, double* y)
{
  int incx = 1;
  int incy = 1;
  dcopy_(&n, x, &incx, y, &incy);
}
// Dot product
double dot_prod(int n, double* x, double* y)
{
  int incx = 1;
  int incy = 1;
  return ddot_(&n, x, &incx, y, &incy);
}

// Euclidean norm2
double norm2(int n, double* x)
{
  int incx = 1;
  return dnrm2_(&n, x, &incx);
}

// Sum of absolute value
double abs_sum(int n, double* x)
{
  int incx = 1;
  return dasum_(&n, x, &incx);
}


//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------
//y = alpha*op(A)*x + beta * y
void matrix_vector_mult(double* A, double* x, double* y, int* dim, double* scale, int transA)
{
  char tA = transA?'T':'N';

  double alpha = scale[0];
  double beta  = scale[1];
  int m    = dim[0];
  int n    = dim[1];
  int incx = 1;
  int incy = 1;
  dgemv_(&tA, &m, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
}

//y = alpha*A*x + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double* A, double* x, double* y, int* dim, double* scale)
{
  char uplo = 'L';
  double alpha = scale[0];
  double beta  = scale[1];
  int m    = dim[0];
  int n    = dim[1];
  int incx = 1;
  int incy = 1;

  //printf("dim: %i %i\n", m, n);
  //printf("%f %f %f  \n", A[2], A[5], A[7]);
  //printf("%f %f %f %f \n", x[0], x[1], x[2], x[3]);

  dsymv_(&uplo, &n,
		 &alpha, A, &m,
		         x, &incx,
		 &beta,  y, &incy);
  //printf("%f %f %f %f \n", y[0], y[1], y[2], y[3]);
  fflush(stdout);

}

//SUBROUTINE DTRMV(UPLO,TRANS,DIAG,N,A,LDA,X,INCX)
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(double* A, double* bx, int lda, int transA)
{
	char uplo = 'L';
	char trnA = transA?'T':'N';
	char diag = 'N';
	int  N  = lda;
	int  incx = 1;

	dtrmv_(&uplo, &trnA, &diag, &N, A, &lda, bx, &incx);
	//dtrmv_(char* uplo, char* trans, char* diag, int N, double* A, int lda, double* X, int incx);

}
//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

//-----------------------------------------------------------------
// Simplified interface, thread-safe routine
//-----------------------------------------------------------------

// C = alpha*op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, int* dim, 
						  double* scale, int* trans)
{
  char transA = (trans[0])?'T':'N';
  char transB = (trans[1])?'T':'N';
  int  m = dim[0];
  int  n = dim[1];
  int  k = dim[2];
  int  lda = (trans[0])?k:m;
  int  ldb = (trans[1])?n:k;
  int  ldc = m;
  double alpha = scale[0];
  double beta  = scale[1];
  //printf("call dgemm: %d %d %d, %c %c\n", m, n, k, transA, transB); fflush(stdout);
  dgemm_(&transA, &transB, 
		 &m, &n, &k,
		 &alpha, A, &lda,
		         B, &ldb, 
		 &beta,  C, &ldc);
}

//-------------------------------------------------------------------------------

// C = alpah*A * B + beta*C
// side = "L", Lower symmetric-triangular data, Non-unit triangular
void sym_matrix_mult(double* A, double* B, double* C, int* dim, double* scale)
{
  char side = 'L'; // alpha*A*B
  char uplo = 'L'; // Lower triangular
  
  int    m     = dim[0];
  int    n     = dim[1];
  double alpha = scale[0];
  double beta  = scale[1];

  dsymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n, 
		 &beta,  C, &n);
}

void matrix_sym_mult(double* B, double* A, double* C, int* dim, double* scale)
{
  char side = 'R'; // alpha*B*A
  char uplo = 'L'; // Lower triangular
  
  int    m     = dim[0];
  int    n     = dim[1];
  double alpha = scale[0];
  double beta  = scale[1];

  dsymm_(&side, &uplo, 
		 &m, &n,
		 &alpha, A, &m,
		         B, &n,
		 &beta,  C, &n);
}

//-------------------------------------------------------
// B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(double* A, double* B, int* dim, int tranA)
{
  char side = 'L'; //alpha * op(A) * B;
  char uplo = 'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tran = (tranA)?'T':'N';
  double alpha = 1.0;
  int m = dim[0]; //Rows of B
  int n = dim[1]; //Columns of B
  int lda = m;
  int ldb = n;
  dtrmm_(&side, &uplo, &tran, &diag, 
		 &m, &n, &alpha, A, &lda, B, &ldb);
}
// B := alpha*A*op( B ), B is lower-non-unit triangular
void matrix_tri_mult(double* B, double* A, int* dim, int tranB)
{
  char side = 'R'; //alpha * A * op(B);
  char uplo = 'L'; //A is lower triangular
  char diag = 'N'; //Non-unit triagnular
  char tTri = (tranB)?'T':'N';
  double alpha = 1.0;
  int m = dim[0]; //Rows of B
  int n = dim[1]; //Columns of B
  int lda = n;
  int ldb = m;

  dtrmm_(&side, &uplo, &tTri, &diag,
		 &m, &n, &alpha, A, &lda, B, &ldb);
}

//------------------------------------------------------------------------
// Solve a lower-triangular, non unit-diagonal triangular matrix equation
// A*x = b
void tri_vector_solve(double* A, double* bx, int* dim, int tranA)
{
  char uplo = 'L';
  char trans= (tranA)?'T':'N';
  char diag = 'N';
  int incx = 1;
  int m = dim[0]; // Leading dimension of A, must be number of rows in A
  int n = dim[1]; // Order of matrix A, must be number of columns in A

  dtrsv_(&uplo,  &trans, &diag,
		 &n, A,  &m,
		 bx, &incx);
}

void tri_matrix_solve(double* A, double* BX, int* dim, int tranA)
{
	char side = 'L'; //op(A) X = B
	char uplo = 'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	int  m   = dim[0]; //number of rows in B
	int  n   = dim[1]; //number of column in B
	int  lda = m;      //first dimension of A
	int  ldb = m;      //first dimension of B
	double alpha = 1.0;

	dtrsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
//void dtrsm_(char* side, char* uplo, char* transA, char* diag, int* M, int* N,
//			  double* alpha, double* A, int* lda,
//			  double* B, int* ldb);
}


void matrix_tri_solve(double* BX, double* A, int* dim, int tranA)
{
	char side = 'R'; //X op(A) = B
	char uplo = 'L';
	char trans= (tranA)?'T':'N';
	char diag = 'N';
	int  m   = dim[0]; //number of rows in B
	int  n   = dim[1]; //number of column in B
	int  lda = m;      //first dimension of A
	int  ldb = n;      //first dimension of B
	double alpha = 1.0;

	dtrsm_(&side, &uplo, &trans, &diag,
		   &m, &n, &alpha, A, &lda, BX, &ldb);
//void dtrsm_(char* side, char* uplo, char* transA, char* diag, int* M, int* N,
//			  double* alpha, double* A, int* lda,
//			  double* B, int* ldb);
}


//-----------------------------------------------------------------------------
//y = A*x
/* void matrix_vector_mult(double* A, double* x, double* y, int m, int n) */
/* { */
/*   char   transA = 'N'; */
/*   double alpha = 1.0; */
/*   double beta  = 0.0; */
/*   int incx = 1; */
/*   int incy = 1; */

/*   dgemv_(&transA, &m, &n, */
/* 		 &alpha, A, &m, */
/* 		         x, &incx,  */
/* 		 &beta,  y, &incy); */
/* } */


//------------------------------------------------------------------------
// Other tools
//------------------------------------------------------------------------

void print_matrix_data(double* M, int m, int n) 
{
  int r, c;
  printf("Matrix %d x %d:\n", m, n);
  for (r=0; r<m; r++)
	{
	  for (c=0; c<n; c++)
		printf("%2.3f \t | ", M[c*n+r]); //data stored in column major
	  printf("\n");
	}
}
//
void print_matrix(char* st, double* M, int m, int n) 
{
  printf("%s", st);
  print_matrix_data(M, m, n);
}
//------------------------------------------------------------------------
void c_mat_mat_mult(double* A, double* B, double* C, int M, int N, int K)
{
  int r, c, k;
 
  for (r=0; r<M; r++)
	{
	  int idx_2 =0;
	  for (c=0; c<N; c++) 
		{
		  double v = 0.0;
		  int idx_1 = r;

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


