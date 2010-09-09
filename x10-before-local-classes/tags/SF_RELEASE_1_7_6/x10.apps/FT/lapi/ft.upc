#include <inttypes.h>
#include <stdlib.h>
#include <math.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>
#include <unistd.h>

#include <upc_relaxed.h>
#include <upc_collective.h>

#include "params.h"
#include "ft.h"

#define CHECKNOW(str,C)	do {		\
			double real, imag;  \
			upc_barrier;\
			checksum(C, &real, &imag); \
			if (!MYTHREAD) \
			printf("Line %d %s; %f %f\n", __LINE__, str, real, imag); \
			upc_barrier;\
			} while (0)

#ifdef M_PI
#  define PI	M_PI
#elif !defined(PI)
#  define PI           3.14159265358979323846  
#endif

/*
 * Configurable defines are
 *
 * FT_NONBLOCK:	if set to 1 and Berkeley UPC compiler is used, non-blocking
 *		operations will be used.
 */

#ifdef __BERKELEY_UPC__
  #ifndef FT_NONBLOCK
    #define FT_NONBLOCK 1
  #endif
#else /* No non-blocking UPC ops */
  #if FT_NONBLOCK 
   #error Only Berkeley UPC compiler has support for non-blocking ops
  #endif
  typedef int bupc_handle_t;
  #define bupc_waitsync(h)
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
  
  
typedef shared []  _ComPLex_t *shComplexPtr_t;

void allocSharedPlanes(shComplexPtr_t *remote1dptrs, shComplexPtr_t *local2d);
void computeInitialConditions(ComplexPtr_t cp);
void FFT2DComm(shComplexPtr_t local2d, shComplexPtr_t *remote1dPtr, ComplexPtr_t X, int dir);
#ifndef USE_EXP_ARRAY
#define USE_EXP_ARRAY 1
#endif 

/* functions to actually perform after FFTs */
#if USE_EXP_ARRAY
void	init_exp(double *ex, double alpha, int loc_threads, int loc_mythread, int lNX, int lNY, int lNZ);
#endif
void	parabolic2(ComplexPtr_t out, ComplexPtr_t in, double *ex, int t, double alpha);
void	checksum(ComplexPtr_t C, double *real, double *imag);	
/* for broken constant translation*/
double dNX;
double dNY;
double dNZ;
/* will be used during the checksum */
static shared [] _ComPLex_t *shared checksum_sumsT0;
static int   *threadPermuteMap = NULL;
void init_threadPermuteMap();

static shared _ComPLex_t *shPlanes0;
static shared _ComPLex_t *shPlanes1;

/* ALL2ALL version requires extra allocation */
static shared _ComPLex_t *shExchange = NULL;

int current_orientation=PLANES_ORIENTED_X_Y_Z;
int dims[3][3];
int print_alltimers = 0;

#define FT_BARRIER do { timer_profile(T_BARRIER_WAIT, FT_TIME_BEGIN); \
		        upc_barrier;				      \
			timer_profile(T_BARRIER_WAIT, FT_TIME_END); } while (0)

