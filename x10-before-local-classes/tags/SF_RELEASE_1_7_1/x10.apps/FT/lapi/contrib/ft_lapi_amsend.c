/****************************************************************************
 *
 *   File: ft_lapi.c:
 *   
 *   Author: Mark Stephenson, Vipin Sachdeva
 *   Date: Wed May  2 10:23:00 2007
 *
 *   Function: This is a LAPI implementation of FT. This code is *heavily* 
 *   adapted from the Berkeley UPC code.  For some reason, MPI and LAPI
 *   are not working well together, and hence the "BENCHMARK" version
 *   that does not use any MPI calls (and therefore forgoes checksums).
 *
 ***************************************************************************/


#include <inttypes.h>
#include<lapi.h>
#include <sys/time.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include "params.h"
#include "ft.h"

/***********************************************************************************
 *
 *  LAPI global variables.
 *
 **********************************************************************************/

int THREADS;
int TID;

lapi_info_t info;
lapi_handle_t handle;
void **hdr_handlr_list;
ComplexPtr_t update_buf_begin;

#define RC(statement) \
{ \
   int rc; \
   if (rc = statement) {\
       fprintf(stderr, #statement " rc = %d, line %d\n", rc, __LINE__); \
       exit (-1); \
   }\
}

#define FT_BARRIER() timer_profile(T_BARRIER_WAIT, FT_TIME_BEGIN);      \
		     LAPI_Gfence(handle);		                \
		     timer_profile(T_BARRIER_WAIT, FT_TIME_END)


void*
receive_update (lapi_handle_t *hndl, 
		void *uhdr, 
		uint *uhdr_len, 
		ulong *msg_len,
		compl_hndlr_t **ucomp, 
		void **uinfo)
/***********************************************************************************
 *
 *  An interrupt handler that is invoked on the target to funnel the data to
 *  the correct location.
 *
 **********************************************************************************/
{
  int ix = *((int*)uhdr);

  *ucomp = NULL;
  return (void*) &(update_buf_begin[ix]);
}


/***********************************************************************************
 *
 *  MPI macros and variables.  Don't include these if we're benchmarking.
 *
 **********************************************************************************/
#ifndef BENCHMARK

#include <mpi.h>

int MPI_THREADS;
int MPI_TID;

#define MAKE_TAG(p,i)  ((unsigned int)((((unsigned int)p)<<16) | (unsigned int)(i)))
#define MAKE_RTAG(p,i)	MPI_ANY_TAG

#define MPI_SAFE(fncall) do {     \
   int retcode = (fncall);        \
   if (retcode != MPI_SUCCESS) {  \
     fprintf(stderr, "MPI Error: %s\n  returned error code %i\n", #fncall, retcode); \
     abort();                     \
   }                              \
 } while (0)

#endif


/***********************************************************************************
 *
 *  Define the type of communication-computation overlap we're using.
 *
 **********************************************************************************/


#ifndef FT_COMM
#define FT_COMM FT_COMM_SLABS
#endif

#if FT_COMM == FT_COMM_SLABS
static char *ft_comm_descr = "Slabs";
#define FT_1DFFT FFT1DLocalTranspose
#else
static char *ft_comm_descr = "Pencils";
#define FT_1DFFT FFT1DLocal
#endif

#ifdef M_PI
#  define PI	M_PI
#elif !defined(PI)
#  define PI           3.14159265358979323846  
#endif


/*
 * Configurable defines are
 *
 * TIMERS:	if set to 1, timer information will be printed
 * FT_NONBLOCK:	if set to 1 and Berkeley UPC compiler is used, non-blocking
 *		operations will be used.
 *
 */

void allocSharedPlanes(ComplexPtr_t *local2d, ComplexPtr_t *local1d, void **lp2o, void **lp1o);
void computeInitialConditions(ComplexPtr_t cp);
void FFT2DComm(ComplexPtr_t local2d, ComplexPtr_t local1d, int dir);

void parabolic2(ComplexPtr_t out, ComplexPtr_t in, double *ex, int t, double alpha);
void mult(ComplexPtr_t C, int nsize, double val);
void checksum(ComplexPtr_t C, double *real, double *imag);	

/*
 * Random number generation, as provided by c_randdp.c
 */
extern void   init_seed(double seed);
extern void   init_seed(double seed);
extern double randlc (double *x, double a);
extern void   vranlc(int n, double *x, double a, double *y);
extern double ipow46(double, int, int);


int current_orientation = PLANES_ORIENTED_X_Y_Z;
int dims[3][3];

/*
 * Index Map Function: Allows to abstract away indexing details
 * Args: in: original x  orignal y  original z
 * Input arguments are assumed to represent the global view. That is z refers
 * to actual plane in z... the function will figure out what plane that locally
 * means Args: out: new x new y new z based on changing cube
 */
							
#define CDIM(i)	(dims[current_orientation][i])
#define indexmap(i,j,k)	(i*CDIM(1) + j + (k%(CDIM(2)/THREADS))*(CDIM(0)*CDIM(1)))
#define NEXT_ORIENTATION ((current_orientation+1)%3)

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

/***************************************/
void switch_view() {
  current_orientation = NEXT_ORIENTATION;
}

void set_view(int new_orientation) {
  current_orientation = new_orientation;
}

void dump_values(ComplexPtr_t in, char* str) {
  int i,j,k;
  int dim0, dim1, dim2;
  dim0 = dims[current_orientation][0];
  dim1 = dims[current_orientation][1];
  dim2 = dims[current_orientation][2];
  for(k = TID*(dim2/THREADS); k<(TID+1)*(dim2/THREADS); k++) {
    for(i=0; i<dim0; i++) {
      for(j=0; j<dim1; j++) {
	printf("%d> %s: %d %d %d = %f %f\n", TID, str, i, j, k, 
	       in[indexmap(i,j,k)].real, in[indexmap(i,j,k)].imag);
      }
    }
  }
}


void dump_values_orig(ComplexPtr_t in, char* str) {
  int i,j,k;
  int dim0, dim1, dim2;
  dim0 = NX;
  dim1 = NY;
  dim2 = NZ;
  for(k = TID*(dim2/THREADS); k<(TID+1)*(dim2/THREADS); k++) {
    for(i=0; i<dim0; i++) {
      for(j=0; j<dim1; j++) {
	printf("%d> %s: %d %d %d = %f %f\n", TID, str, i, j, k, 
	       in[origindexmap(i,j,k)].real, in[origindexmap(i,j,k)].imag);
      }
    }
  }
}

#ifndef BENCHMARK
/* Allocate main timer structure on T0 */
void
print_timers_T0()
{
  int i;
  
  double    avg;
  double mflops;
  /* Update all timers on T0 */
  double FTTimers_T0[T_NUMTIMERS]; 
  double total_timers[T_NUMTIMERS];

  for (i=0; i < T_NUMTIMERS; i++)
    total_timers[i] = timer_val(i);
  
  MPI_Reduce(total_timers, FTTimers_T0, T_NUMTIMERS, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);

  if (MPI_TID == 0) {
    char buf[64];

    for (i=0; i < T_NUMTIMERS; i++) {
      avg = FTTimers_T0[i]/(1.0e6*MPI_THREADS);
      printf(" 0> %32s: %10.4f (%10.4f)s\n", timer_descr(i), avg, 
	     ((double)FTTimers_T0[i])/(1.0e6));
    } 
	
    avg = (double) (FTTimers_T0[T_TOTAL] / (1.0e6*MPI_THREADS));
	
    mflops = (1.0e-6*((double)NX*NY*NZ) *
	      (14.8157 + 7.19641*log((double)NX*NY*NZ)
	       + (5.23518 + 7.21113*log((double)NX*NY*NZ))*MAX_ITER)) /avg;
    snprintf(buf, 64, "%d Threads MFlops Rate", MPI_THREADS);
    printf(" 0> %32s: %10.4f mflops\n", buf, mflops);
  }
}
#endif

void
print_local_timers ()
{
  int i;
  
  double avg;
  double mflops;
  /* Update all timers on T0 */
  double FTTimers_T0[T_NUMTIMERS]; 

  printf (" 0> Benchmark version for process %d\n", TID);

  for (i=0; i < T_NUMTIMERS; i++) {
    printf(" 0> Process %d, %32s: %10.4f s\n", TID,
	   timer_descr(i), 
	   ((double)timer_val(i)/(1.0e6)));
  }  
}


int main(int argc, char **argv) {
    
  /* Local Planes */    
  ComplexPtr_t    localPlanes2d; 
  ComplexPtr_t    localPlanes1d;
  ComplexPtr_t    V;

  void *lp2o, *lp1o, *Vo, *exo; 
  double  *ex;

#ifndef BENCHMARK
  double  checksum_real[MAX_ITER];
  double  checksum_imag[MAX_ITER];
#endif  

  int	    iter;
  int saved_orientation;
  int i,p;

  /*
   * We only support a 1d layout, for now
   */
  dims[0][0] = NX;
  dims[0][1] = NY+CPAD_COLS;
  dims[0][2] = NZ;

  dims[1][0] = NY;
  dims[1][1] = NZ+CPAD_COLS;
  dims[1][2] = NX;

  dims[2][0] = NZ;
  dims[2][1] = NX+CPAD_COLS;
  dims[2][2] = NY;

  /** LAPI initialization */
  bzero(&info, sizeof(lapi_info_t));
  RC((LAPI_Init(&handle, &info)));
  RC((LAPI_Qenv(handle, TASK_ID, &TID)));
  RC((LAPI_Qenv(handle, NUM_TASKS, &THREADS)));
  RC((LAPI_Senv(handle, ERROR_CHK, 0)));
  hdr_handlr_list = (void**)malloc(sizeof(void*)*THREADS);
  LAPI_Address_init(handle, (void*)receive_update, hdr_handlr_list);
    
#ifndef BENCHMARK
  /** MPI initialization process */
  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &MPI_THREADS);
  MPI_Comm_rank(MPI_COMM_WORLD, &MPI_TID);
  MPI_Errhandler_set(MPI_COMM_WORLD, MPI_ERRORS_RETURN);
#endif

  /*
   * Allocate memory in shared space, cache THREAD allocations and return our
   * local pointer in the shared space.  Then initialize the pointers to our
   * local data and all other thread's pointers to their data.
   */
  allocSharedPlanes(&localPlanes2d, &localPlanes1d, &lp2o, &lp1o);
  update_buf_begin = localPlanes1d;

  FFTInit(THREADS, dims, FT_COMM, localPlanes2d, localPlanes1d);

  V = (ComplexPtr_t) malloc_align(&Vo, SIZEOF_COMPLEX*MAX_PADDED_SIZE);
  ex = (double *) 
    malloc_align(&exo, sizeof(double)*(NX/THREADS)*NY*NZ);
   
   
  init_exp(ex, 1.0e-6, THREADS, TID, NX, NY, NZ);
  /*
   * Run the entire problem once to make sure all the data is touched. THis
   * reduces variable startup costs, which is important for short benchmarks
   * (AS IS from the original Fortrain MPI FT benchmark)
   */
  computeInitialConditions(localPlanes2d);

  FFT2DComm(localPlanes2d, localPlanes1d, FFT_FWD);
  LAPI_Gfence(handle);

  FT_1DFFT(localPlanes1d, localPlanes2d, FFT_FWD, PLANES_ORIENTED_X_Y_Z);
  LAPI_Gfence(handle);
    
  /*
   * Start over from the beginning and benchmark everything
   */
    
  if (TID == 0) {
    printf("\n 0> LAPI NAS FT (Comm=%s FFTBackend=%s"
	   " Class=%c Procs=%d)\n"
	   " 0> %s\n\n", ft_comm_descr, FFTName, class_id_char, THREADS, 
	   class_id_str);
    fflush(stdout);
  }

  LAPI_Gfence(handle);

  timer_clear();

  timer_total(T_TOTAL, FT_TIME_BEGIN);
  set_view(PLANES_ORIENTED_X_Y_Z);
    
  timer_profile(T_SETUP, FT_TIME_BEGIN);
  computeInitialConditions(localPlanes2d);
  init_exp(ex, 1.0e-6, THREADS, TID, NX, NY, NZ);
  timer_profile(T_SETUP, FT_TIME_END);

  /*
   * Compute 2D FFTs over local planes and distribute the results to all
   * threads in preparation for their 1d FFT
   */

  FFT2DComm(localPlanes2d, localPlanes1d, FFT_FWD);
  FT_1DFFT(localPlanes1d, V, FFT_FWD, current_orientation);

  switch_view();
  saved_orientation = current_orientation;

  for (iter=1; iter<=MAX_ITER; iter++) {
      
    set_view(saved_orientation);
    parabolic2(localPlanes2d, V, ex, iter, 1.0e-6);

    FFT2DComm(localPlanes2d, localPlanes1d, FFT_BWD);
    FT_1DFFT(localPlanes1d, localPlanes2d, FFT_BWD, current_orientation);
    switch_view();

#ifndef BENCHMARK
    checksum(localPlanes2d, &(checksum_real[iter-1]), &(checksum_imag[iter-1]));      
    if (TID==0) {
      fprintf(stdout, " 0> %30s %2d: %#17.14g %#17.14g\n",
	      "Checksum", iter, checksum_real[iter-1], checksum_imag[iter-1]);
      fflush(stdout);
    }
#endif
  }

  timer_total(T_TOTAL, FT_TIME_END);
  LAPI_Gfence(handle);

#ifndef BENCHMARK
  if(TID==0) {
    checksum_verify(NX, NY, NZ, MAX_ITER, checksum_real, checksum_imag);
  }

  print_timers_T0();
#else
  print_local_timers();
#endif

#ifndef BENCHMARK
    MPI_Finalize();
#endif

  LAPI_Term(handle);

  free(hdr_handlr_list);
  return 0;
}

