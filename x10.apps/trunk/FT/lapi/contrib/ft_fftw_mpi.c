/* MPI Version of the UPC code. Same operations as UPC version but uses 
   MPI communication library
   
*/

#include <inttypes.h>
#ifdef __INTEL_COMPILER
#include <stdint.h>
#endif

#include <sys/time.h>
#include <stdio.h>
#include <math.h>
#include <assert.h>
#include <string.h>
#include <mpi.h>
#include <stdlib.h>

#include "params.h"
#include "ft.h"
#include "fftw_mpi.h"

#ifdef M_PI
#  define PI	M_PI
#elif !defined(PI)
#  define PI           3.14159265358979323846  
#endif

#define MPI_SAFE(fncall) do {     \
   int retcode = (fncall);        \
   if (retcode != MPI_SUCCESS) {  \
     fprintf(stderr, "MPI Error: %s\n  returned error code %i\n", #fncall, retcode); \
     abort();                     \
   }                              \
 } while (0)


/*
 * Configurable defines are
 *
 * TIMERS:	if set to 1, timer information will be printed
 * FT_NONBLOCK:	if set to 1 and Berkeley UPC compiler is used, non-blocking
 *		operations will be used.
 *
 */

#ifndef FT_PROFILE
  #define FT_PROFILE 1
#endif

#ifndef FT_COMM
  #define FT_COMM FT_COMM_SLABS
#endif

#if FT_COMM == FT_COMM_PENCILS
  static char *ft_comm_descr = "Pencils";
  #define FT_1DFFT FFT1DLocal
#elif FT_COMM == FT_COMM_SLABS
  static char *ft_comm_descr = "Slabs";
  #define FT_1DFFT FFT1DLocalTranspose
#else
  static char *ft_comm_descr = "Alltoall";
  #define FT_1DFFT FFT1DLocalTranspose
#endif
  
/*MPI Specific stuff same name as UPC to simplify things*/
int THREADS;
int MYTHREAD;
/**************/

typedef struct tagFFTWOrientationInfo{
  int loc_nz; /*number planes before transpose*/
  int loc_nz_start; /*the starting plane before transpose*/
  int loc_ny_t; /*num planes of new orientation after transpose*/
  int loc_ny_start_t; /*start plane of newly oriented planes after transpose*/
  int total_local_size; /*max amount of data that will ever be stored locally
			  Can be used to allocated temporary buffer space*/
} fftw_orientation_info_t;

fftw_orientation_info_t state_after_forward;

void	allocSharedPlanes(ComplexPtr_t *local2d, ComplexPtr_t *local1d, void **lp2o, void **lp1o);
void    computeInitialConditions(ComplexPtr_t cp);
void    FFT2DComm(ComplexPtr_t local2d, ComplexPtr_t local1d, int dir);

/* functions to actually perform after FFTs */
void	init_exp(double *ex, double alpha);

void parabolic2(ComplexPtr_t out, ComplexPtr_t in, double *ex, int t, double alpha);
void mult(ComplexPtr_t C, int nsize, double val);
void checksum(ComplexPtr_t C, double *real, double *imag);	

/*
 * Random number generation, as provided by c_randdp.c
 */
extern void   init_seed(double seed);

extern void   init_seed(double seed);
extern double randlc (double *x, double a);
extern void vranlc(int n, double *x, double a, double *y);
extern double ipow46(double, int, int);



int dims[3];

#define FT_BARRIER do { timer_profile(T_BARRIER_WAIT, FT_TIME_BEGIN); \
		        MPI_Barrier(MPI_COMM_WORLD);		      \
			timer_profile(T_BARRIER_WAIT, FT_TIME_END); } while (0)


/*
 * Index Map Function: Allows to abstract away indexing details
 * Args: in: original x  orignal y  original z
 * Input arguments are assumed to represent the global view. That is z refers
 * to actual plane in z... the function will figure out what plane that locally
 * means Args: out: new x new y new z based on changing cube
 */
							
#define CDIM(i)	(dims[i])
#define indexmap(i,j,k)	(i*CDIM(1) + j + (k%(CDIM(2)/THREADS))*(CDIM(0)*CDIM(1)))
#define getowner(x,y,z) (z/(NZ/THREADS))

/***************************************/

