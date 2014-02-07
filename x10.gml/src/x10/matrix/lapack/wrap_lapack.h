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

#ifndef WRAP_LAPACK_H
#define WRAP_LAPACK_H

//------------------------------------------------------------------------
// Solve linear equations
//------------------------------------------------------------------------
// A * X = B
int solve_linear_equation(double* A, double* B, int* IPIV, int* dim);

//------------------------------------------------------------------------
// Compute eigenvalues and eigenvector
//------------------------------------------------------------------------
int comp_eigenvalue(double* A, double* W, double* WORK, int* dim);
int comp_eigenvector(double* A, double* W, double* WORK, int* dim);

#endif