#if FT_COMM == FT_COMM_SLABS
/**************************************************************************
 *
 *   The SLABS version.
 *
 *************************************************************************/

void    
FFT2DComm(ComplexPtr_t local2d, ComplexPtr_t local1d, int dir)
{
  int	  p, i, t;
  int	  dim0, dim1, dim2;
  size_t  plane_size;
  int	  CHUNK_SZ;
  int     ix;

  dim0 = CDIM(0);
  dim1 = CDIM(1);
  dim2 = CDIM(2);
  plane_size = dim0*dim1;
  CHUNK_SZ = (dim0/THREADS)*dim1;

  FT_BARRIER ();

  for (p=0; p < dim2/THREADS; p++) {
    ComplexPtr_t Pxy_b = local2d + plane_size*p;

    timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);
    FFT2DLocalCols(Pxy_b, dir, current_orientation, 
		   &dims[current_orientation][0]);
    timer_profile(T_FFT1DCOLS, FT_TIME_END);

    for (t = 0; t < THREADS; t++) {
      int           thread = (TID + t) % THREADS;
      int	    numrows = dim0 / THREADS;
      ComplexPtr_t  Pxy = Pxy_b + thread*CHUNK_SZ;

      timer_profile(T_FFT1DROWS, FT_TIME_BEGIN);
      for (i = 0; i < numrows; i++)
	FFT2DLocalRow(Pxy+i*dim1, dir, current_orientation, 
		      &dims[current_orientation][0]);
      timer_profile(T_FFT1DROWS, FT_TIME_END);
      
      ix = (TID*dim2/THREADS * CHUNK_SZ + p*CHUNK_SZ);
	       
      timer_profile(T_EXCH, FT_TIME_BEGIN);

      RC(LAPI_Amsend(handle, thread, 
		     hdr_handlr_list[thread],
		     &ix,
		     sizeof(int), &(Pxy[0]), 
		     CHUNK_SZ*SIZEOF_COMPLEX,
		     NULL, NULL, NULL));
      
      timer_profile(T_EXCH, FT_TIME_END);
    }	
  }

  timer_profile(T_EXCH_WAIT, FT_TIME_BEGIN);
  LAPI_Gfence (handle);
  timer_profile(T_EXCH_WAIT, FT_TIME_END);
}

