#include <inttypes.h>
#include <stdlib.h>
#include <fftw3.h>
#include <assert.h>
#include <string.h>


#if MAKE_FFTW_THREADSAFE
#include <pthread.h>
#endif

#include "ft.h"

static int		ft_dims[9];
static int		ft_threads;

/* 
 * Strided plans to do Columns for 3 possible orientations and either backward
 * or forward
 */

struct fftw_plans_t_{
  fftw_plan    plans_1dcol[3][2]; 
  fftw_plan    plans_1drow[3][2]; 
  fftw_plan    plans_1drow_out[3][2]; 
  fftw_plan    plans_1dfin[3][2]; 
  fftw_plan    plans_1dfin2[3][2]; 
  
  fftw_plan    plans_1dblock[3][2];
  fftw_plan    plans_1dblock2[3][2];

};

typedef struct fftw_plans_t_* fftw_plans_t;
fftw_plans_t *all_fftw_plans;
int myfftw_threadid;
fftw_plans_t myfftw_planset;

#if MAKE_FFTW_THREADSAFE
/*a guess of the number of threads that will be used*/
/*the number keeps going up by 1 when this boundry is crossed*/
#define INIT_NUM_THREADS 16
static int myfftw_num_plans_allocated;
static int myfftw_thread_counter=0;

#endif

#define MAXDIM	    2048
#define FFTBLOCK    16
#define FFTBLOCKPAD 18
#define FT_FFTW_FLAGS	FFTW_MEASURE

/*currently unused*/
#if 0
static ComplexPtr_t ftblock;
#endif

const char *FFTName = "FFTW3";

#define FPTR(x)	((fftw_complex *)(x))
#define PLAN1DROW(x,ptr1,ptr2,dir)  \
	fftw_plan_dft_1d((x-CPAD_COLS),FPTR(ptr1),FPTR(ptr2),dir,FT_FFTW_FLAGS)
#define PLAN1DCOL(x,y,ptr1,ptr2,dir)	    \
	fftw_plan_many_dft(1,&x,(y-CPAD_COLS),FPTR(ptr1),NULL,y,1,FPTR(ptr2),NULL,y,1,dir,FT_FFTW_FLAGS)

#define PLAN1DFIN2(y,z,stride,ptr1,ptr2,dir)	\
	fftw_plan_many_dft(1,&z,y-CPAD_COLS,FPTR(ptr1),NULL,stride,1,FPTR(ptr2),NULL,1,z+CPAD_COLS,dir,FT_FFTW_FLAGS)

#if MAKE_FFTW_THREADSAFE
pthread_mutex_t plan_lock = PTHREAD_MUTEX_INITIALIZER;
#endif


