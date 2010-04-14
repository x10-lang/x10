#include <inttypes.h>
#include <stdio.h>
#include <math.h>
#include <fftw3.h>
//#include "Ft_x10stub.c"
#include "Ft_externs.h"
#include "stdlib.h"
#include <iostream>

#define A (5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0*5.0)
#define SEED 314159265.0
#ifdef M_PI
#  define PI	M_PI
#elif !defined(PI)
#  define PI           3.14159265358979323846  
#endif

/*
 Places in X10 are modeled as threads, not processes.  
 They share the memory space within a single process (the JVM), 
 including the C heap.
//static  int MYTHREAD;
*/

using namespace std;

static int THREADS;
static int OFFSET;
int CPAD_COLS;
static int NX, NY, NZ;
static int current_orientation = PLANES_ORIENTED_X_Y_Z;
static int dims[3][3];

#define CDIM(i)	(dims[current_orientation][i])
#define indexmap(i,j,k)	(i*CDIM(1) + j + (k%(CDIM(2)/THREADS))*(CDIM(0)*CDIM(1)))
#define NEXT_ORIENTATION ((current_orientation+1)%3)

inline void *malloc_align(void **orig, size_t size)
{
  char *p;
  *orig = (void *) malloc(size+CACHE_LINE_SZ-1);

  p = (char*) *orig;
  p = (char*) (((uintptr_t)p + CACHE_LINE_SZ - 1)&(~CACHE_LINE_MASK));

  return (void*)p;
}

inline void 
rowcheck (ComplexPtr_t row, int len, int tid, int rid, int pid)
{
  int i;

  fprintf (stderr, "Thread %d dump starting at %d,%d: ", tid, rid, pid);
  for (i = 0; i < 5; i++)
  {
    fprintf (stderr, "{%4.2f,%4.2f}, ", row[i].real, row[i].imag);
  }
  fprintf (stderr, "\n");
}

int origindexmap(int x, int y, int z) {
  switch(current_orientation) {
  case PLANES_ORIENTED_X_Y_Z:
    return indexmap(x,y,z);
  case PLANES_ORIENTED_Y_Z_X:
    return indexmap(y,z,x);
  case PLANES_ORIENTED_Z_X_Y:
    return indexmap(z,x,y);
  default:
    return indexmap(x,y,z); /*should never get here*/
  }
}

extern int Ft_origindexmap(signed int x, signed int y, signed int z){
  return origindexmap(x, y, z);
}

int getowner(int x, int y, int z) {
  if(current_orientation == PLANES_ORIENTED_X_Y_Z) {
    return (z/(NZ/THREADS));
  } else if(current_orientation == PLANES_ORIENTED_Y_Z_X) {
    return (x/(NX/THREADS));
  } else if(current_orientation == PLANES_ORIENTED_Z_X_Y) {
    return (y/(NY/THREADS));
  }
  return -1;
}

extern int Ft_getowner(signed int x, signed int y, signed int z){
  return getowner(x, y, z);
}

extern void Ft_set_orientation(signed int orientation){
  current_orientation = orientation;
}

/* random number generator: content of c_randdp.c */

/* array initialization and operation */

extern void  Ft_initializeC(signed int numPlace, signed int nx, signed int ny, signed int nz, 
			    signed int offset, signed  int cpad_cols) {
  THREADS = numPlace;
  //MYTHREAD = placeID;
  NX = nx; NY = ny; NZ = nz;
  OFFSET = offset; CPAD_COLS = cpad_cols;
  
  dims[0][0] = NX;
  dims[0][1] = NY+CPAD_COLS;
  dims[0][2] = NZ;

  dims[1][0] = NY;
  dims[1][1] = NZ+CPAD_COLS;
  dims[1][2] = NX;

  dims[2][0] = NZ;
  dims[2][1] = NX+CPAD_COLS;
  dims[2][2] = NY;
}

extern void  Ft_initialize(double* Array_x10PoInTeR, int* Array_x10DeScRiPtOr, signed int size, signed int offset, signed int PID) {
  int MYTHREAD = PID;
  double * da = Array_x10PoInTeR+offset;
  int i; 
  double start, an;
  start=SEED;
  int pid = MYTHREAD * NZ/THREADS;
  init_seed(start);
  an=ipow46(A, 2*NX, pid*NY);
  randlc(&start,an);
  an = ipow46(A, 2*NX, NY);
  for (i =0; i < size; i++) {da[i]=randlc(&start,an);}
}

