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

#ifndef WRAP_BLAS_H
#define WRAP_BLAS_H

#ifdef jniport_h
typedef long long blas_long;
#else
typedef int64_t blas_long;
#endif

//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
void scale(blas_long n, double a, double* x);
void copy(blas_long n, double* x, double* y);
double dot_prod(blas_long n, double* x, double* y); 
double norm2(blas_long n, double* x);
double abs_sum(blas_long n, double* x);


//------------------------------------------------------------------------
//Simplified interface
//------------------------------------------------------------------------

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, 
						blas_long* dim, blas_long* ld, blas_long* offset, double* scale, int* trans);

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, 
						blas_long* dim, blas_long* ld, double* scale, int* trans);

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(double* A, double* B, double* C, 
						blas_long* dim, double* scale, int* trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(double* A, double* C, blas_long* dim, blas_long* ld,
                       blas_long* offset, double* scale, bool upper, bool trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(double* A, double* C, blas_long* dim, 
                       double* scale, bool upper, bool trans);

// C = alpha* A * B + beta*C, where A is symmetrix matrix of lower trianular part
void sym_matrix_mult(double* A, double* B, double* C, 
					 blas_long* dim, double* scale);
void matrix_sym_mult(double* B, double* A, double* C,
					 blas_long* dim, double* scale);

//------------------------------------------------------------------------

//y = A*x
/* void matrix_vector_mult(double* A, double* x, double* y,  */
/* 						blas_long m, blas_long n); */
//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(double* A, double* x, double* y, 
						blas_long* dim, blas_long lda, blas_long* offset, double* scale, int transA);
//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(double* A, double* x, double* y, 
						blas_long* dim, double* scale, int transA);
//y = alpha* x *A + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double* x, double* A, double* y,
					 blas_long* dim, double* scale);
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(double* A, blas_long uplo, double* bx, blas_long lda, int transA);

// A = alpha*x*y**T + A
void rank_one_update(double* A, double* x, double* y, blas_long* dim, blas_long* offset, blas_long* inc, blas_long lda, double alpha);

//  B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(double* A, double* B, blas_long* dim, int tranB);
// A := alpha*B*op( A ), B is lower-non-unit triangular
void matrix_tri_mult(double* B, double* A, blas_long* dim, int tranA);

//-------------------------------------------------------------------
//Solve Ax=b. result->x
void tri_vector_solve(double* A, double* bx, blas_long* dim, int tranA);
//Solve op(A)X=B
void tri_matrix_solve(double* A, double* BX, blas_long* dim, int tranA);
void matrix_tri_solve(double* BX, double* A, blas_long* dim, int tranA);

//------------------------------------------------------------------------
// Other tools
//------------------------------------------------------------------------
void print_matrix(char*, double*, long, long);
void print_matrix_data(double*, long, long);
void c_mat_mat_mult(double* A, double* B, double* C, 
					blas_long M, blas_long N, blas_long K);

#endif
