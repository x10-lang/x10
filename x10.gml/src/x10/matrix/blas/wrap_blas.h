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

#ifndef WRAP_BLAS_H
#define WRAP_BLAS_H


//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
void scale(int64_t n, double a, double* x);
void copy(int64_t n, double* x, double* y);
double dot_prod(int64_t n, double* x, double* y); 
double norm2(int64_t n, double* x);
double abs_sum(int64_t n, double* x);


//------------------------------------------------------------------------
//Simplified interface
//------------------------------------------------------------------------
// C = A * B
/* void matrix_matrix_mult(double* A, double* B, double* C, */
/* 						int64_t m, int64_t n, int64_t k); */
// C = alpah* op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, 
						int64_t* dim, int64_t* ld, double* scale, int* trans);

// C = alpah* op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, 
						int64_t* dim, double* scale, int* trans);

// C = alpah* A * B + beta*C, where A is symmetrix matrix of lower trianular part
void sym_matrix_mult(double* A, double* B, double* C, 
					 int64_t* dim, double* scale);
void matrix_sym_mult(double* B, double* A, double* C,
					 int64_t* dim, double* scale);

//------------------------------------------------------------------------

//y = A*x
/* void matrix_vector_mult(double* A, double* x, double* y,  */
/* 						int64_t m, int64_t n); */
//y = alpah * op(A)*x + beta * y
void matrix_vector_mult(double* A, double* x, double* y, 
						int64_t* dim, double* scale, int transA);
//y = alpah* x *A + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double* x, double* A, double* y,
					 int64_t* dim, double* scale);
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(double* A, int64_t uplo, double* bx, int64_t lda, int transA);

// A = alpha*x*y**T + A
void rank_one_update(double* A, double* x, double* y, int64_t* dim, int64_t* offset, int64_t* inc, int64_t lda, double alpha);

//  B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(double* A, double* B, int64_t* dim, int tranB);
// A := alpha*B*op( A ), B is lower-non-unit triangular
void matrix_tri_mult(double* B, double* A, int64_t* dim, int tranA);

//-------------------------------------------------------------------
//Solve Ax=b. result->x
void tri_vector_solve(double* A, double* bx, int64_t* dim, int tranA);
//Solve op(A)X=B
void tri_matrix_solve(double* A, double* BX, int64_t* dim, int tranA);
void matrix_tri_solve(double* BX, double* A, int64_t* dim, int tranA);

//------------------------------------------------------------------------
// Other tools
//------------------------------------------------------------------------
void print_matrix(char*, double*, long, long);
void print_matrix_data(double*, long, long);
void c_mat_mat_mult(double* A, double* B, double* C, 
					int64_t M, int64_t N, int64_t K);

#endif