/*
 * Compute initial conditions in NZ/THREADS planes starting at cp;
 *
 */
 
extern void computeInitialConditions (ComplexPtr_t cp, int PID)
{
  /*--------------------------------------------------------------------
    c Fill in array u0 with initial conditions from 
    c random number generator 
    c-------------------------------------------------------------------*/
  int MYTHREAD = PID;
  double  x0, start, an;
  double  *tmp;
  void    *tmp_orig;
  int	  i, j, k, pid;
  size_t  plane_off = NX*NY;

  ComplexPtr_t	c0;
  tmp = (double*) malloc_align (&tmp_orig, sizeof(double)*2*NX);
  start = SEED;

  /*--------------------------------------------------------------------
    c Jump to the starting element for our first plane.
    c
    c 
    c-------------------------------------------------------------------*/
  init_seed (start);
   
  pid = MYTHREAD * NZ/THREADS;	   /* First plane for this thread */

  /*
   * For plane IDs greater than 0, the random number generator needs to be
   * advanced to the correct offset
   */
  
  an = ipow46 (A, 2*NX, pid * NY );
  randlc (&start, an);
  an = ipow46 (A, 2 * NX , NY);

  /*--------------------------------------------------------------------
    c Go through by z planes filling in one square at a time.
    c-------------------------------------------------------------------*/
  
  //printf("start = %f pid = %d \n", start, pid);
  for (k = 0; k < NZ/THREADS; k++) 
  {
    x0 = start;	
    c0 = cp + k*NX*NYp;

    for (j=0; j<NY; j++) 
    {
      double *tmp_ptr;
      vranlc (2 * NX, &x0, A, tmp);
      tmp_ptr = tmp;
	    
      for(i=0; i<NX; i++) {
	c0[i*(NYp)+j].real = tmp_ptr[i*2+0]; 	
	c0[i*(NYp)+j].imag = tmp_ptr[i*2+1]; 
      }
    }

    if (k!=(NZ/THREADS - 1)) 
      randlc (&start, an);
  }	

  free (tmp_orig);
}

extern void Ft_computeInitialConditions (double* Array_x10PoInTeR, int* Array_x10DeScRiPtOr, signed int PID){
  computeInitialConditions((ComplexPtr_t) (Array_x10PoInTeR+OFFSET), PID);
}

void init_exp(double *ex, double alpha, int PID)
{
  int MYTHREAD =  PID;
  double   kon;
  //double   *exptr;
  int i,j,k,t,n;
  int it,jt,kt;
  int i_start, i_end;
  //double ex_0 = 1.0;
  //double ex_1;
  double prev;
  double ii,jj;
  
  n=0;
  kon  = -4.*alpha*PI*PI;
  i_start = (NX/THREADS)*MYTHREAD;
  i_end = (NX/THREADS)*(MYTHREAD+1);
  

  for(i=i_start; i < i_end; i++) {
    if (i >= NX/2)
      it = i - NX;
    else
      it = i;
    ii = it*it;

    for(j=0; j < NY; j++) {
      if (j >= NY/2)
        jt = j - NY;
      else
        jt = j;
      jj = jt*jt;

      for (k=0; k < NZ; k++) {
        if (k >= NZ/2)
          kt = k - NZ;
	else
	  kt = k;
	ex[n++] = exp(kon*(ii + jj + kt*kt));
      }
    }
  }
}

extern void Ft_init_exp (double* Array_x10PoInTeR, int* Array_x10DeScRiPtOr, double alpha, signed int PID ){
  init_exp(Array_x10PoInTeR, alpha, PID);
}

