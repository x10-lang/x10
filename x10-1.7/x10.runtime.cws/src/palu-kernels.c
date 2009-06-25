/* Copyright 2006 Keith Randall and Bradley C. Kuszmaul. */
/* #include <mkl.h> */
#include "cblas.h"
#include "palu-kernels.h"

#include <stdlib.h>
#include <string.h>
#include <strings.h>
#include <alloca.h>

static inline REAL rabs(REAL x) {
  return (x >= 0) ? x : -x;
}

// O(nzm)
void mulsub_ser(REAL *A, REAL *B, REAL *C, int n, int z, int m, long rowsep) {
  cblas_dgemm(CblasRowMajor, CblasNoTrans, CblasNoTrans, n, m, z, -1.0, A, rowsep, B, rowsep, 1.0, C, rowsep);
}

// O(nˆ2m)
void backsolve_ser(REAL *A, REAL *L, REAL *M, int n, int m, long rowsep) {
  int i, j, k;
  for (i = 0; i < n; i++) {
    for (j = 0; j < m; j++) {
      double r = 0.0;
      for (k = 0; k < i; k++) {
	r += L_(i, k) * M_(k, j);
      }
      M_(i, j) = A_(i, j) - r;
    }
  }
}

// This version is slightly faster, where we postpone the last big
// matrix update until we actually need the data for that row/column.
// At that point we "materialize" all the data we need, doing the
// computations we skipped earlier.
void lu_ser(REAL *A, int *P, int n, int m, long rowsep) {
  int i, j, k;
  for (i = 0; i < n; i++) P[i] = i; // initialize P
  for (i = 0; i < m; i++) {

    // materialize column i (from row i to row n). Need to materialize
    // in increasing order, as subsequent materializations need older data.
    for (j = i; j < n; j++) {
      REAL z = 0;
      for (k = 0; k < i; k++) {
	z += A_(k, i) * A_(j, k);
      }
      A_(j, i) -= z;
    }

    // find pivot row (largest value in column i from row i to n)
    REAL p = rabs(A_(i, i));
    int prow = i;
    for (j = i + 1; j < n; j++) {
      REAL q = rabs(A_(j, i));
      if (q > p) {
	p = q;
	prow = j;
      }
    }

    // swap rows i and prow (if needed)
    if (i != prow) {
      for (k = 0; k < m; k++) {
	REAL t = A_(i, k);

	A_(i, k) = A_(prow, k);
	A_(prow, k) = t;
      }
      // record swap in permutation array
      // TODO: faster way to do this?
      for (k = 0; k < n; k++) {
	if (P[k] == i) P[k] = prow;
	else if (P[k] == prow) P[k] = i;
      }
    }

    // materialize the rest of pivot row
    for (j = i + 1; j < m; j++) {
      REAL z = 0;
      for (k = 0; k < i; k++) {
	z += A_(k, j) * A_(i, k);
      }
      A_(i, j) -= z;
    }

    // divide out pivot
    // TODO: handle p==0 case?
    p = 1 / A_(i, i);
    for (j = i + 1; j < n; j++) {
      A_(j, i) *= p;
    }
  }
}

// A permutation P is interpreted as follows. P[i] = j means
// that row i of A should be equal to row j of PA. Read P as
// "P[i] == j then row i goes to row j".
void permute_ser(REAL *A, int n, int m, int *P, long rowsep) {
  REAL *tmp = alloca(m * sizeof(*tmp));
  char *z = alloca(n * sizeof(*z));
  int i;

  bzero(z, n * sizeof(*z));
  for (i = 0; i < n; i++) {
    int desti;
    if (z[i] != 0) continue; // already processed in an earlier chain
    desti = P[i];
    while (desti != i) {
      z[desti] = 1;
      memcpy(tmp, &A_(desti, 0), m*sizeof(*A)); // save row desti
      memcpy(&A_(desti, 0), &A_(i, 0), m*sizeof(*A)); // move i -> desti
      memcpy(&A_(i, 0), tmp, m*sizeof(*A)); // keep old desti in i
      desti = P[desti];
    }
  }
}