#else

/**************************************************************************
 *
 *   The Pencils version.
 *
 *************************************************************************/

void    
FFT2DComm(ComplexPtr_t local2d, ComplexPtr_t local1d, int dir)
{
  int		p, i, t;
  int dim0, dim1, dim2;
  int	slab_sz;

  dim0 = dims[current_orientation][0];
  dim1 = dims[current_orientation][1];
  dim2 = dims[current_orientation][2];

  slab_sz = dim0/THREADS*dim1;
    
  FT_BARRIER ();
    
  for (p=0; p < dim2/THREADS; p++) {
    ComplexPtr_t Pxy_b = local2d + dim0*dim1*p;
	
    timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);
    FFT2DLocalCols(Pxy_b, dir, current_orientation, &dims[current_orientation][0]);
    timer_profile(T_FFT1DCOLS, FT_TIME_END);

    for (i = 0; i < dim0/THREADS; i++) {
      for (t = 0; t < THREADS; t++) {
	int		    thread = (TID + t) % THREADS;
	ComplexPtr_t    Pxy = Pxy_b + thread*slab_sz + i*dim1;

	timer_profile(T_FFT1DROWS, FT_TIME_BEGIN);
	FFT2DLocalRow(Pxy, dir, current_orientation, &dims[current_orientation][0]);
	timer_profile(T_FFT1DROWS, FT_TIME_END);

	timer_profile(T_EXCH, FT_TIME_BEGIN);

	LAPI_Put(handle, thread, dim1*SIZEOF_COMPLEX,
		 hdr_handlr_list[thread] + 
		 (TID*dim2/THREADS*dim1 + p*dim1 + i*dim1*dim2)*
		 SIZEOF_COMPLEX,
		 Pxy, NULL, NULL, NULL);

	timer_profile(T_EXCH, FT_TIME_END);
      }
    }
  }

  timer_profile(T_EXCH_WAIT, FT_TIME_BEGIN);
  LAPI_Gfence (handle);
  timer_profile(T_EXCH_WAIT, FT_TIME_END);
}