/*
 * Index Map Function: Allows to abstract away indexing details
 * Args: in: original x  orignal y  original z
 * Input arguments are assumed to represent the global view. That is z refers to actual plane in z... the function will figure out what plane that locally means
 * Args: out: new x new y new z based on changing cube
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
	return 0;
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
  for(k = MYTHREAD*(dim2/THREADS); k<(MYTHREAD+1)*(dim2/THREADS); k++) {
    for(i=0; i<dim0; i++) {
      for(j=0; j<dim1; j++) {
	printf("%d> %s: %d %d %d = %f %f\n", MYTHREAD, str, i, j, k, 
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
  for(k = MYTHREAD*(dim2/THREADS); k<(MYTHREAD+1)*(dim2/THREADS); k++) {
    for(i=0; i<dim0; i++) {
      for(j=0; j<dim1; j++) {
	printf("%d> %s: %d %d %d = %f %f\n", MYTHREAD, str, i, j, k, 
	  in[origindexmap(i,j,k)].real, in[origindexmap(i,j,k)].imag);
      }
    }
  }
}

void
print_timers_T0()
{
    int i, j;

    /* Allocate main timer structure on T0 */
    static shared [] uint64_t   *shared FTTimers_T0;
    uint64_t mytimers[T_NUMTIMERS];

    uint64_t *timers_all;
    uint64_t  sum,min,max,tm;
    double    avg;
    double mflops;
    char  *p;

    if (MYTHREAD == 0)
	FTTimers_T0 = upc_alloc(THREADS*T_NUMTIMERS*sizeof(uint64_t));

    if (print_alltimers) {
      char myhostname[256];
      double mytime;
      gethostname(myhostname, 256);

      for (i=0; i < T_NUMTIMERS; i++) {
        mytime = ((double)timer_val(i))/1.0e6;
        printf("%3d (%30s)> %32s: %10.4f\n", 
            MYTHREAD, myhostname, timer_descr(i), mytime);
      }
    }

    /* Update all timers on T0 */
    for (i=0; i < T_NUMTIMERS; i++) 
	mytimers[i] = timer_val(i);

    upc_barrier;

    upc_memput(&FTTimers_T0[MYTHREAD*T_NUMTIMERS], mytimers, 
	       sizeof(uint64_t)*T_NUMTIMERS);

    upc_barrier;

    if (MYTHREAD == 0) {
	timers_all = (uint64_t *) FTTimers_T0;
	char buf[64];

	#if FT_PROFILE
	for (i=0; i < T_NUMTIMERS; i++) {
	    sum = max = 0;
	    min = (uint64_t) -1;
	    for (j=0; j < THREADS; j++) {
		tm = timers_all[(j*T_NUMTIMERS)+i];
		sum += tm;
		min = MIN(min, tm);
		max = MAX(max, tm);
	    }

	    avg = (double)(sum) / (double)(THREADS*1.0e6);

	    printf(" 0> %32s: %10.4f (%10.4f,%10.4f)s\n", timer_descr(i), avg,
		((double)min)/1.0e6,((double)max)/1.0e6);
	    fflush(stdout);
	} 
	#endif

	sum = 0;
	avg = (double) (timers_all[0+T_TOTAL] / (1.0e6));
	mflops = (double)(1.0e-6*((double)NX*NY*NZ) *
		 (14.8157 + 7.19641*log((double)NX*NY*NZ)
		 + (5.23518 + 7.21113*log((double)NX*NY*NZ))*MAX_ITER)) /avg;
	snprintf(buf, 64, "%d Threads MFlops Rate", THREADS);
	printf(" 0> %32s: %10.4f mflops\n", buf, mflops);
	fflush(stdout);

	upc_free(FTTimers_T0);
    }
}

