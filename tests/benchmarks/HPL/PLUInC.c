/*(C) Copyright IBM Corp. 2007*/
#include <stdlib.h>
#include "lu_PLU2_1C_x10stub.c"

extern void  lu_PLU2_1C_blockLower(double* me_x10PoInTeR, 
        int* me_x10DeScRiPtOr, 
        double* diag_x10PoInTeR, 
        int* diag_x10DeScRiPtOr, 
        signed int col, 
        signed int B, 
        double diagColCol)
{
    {
        char TRANSA = 'N';
        int M = B;
        int N = col;
        double ALPHA = -1.0;
        double *mA = me_x10PoInTeR;
        int LDA = B;
        double *mX = diag_x10PoInTeR + (B*col);
        int INCX = 1;
        double BETA = 1.0;
        double *mY = me_x10PoInTeR + (B*col);
        int INCY = 1;

        dgemv (&TRANSA, &M, &N, &ALPHA, mA, &LDA,
                mX, &INCX, &BETA, mY, &INCY);
    }

    {
        int N = B;
        double DA = 1.0/(diagColCol);
        double *DX = me_x10PoInTeR + (B*col);
        int INCX = 1;

        dscal (&N, &DA, DX, &INCX);
    }
}

extern void lu_PLU2_1C_blockBackSolve(double* me_x10PoInTeR, 
        int* me_x10DeScRiPtOr, 
        double* diag_x10PoInTeR, 
        int* diag_x10DeScRiPtOr, 
        signed int B)
{
    char SIDE = 'L'; //diag is to left of X
    char UPLO = 'L'; //diag is lower-triangular
    char TRANSA = 'N'; //No transpose of diag
    char DIAG = 'U'; //Unit-diagonal for diag
    int M = B; //#rows of diag
    int N = B; //#cols of diag
    double ALPHA = 1.0; //scale on right-hand size
    double *mA = diag_x10PoInTeR;
    int LDA = B;
    double *mB = me_x10PoInTeR;
    int LDB = B;

    dtrsm (&SIDE, &UPLO, &TRANSA, &DIAG, &M, &N, &ALPHA,
      mA, &LDA, mB, &LDB);
}

extern void lu_PLU2_1C_blockMulSub(double* me_x10PoInTeR, 
        int* me_x10DeScRiPtOr, 
        double* left_x10PoInTeR, 
        int* left_x10DeScRiPtOr, 
        double* upper_x10PoInTeR, 
        int* upper_x10DeScRiPtOr, 
        signed int B)
{
    char transa = 'N', transb = 'N';
    int m=B, n=B, k=B;
    double alpha = -1.0, beta = 1.0;
    int lda = B, ldb = B, ldc = B;

    double *mA = left_x10PoInTeR;
    double *mB = upper_x10PoInTeR;
    double *mC = me_x10PoInTeR;

    dgemm (&transa, &transb, &m, &n, &k, &alpha, mA, &lda,
      mB, &ldb, &beta, mC, &ldc);
}
