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

#ifndef WRAP_LAPACK_H
#define WRAP_LAPACK_H

/**
  vj: Added support for single precision operations. 
 */
#include "../elem_type.h"

// A * X = B
int solve_linear_equation(ElemType* A, ElemType* B, int* IPIV, int* dim);

int comp_eigenvalues(ElemType* A, ElemType* W, ElemType* WORK, int* IWORK, int* dim);
int comp_eigenvectors(ElemType* A, ElemType* W, ElemType* Z, ElemType* WORK, int* IWORK, int* IFAIL, int* dim);

#endif
