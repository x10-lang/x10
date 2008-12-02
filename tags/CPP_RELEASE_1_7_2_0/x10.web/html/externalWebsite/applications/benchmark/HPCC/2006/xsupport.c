#include "fft_x10stub.c"
#include <fftw3.h>
#include <math.h>

/***
    FFT_HOME=$HOME/fftw/fftw-3.1.2/
    slibclean; xlc_r -O3 -qnolm -bM:SHR -bnoentry -bexpall -bmaxdata:0x80000000 -I$JAVA_HOME/bin/include -I$JAVA_HOME/include -o libxsupport.a xsupport.c -I$FFT_HOME/api -L$FFT_HOME/.libs -l fftw3 -lm
    FFT_HOME=$HOME/fftw/fftw-3.1.2_64/
    slibclean; xlc_r -q64 -O3 -qnolm -bM:SHR -bnoentry -bexpall -blpdata -bmaxdata:0x8000000000 -I$JAVA_HOME/bin/include -I$JAVA_HOME/include -o libxsupport_64.a xsupport.c -I$FFT_HOME/api -L$FFT_HOME.libs -l fftw3 -lm
    (C) Copyright IBM Corp. 2006
***/

extern jlong  fft_fftw_plan_dft_1d(signed int SQRTN, double* A1_x10PoInTeR, int* A1_x10DeScRiPtOr, double* A2_x10PoInTeR, int* A2_x10DeScRiPtOr, signed int m1, signed int what) {
   return fftw_plan_dft_1d(SQRTN, (fftw_complex *) A1_x10PoInTeR, (fftw_complex *) A2_x10PoInTeR, m1, what);
}

static int SQRTN;
static int N;
static fftw_complex *tw0;
static fftw_complex *tw1;

static fftw_complex *A_debug = NULL;
/* row-major array indexing */
#define SUB(A, i, j) (A)[(i)*SQRTN+(j)]

extern void   fft_transpose_and_swapx(double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int start, signed int i0, signed int i1, signed int j0, signed int j1) {
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  int i, j;
  for (i = i0; i < i1; ++i)
    for (j = j0; j < j1; ++j) {
      /* swap A[i][j] <-> A[j][i] */
      fftw_complex tmp;
      tmp[0] = SUB(A, i, j + start)[0];
      tmp[1] = SUB(A, i, j + start)[1];
      SUB(A, i, j + start)[0] = SUB(A, j, i + start)[0];
      SUB(A, i, j + start)[1] = SUB(A, j, i + start)[1];
      SUB(A, j, i + start)[0] = tmp[0];
      SUB(A, j, i + start)[1] = tmp[1];
    }
}

extern void   fft_transpose_and_swap(double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int topLefta_r, signed int topLefta_c, signed int topLeftb_r, signed int topLeftb_c, signed int bSize) {
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  int i, j;
  for (i = 0; i < bSize; ++i) {
    for (j = 0; j < bSize; ++j) {
      if ((topLefta_r != topLefta_c) ||
          (j > i)) {
        fftw_complex tmp;
        tmp[0] = SUB(A, topLefta_r + i, topLefta_c + j)[0];
        tmp[1] = SUB(A, topLefta_r + i, topLefta_c + j)[1];
        SUB(A, topLefta_r + i, topLefta_c + j)[0] = SUB(A, topLeftb_r + j, topLeftb_c + i)[0];
        SUB(A, topLefta_r + i, topLefta_c + j)[1] = SUB(A, topLeftb_r + j, topLeftb_c + i)[1];
        SUB(A, topLeftb_r + j, topLeftb_c + i)[0] = tmp[0];
        SUB(A, topLeftb_r + j, topLeftb_c + i)[1] = tmp[1];
      }
    }
  }
}

/* transform row i, for i0 <= i < i1 */
extern void   fft_execute_dft(jlong plan, double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int i0, signed int i1) {
  int i;
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  for (i = i0; i < i1; ++i) {
    fftw_execute_dft((fftw_plan) plan, &SUB(A, i, 0), &SUB(A, i, 0));
  }
}

extern void   fft_bytwiddle(double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int i0, signed int i1, signed int j0, signed int j1, signed int N) {
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  int i, j;
  for (i = i0; i < i1; ++i)
    for (j = j0; j < j1; ++j) {
      double ar = SUB(A, i, j)[0];
      double ai = SUB(A, i, j)[1];
      #if 0
      double theta = (2.0 * M_PI / N) * i * j;
      double c = cos(theta);
      double s = sin(theta);
      #else
      int ij = i * j;
      int ij0 = ij % SQRTN;
      int ij1 = ij / SQRTN;
      double c0 = tw0[ij0][0], s0 = tw0[ij0][1];
      double c1 = tw1[ij1][0], s1 = tw1[ij1][1];
      double c = c0 * c1 - s0 * s1;
      double s = c0 * s1 + s0 * c1;
      #endif
      SUB(A, i, j)[0] = ar * c + ai * s;
      SUB(A, i, j)[1] = ai * c - ar * s;
    }
}

static double dabs(double x)
{
     return (x < 0) ? -x : x;
}
extern void   fft_start(double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int sqrtn, signed int n) {
  SQRTN = sqrtn;    /* without start, do this differently!!! */
  N = n;            /* without start, do this differently!!! */
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  tw0 = fftw_malloc(SQRTN * sizeof(*tw0));
  tw1 = fftw_malloc(SQRTN * sizeof(*tw1));
  int i;
  for (i = 0; i < SQRTN; ++i) {
     tw0[i][0] = cos((2 * M_PI / N) * i);
     tw0[i][1] = sin((2 * M_PI / N) * i);
     tw1[i][0] = cos((2 * SQRTN * M_PI / N) * i);
     tw1[i][1] = sin((2 * SQRTN * M_PI / N) * i);
  }
  A_debug = fftw_malloc(N * sizeof(*A));
  /* debug plan using FFTW for the whole thing */
  fftw_plan plan_debug = fftw_plan_dft_1d(N, A, A_debug, -1, FFTW_ESTIMATE);
  /* compute FFT using FFTW from A into A_debug */
  fftw_execute(plan_debug);
}

extern void   fft_check(double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int n) {
  int i,j,k;
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  for (i = 0; i < SQRTN; ++i) 
    for (j = 0; j < SQRTN; ++j) 
      for (k = 0; k < 1; ++k) {
        double err = 
          dabs(SUB(A, i, j)[k] - SUB(A_debug, i, j)[k]) /
          (dabs(SUB(A, i, j)[k]) + dabs(SUB(A_debug, i, j)[k]));
        if (err > 1.0e-7)
          printf("BUG %g %g %g\n", err,
                 SUB(A, i, j)[k], SUB(A_debug, i, j)[k]);
      }
  fftw_free(A_debug);
  fftw_free(tw0);
  fftw_free(tw1);
}