#endif


/* 
 * Each thread requires twice the size of its partition of the 3D CUBE.
 * 
 * If the FT Cube has 128 planes, each thread has 128/THREADS planes of
 * NX*NY elements, and must allocate two of these.
 * 1. One contiguous chunk of memory to compute 128/THREADS 2D FFTs
 * 2. One contiguous chunk of memory to hold the result of the all-to-all
 *    transpose from the previous 2D FFT.  This memory will be locally
 *    transposed across the X-Z axis and a set of 1D FFTs is computed.
 *
 * Each thread allocates 2*NZ/THREADS of NX*NY complexes and updates the
 * shared pointer in a directory of shared pointers located at 0.  Once all
 * threads have updated the directory at 0, they each cache a copy of
 * shared pointers to the first plane on each thread.
 *
 * Planes (0..PLANE-1) are used to calculate the 2D FFT.
 * Planes (PLANE..2*PLANE-1) are used to distributed the results of the
 *                           local 2D FFTs to all processors.  The 2D
 *                           communication is overlapped with computation.
 * 
 */

void
allocSharedPlanes(ComplexPtr_t *local2d, ComplexPtr_t *local1d, void **lp2o, void **lp1o)
{
  ComplexPtr_t    alloc;

  alloc = malloc_align(lp2o, (MAX_PADDED_SIZE+3) * SIZEOF_COMPLEX);
  *local2d = (alloc + 3);

  *local1d = malloc_align(lp1o, MAX_PADDED_SIZE * SIZEOF_COMPLEX);

  if (*local2d == NULL || *local1d == NULL) {
    fprintf(stderr, "Failed to allocate %ld bytes of memory for operations... quitting\n", 
	    16*((NZ/THREADS)*2*NXp*NYp * SIZEOF_COMPLEX));
    exit(1);
  }

  return;
}