int
main()
{
    shComplexPtr_t *remotePlanes1d;    /* Array of pointers to remote data */
    shComplexPtr_t	myPlanes;     /* Initial local shared allocation */

    /* Local Planes */    
    shComplexPtr_t  shlocalPlanes2d; 
    ComplexPtr_t    localPlanes2d; 
    ComplexPtr_t    localPlanes1d; 
    ComplexPtr_t    V,X;
    void	    *Vorig,*Xorig;

    double *ex;
    void   *exOrig;

    double  checksum_real[MAX_ITER];
    double  checksum_imag[MAX_ITER];

    int	    iter;
    int	    saved_orientation;
    
    /*
     * Allocate memory in shared space, cache THREAD allocations and return our
     * local pointer in the shared space.  Then initialize the pointers to our
     * local data and all other thread's pointers to their data.
     */
    remotePlanes1d = 
	(shComplexPtr_t *) calloc(THREADS,sizeof(shComplexPtr_t));
    allocSharedPlanes(remotePlanes1d, &shlocalPlanes2d);
    localPlanes2d = (ComplexPtr_t) shlocalPlanes2d;
    localPlanes1d = (ComplexPtr_t) remotePlanes1d[MYTHREAD];

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
    
    
    V = (ComplexPtr_t) 
      //malloc_align(&Vorig, SIZEOF_COMPLEX*NX*NYp*(NZ/THREADS));
      malloc_align(&Vorig, SIZEOF_COMPLEX*MAX_PADDED_SIZE);
    X = (ComplexPtr_t) 
	malloc_align(&Xorig, SIZEOF_COMPLEX*(MAX_PADDED_SIZE+3*NYp*NZp)*2);
    X += 3*NX;
#if USE_EXP_ARRAY    
    ex = (double *) malloc_align(&exOrig,
		      sizeof(double)*NX/THREADS*NY*NZ*3);
#else 
    ex = NULL;
#endif
    if (!MYTHREAD) 
	printf("local2d=%p, local1d=%p\n", localPlanes2d, localPlanes1d);
   
    if (MYTHREAD == 0) {
	printf("\n 0> Berkeley UPC NAS FT 2.3 (NonBlocking=%s Comm=%s FFTBackend=%s"
	       " Class=%c Procs=%d)\n"
	       " 0> %s\n\n", FT_NONBLOCK ? "YES" : "NO", ft_comm_descr, FFTName, 
	       class_id_char, THREADS, class_id_str);
	fflush(stdout);
    }

    /* Allow fft implementation to initialize its data */
    FFTInit(THREADS, dims, FT_COMM, localPlanes2d, localPlanes1d);


    /* Allocate checksums array at T0 */
    if (MYTHREAD == 0)
	checksum_sumsT0 = upc_alloc(THREADS*SIZEOF_COMPLEX);
    /*
     * Run the entire problem once to make sure all the data is touched. THis
     * reduces variable startup costs, which is important for short benchmarks
     * (AS IS from the original Fortrain MPI FT benchmark)
     */
    computeInitialConditions(localPlanes2d);
    init_threadPermuteMap();
	
    //CHECKNOW("Initial Conditions", localPlanes2d);
#if USE_EXP_ARRAY    
    init_exp(ex, 1.0e-6, THREADS, MYTHREAD, NX, NY, NZ);
#endif
    
    current_orientation = PLANES_ORIENTED_X_Y_Z;
    
    FFT2DComm(shlocalPlanes2d, remotePlanes1d, X, FFT_FWD);
    //CHECKNOW("FT2D Forward", localPlanes2d);
    //CHECKNOW("FT2D Inner1d", localPlanes1d);
    FT_1DFFT((ComplexPtr_t) remotePlanes1d[MYTHREAD], 
	     localPlanes2d, FFT_FWD, PLANES_ORIENTED_X_Y_Z);
    switch_view();
    //CHECKNOW("FT1D Forward", localPlanes2d);

    /* 
     * Start over from the beginning and benchmark everything
     */

    upc_barrier;
    timer_clear();
    /*
     * Optionally have each thread print its timers.  This generates a 
     * lot of output on large runs but is useful to detect node imbalance
     */
    {
      char *p = bupc_getenv("FT_ALLTIMERS");
      if (p != NULL && *p != '\0')
        print_alltimers = 1;
    }

    timer_total(T_TOTAL, FT_TIME_BEGIN);
    set_view(PLANES_ORIENTED_X_Y_Z);

    timer_profile(T_SETUP, FT_TIME_BEGIN);
    computeInitialConditions(localPlanes2d);
#if USE_EXP_ARRAY
    init_exp(ex, 1.0e-6, THREADS, MYTHREAD, NX, NY, NZ);
#endif     
    timer_profile(T_SETUP, FT_TIME_END);
 
    /*
     * Compute 2D FFTs over local planes and distribute the results to all
    * threads in preparation for their 1d FFT
     */

    FFT2DComm(shlocalPlanes2d, remotePlanes1d, X, FFT_FWD);
    FT_1DFFT((ComplexPtr_t) remotePlanes1d[MYTHREAD], V, 
	     FFT_FWD, current_orientation);
    FT_BARRIER;
    switch_view();
    saved_orientation = current_orientation;
    //CHECKNOW("V", V);


    for (iter=1; iter<=MAX_ITER; iter++) {
      
      set_view(saved_orientation);
      //CHECKNOW("V", V);
      //CHECKNOW("before parabolic", localPlanes2d);
      parabolic2(localPlanes2d, V, ex, (double) iter, 1.0e-6); 	
      //CHECKNOW("parabolic", localPlanes2d);

      FFT2DComm(shlocalPlanes2d, remotePlanes1d, X, FFT_BWD);
      FT_1DFFT((ComplexPtr_t) remotePlanes1d[MYTHREAD], 
	       localPlanes2d, FFT_BWD, current_orientation);
      switch_view();
      /* Checksum has a barrier and solves the global dependency on
       * remotePlanes1d for the next iteration */
      checksum(localPlanes2d, &(checksum_real[iter-1]), &(checksum_imag[iter-1]));
      
      if (MYTHREAD==0)
	fprintf(stdout, " 0> %30s %2d: %#17.14g %#17.14g\n", 
	        "Checksum", iter, checksum_real[iter-1], checksum_imag[iter-1]); 
	fflush(stdout);
    }
    
    timer_total(T_TOTAL, FT_TIME_END);
    
    upc_barrier;
    if(MYTHREAD==0) {
      checksum_verify(NX,NY,NZ, MAX_ITER, checksum_real, checksum_imag);
    }
    print_timers_T0();
    FFTFinalize();
    
    if (MYTHREAD == 0)
	upc_free(checksum_sumsT0);

    //upc_free(myPlanes);
    //free(remotePlanes1d);
    //free(Vorig);
    //free(exOrig);
    //free(dims);

    upc_barrier;
    upc_global_exit(0);
}

