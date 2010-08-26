#include "fftDist_x10stub.c"
#include <fftw3.h>
#include <math.h>

/***
    FFT_HOME=$HOME/fftw/fftw-3.1.2/
    slibclean; xlc_r -O3 -qnolm -bM:SHR -bnoentry -bexpall -bmaxdata:0x80000000 -I$JAVA_HOME/bin/include -I$JAVA_HOME/include -o libxsupport.a xsupport.c -I$FFT_HOME/api -L$FFT_HOME/.libs -l fftw3 -lm
    FFT_HOME=$HOME/fftw/fftw-3.1.2_64/
    slibclean; xlc_r -q64 -O3 -qnolm -bM:SHR -bnoentry -bexpall -blpdata -bmaxdata:0x8000000000 -I$JAVA_HOME/bin/include -I$JAVA_HOME/include -o libxsupport_64.a xsupport.c -I$FFT_HOME/api -L$FFT_HOME.libs -l fftw3 -lm
    (C) Copyright IBM Corp. 2006
***/

extern jlong  fftDist_fftw_plan_dft_1d(signed int SQRTN, double* A1_x10PoInTeR, int* A1_x10DeScRiPtOr, double* A2_x10PoInTeR, int* A2_x10DeScRiPtOr, signed int m1, signed int what) {
  //return (int64_t) fftw_plan_dft_1d(SQRTN, (fftw_complex *) A1_x10PoInTeR, (fftw_complex *) A2_x10PoInTeR, m1, what);
  return (jlong) fftw_plan_dft_1d(SQRTN, (fftw_complex *) A1_x10PoInTeR, (fftw_complex *) A2_x10PoInTeR, m1, what);
}

static int SQRTN;
static int N;

/* row-major array indexing */
#define SUB(A, i, j) (A)[(i)*SQRTN+(j)]



/* transform row i, for i0 <= i < i1 */
extern void   fftDist_executedft(jlong plan, double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int i0, signed int i1) {
  int i;
  fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  for (i = i0; i < i1; ++i) {
    fftw_execute_dft((fftw_plan) plan, &SUB(A, i, 0), &SUB(A, i, 0));
  }
}

static double dabs(double x)
{
     return (x < 0) ? -x : x;
}

//extern void   fftDist_start(jlong plan, double* A_x10PoInTeR, int* A_x10DeScRiPtOr, signed int sqrtn, signed int n) {
extern void   fftDist_start(signed int sqrtn, jlong n) {

  SQRTN = sqrtn;    /* without start, do this differently!!! */
  N = n;            /* without start, do this differently!!! */
  //fftw_complex *A = (fftw_complex *) A_x10PoInTeR;
  
  /* debug plan using FFTW for the whole thing 
     fftw_plan plan_debug = fftw_plan_dft_1d(N, A, A, -1, FFTW_ESTIMATE);*/
  /* compute FFT using FFTW from A into A */
  //fftw_execute((fftw_plan)plan);
}

extern double fftDist_getPI(){return M_PI;}


