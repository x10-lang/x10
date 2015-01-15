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
SUBROUTINE DSYEVX( JOBZ, RANGE, UPLO, N, A, LDA, VL, VU, IL, IU,
     $                   ABSTOL, M, W, Z, LDZ, WORK, LWORK, IWORK,
     $                   IFAIL, INFO )
*
*  -- LAPACK driver routine (version 3.1) --
*     Univ. of Tennessee, Univ. of California Berkeley and NAG Ltd..
*     November 2006
*
*     .. Scalar Arguments ..
      CHARACTER          JOBZ, RANGE, UPLO
      INTEGER            IL, INFO, IU, LDA, LDZ, LWORK, M, N
      DOUBLE PRECISION   ABSTOL, VL, VU
*     ..
*     .. Array Arguments ..
      INTEGER            IFAIL( * ), IWORK( * )
      DOUBLE PRECISION   A( LDA, * ), W( * ), WORK( * ), Z( LDZ, * )
*     ..
*
*  Purpose
*  =======
*
*  DSYEVX computes selected eigenvalues and, optionally, eigenvectors
*  of a real symmetric matrix A.  Eigenvalues and eigenvectors can be
*  selected by specifying either a range of values or a range of indices
*  for the desired eigenvalues.
*
*  Arguments
*  =========
*
*  JOBZ    (input) CHARACTER*1
*          = 'N':  Compute eigenvalues only;
*          = 'V':  Compute eigenvalues and eigenvectors.
*
*  RANGE   (input) CHARACTER*1
*          = 'A': all eigenvalues will be found.
*          = 'V': all eigenvalues in the half-open interval (VL,VU]
*                 will be found.
*          = 'I': the IL-th through IU-th eigenvalues will be found.
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
*          On exit, the lower triangle (if UPLO='L') or the upper
*          triangle (if UPLO='U') of A, including the diagonal, is
*          destroyed.
*
*  LDA     (input) INTEGER
*          The leading dimension of the array A.  LDA >= max(1,N).
*
*  VL      (input) DOUBLE PRECISION
*  VU      (input) DOUBLE PRECISION
*          If RANGE='V', the lower and upper bounds of the interval to
*          be searched for eigenvalues. VL < VU.
*          Not referenced if RANGE = 'A' or 'I'.
*
*  IL      (input) INTEGER
*  IU      (input) INTEGER
*          If RANGE='I', the indices (in ascending order) of the
*          smallest and largest eigenvalues to be returned.
*          1 <= IL <= IU <= N, if N > 0; IL = 1 and IU = 0 if N = 0.
*          Not referenced if RANGE = 'A' or 'V'.
*
*  ABSTOL  (input) DOUBLE PRECISION
*          The absolute error tolerance for the eigenvalues.
*          An approximate eigenvalue is accepted as converged
*          when it is determined to lie in an interval [a,b]
*          of width less than or equal to
*
*                  ABSTOL + EPS *   max( |a|,|b| ) ,
*
*          where EPS is the machine precision.  If ABSTOL is less than
*          or equal to zero, then  EPS*|T|  will be used in its place,
*          where |T| is the 1-norm of the tridiagonal matrix obtained
*          by reducing A to tridiagonal form.
*
*          Eigenvalues will be computed most accurately when ABSTOL is
*          set to twice the underflow threshold 2*DLAMCH('S'), not zero.
*          If this routine returns with INFO>0, indicating that some
*          eigenvectors did not converge, try setting ABSTOL to
*          2*DLAMCH('S').
*
*          See "Computing Small Singular Values of Bidiagonal Matrices
*          with Guaranteed High Relative Accuracy," by Demmel and
*          Kahan, LAPACK Working Note #3.
*
*  M       (output) INTEGER
*          The total number of eigenvalues found.  0 <= M <= N.
*          If RANGE = 'A', M = N, and if RANGE = 'I', M = IU-IL+1.
*
*  W       (output) DOUBLE PRECISION array, dimension (N)
*          On normal exit, the first M elements contain the selected
*          eigenvalues in ascending order.
*
*  Z       (output) DOUBLE PRECISION array, dimension (LDZ, max(1,M))
*          If JOBZ = 'V', then if INFO = 0, the first M columns of Z
*          contain the orthonormal eigenvectors of the matrix A
*          corresponding to the selected eigenvalues, with the i-th
*          column of Z holding the eigenvector associated with W(i).
*          If an eigenvector fails to converge, then that column of Z
*          contains the latest approximation to the eigenvector, and the
*          index of the eigenvector is returned in IFAIL.
*          If JOBZ = 'N', then Z is not referenced.
*          Note: the user must ensure that at least max(1,M) columns are
*          supplied in the array Z; if RANGE = 'V', the exact value of M
*          is not known in advance and an upper bound must be used.
*
*  LDZ     (input) INTEGER
*          The leading dimension of the array Z.  LDZ >= 1, and if
*          JOBZ = 'V', LDZ >= max(1,N).
*
*  WORK    (workspace/output) DOUBLE PRECISION array, dimension (MAX(1,LWORK))
*          On exit, if INFO = 0, WORK(1) returns the optimal LWORK.
*
*  LWORK   (input) INTEGER
*          The length of the array WORK.  LWORK >= 1, when N <= 1;
*          otherwise 8*N.
*          For optimal efficiency, LWORK >= (NB+3)*N,
*          where NB is the max of the blocksize for DSYTRD and DORMTR
*          returned by ILAENV.
*
*          If LWORK = -1, then a workspace query is assumed; the routine
*          only calculates the optimal size of the WORK array, returns
*          this value as the first entry of the WORK array, and no error
*          message related to LWORK is issued by XERBLA.
*
*  IWORK   (workspace) INTEGER array, dimension (5*N)
*
*  IFAIL   (output) INTEGER array, dimension (N)
*          If JOBZ = 'V', then if INFO = 0, the first M elements of
*          IFAIL are zero.  If INFO > 0, then IFAIL contains the
*          indices of the eigenvectors that failed to converge.
*          If JOBZ = 'N', then IFAIL is not referenced.
*
*  INFO    (output) INTEGER
*          = 0:  successful exit
*          < 0:  if INFO = -i, the i-th argument had an illegal value
*          > 0:  if INFO = i, then i eigenvectors failed to converge.
*                Their indices are stored in array IFAIL.
*/
#if defined(__essl__)
  void dsyevx(char* JOBZ, char* RANGE, char* UPLO, int* N, double* A, int* LDA, double* VL, double* VU, int* IL, int* IU, double *ABSTOL, int *M, double* W, double* Z, int* LDZ, double* WORK, int* LWORK, int* IWORK, int* IFAIL, int* INFO );