/*
 * Compute initial conditions in NZ/THREADS planes starting at cp;
 *
 */
void 
computeInitialConditions(ComplexPtr_t cp)
{

  /*--------------------------------------------------------------------
    c Fill in array u0 with initial conditions from 
    c random number generator 
    c-------------------------------------------------------------------*/

  double  x0, start, an;
  double  *tmp;
  void *tmp_orig;
  int	    i, j, k, pid;
  size_t  plane_off = NX*NY;

  ComplexPtr_t	c0;
  tmp = (double*) malloc_align(&tmp_orig, sizeof(double)*2*NX);
  start = SEED;

  /*--------------------------------------------------------------------
    c Jump to the starting element for our first plane.
    c
    c 
    c-------------------------------------------------------------------*/
  init_seed(start);
  
  pid = TID * NZ/THREADS;	   /* First plane for this thread */

  /*
   * For plane IDs greater than 0, the random number generator needs to be
   * advanced to the correct offset
   */

  an    = ipow46 (A, 2*NX, pid * NY );
  randlc (&start, an);
  an = ipow46 (A, 2 * NX , NY);
	

  /*--------------------------------------------------------------------
    c Go through by z planes filling in one square at a time.
    c-------------------------------------------------------------------*/
   
    
  for (k = 0; k < NZ/THREADS; k++) {
    x0 = start;	
    c0 = cp + k*NX*NYp;

    for (j=0; j<NY; j++) {
      double *tmp_ptr;
      vranlc (2 * NX, &x0, A, tmp);
      tmp_ptr = tmp;
	    
      for(i=0; i<NX; i++) {
	c0[i*(NYp)+j].real = tmp_ptr[i*2+0]; 	
	c0[i*(NYp)+j].imag = tmp_ptr[i*2+1]; 
      }
    }
    if(k!=(NZ/THREADS - 1)) randlc(&start, an);
  }	
  free(tmp_orig);
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
  
  timer_profile(T_EVOLVE, FT_TIME_BEGIN);

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

  timer_profile(T_EVOLVE, FT_TIME_END);
}

/* will be used during the checksum */

#ifndef BENCHMARK
void checksum(ComplexPtr_t C, double *real, double *imag)
{
  int j, q, r, s;
  double    c[2] = {0.0, 0.0}; 
  double sum[2] = {0.0, 0.0}; 
  
  int proc;
  ComplexPtr_t tmp;

  timer_profile(T_CHECKSUM, FT_TIME_BEGIN);

  for (j = 1; j <= 1024; ++j) {
    q = j % NX;
    r = (3*j) % NY;
    s = (5*j) % NZ;
      
    proc = getowner(q,r,s);

    if (MPI_TID==proc) {
      tmp = C + origindexmap(q,r,s);/*(q*NY+r+(s%planes_per_proc)*NX*NY);*/
	
      c[0] += (tmp->real);
      c[1] += (tmp->imag);

    } 
  }

  MPI_Reduce(c, sum, 2, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
  
  *real = ((sum[0]/NX)/NY)/NZ;
  *imag = ((sum[1]/NX)/NY)/NZ;

  timer_profile(T_CHECKSUM, FT_TIME_END);
}
#endif
