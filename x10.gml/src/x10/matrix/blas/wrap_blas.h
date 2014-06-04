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
// Level Two 
//------------------------------------------------------------------------

//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(double alpha, double* A, double* x, double beta, double* y, 
						blas_long* dim, blas_long lda, blas_long* offset, int transA);
//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(double alpha, double* A, double* x, double beta, double* y, 
						blas_long* dim, int transA);
//y = alpha* x *A + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double alpha, double* x, double* A, double beta, double* y,
					 blas_long* dim);
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(double* A, blas_long uplo, double* bx, blas_long lda, int transA);

// A = alpha*x*y**T + A
void rank_one_update(double* A, double* x, double* y, blas_long* dim, blas_long* offset, blas_long* inc, blas_long lda, double alpha);

//  B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(double* A, double* B, blas_long* dim, int tranB);
// A := alpha*B*op( A ), B is lower-non-unit triangular
void matrix_tri_mult(double* B, double* A, blas_long* dim, int tranA);

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(double alpha, double* A, double* B, double beta, double* C,
						blas_long* dim, blas_long* ld, blas_long* offset, int* trans);

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(double alpha, double* A, double* B, double beta, double* C,
						blas_long* dim, blas_long* ld, int* trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(double alpha, double* A, double beta, double* C, blas_long* dim, blas_long* ld,
                       blas_long* offset, bool upper, bool trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(double alpha, double* A, double beta, double* C,
                       blas_long* dim, bool upper, bool trans);

// C = alpha* A * B + beta*C, where A is symmetrix matrix of lower trianular part
void sym_matrix_mult(double alpha, double* A, double* B, double beta, double* C, 
					 blas_long* dim);
void matrix_sym_mult(double* B, double alpha, double* A, double beta, double* C,
					 blas_long* dim);

//Solve Ax=b. result->x
void tri_vector_solve(double* A, double* bx, blas_long* dim, int tranA);
//Solve op(A)X=B
void tri_matrix_solve(double* A, double* BX, blas_long* dim, int tranA);
void matrix_tri_solve(double* BX, double* A, blas_long* dim, int tranA);

#endif
