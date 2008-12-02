/* Copyright 2006 Keith Randall and Bradley C. Kuszmaul. */
typedef double REAL;

#define A_(i,j) (A[(i)*(long)rowsep + (j)])

#define B_(i,j) (B[(i)*(long)rowsep + (j)])
#define C_(i,j) (C[(i)*(long)rowsep + (j)])
#define AORIG_(i,j) (AORIG[(i)*(long)rowsep + (j)])
#define CORIG_(i,j) (CORIG[(i)*(long)rowsep + (j)])
#define LU_(i,j) (LU[(i)*(long)rowsep + (j)])
#define L_(i,j) (L[(i)*(long)rowsep + (j)])
#define M_(i,j) (M[(i)*(long)rowsep + (j)])

extern void mulsub_ser(REAL *A, REAL *B, REAL *C, int n, int z, int m, long rowsep);
extern void backsolve_ser(REAL *A, REAL *L, REAL *M, int n, int m, long rowsep);
extern void lu_ser(REAL *A, int *P, int n, int m, long rowsep);
extern void permute_ser(REAL *A, int n, int m, int *P, long rowsep);
