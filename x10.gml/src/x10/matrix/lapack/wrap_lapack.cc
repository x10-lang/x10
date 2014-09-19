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

#include <stdio.h>
#include <stdlib.h>

//using namespace std;
#include "wrap_lapack.h"

#ifdef __cplusplus
extern "C"  {
#endif

void error_missing_lapack() {
  printf("GML was built without LAPACK library.\n");
  printf("Check build settings in system_setting.mk, including definition of BLASLIB variable\n");
  fflush(stdout);
  exit(1);
}

/*********************************************************************
SUBROUTINE DGESV( N, NRHS, A, LDA, IPIV, B, LDB, INFO )
*
*  -- LAPACK driver routine (version 3.2) --
*  -- LAPACK is a software package provided by Univ. of Tennessee,    --
*  -- Univ. of California Berkeley, Univ. of Colorado Denver and NAG Ltd..--
*     November 2006
*
*     .. Scalar Arguments ..
      INTEGER            INFO, LDA, LDB, N, NRHS
*     ..
*     .. Array Arguments ..
      INTEGER            IPIV( * )
      DOUBLE PRECISION   A( LDA, * ), B( LDB, * )
*     ..
*
*  Purpose
*  =======
*
*  DGESV computes the solution to a real system of linear equations
*     A * X = B,
*  where A is an N-by-N matrix and X and B are N-by-NRHS matrices.
*
*  The LU decomposition with partial pivoting and row interchanges is
*  used to factor A as
*     A = P * L * U,
*  where P is a permutation matrix, L is unit lower triangular, and U is
*  upper triangular.  The factored form of A is then used to solve the
*  system of equations A * X = B.
*
*  Arguments
*  =========
*
*  N       (input) INTEGER
*          The number of linear equations, i.e., the order of the
*          matrix A.  N >= 0.
*
*  NRHS    (input) INTEGER
*          The number of right hand sides, i.e., the number of columns
*          of the matrix B.  NRHS >= 0.
*
*  A       (input/output) DOUBLE PRECISION array, dimension (LDA,N)
*          On entry, the N-by-N coefficient matrix A.
*          On exit, the factors L and U from the factorization
*          A = P*L*U; the unit diagonal elements of L are not stored.
*
*  LDA     (input) INTEGER
*          The leading dimension of the array A.  LDA >= max(1,N).
*
*  IPIV    (output) INTEGER array, dimension (N)
*          The pivot indices that define the permutation matrix P;
*          row i of the matrix was interchanged with row IPIV(i).
*
*  B       (input/output) DOUBLE PRECISION array, dimension (LDB,NRHS)
*          On entry, the N-by-NRHS matrix of right hand side matrix B.
*          On exit, if INFO = 0, the N-by-NRHS solution matrix X.
*
*  LDB     (input) INTEGER
*          The leading dimension of the array B.  LDB >= max(1,N).
*
*  INFO    (output) INTEGER
*          = 0:  successful exit
*          < 0:  if INFO = -i, the i-th argument had an illegal value
*          > 0:  if INFO = i, U(i,i) is exactly zero.  The factorization
*                has been completed, but the factor U is exactly
*                singular, so the solution could not be computed.
*
*/
#if defined(__essl__)
void dgesv(int* N, int* NRHS, double* A, int* LDA, int* IPIV, double* B, int* LDB, int* INFO);
#else
void dgesv_(int* N, int* NRHS, double* A, int* LDA, int* IPIV, double* B, int* LDB, int* INFO);
#endif

/****************************************************************************
SUBROUTINE DSYEV( JOBZ, UPLO, N, A, LDA, W, WORK, LWORK, INFO )
*
*  -- LAPACK driver routine (version 3.2) --
*  -- LAPACK is a software package provided by Univ. of Tennessee,    --
*  -- Univ. of California Berkeley, Univ. of Colorado Denver and NAG Ltd..--
*     November 2006
*
*     .. Scalar Arguments ..
  CHARACTER          JOBZ, UPLO
  INTEGER            INFO, LDA, LWORK, N
*     ..
*     .. Array Arguments ..
  DOUBLE PRECISION   A( LDA, * ), W( * ), WORK( * )
*     ..
*
*  Purpose
*  =======
*
*  DSYEV computes all eigenvalues and, optionally, eigenvectors of a
*  real symmetric matrix A.
*
*  Arguments
*  =========
*
*  JOBZ    (input) CHARACTER*1
*          = 'N':  Compute eigenvalues only;
*          = 'V':  Compute eigenvalues and eigenvectors.
*
*  UPLO    (input) CHARACTER*1
*          = 'U':  Upper triangle of A is stored;
*          = 'L':  Lower triangle of A is stored.
*
*  N       (input) INTEGER
*          The order of the matrix A.  N >= 0.
*
*  A       (input/output) DOUBLE PRECISION array, dimension (LDA, N)
*          On entry, the symmetric matrix A.  If UPLO = 'U', the
*          leading N-by-N upper triangular part of A contains the
*          upper triangular part of the matrix A.  If UPLO = 'L',
*          the leading N-by-N lower triangular part of A contains
*          the lower triangular part of the matrix A.
*          On exit, if JOBZ = 'V', then if INFO = 0, A contains the
*          orthonormal eigenvectors of the matrix A.
*          If JOBZ = 'N', then on exit the lower triangle (if UPLO='L')
*          or the upper triangle (if UPLO='U') of A, including the
*          diagonal, is destroyed.
*
*  LDA     (input) INTEGER
*          The leading dimension of the array A.  LDA >= max(1,N).
*
*  W       (output) DOUBLE PRECISION array, dimension (N)
*          If INFO = 0, the eigenvalues in ascending order.
*
*  WORK    (workspace/output) DOUBLE PRECISION array, dimension (MAX(1,LWORK))
*          On exit, if INFO = 0, WORK(1) returns the optimal LWORK.
*
*  LWORK   (input) INTEGER
*          The length of the array WORK.  LWORK >= max(1,3*N-1).
*          For optimal efficiency, LWORK >= (NB+2)*N,
*          where NB is the blocksize for DSYTRD returned by ILAENV.
*
*          If LWORK = -1, then a workspace query is assumed; the routine
*          only calculates the optimal size of the WORK array, returns
*          this value as the first entry of the WORK array, and no error
*          message related to LWORK is issued by XERBLA.
*
*  INFO    (output) INTEGER
*          = 0:  successful exit
*          < 0:  if INFO = -i, the i-th argument had an illegal value
*          > 0:  if INFO = i, the algorithm failed to converge; i
*                off-diagonal elements of an intermediate tridiagonal
*                form did not converge to zero.
*
*/
#if defined(__essl__)
void dsyev(char* JOBZ, char* UPLO, int* N, double* A, int* LDA, double* W, double* WORK, int* LWORK, int* INFO );
#else
void dsyev_(char* JOBZ, char* UPLO, int* N, double* A, int* LDA, double* W, double* WORK, int* LWORK, int* INFO );
#endif



#ifdef __cplusplus
}
#endif
/*
 * =====================================================================
 * =====================================================================
 */

