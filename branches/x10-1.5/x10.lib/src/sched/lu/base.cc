#include "base.h"

int gBlockSize; /*The block size -- a global variable for now*/
int gBlockStride; /*stride between blocks -- should be  gBlockSize^2*/
int gMatrixSize; /*Size of the 2-D matrix along each dimension*/


/*------------Helper routines-----------------------------*/

/* Fill in matrix M with random values */
void init_matrix(matrix_t A, int nb) {
  int xT, yT, xI, yI;

  /* Initialize random number generator. */
  srand(1);
  
  /* For each element of each block, fill in random value. */
  for (xT = 0; xT < nb; xT++)
    for (yT = 0; yT < nb; yT++)
      for (xI = 0; xI < gBlockSize; xI++)
	for (yI = 0; yI < gBlockSize; yI++)
	  BLOCK(MATRIX(A, xT, yT), xI, yI) = 
	    ((double)rand()) / (double)RAND_MAX;

  for (xT = 0; xT < nb; xT++)
    for (xI = 0; xI < gBlockSize; xI++)
      BLOCK(MATRIX(A, xT, xT), xI, xI) *= 10.0;
}

/* Print matrix */
void print_matrix(matrix_t A, int nb) {
  int i, j;
  
  /* Print out matrix. */
  for (i = 0; i < nb*gBlockSize; i++) {
    for (j = 0; j < nb*gBlockSize; j++)
      printf(" %6.4f",
	     BLOCK(MATRIX(A, i / gBlockSize, j /gBlockSize),
		   i % gBlockSize, j % gBlockSize));
    printf("\n");
  }
}

/* test_result - Check that matrix LU contains LU decomposition of M. */
int test_result(matrix_t LU, matrix_t M, int nb) {
  int I, J, K, i, j, k;
  double diff, max_diff;
  double v;
  
  /* Initialize test. */
  max_diff = 0.0;
  
  /* Find maximum difference between any element of LU and M. */
  for (i = 0; i < nb * gBlockSize; i++)
    for (j = 0; j < nb * gBlockSize; j++) {
      I = i / gBlockSize;
      J = j / gBlockSize;
      v = 0.0;
      for (k = 0; k < i && k <= j; k++) {
	K = k / gBlockSize;
	v += BLOCK(MATRIX(LU, I, K), i % gBlockSize,
		   k % gBlockSize) *
	  BLOCK(MATRIX(LU, K, J), k % gBlockSize,
		j % gBlockSize);
      }
      if (k == i && k <= j) {
	K = k / gBlockSize;
	v += BLOCK(MATRIX(LU, K, J), k % gBlockSize,
		   j % gBlockSize);
      }
      diff = fabs(BLOCK(MATRIX(M, I, J), i % gBlockSize,
			j % gBlockSize) - v);
      if (diff > max_diff)
	max_diff = diff;
    }
  
  /* Check maximum difference against threshold. */
  if (max_diff > 0.00001)
    return 0;
  else
    return 1;
}

/* Return number of flops to perform non-pivoted LU decomposition. */
double count_flops(int n) {
  return ((4.0*n - 3.0) * n - 1.0) * n / 6.0;
}

/*Compute y' = y - ax  */
void elem_daxmy(double a, double *x, double *y, int n) {
  for (n--; n >= 0; n--)
    y[n] -= a * x[n];
}


/*---------------Block operations----------------------------*/

/*These routines help perform LU decomposition on a single block*/

/* Factor block B */
void block_lu(block_t B) {
  int i, k;
  
  //fprintf(stderr, "Entered block_lu. B=%p\n", B);
  //print_matrix(B, 1);
 
  /* Factor block. */
  for (k = 0; k < gBlockSize; k++)
    for (i = k + 1; i < gBlockSize; i++) {
      BLOCK(B, i, k) /= BLOCK(B, k, k);
      elem_daxmy(BLOCK(B, i, k), &BLOCK(B, k, k + 1),
		 &BLOCK(B, i, k + 1), gBlockSize - k - 1);
    }
  //fprintf(stderr, "Leaving block_lu\n");
}


/*Perform forward substitution to solve for B' in LB' = B */
void block_lower_solve(block_t B, block_t L) {
  int i, k;
  
  /* Perform forward substitution. */
  for (i = 1; i < gBlockSize; i++)
    for (k = 0; k < i; k++)
      elem_daxmy(BLOCK(L, i, k), &BLOCK(B, k, 0),
		 &BLOCK(B, i, 0), gBlockSize);
}


/* Perform forward substitution to solve for B' in B'U = B */
void block_upper_solve(block_t B, block_t U) {
  int i, k;
  
  /* Perform forward substitution. */
  for (i = 0; i < gBlockSize; i++)
    for (k = 0; k < gBlockSize; k++) {
      BLOCK(B, i, k) /= BLOCK(U, k, k);
      elem_daxmy(BLOCK(B, i, k), &BLOCK(U, k, k + 1),
		 &BLOCK(B, i, k + 1), gBlockSize - k - 1);
    }
}


/* Compute Schur complement B' = B - AC */
void block_schur(block_t B, block_t A, block_t C) {
  int i, k;
  
  /* Compute Schur complement. */
  for (i = 0; i < gBlockSize; i++)
    for (k = 0; k < gBlockSize; k++)
      elem_daxmy(BLOCK(A, i, k), &BLOCK(C, k, 0),
		 &BLOCK(B, i, 0), gBlockSize);
}

