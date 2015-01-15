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

/**
  vj: Added support for single precision operations. 
 */
#include "../elem_type.h"


typedef int64_t blas_long;
//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------

void scale(blas_long n, ElemType a, ElemType* x);
void copy(blas_long n, ElemType* x, ElemType* y);
ElemType dot_prod(blas_long n, ElemType* x, ElemType* y); 
ElemType norm2(blas_long n, ElemType* x);
ElemType abs_sum(blas_long n, ElemType* x);

//------------------------------------------------------------------------
// Level Two 
//------------------------------------------------------------------------

//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(ElemType alpha, ElemType* A, ElemType* x, ElemType beta, ElemType* y, 
						blas_long* dim, blas_long lda, blas_long* offset, int transA);
//y = alpha * op(A)*x + beta * y
void matrix_vector_mult(ElemType alpha, ElemType* A, ElemType* x, ElemType beta, ElemType* y, 
						blas_long* dim, int transA);
//y = alpha* x *A + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(ElemType alpha, ElemType* x, ElemType* A, ElemType beta, ElemType* y,
					 blas_long* dim);
//   A*x = b,   or   A'*x = b,
void tri_vector_mult(ElemType* A, blas_long uplo, ElemType* bx, blas_long lda, int transA);

// A = alpha*x*y**T + A
void rank_one_update(ElemType alpha, ElemType* x, ElemType* y, ElemType* A, blas_long* dim, blas_long* offset, blas_long* inc, blas_long lda);

//  B := alpha*op( A )*B, A is lower-non-unit triangular
void tri_matrix_mult(ElemType* A, ElemType* B, blas_long* dim, int tranB);
// A := alpha*B*op( A ), B is lower-non-unit triangular
void matrix_tri_mult(ElemType* B, ElemType* A, blas_long* dim, int tranA);

//------------------------------------------------------------------------
// Level Three 
//------------------------------------------------------------------------

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C,
						blas_long* dim, blas_long* ld, blas_long* offset, int* trans);

// C = alpha* op(A) * op(B) + beta*C
void matrix_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C,
						blas_long* dim, blas_long* ld, int* trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(ElemType alpha, ElemType* A, ElemType beta, ElemType* C, blas_long* dim, blas_long* ld,
                       blas_long* offset, bool upper, bool trans);

// C = alpha*A*A**T + beta*C
void sym_rank_k_update(ElemType alpha, ElemType* A, ElemType beta, ElemType* C,
                       blas_long* dim, bool upper, bool trans);

// C = alpha* A * B + beta*C, where A is symmetrix matrix of lower trianular part
void sym_matrix_mult(ElemType alpha, ElemType* A, ElemType* B, ElemType beta, ElemType* C, 
					 blas_long* dim);
void matrix_sym_mult(ElemType* B, ElemType alpha, ElemType* A, ElemType beta, ElemType* C,
					 blas_long* dim);

//Solve Ax=b. result->x
void tri_vector_solve(ElemType* A, ElemType* bx, blas_long* dim, int tranA);
//Solve op(A)X=B
void tri_matrix_solve(ElemType* A, ElemType* BX, blas_long* dim, int tranA);
void matrix_tri_solve(ElemType* BX, ElemType* A, blas_long* dim, int tranA);

#endif