#if 0 && __UPC_COLLECTIVE__
/*
 * The all-to-all variants of the global exchange use the TranposeGlobal and
 * TransposeLocal
 */
/*
 * Need to restructure application to allocate from the global heap so we can
 * use all_exchange
 */
void
TransposeGlobal(int dim0, int dim1, int dim2, shared _ComPLex_t *shIn, shComplexPtr_t *remote1dPtr)
{
    int	     i, t, thread;
    size_t   perthread = (dim0/THREADS)*dim1*(dim2/THREADS);

    /* We want a barrier after the transpose */
    timer_profile(T_EXCH, FT_TIME_BEGIN);
    upc_all_exchange((shared void *)remote1dPtr[0], shIn, 
                      perthread*SIZEOF_COMPLEX, UPC_IN_NOSYNC | UPC_OUT_ALLSYNC);
    timer_profile(T_EXCH, FT_TIME_END);
}
#else
void
TransposeGlobal(int dim0, int dim1, int dim2, shared _ComPLex_t *shIn, shComplexPtr_t *remote1dPtr)
{
    int	     i, t, thread;
    int	     handle_idx = 0;
    size_t   perthread = (dim0/THREADS)*dim1*(dim2/THREADS);
    ComplexPtr_t    in = (ComplexPtr_t) &shIn[MYTHREAD];

    bupc_handle_t    *handles;

    handles = (bupc_handle_t*) malloc(sizeof(bupc_handle_t) * THREADS);

    timer_profile(T_EXCH, FT_TIME_BEGIN);
    for (t = 0; t < THREADS; t++) {
	int		thread = (MYTHREAD + t) % THREADS;
	ComplexPtr_t    Pxy = in + thread*perthread;
	shComplexPtr_t  Pxz;

	Pxz = remote1dPtr[thread] + perthread*MYTHREAD;
	#if FT_NONBLOCK
	    handles[handle_idx] = 
		bupc_memput_async(Pxz, Pxy, perthread*SIZEOF_COMPLEX);
	    handle_idx++;
	#else
	    upc_memput(Pxz, Pxy, perthread*SIZEOF_COMPLEX);
	#endif
    }
    if (FT_NONBLOCK) {
	for (i = 0; i < handle_idx; i++)
	    bupc_waitsync(handles[i]);
    }
    free(handles);
    FT_BARRIER;
    timer_profile(T_EXCH, FT_TIME_END);
}
#endif

void
Transpose2Local(int dim0, int dim1, int dim2, ComplexPtr_t in, ComplexPtr_t out)
{
    int i, j;
    int planesz = dim0*dim1;
    int slab01 = (dim0/THREADS)*dim1;
    int planes2 = dim2/THREADS;

    for (j = 0; j < planes2; j++) {
	for (i = 0; i < THREADS; i++) {
	memcpy(out + i*planes2*slab01 + j*slab01, 
	       in + i*slab01 + j*planesz,
	       slab01*SIZEOF_COMPLEX);
	}
    }
}

#if FT_COMM == FT_COMM_ALL2ALL
void
FFT2DComm(shComplexPtr_t shlocal2d, shComplexPtr_t *remote1dPtr, ComplexPtr_t Xo, int dir)
{
    int	    p,i;
    int	    dim0,dim1,dim2;
    int	    loc_dim[3];

    ComplexPtr_t     local2d = (ComplexPtr_t) shlocal2d;
    bupc_handle_t   *handles;

    dim0 = CDIM(0);
    dim1 = CDIM(1);
    dim2 = CDIM(2);
    loc_dim[0] = dim0;
    loc_dim[1] = dim1;
    loc_dim[2] = dim2;

    if (shExchange == NULL) 
        shExchange = upc_all_alloc(THREADS, 
				   SIZEOF_COMPLEX*NXp*NYp*(NZ/THREADS));

    for (p = 0; p < dim2/THREADS; p++) {
	ComplexPtr_t Pxy_b = local2d + dim0*dim1*p;

	timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);
	FFT2DLocalCols(Pxy_b, dir, current_orientation, loc_dim);
	timer_profile(T_FFT1DCOLS, FT_TIME_END);

	timer_profile(T_FFT1DROWS, FT_TIME_BEGIN);
	for (i = 0; i < dim0; i++) {
	    FFT2DLocalRow(Pxy_b+i*dim1, dir, current_orientation, loc_dim);
	}
	timer_profile(T_FFT1DROWS, FT_TIME_END);
    }


    Transpose2Local(dim0,dim1,dim2,local2d,(ComplexPtr_t)&shExchange[MYTHREAD]);
    TransposeGlobal(dim0,dim1,dim2,shExchange,remote1dPtr);
    /* Barrier done in TranseposeGlobal */
}
#else