//Solve linear equations: A * X = B
int solve_linear_equation(double* A, double* B, int* IPIV, int* dim)
{
	int N    = dim[0]; //number of linear equations, order of A, or A's columns
	int NRHS = dim[1]; //number of right hand side, order of B.
	int LDA  = N;      //leading dimension of A, must >= N
	int LDB  = N;      //leading dimension of B, must >= N
	//int* IPIV; permutation of rows - exchange row i with IPIV[i]
	//double* A; // On exit, A = P*L*U; Output: the factors L and U from the factorizationthe,
	             // unit diagonal elements of L are not stored.
	//double* B; // On exit, if INFO = 0, the N-by-NRHS solution matrix X.
	int INFO;
#ifdef ENABLE_LAPACK

#if defined(__essl__)
	dgesv(&N, &NRHS, A, &LDA, IPIV, B, &LDB, &INFO);
#else
	dgesv_(&N, &NRHS, A, &LDA, IPIV, B, &LDB, &INFO);
#endif

#else
    error_missing_lapack();
#endif
	return INFO;
}

int comp_eigenvalue(double* A, double* W, double* WORK, int* dim)
{
	char JOBZ = 'N';   //Compute eigenvalues
	char UPLO = dim[1]?'U':'L';
	int  N   = dim[0]; //order of A
	int  LDA = N; //leading dimension of A
	//double* A;// Input, on exit, A is destroyed.
	//doulbe* W;// If INFO = 0, the eigenvalues in ascending order
	//double* WORK; //work space
	//int LWORK; //work space size optimized on exit, LWORK >= max(1,3*N-1).
	int*  LWORK = &dim[1];//dim[1];
	int INFO;
#ifdef ENABLE_LAPACK

#if defined(__essl__)
	dsyev(&JOBZ, &UPLO, &N, A, &LDA, W, WORK, LWORK, &INFO );
#else
	dsyev_(&JOBZ, &UPLO, &N, A, &LDA, W, WORK, LWORK, &INFO );
#endif

#else
    error_missing_lapack();
#endif

	return INFO;
}

int comp_eigenvector(double* A, double* W, double* WORK, int* dim)
{
	char JOBZ = 'V';    //Compute eigenvalues and eigenvectors
	char UPLO = dim[1]?'U':'L';
	int  N   = dim[0]; //order of A
	int  LDA  = N; //leading dimension of A
	//double* A;// On exit, if INFO = 0, A contains the orthonormal eigenvectors of the matrix A.
	//doulbe* W;// If INFO = 0, the eigenvalues in ascending order
	//double* WORK; //work space
	//int LWORK; //work space size optimized on exit, LWORK >= max(1,3*N-1).
	int*  LWORK = &dim[1];
	int INFO;

#ifdef ENABLE_LAPACK

#if defined(__essl__)
	dsyev(&JOBZ, &UPLO, &N, A, &LDA, W, WORK, LWORK, &INFO );
#else
	dsyev_(&JOBZ, &UPLO, &N, A, &LDA, W, WORK, LWORK, &INFO );
#endif
#else
    error_missing_lapack();
#endif
	return INFO;
}