void parabolic2(ComplexPtr_t out, ComplexPtr_t in, 
	        double *ex, int t, double alpha)
{
  
  int i,j,k,ii,ii2,jj,kk;
  int dim2 = CDIM(2);
  int dim1 = CDIM(1); /* Padded dimension */
  int dim1p= CDIM(1)-CPAD_COLS;
  int dim0 = CDIM(0);
  double mult;
  
  //timer_profile(T_EVOLVE, FT_TIME_BEGIN);

  for (k=0; k < dim2/THREADS; k++) {
    kk = k*dim0*dim1;
    for (i=0; i < dim0; i++) {
      ii  = kk + i*dim1;
      ii2 = k*dim0*dim1p + i*dim1p;
      for (j = 0; j < dim1p; j++) {
	jj = ii + j;
	mult = ex[ii2 + j];
	in[jj].real *= mult;
	out[jj].real = in[jj].real;
	in[jj].imag *= mult;
	out[jj].imag = in[jj].imag;
      }
    }
  }

  //timer_profile(T_EVOLVE, FT_TIME_END);
}

extern void Ft_parabolic2 (double* out_x10PoInTeR, int* out_x10DeScRiPtOr, 
		    double* in_x10PoInTeR, int* in_x10DeScRiPtOr,
		    double* ex_x10PoInTeR, int* ex_x10DeScRiPtOr, signed int t, double alpha ){
  parabolic2( (ComplexPtr_t) (out_x10PoInTeR+OFFSET), (ComplexPtr_t)in_x10PoInTeR, ex_x10PoInTeR, t, alpha);
}

extern void Ft_FFTInit ( signed int comm, double* local2d_x10PoInTeR, int* local2d_x10DeScRiPtOr, 
		    double* local1d_x10PoInTeR, int* local1d_x10DeScRiPtOr, signed int PID){
   FFTInit( THREADS, dims, comm, (ComplexPtr_t) (local2d_x10PoInTeR+OFFSET), (ComplexPtr_t)local1d_x10PoInTeR, PID);
}

extern void Ft_FFTWTest(void){
  int N =32;
  fftw_complex *in, *out;
  in = (fftw_complex*) fftw_malloc(sizeof(fftw_complex) * N);
  out = (fftw_complex*) fftw_malloc(sizeof(fftw_complex) * N);
   printf("array allocated \n");
  fftw_plan plan = fftw_plan_dft_1d(N, in, out, FFTW_FORWARD, FFTW_ESTIMATE);
  fftw_destroy_plan(plan);
  fftw_free(in); fftw_free(out);
}


extern void Ft_FT_1DFFT (signed int ft_comm, double* in_x10PoInTeR, int* in_x10DeScRiPtOr, 
			 double* out_x10PoInTeR, int* out_x10DeScRiPtOr,
			 signed int offset, signed int dir, signed int orientation, signed int PID ){
  if (ft_comm == FT_COMM_SLABS){
    if (offset > 0) 
      FFT1DLocalTranspose((ComplexPtr_t) (in_x10PoInTeR), (ComplexPtr_t) (out_x10PoInTeR+OFFSET), dir, orientation, PID);
    else
      FFT1DLocalTranspose((ComplexPtr_t) (in_x10PoInTeR), (ComplexPtr_t) (out_x10PoInTeR), dir, orientation, PID);
  }
  else
    if (offset > 0)
      FFT1DLocal((ComplexPtr_t) (in_x10PoInTeR), (ComplexPtr_t) (out_x10PoInTeR+OFFSET), dir, orientation, PID);
    else
      FFT1DLocal((ComplexPtr_t) (in_x10PoInTeR), (ComplexPtr_t) (out_x10PoInTeR), dir, orientation, PID);

}

extern void Ft_FFT2DLocalCols (double* local2d_x10PoInTeR, int* local2d_x10DeScRiPtOr, 
			 signed int ComplexOffset, signed int dir, signed int orientation, signed int PID ){
  
  FFT2DLocalCols((ComplexPtr_t) (local2d_x10PoInTeR+OFFSET+2*ComplexOffset), dir, orientation, &dims[orientation][0], PID);
}

extern void Ft_FFT2DLocalRow (double* local2d_x10PoInTeR, int* local2d_x10DeScRiPtOr, 
			 signed int ComplexOffset, signed int dir, signed int orientation, signed int PID ){
  
  FFT2DLocalRow((ComplexPtr_t) (local2d_x10PoInTeR+OFFSET+2*ComplexOffset), dir, orientation, &dims[orientation][0], PID);
}