fftw_plans_t make_fftw_plan_set(int total_upc_threads, int dims[3][3], int comm, 
				ComplexPtr_t local2d, ComplexPtr_t local1d) {
  fftw_plans_t ret;

  ret = (fftw_plans_t) malloc(sizeof(struct fftw_plans_t_));
  ret->plans_1dcol[0][0] = PLAN1DCOL(dims[0][0],dims[0][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1dcol[1][0] = PLAN1DCOL(dims[1][0],dims[1][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1dcol[2][0] = PLAN1DCOL(dims[2][0],dims[2][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1dcol[0][1] = PLAN1DCOL(dims[0][0],dims[0][1],local2d,local2d,FFTW_FORWARD);
  ret->plans_1dcol[1][1] = PLAN1DCOL(dims[1][0],dims[1][1],local2d,local2d,FFTW_FORWARD);
  ret->plans_1dcol[2][1] = PLAN1DCOL(dims[2][0],dims[2][1],local2d,local2d,FFTW_FORWARD);
  
  ret->plans_1drow[0][0] = PLAN1DROW(dims[0][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1drow[1][0] = PLAN1DROW(dims[1][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1drow[2][0] = PLAN1DROW(dims[2][1],local2d,local2d,FFTW_BACKWARD);
  ret->plans_1drow[0][1] = PLAN1DROW(dims[0][1],local2d,local2d,FFTW_FORWARD);
  ret->plans_1drow[1][1] = PLAN1DROW(dims[1][1],local2d,local2d,FFTW_FORWARD);
  ret->plans_1drow[2][1] = PLAN1DROW(dims[2][1],local2d,local2d,FFTW_FORWARD);
  
  /*
   * The remaining FFT is done in dims0/THREADS groups of dim1-1 FFTS
   *
   * Under slabs   the stride is dims0/THREADS*dim1
   * Under pencils the stride is dim1
   */
#define FIN2_STRIDE_PENCILS(v) (dims[v][1])
#define FIN2_STRIDE_SLABS(v) ((dims[v][0]/ft_threads)*dims[v][1])
#define FIN2_STRIDE(v) (comm == FT_COMM_PENCILS ? FIN2_STRIDE_PENCILS(v) \
						  : FIN2_STRIDE_SLABS(v))
  ret->plans_1dfin2[0][0] = PLAN1DFIN2(dims[0][1],dims[0][2],FIN2_STRIDE(0),
					   local1d,local2d,FFTW_BACKWARD);
  ret->plans_1dfin2[1][0] = PLAN1DFIN2(dims[1][1],dims[1][2],FIN2_STRIDE(1),
					   local1d,local2d,FFTW_BACKWARD);
  ret->plans_1dfin2[2][0] = PLAN1DFIN2(dims[2][1],dims[2][2],FIN2_STRIDE(2),
					   local1d,local2d,FFTW_BACKWARD);
  ret->plans_1dfin2[0][1] = PLAN1DFIN2(dims[0][1],dims[0][2],FIN2_STRIDE(0),
					   local1d,local2d,FFTW_FORWARD);
  ret->plans_1dfin2[1][1] = PLAN1DFIN2(dims[1][1],dims[1][2],FIN2_STRIDE(1),
					   local1d,local2d,FFTW_FORWARD);
  ret->plans_1dfin2[2][1] = PLAN1DFIN2(dims[2][1],dims[2][2],FIN2_STRIDE(2),
					   local1d,local2d,FFTW_FORWARD);
  
  
  /* We currently don't do this */
#if 0
#define PLAN1DBLOCK(n,dir)	\
	fftw_plan_many_dft(1,&n,FFTBLOCK,FPTR(ftblock),NULL,1,MAXDIM,\
			   FPTR(ftblock+FFTBLOCKPAD*MAXDIM),NULL,1,MAXDIM,dir,FT_FFTW_FLAGS)
#define PLAN1DBLOCK2(x,y,dir)	\
	fftw_plan_dft_1d(x,FPTR(&ftblock[0][0][0]),FPTR(&ftblock[1][0][0]),dir,FT_FFTW_FLAGS)
  
  ftblock = (ComplexPtr_t) malloc_align(&orig, 2*FFTBLOCKPAD*MAXDIM*SIZEOF_COMPLEX);
  
  plans_1dblock[0][0] = PLAN1DBLOCK(dims[0][0],FFTW_BACKWARD);
  plans_1dblock[1][0] = PLAN1DBLOCK(dims[1][0],FFTW_BACKWARD);
  plans_1dblock[2][0] = PLAN1DBLOCK(dims[2][0],FFTW_BACKWARD);
  plans_1dblock[0][1] = PLAN1DBLOCK(dims[0][0],FFTW_FORWARD);
  plans_1dblock[1][1] = PLAN1DBLOCK(dims[1][0],FFTW_FORWARD);
  plans_1dblock[2][1] = PLAN1DBLOCK(dims[2][0],FFTW_FORWARD);
  
  plans_1dblock2[0][0] = PLAN1DBLOCK(dims[0][2],FFTW_BACKWARD);
  plans_1dblock2[1][0] = PLAN1DBLOCK(dims[1][2],FFTW_BACKWARD);
  plans_1dblock2[2][0] = PLAN1DBLOCK(dims[2][2],FFTW_BACKWARD);
  plans_1dblock2[0][1] = PLAN1DBLOCK(dims[0][2],FFTW_FORWARD);
  plans_1dblock2[1][1] = PLAN1DBLOCK(dims[1][2],FFTW_FORWARD);
  plans_1dblock2[2][1] = PLAN1DBLOCK(dims[2][2],FFTW_FORWARD);
#endif
  return ret;
}


void
FFTInit(int threads, int dims[3][3], int comm, ComplexPtr_t local2d, ComplexPtr_t local1d)
{
    
  
    int	i,j;
    void *orig;
    
        
#if MAKE_FFTW_THREADSAFE
    /* in this function we will over synchronize since it is called exactly once at startup*/
    pthread_mutex_lock(&plan_lock);
#endif
    ft_threads = threads;
    for (i = 0; i < 3; i++)  {
      for (j = 0; j < 3; j++)  {
	ft_dims[i*3+j] = dims[i][j];
      }
    }
    
    
    /* all threads will set the same value so there is no need to have thread specific
       data ... however we overserialize to ensure no odd behavior*/
    
#if MAKE_FFTW_THREADSAFE
    myfftw_threadid = myfftw_thread_counter;
    myfftw_thread_counter++;
    /*if i am the first thread to enter then create the array with INIT_NUM_THREADS plan sets*/
    if(myfftw_threadid == 0) {
      all_fftw_plans = (fftw_plans_t*) malloc(sizeof(fftw_plans_t)*INIT_NUM_THREADS);
      myfftw_num_plans_allocated = INIT_NUM_THREADS;
    } else if(myfftw_threadid < myfftw_num_plans_allocated) {
      /*else check whether there have been enough plans sets allocated*/
    } else {
      /* else grow the number of plan sets by 1*/
      all_fftw_plans = (fftw_plans_t*) realloc(all_fftw_plans, sizeof(fftw_plans_t)*
					      (myfftw_num_plans_allocated+1));
      myfftw_num_plans_allocated++;
    }
#else
    /* allocate one plan set*/
    all_fftw_plans = (fftw_plans_t*) malloc(sizeof(fftw_plans_t));
    myfftw_threadid = 0;
#endif

    all_fftw_plans[myfftw_threadid] = 
      make_fftw_plan_set(threads, dims, comm, local2d, local1d);
    myfftw_planset = all_fftw_plans[myfftw_threadid];
#if MAKE_FFTW_THREADSAFE
    pthread_mutex_unlock(&plan_lock);
#endif

    return;
}

void 
FFTFinalize()
{

}

/** WARNING ... THIS IS THREADSAFE BUT IT WILL BE SLOW ... USE LocalRow instead**/
void
FFT2DLocal(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  int i;
  int		dirf = (dir == FFT_FWD ? FFTW_FORWARD : FFTW_BACKWARD);

#if MAKE_FFTW_THREAD_SAFE
    pthread_mutex_lock(&plan_lock);
    fprintf(stderr, "PERFORMANCE WARNING: Do not use FFT2DLocal with pthreads\n");
#endif
    fftw_plan plan = fftw_plan_dft_2d(dims[0],dims[1], 
		  (fftw_complex*)inout, (fftw_complex*)inout, dirf, FT_FFTW_FLAGS);
#if MAKE_FFTW_THREAD_SAFE
    pthread_mutex_unlock(&plan_lock);
#endif

  fftw_execute(plan);

  return;
}

void
FFT2DLocalCols(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan	plan = myfftw_planset->plans_1dcol[orientation][dir];
  fftw_execute_dft(plan,FPTR(inout),FPTR(inout));
  return;
}

void
FFT2DLocalRow(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  fftw_plan     plan = myfftw_planset->plans_1drow[orientation][dir];
  fftw_execute_dft(plan,FPTR(inout),FPTR(inout));
  return;
}

void
FFT2DLocalRows(int nrows, ComplexPtr_t inout, int dir, int orientation, int *dims)
{
  int	i;
    
  for (i = 0; i < nrows; i++)
    FFT2DLocalRow(inout, dir, orientation, dims);

  return;
}

void
FFT1DLocalTranspose(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation)
{
  fftw_plan plan = myfftw_planset->plans_1dfin2[orientation][dir];
  int dims0, dims1, dims2;
  int dims0_n, dims1_n, dims2_n;
  int i,j,n;
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];

  timer_update(T_FFT1DPOST, FT_TIME_BEGIN);
  for (i = 0; i < dims0/ft_threads; i++) {
    fftw_execute_dft(plan, FPTR(in+i*dims1), 
       FPTR(out+i*(dims1-CPAD_COLS)*(dims2+CPAD_COLS))); 
  }
  timer_update(T_FFT1DPOST, FT_TIME_END);
}

void
FFT1DLocal(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation)
{
  int dims0, dims1, dims2;
  fftw_plan plan = myfftw_planset->plans_1dfin2[orientation][dir];
  int i;
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];

  timer_update(T_FFT1DPOST, FT_TIME_BEGIN);
  for (i = 0; i < dims0/ft_threads; i++) {
    fftw_execute_dft(plan, 
	FPTR(in+i*dims1*dims2), 
	FPTR(out+i*(dims1-CPAD_COLS)*(dims2+CPAD_COLS))); 
  }
  timer_update(T_FFT1DPOST, FT_TIME_END);
}

#if 0
void
FFT1DLocalTranspose(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation)
{
    int ii,jj,i,j;
    restrict ComplexPtr_t  inc, outc;
    restrict ComplexPtr_t  ftblockout = ftblock + MAXDIM*FFTBLOCKPAD;
    restrict ComplexPtr_t  ftblockout_i;
    restrict ComplexPtr_t  ftblockin  = ftblock;
    int dims0 = ft_dims[3*orientation+0];
    int dims1 = ft_dims[3*orientation+1];
    int dims2 = ft_dims[3*orientation+2];
    int numz = dims0/ft_threads*dims1;
    fftw_plan plan = plans_1dblock2[orientation][dir];
    
    timer_update(T_FFT1DPOST, FT_TIME_BEGIN);

    for (ii = 0; ii <= numz - FFTBLOCK; ii += FFTBLOCK) {
      for (j = 0; j < dims2; j++) {
	inc = in + ii + numz*j;
	for (i = 0; i < FFTBLOCK; i++)
	  ftblockin[i*MAXDIM+j] = inc[i];
      }

      fftw_execute(plan);

      for (i = 0; i < FFTBLOCK; i++) {
	ftblockout_i = ftblockout + i*MAXDIM;

        for (j = 0; j < dims2; j++) {
	  outc = out + ii + numz*j;
	  outc[i] = ftblockout_i[j];
	}
      }
    }
    timer_update(T_FFT1DPOST, FT_TIME_END);
}

void
FFT2DLocalCols(ComplexPtr_t inout, int dir, int orientation, int *dims)
{
    int			    ii,jj,i,j;
    restrict ComplexPtr_t  in_row;
    restrict ComplexPtr_t  ftblockout = ftblock + MAXDIM*FFTBLOCKPAD;
    restrict ComplexPtr_t  ftblockout_i;
    restrict ComplexPtr_t  ftblockin  = ftblock;
    fftw_plan plan = plans_1dblock[orientation][dir];

    assert(dims[1] % FFTBLOCK == 0);

    for (ii = 0; ii <= dims[1] - FFTBLOCK; ii += FFTBLOCK) {
      for (j = 0; j < dims[0]; j++) {
	in_row = inout + j*dims[1] + ii;
	for (i = 0; i < FFTBLOCK; i++)
	  ftblockin[i*MAXDIM+j] = in_row[i];
      }

      /*
      for (i = 0; i < FFTBLOCK; i++)
	fftw_execute_dft(plan, FPTR(&ftblock[0][i][0]), FPTR(&ftblock[1][i][0]));
       */

      fftw_execute(plan);

      for (i = 0; i < FFTBLOCK; i++) {
	ftblockout_i = ftblockout + i*MAXDIM;
        for (j = 0; j < dims[0]; j++) {
	  in_row = inout + j*dims[1] + ii;
	  in_row[i] = ftblockout_i[j]; 
	}
      }
    }
}

void
FFT2DLocalColsOut(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation, int *dims)
{
    int			    ii,jj,i,j;
    restrict ComplexPtr_t  in_row;
    restrict ComplexPtr_t  ftblockout = ftblock + MAXDIM*FFTBLOCKPAD;
    restrict ComplexPtr_t  ftblockout_i;
    restrict ComplexPtr_t  ftblockin  = ftblock;
    fftw_plan plan = plans_1dblock[orientation][dir];

    assert(dims[1] % FFTBLOCK == 0);

    for (ii = 0; ii <= dims[1] - FFTBLOCK; ii += FFTBLOCK) {
      for (j = 0; j < dims[0]; j++) {
	in_row = in + j*dims[1] + ii;
	for (i = 0; i < FFTBLOCK; i++)
	  ftblockin[i*MAXDIM+j] = in_row[i];
      }

      fftw_execute(plan);

      for (i = 0; i < FFTBLOCK; i++) {
	ftblockout_i = ftblockout + i*MAXDIM;
        for (j = 0; j < dims[0]; j++) {
	  in_row = out + j*dims[1] + ii;
	  in_row[i] = ftblockout_i[j]; 
	}
      }
    }
}
#endif