void
FFT2DComm(shComplexPtr_t shlocal2d, shComplexPtr_t *remote1dPtr, ComplexPtr_t X, int dir)
{
    int		i,t,p;
    int dim0, dim1, dim2;
    size_t	elem_off;
    int CHUNK_SZ;
    int slab_off;
    int slab_sz;
    int handle_idx=0;
    bupc_handle_t   *handles;
    int loc_dim[3];
    ComplexPtr_t     local2d = (ComplexPtr_t) shlocal2d;
    
    dim0 = dims[current_orientation][0];
    dim1 = dims[current_orientation][1];
    dim2 = dims[current_orientation][2];
    loc_dim[0] = dim0;
    loc_dim[1] = dim1;
    loc_dim[2] = dim2;
    elem_off = dim0*dim1;

    slab_off = (dim0/THREADS)*dim1+CPAD_SLABS;
    slab_sz  = (dim0/THREADS)*dim1;
    
    handles = (bupc_handle_t*) malloc(
		sizeof(bupc_handle_t) * dim0 * (dim2/THREADS));    

    for (p=0; p < dim2/THREADS; p++) {
	ComplexPtr_t Pxy_b = local2d + elem_off*p;

	/*
	 * 1. NZ/THREADS 2D FFTs in the xy dimension
	 */
	timer_profile(T_FFT1DCOLS, FT_TIME_BEGIN);
	FFT2DLocalCols(Pxy_b, dir, current_orientation, loc_dim);
	timer_profile(T_FFT1DCOLS, FT_TIME_END);

	//if (p==dim2/THREADS-1) CHECKNOW("after cols", local2d);
	
	#if FT_COMM == FT_COMM_SLABS
	for (t = 0; t < THREADS; t++) {
	    //int		    thread = (MYTHREAD + t) % THREADS;
	    int		    thread = threadPermuteMap[t];
	    int		    numrows = dim0 / THREADS;
	    ComplexPtr_t    Pxy = Pxy_b + thread*slab_sz;
	    shComplexPtr_t  Pxz;

	    /* Do dim1 FFTs */
	    timer_profile(T_FFT1DROWS, FT_TIME_BEGIN);
	    for (i = 0; i < numrows; i++)
		FFT2DLocalRow(Pxy+i*dim1, dir, current_orientation, loc_dim);
	    timer_profile(T_FFT1DROWS, FT_TIME_END);

	    timer_profile(T_EXCH, FT_TIME_BEGIN);
	    Pxz = remote1dPtr[thread] + MYTHREAD*dim2/THREADS*slab_off+p*slab_off;
	    #if FT_NONBLOCK
		handles[handle_idx] = 
		    bupc_memput_async(Pxz, Pxy, slab_sz*SIZEOF_COMPLEX);
		handle_idx++;
            #else
		upc_memput(Pxz, Pxy, slab_sz*SIZEOF_COMPLEX);
	    #endif
	    timer_profile(T_EXCH, FT_TIME_END);
	}
	#else /* COMM is PENCILS */
	for (i = 0; i < dim0/THREADS; i++) {
	    for (t = 0; t < THREADS; t++) {
		//int		thread = threadPermuteMap[t];
		int		thread = (MYTHREAD + t) % THREADS;
		//int		thread = t;
		ComplexPtr_t    Pxy = Pxy_b + thread*slab_sz + i*dim1;
		shComplexPtr_t  Pxz;

		timer_profile(T_FFT1DROWS, FT_TIME_BEGIN);
		FFT2DLocalRow(Pxy, dir, current_orientation, loc_dim);
		timer_profile(T_FFT1DROWS, FT_TIME_END);

		Pxz = remote1dPtr[thread] + MYTHREAD*dim2/THREADS*dim1 + 
		      p*dim1 + i*dim1*dim2;

		timer_profile(T_EXCH, FT_TIME_BEGIN);
		#if FT_NONBLOCK
		    handles[handle_idx] =
			bupc_memput_async(Pxz, Pxy, (dim1-CPAD_COLS)*SIZEOF_COMPLEX);
		    handle_idx++;
		#else
		    upc_memput(Pxz, Pxy, (dim1-CPAD_COLS)*SIZEOF_COMPLEX);
		#endif
		timer_profile(T_EXCH, FT_TIME_END);
	    }
	}
	#endif
    }

    if (FT_NONBLOCK) {
	timer_profile(T_EXCH_WAIT, FT_TIME_BEGIN);
	for (i = 0; i < handle_idx; i++)
	    bupc_waitsync(handles[i]);
	timer_profile(T_EXCH_WAIT, FT_TIME_END);
    }
    free(handles);

    FT_BARRIER;
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
allocSharedPlanes(shComplexPtr_t *cachedDir, shComplexPtr_t *local2d)
{
    shared _ComPLex_t *shalloc;
    int	    i = 0, j = 0;
    size_t  allocsz;
    int	    off = 3;

    shComplexPtr_t  shared_p;

    ComplexPtr_t    local_p;
    ComplexPtr_t    temp;
    ComplexPtr_t    local1d;

    shPlanes0 = upc_all_alloc(THREADS, MAX_PADDED_SIZE*SIZEOF_COMPLEX);

    shPlanes1 = upc_all_alloc(THREADS, (MAX_PADDED_SIZE+off)*SIZEOF_COMPLEX);

    /* local1d at base 0, local2d at base 1 */
    for (i = 0; i < THREADS; i++) {
	cachedDir[i] = (shComplexPtr_t) &shPlanes0[i];
    }

    /* local2d padded by an extra NX row */

    shared_p = (shComplexPtr_t) &shPlanes1[MYTHREAD];
    *local2d = shared_p + off;
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

void
init_threadPermuteMap()
{
    int	i, j, temp;

    if (threadPermuteMap == NULL)
	threadPermuteMap = malloc(THREADS*sizeof(int));

    for (i = 0; i < THREADS; i++)
	threadPermuteMap[i] = i;

    srand(MYTHREAD);

    for (i = THREADS-1; i >= 0; i--) {
	j = rand() % (i+1);
	temp = threadPermuteMap[j];
	threadPermuteMap[j] = threadPermuteMap[i];
	threadPermuteMap[i] = temp;
    }

/*
    for (i = 0; i < THREADS; i++)
	printf("%d> %d, %d\n", MYTHREAD, i, threadPermuteMap[i]);
	*/
}
#if USE_EXP_ARRAY

void parabolic2(ComplexPtr_t out, ComplexPtr_t in, 
	        double *ex, int t, double alpha)
{
 	uint64_t i,j,k,ii,ii2,jj,kk;
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
#else 
void parabolic2(ComplexPtr_t out, ComplexPtr_t in, 
	        double *ex, int t, double alpha)
{
  int i,j,k,ii,ii2,jj,kk,pid, it, jt,kt;
  int dim2 = CDIM(2);
  int dim1 = CDIM(1); /* Padded dimension */
  int dim1p= CDIM(1)-CPAD_COLS;
  int dim0 = CDIM(0);
  double mult;
  double kon;
  kon  = -4.*alpha*PI*PI;	
  
  timer_profile(T_EVOLVE, FT_TIME_BEGIN);

  for (k=MYTHREAD*(dim2/THREADS), pid=0; k < (MYTHREAD+1)*(dim2/THREADS); k++, pid++) {
    if(k >= NX/2) {
      kt = k - NX;
    } else {
      kt = k;
    } 
    kk = kt*kt;
    for (i=0; i < dim0; i++) {
      if(i >= NY/2) {
	it = i - NY;
      } else {
	it = i;
      } 
      ii = it*it;
      for (j = 0; j < dim1p; j++) {
	if(j >= NZ/2) {
	  jt = j-NZ;
	} else {
	  jt = j;
	} 
	jj = jt*jt;
	mult = exp(kon*(ii+jj+kk));
	in[i*dim1 + j + pid*dim0*dim1].real *= mult;
	out[i*dim1 + j + pid*dim0*dim1].real = in[i*dim1 + j + pid*dim0*dim1].real;
	in[i*dim1 + j + pid*dim0*dim1].imag *= mult;
	out[i*dim1 + j + pid*dim0*dim1].imag = in[i*dim1 + j + pid*dim0*dim1].imag;
      }
    }
  }

  timer_profile(T_EVOLVE, FT_TIME_END);
}
#endif

void checksum(ComplexPtr_t C, double *real, double *imag)
{
  int j, q, r, s;
  int ind;
  int planes_per_proc = NZ/THREADS;
  double    c_real = 0.0, c_imag = 0.0;
  int proc;
  int i;
  ComplexPtr_t tmp;
  static _ComPLex_t mychk;
  double dNTOTAL;
  #ifndef TREE_REDUCE
    #define TREE_REDUCE 1 
  #endif

  timer_profile(T_CHECKSUM, FT_TIME_BEGIN);
  dNX = NX;
  dNY = NY;
  dNZ = NZ;
  dNTOTAL = dNX * dNY * dNZ;
 /* MUST GO FROM 1 to 1024 and NOT 0 to 1023 otherwise class D breaks*/	
  for (j = 1; j <= 1024; ++j) {
      q = j % NX;
      r = (3*j) % NY;
      s = (5*j) % NZ;
      
      proc = getowner(q,r,s);
      
      if (MYTHREAD==proc) {
	tmp = C + origindexmap(q,r,s);
	/*
	printf("%d, %d> checksum accessing element %d (%d %d %d) dims (%d %d %d), %f %f\n", 
	MYTHREAD, current_orientation, origindexmap(q,r,s), q, r, s, CDIM(0),
	CDIM(1), CDIM(2), tmp->real, tmp->imag);
	*/
	c_real += tmp->real;
	c_imag += tmp->imag;
     } 
  }

  #if TREE_REDUCE
    /* tree reduce is barrier-free, but we need a barrier for data dependencies */
    timer_profile(T_BARRIER_CHK, FT_TIME_BEGIN);
      upc_barrier;
    timer_profile(T_BARRIER_CHK, FT_TIME_END);

  { /* Send checksums to T0 -- using hand-rolled tree reduction */
    typedef struct {
      double value_r;
      double value_i;
      int64_t flag_r;
      int64_t flag_i;
    } bundle_t;
    #ifndef REDUCE_DEGREE
    #define REDUCE_DEGREE 64
    #endif
    #ifndef REDUCE_DEBUG
    #define REDUCE_DEBUG 0
    #endif
    #define MATCH(x,y) (!memcmp(&(x),&(y),sizeof(x)))
    static int phase = 0;
    static shared [2*REDUCE_DEGREE] bundle_t ReduceBuf[THREADS*2*REDUCE_DEGREE];
    shared [REDUCE_DEGREE] bundle_t *thisReduce = (shared [REDUCE_DEGREE] bundle_t *)(ReduceBuf+phase*REDUCE_DEGREE);
    shared [REDUCE_DEGREE] bundle_t *offReduce = (shared [REDUCE_DEGREE] bundle_t *)(ReduceBuf+(!phase)*REDUCE_DEGREE);
    bundle_t *myReduce = (bundle_t*)(thisReduce + REDUCE_DEGREE*MYTHREAD);
    bundle_t *myoffReduce = (bundle_t*)(offReduce + REDUCE_DEGREE*MYTHREAD);

    static int firsttime=1;
    static int parent;
    static int childid;
    static int numchildren;
    static int64_t clearflag;
    static int64_t setflag;
    if (firsttime) {
      firsttime = 0;
      parent = (MYTHREAD+REDUCE_DEGREE-1)/REDUCE_DEGREE - 1; 
      childid = (MYTHREAD-1) % REDUCE_DEGREE;
      int firstchild = MYTHREAD*REDUCE_DEGREE+1;
      int lastchild = (MYTHREAD+1)*REDUCE_DEGREE;
      numchildren = MIN(lastchild+1,THREADS) - MIN(firstchild,THREADS);
      memset(&clearflag,0,sizeof(clearflag));
      memset(&setflag,-1,sizeof(setflag));
      for (int i=0; i < numchildren; i++) {
        myReduce[i].flag_r = clearflag;
        myReduce[i].flag_i = clearflag;
      }
      #if REDUCE_DEBUG
        printf("%i> parent=%i childid=%i numchildren=%i\n",MYTHREAD,parent,childid,numchildren);
      #endif
      upc_barrier;
    }

    bundle_t mybundle; /* setup my bundle */
    mybundle.value_r = c_real;
    mybundle.value_i = c_imag;

    /* clear off-phase landing zone */
    for (int i=0; i < numchildren; i++) {
      myoffReduce[i].flag_r = clearflag;
      myoffReduce[i].flag_i = clearflag;
    }
    
    for (int i=0; i < numchildren; i++) {
      bundle_t childbundle;
      while (1) {
        childbundle = myReduce[i];
        if ((!MATCH(childbundle.flag_r, clearflag) || !MATCH(childbundle.flag_i, clearflag)) && /* child flag not clear */
            (MATCH(childbundle.flag_r,childbundle.value_r) && MATCH(childbundle.flag_r,childbundle.value_r)) || /* flag matches value */
             (MATCH(childbundle.flag_r,setflag) && MATCH(childbundle.flag_i,setflag) &&
              MATCH(childbundle.value_r,clearflag) && MATCH(childbundle.value_i,clearflag))) break;
        bupc_poll();
      }
      #if REDUCE_DEBUG
        printf("%i> reaped child=%i\n",MYTHREAD,i);
      #endif
      mybundle.value_r += childbundle.value_r;
      mybundle.value_i += childbundle.value_i;
    }

    memcpy(&mybundle.flag_r, &mybundle.value_r, sizeof(double));
    memcpy(&mybundle.flag_i, &mybundle.value_i, sizeof(double));
    if (MATCH(mybundle.flag_r,clearflag) && MATCH(mybundle.flag_i,clearflag)) {
      mybundle.flag_r = setflag;
      mybundle.flag_i = setflag;
    }

    if (MYTHREAD == 0) {
      c_real = mybundle.value_r;
      c_imag = mybundle.value_i;
      #if REDUCE_DEBUG
        printf("REDUCE COMPLETE!\n");
      #endif
    } else {
      thisReduce[REDUCE_DEGREE*parent+childid] = mybundle;
      c_real = 0.0;
      c_imag = 0.0;
    }

    phase = !phase;
    #undef MATCH
  }
  #elif __UPC_COLLECTIVE__
  { /* Send checksums to T0 -- using UPC collectives */
    static shared double AllCReal[THREADS];
    static shared double AllCImag[THREADS];
    static shared double RedCReal;
    static shared double RedCImag;

    AllCReal[MYTHREAD] = c_real;
    AllCImag[MYTHREAD] = c_imag;

    /* Input nosync assumes that there is at least one barrier in the
     * computation loop. UPC has no multiple field collective ops like
     * MPI so we actually have to issue two separate reductions. */
    upc_all_reduceD(&RedCReal, AllCReal, UPC_ADD, THREADS, 1, NULL,
		    UPC_IN_MYSYNC | UPC_OUT_NOSYNC);

    upc_all_reduceD(&RedCImag, AllCImag, UPC_ADD, THREADS, 1, NULL,
		    UPC_IN_MYSYNC | UPC_OUT_ALLSYNC);

    if (MYTHREAD == 0) {
      c_real = RedCReal;
      c_imag = RedCImag;
    }
    else {
      c_real = 0.0;
      c_imag = 0.0;
    }
  }
  #else
    mychk.real = c_real;
    mychk.imag = c_imag;

    upc_memput(checksum_sumsT0+MYTHREAD,&mychk,sizeof(mychk));

    /* Barrier is timed in T_CHECKSUM */
    upc_barrier; 
    if (MYTHREAD == 0) {
      ComplexPtr_t    chk_red = (ComplexPtr_t) checksum_sumsT0;
      c_real = 0.0;
      c_imag = 0.0;
      for(i=0; i<THREADS; i++) {
	
	  c_real += chk_red[i].real;
	  c_imag += chk_red[i].imag;
	  chk_red[i].real=chk_red[i].imag=0;
      }
    }
  #endif

  /* we never actually scale teh last step.. so performance hack time :-)*/
  //*real = ((c_real/NX)/NY)/NZ;
  //*imag = ((c_imag/NX)/NY)/NZ;
  *real = c_real / dNTOTAL;
  *imag = c_imag / dNTOTAL;
  timer_profile(T_CHECKSUM, FT_TIME_END);

}