#else
  void dsyevx_(char* JOBZ, char* RANGE, char* UPLO, int* N, double* A, int* LDA, double* VL, double* VU, int* IL, int* IU, double *ABSTOL, int *M, double* W, double* Z, int* LDZ, double* WORK, int* LWORK, int* IWORK, int* IFAIL, int* INFO );
#endif



#ifdef __cplusplus
}
#endif
/*
 * =====================================================================
 * =====================================================================
 */

//Solve linear equations: A * X = B
int solve_linear_equation(ElemType* A, ElemType* B, int* IPIV, int* dim)
{
	int N    = dim[0]; //number of linear equations, order of A, or A's columns
	int NRHS = dim[1]; //number of right hand side, order of B.
	int LDA  = N;      //leading dimension of A, must >= N
	int LDB  = N;      //leading dimension of B, must >= N
	//int* IPIV; permutation of rows - exchange row i with IPIV[i]
	//ElemType* A; // On exit, A = P*L*U; Output: the factors L and U from the factorizationthe,
	             // unit diagonal elements of L are not stored.
	//ElemType* B; // On exit, if INFO = 0, the N-by-NRHS solution matrix X.
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

int comp_eigenvalues(ElemType* A, ElemType* W, ElemType* WORK, int* IWORK, int* dim)
{
	char JOBZ = 'N'; // compute eigenvalues only
    char RANGE = 'A'; // all eigenvalues
	char UPLO = 'U';
	int N   = dim[0]; // order of A
    int M   = N; // all eigenvalues
	int LDA = N;
    ElemType ABSTOL = 0.0;
    int LDZ = N;
	int* LWORK = &dim[1];
	int INFO;

#ifdef ENABLE_LAPACK

#if defined(__essl__)
    dsyevx(&JOBZ, &RANGE, &UPLO, &N, A, &LDA,
           NULL, NULL, // VL, VU
           NULL, NULL, // IL, IU
           &ABSTOL, &M, W, NULL, &LDZ,
           WORK, LWORK, IWORK, NULL, &INFO);
#else
    dsyevx_(&JOBZ, &RANGE, &UPLO, &N, A, &LDA,
           NULL, NULL, // VL, VU
           NULL, NULL, // IL, IU
           &ABSTOL, &M, W, NULL, &LDZ,
           WORK, LWORK, IWORK, NULL, &INFO);
#endif
#else
    error_missing_lapack();
#endif

	return INFO;
}

int comp_eigenvectors(ElemType* A, ElemType* W, ElemType* Z, ElemType* WORK, int* IWORK, int* IFAIL, int* dim)
{
	char JOBZ = 'V'; // compute eigenvalues and eigenvectors
    char RANGE = 'A'; // all eigenvalues
	char UPLO = 'U';
	int N   = dim[0]; // order of A
    int M   = N; // all eigenvalues
	int LDA = N;
    ElemType ABSTOL = 0.0;
    int LDZ = N;
	int* LWORK = &dim[1];
	int INFO;

#ifdef ENABLE_LAPACK

#if defined(__essl__)
    dsyevx(&JOBZ, &RANGE, &UPLO, &N, A, &LDA,
           NULL, NULL, // VL, VU
           NULL, NULL, // IL, IU
           &ABSTOL, &M, W, Z, &LDZ,
           WORK, LWORK, IWORK, IFAIL, &INFO);
#else
    dsyevx_(&JOBZ, &RANGE, &UPLO, &N, A, &LDA,
           NULL, NULL, // VL, VU
           NULL, NULL, // IL, IU
           &ABSTOL, &M, W, Z, &LDZ,
           WORK, LWORK, IWORK, IFAIL, &INFO);
#endif
#else
    error_missing_lapack();
#endif
	return INFO;
}