void dump_values(ComplexPtr_t in, char* str) {
  int i,j,k;

  for(k = (MYTHREAD*(NZ/THREADS)) ; k<(MYTHREAD+1)*(NZ/THREADS); k++) {
    for(i=0; i<NX; i++) {
      for(j=0; j<NY; j++) {
	printf("%d> %s %d %d %d =  %f %f\n", MYTHREAD, str, i, j, k, in[indexmap(i,j,k)].real, in[indexmap(i,j,k)].imag);
      }
    }
  }
}


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

  if (MYTHREAD == 0) {
	char buf[64];

	for (i=0; i < T_NUMTIMERS; i++) {
	    avg = FTTimers_T0[i]/(1.0e6*THREADS);
	    printf(" 0> %32s: %10.4f (%10.4f)s\n", timer_descr(i), avg, 
	           ((double)FTTimers_T0[i])/(1.0e6));
	} 
	
	avg = (double) (FTTimers_T0[T_TOTAL] / (1.0e6*THREADS));
	
	mflops = (1.0e-6*((double)NX*NY*NZ) *
		 (14.8157 + 7.19641*log((double)NX*NY*NZ)
		 + (5.23518 + 7.21113*log((double)NX*NY*NZ))*MAX_ITER)) /avg;
	snprintf(buf, 64, "%d Threads MFlops Rate", THREADS);
	printf(" 0> %32s: %10.4f mflops\n", buf, mflops);
    }
}
const char *FFTName = "FFTW2_MPI";
int main(int argc, char **argv) {
    
  /* Local Planes */    
  ComplexPtr_t    localPlanes2d; 
  ComplexPtr_t    localPlanes1d;
  ComplexPtr_t    V;

  void *lp2o, *lp1o, *Vo, *exo; 

  double  checksum_real[MAX_ITER];
  double  checksum_imag[MAX_ITER];
  double  *ex;
  
  int	    iter;
  int saved_orientation;
  fftwnd_mpi_plan forward_plan;
  fftwnd_mpi_plan reverse_plan;
  
  int i,j,k;
    /*
     * We only support a 1d layout, for now
     */
  dims[0] = NX;
  dims[1] = NY;
  dims[2] = NZ;
  
 

  /** MPI initialization process */
  MPI_Init(&argc, &argv);
  MPI_Comm_size(MPI_COMM_WORLD, &THREADS);
  MPI_Comm_rank(MPI_COMM_WORLD, &MYTHREAD);
  MPI_Errhandler_set(MPI_COMM_WORLD, MPI_ERRORS_RETURN);
 
    /*
     * Allocate memory in shared space, cache THREAD allocations and return our
     * local pointer in the shared space.  Then initialize the pointers to our
     * local data and all other thread's pointers to their data.
     */
    allocSharedPlanes(&localPlanes2d, &localPlanes1d, &lp2o, &lp1o);

    /*fftw_mpi plans*/
    /* One major thing to note is that our ordering of NX, NY, and NZ varies from what FFTW lables these dimensions 
       our NZ is FFTW's NX
       our NX is FFTW's NY
       our NY is FFTW's NZ

       in FFTW the least varying dimension is NX, second is NY, and most varying is NZ
       in our code least varying dimension is NZ, second is NX, and most is NY
    */
    forward_plan = fftw3d_mpi_create_plan(MPI_COMM_WORLD,
					  NZ, NX, NY,
					  FFTW_FORWARD,
					  FFTW_MEASURE);
    
    reverse_plan = fftw3d_mpi_create_plan(MPI_COMM_WORLD,
					  NX, NZ, NY,
					  FFTW_BACKWARD,
					  FFTW_MEASURE);
    
    V = (ComplexPtr_t) malloc_align(&Vo, SIZEOF_COMPLEX*(NX*NY*(NZ/THREADS)));
    ex = (double *) 
	malloc_align(&exo, sizeof(double)*(NX/THREADS)*NY*NZ);
   
    init_exp(ex, 1.0e-6);
    /*
     * Run the entire problem once to make sure all the data is touched. THis
     * reduces variable startup costs, which is important for short benchmarks
     * (AS IS from the original Fortrain MPI FT benchmark)
     */

    computeInitialConditions(localPlanes2d);
    /*fftw forward 3d here*/
    
    fftwnd_mpi(forward_plan, 1, (fftw_complex*) localPlanes2d,  
	       (fftw_complex*)localPlanes1d,
	       FFTW_TRANSPOSED_ORDER);



    MPI_Barrier(MPI_COMM_WORLD);    /* All threads must have completed their 1D FFTs */
 
    /*
     * Start over from the beginning and benchmark everything
     */

    if (MYTHREAD == 0) {
	printf("\n 0> Berkeley MPI NAS FT 2.3 (NonBlocking=YES Comm=%s FFTBackend=%s"
	       " Class=%c Procs=%d)\n"
	       " 0> %s\n\n", ft_comm_descr, FFTName, class_id_char, THREADS, 
	       class_id_str);
	fflush(stdout);
    }
    
    MPI_Barrier(MPI_COMM_WORLD);
    
    timer_clear();

    timer_total(T_TOTAL, FT_TIME_BEGIN);

    
    timer_profile(T_SETUP, FT_TIME_BEGIN);
    computeInitialConditions(localPlanes2d);
    init_exp(ex, 1.0e-6);
    timer_profile(T_SETUP, FT_TIME_END);

    /*
     * Compute 2D FFTs over local planes and distribute the results to all
     * threads in preparation for their 1d FFT
     */
   timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);	 
    /*fftw forward here*/
    fftwnd_mpi(forward_plan, 1, (fftw_complex*) localPlanes2d, 
	       (fftw_complex*)localPlanes1d,
	       FFTW_TRANSPOSED_ORDER);
   timer_profile(T_FFT1DCOLS, FT_TIME_END);
    FT_BARRIER;

  
    for (iter=1; iter<=MAX_ITER; iter++) {
      
  
      parabolic2(V, localPlanes2d, ex, iter, 1.0e-6);
      
      /*REVERSE FFT HERE*/
timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);
      fftwnd_mpi(reverse_plan, 1, (fftw_complex*) V, 
		 (fftw_complex*)localPlanes1d, 
		 FFTW_TRANSPOSED_ORDER);
      timer_profile(T_FFT1DCOLS, FT_TIME_END);
  
      checksum(V, &(checksum_real[iter-1]), &(checksum_imag[iter-1]));
      
      if (MYTHREAD==0)
	fprintf(stdout, " 0> %30s %2d: %#17.14g %#17.14g\n",
	        "Checksum", iter, checksum_real[iter-1], checksum_imag[iter-1]);
	fflush(stdout);
    }
    timer_total(T_TOTAL, FT_TIME_END);
    MPI_Barrier(MPI_COMM_WORLD);   
    if(MYTHREAD==0) {
      checksum_verify(NX, NY, NZ, MAX_ITER, checksum_real, checksum_imag);
    }
    print_timers_T0();
 
    MPI_Finalize();

    return 0;
}


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

    *local1d =  
	    malloc_align(lp1o, MAX_PADDED_SIZE * SIZEOF_COMPLEX);

    if (!MYTHREAD)
	printf("local2d=%p, local1d=%p\n", *local2d, *local1d);
  
    if (*local2d == NULL || *local1d == NULL) {
      fprintf(stderr, "Failed to allocate %ld bytes of memory for operations... quitting", 
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
  
    pid = MYTHREAD * NZ/THREADS;	   /* First plane for this thread */

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

void init_exp(double *ex, double alpha)
{
  double   kon;
  int i,j,k,n;
  int it,jt,kt;
  int i_start, i_end;
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
    for (k=0; k < NZ; k++) {
      if (k >= NZ/2)
	kt = k - NZ;
      else
	kt = k;
      for(j=0; j < NY; j++) {
	if (j >= NY/2)
	  jt = j - NY;
	else
	  jt = j;
	jj = jt*jt;
	ex[n++] = exp(kon*(ii + jj + kt*kt));
      }
    }
  }
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

  for (k=0; k < NX/THREADS; k++) {
    kk = k*NZ*NY;
    for (i=0; i < NZ; i++) {
      ii  = kk + i*NY;
      for (j = 0; j < NY; j++) {
	jj = ii + j;
	mult = ex[jj];
	 in[jj].real *= mult;
	out[jj].real = in[jj].real;
	 in[jj].imag *= mult;
	out[jj].imag = in[jj].imag;
      }
    }
  }

  timer_profile(T_EVOLVE, FT_TIME_END);
}
#if 0
void init_exp(double *ex, double alpha)
{
  double   kon;
  int i,j,k,n;
  int it,jt,kt;
  int k_start, k_end;
  double ii,jj,kk;
  
  n=0;
  kon  = -4.*alpha*PI*PI;
  k_start = (NZ/THREADS)*MYTHREAD;
  k_end = (NZ/THREADS)*(MYTHREAD+1);
  
  for(k=k_start; k<k_end; k++) {
    if (k >= NZ/2)
      kt = k - NZ;
    else
      kt = k;
    kk=kt*kt;
    for(i=0; i<NX; i++) {
      if (i >= NX/2)
	it = i - NX;
      else
	it = i;
      ii = it*it;
      for(j=0; j<NY; j++) {
	if (j >= NY/2)
	  jt = j - NY;
	else
	  jt = j;
	jj = jt*jt;
      	ex[n++] = exp(kon*(ii + jj + kk));
      }
    }
  }
}

void parabolic2(ComplexPtr_t out, ComplexPtr_t in, 
	        double *ex, int t, double alpha)
{
  int i,j,k,ii,ii2,jj,kk;
  int dim2 = CDIM(2);
  int dim1 = CDIM(1); /* Padded dimension */
  int dim1p= CDIM(1);
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
#endif
/* will be used during the checksum */

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

      /*      ind = myind(s, r, q, n3, n2, n1); */
      
      if (MYTHREAD==proc) {
	tmp = C + indexmap(q,r,s);/*(q*NY+r+(s%planes_per_proc)*NX*NY);*/
	
	
	c[0] += (tmp->real);
	c[1] += (tmp->imag);

     } 
  }

  MPI_Reduce(c, sum, 2, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
  
  *real = ((sum[0]/NX)/NY)/NZ;
  *imag = ((sum[1]/NX)/NY)/NZ;

  timer_profile(T_CHECKSUM, FT_TIME_END);
}

