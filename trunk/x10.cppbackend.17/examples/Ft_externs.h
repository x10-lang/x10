#ifndef _FT_H___
#define _FT_H___

/*
 * Create our own Complex type, making sure its name is mangled enough not to
 * collide with existing compiler complex types (that we don't want to rely
 * on).
 */

typedef 
struct __ComPLex {
    double real;
    double imag;
}
_ComPLex_t;
#define SIZEOF_COMPLEX	sizeof(_ComPLex_t)
typedef           _ComPLex_t *ComplexPtr_t;

/****INDEXING STUFF....*/
/* first index specifies current_orientation..
   the second index is the ith dimension
   if oriented X_Y_Z 0->NX 1->NY 2->NZ
   if oriented Y_Z_X 0->NY 1->NZ 2->NX
   if oriented Z_X_Y 0->NZ 1->NX 2->NY
*/
#define PLANES_ORIENTED_X_Y_Z 0 /*assumes original data layout (each plane is 
				  row major with the z dimension appearing z*NX*NY */ 
#define PLANES_ORIENTED_Y_Z_X 1 /*assumes that Y Z planes are contiguous in the memory*/
#define PLANES_ORIENTED_Z_X_Y 2 /*assumes that Z X planes are contiguous in the memory*/

#define CPAD_SLABS  0	// Cache-pad slice elements by 1 element 
/*
  #ifndef CPAD_COLS
  #define CPAD_COLS 0 
  #endif
*/


#ifndef MAX
#define MAX(a,b) ((a) > (b) ? (a) : (b))
#endif

#define NXp (NX+CPAD_COLS)
#define NYp (NY+CPAD_COLS)
#define NZp (NZ+CPAD_COLS)

/*macro that returns the maximum size of the cubes we'll need*/
#define MAX_PADDED_SIZE (MAX((NXp*NY*(NZ/THREADS)), (MAX((NX*NYp*(NZ/THREADS)), \
(NX*NY*((NZ/THREADS)+CPAD_COLS))))))

#define FT_COMM_SLABS	0
#define FT_COMM_PENCILS	1
#define FT_COMM_ALL2ALL 2
#define FT_COMM_LAPI_SLABS 3

#ifndef FT_COMM
  #define FT_COMM FT_COMM_SLABS
#endif


/*
 * Ft implementations implement these 4 functions
 * fft_fftw.c, fft_kube-gustavson.c
 */
#define FFT_FWD 1
#define FFT_BWD 0
extern void FFTInit(int threads, int dims[3][3], int comm, ComplexPtr_t local2d, ComplexPtr_t local1d, int);
extern void FFTFinalize();
extern void FFT2DLocal(ComplexPtr_t inout, int dir, int orientation, int *dims);
extern void FFT2DLocalCols(ComplexPtr_t in, int dir, int orientation, int *dims, int);
extern void FFT2DLocalRow(ComplexPtr_t in, int dir, int orientation, int *dims, int);
extern void FFT2DLocalRows(int nrows, ComplexPtr_t inout, int dir, int orientation, int *dims, int);
extern void FFT1DLocalTranspose(ComplexPtr_t local1d, ComplexPtr_t local2d, int dir, int orient, int);
extern void FFT1DLocal(ComplexPtr_t local1d, ComplexPtr_t local2d, int dir, int orient, int);
extern void checksum_verify (int d1, int d2, int d3, int nt, double *real_sums, double *imag_sums);
extern const char *FFTName;

extern void init_exp(double*, double, int);

/*
 * Random number generation, as provided by c_randdp.c
 */
void   init_seed(double seed);
double randlc (double *x, double a);
void vranlc(int n, double *x, double a, double *y);
double ipow46(double, int, int);

/*
 * Timers
 */

#define T_SETUP		    0
#define T_FFT1DCOLS	    1
#define T_FFT1DROWS	    2
#define T_FFT1DPOST	    3
#define T_EVOLVE	    4
#define T_CHECKSUM	    5
#define T_EXCH		    6
#define T_EXCH_WAIT	    7
#define T_BARRIER_WAIT	    8
#define T_BARRIER_CHK       9
#define T_TOTAL		   10 
#define T_NUMTIMERS	   11

#define FT_TIME_BEGIN	0
#define FT_TIME_END	1

extern uint64_t    timer_val(int tid);
extern void	   timer_update(int tid, int action);
extern void	   timer2_update(int tid, int idx, int action);
extern void	   timer2_dump(int cols, int rows);
extern void	   timer_clear();
extern char	  *timer_descr(int tid);

/* Always enable timers unless otherwise stated */
#ifndef FT_PROFILE
  #define FT_PROFILE 1
#endif

#if FT_PROFILE
#  define timer_profile(tid,action) timer_update(tid,action)
#else
#  define timer_profile(tid,action) 
#endif

#define timer_total(tid,action)   timer_update(tid,action)

/* 
 * Malloc aligned on cache lines
 */
#define CACHE_LINE_SZ   128
#define CACHE_LINE_MASK (CACHE_LINE_SZ-1)

extern inline void *malloc_align(void **orig, size_t size);
/*
inline void *malloc_align(void **orig, size_t size)
{
  char *p;
  *orig = (void *) malloc(size+CACHE_LINE_SZ-1);

  p = (char*) *orig;
  p = (char*) (((uintptr_t)p + CACHE_LINE_SZ - 1)&(~CACHE_LINE_MASK));

  return (void*)p;
}
*/
/*
 * A debugging routine.
 */
extern inline void rowcheck (ComplexPtr_t row, int len, int tid, int rid, int pid);
 /*
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
 */

#endif
