#ifndef WRAP_BLAS_H
#define WRAP_BLAS_H

#include <string>


//------------------------------------------------------------------------
// Level One 
//------------------------------------------------------------------------
void scal(int n, double a, double* x);
void copy(int n, double* x, double* y);
double dot_prod(int n, double* x, double* y); 
double norm2(int n, double* x);
double abs_sum(int n, double* x);


//------------------------------------------------------------------------
//Simplified interface
//------------------------------------------------------------------------
// C = A * B
void matrix_matrix_mult(double* A, double* B, double* C,
						int m, int n, int k);
// C = alpah* op(A) * op(B) + beta*C
void matrixT_matrixT_mult(double* A, double* B, double* C, 
						  int* dim, double* scale, int* trans);
// C = alpah* A * B + beta*C, where A is symmetrix matrix of lower trianular part
void sym_matrix_mult(double* A, double* B, double* C, 
					 int* dim, double* scale);

//------------------------------------------------------------------------

//y = A*x
void matrix_vector_mult(double* A, double* x, double* y, 
						int m, int n);
//y = alpah * op(A)*x + beta * y
void matrixT_vector_mult(double* A, double* x, double* y, 
						 int* dim, double* scale, int transA);
//y = alpah* A *x + beta * y, A is symmetrix matrix of lower triangular part
void sym_vector_mult(double* A, double* x, double* y, 
					 int* dim, double* scale);

//------------------------------------------------------------------------
//  B := alpha*op( A )*B, A is lower-non-unit triangular
void triT_matrix_mult(double* A, double* B, int* dim, int tranA);
// B := alpha*B*op( A ), A is lower-non-unit triangular
void matrix_triT_mult(double* B, double* A, int* dim, int tranA);
//Solve Ax=b. result->x
void tri_matrix_solve(double* A, double* bx, int m, int n);

//------------------------------------------------------------------------
// Other tools
//------------------------------------------------------------------------
void print_matrix(char*, double*, int, int);
void print_matrix(double*, int, int);
void c_mat_mat_mult(double* A, double* B, double* C, 
					int M, int N, int K);

#endif
