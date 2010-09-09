/** 
 * A C++ cws version of the Cilk LU code. Generic header file. 
 * @author Sriram Krishnamoorthy
 */

#ifndef __LU_BASE_H__
#define __LU_BASE_H__

#include <iostream>
#include <string>

#include "Closure.h"
#include "Cache.h"
#include "Frame.h"
#include "Worker.h"
#include "Job.h"
#include "Pool.h"
#include "Sys.h"
#include <assert.h>
#include <stdlib.h>
#include <math.h>

using namespace std;
using namespace x10lib_cws;


extern int gBlockSize; /*The block size -- a global variable for now*/
extern int gBlockStride; /*stride between blocks -- should be  gBlockSize^2*/
extern int gMatrixSize; /*Size of the 2-D matrix along each dimension*/

typedef double *matrix_t;
typedef double *block_t;

/*Matrix element indexed by (I,J) within a block. D is the base pointer of that block*/
#define BLOCK(D,I,J) ((D)[(I)*gBlockSize + (J)])

/*Pointer to block indexed by (I,J)*/
#define MATRIX(M,I,J) (&((M)[(I)*(gMatrixSize/gBlockSize)*gBlockStride + (J)*gBlockStride]))


/*------------Helper routines-----------------------------*/

/* Fill in matrix M with random values */
void init_matrix(matrix_t A, int nb);

/* Print matrix */
void print_matrix(matrix_t A, int nb);

/* test_result - Check that matrix LU contains LU decomposition of M. */
int test_result(matrix_t LU, matrix_t M, int nb);

/* Return number of flops to perform non-pivoted LU decomposition. */
double count_flops(int n);

/*Compute y' = y - ax  */
void elem_daxmy(double a, double *x, double *y, int n);

/*---------------Block operations----------------------------*/

/*These routines help perform LU decomposition on a single block*/

/* Factor block B */
void block_lu(block_t B);

/*Perform forward substitution to solve for B' in LB' = B */
void block_lower_solve(block_t B, block_t L);

/* Perform forward substitution to solve for B' in B'U = B */
void block_upper_solve(block_t B, block_t U);

/* Compute Schur complement B' = B - AC */
void block_schur(block_t B, block_t A, block_t C);


#endif /*__LU_BASE_H__*/

