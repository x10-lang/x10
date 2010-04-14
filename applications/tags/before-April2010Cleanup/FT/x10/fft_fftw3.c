#include <inttypes.h>
#include <stdlib.h>
#include <fftw3.h>
#include <assert.h>
#include <string.h>

#define  MAKE_FFTW_THREADSAFE 0

#if MAKE_FFTW_THREADSAFE
#include <pthread.h>
#endif

#include "Ft.h"

static int		ft_dims[9];
static int		ft_threads;
extern int CPAD_COLS;
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
  
   fftw_plan temp =PLAN1DCOL(dims[0][0],dims[0][1],local2d,local2d,FFTW_BACKWARD);
   ret->plans_1dcol[0][0] = temp; //PLAN1DCOL(dims[0][0],dims[0][1],local2d,local2d,FFTW_BACKWARD);
   
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

  return ret;
}


void
FFTInit(int threads, int dims[3][3], int comm, ComplexPtr_t local2d, ComplexPtr_t local1d, int PID)
{
    int	i,j;
    void *orig;
    
        
#if MAKE_FFTW_THREADSAFE
    //printf("Thead safe \n");
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
      //all_fftw_plans = (fftw_plans_t*) malloc(sizeof(fftw_plans_t)*INIT_NUM_THREADS);
      //myfftw_num_plans_allocated = INIT_NUM_THREADS;
      all_fftw_plans = (fftw_plans_t*) malloc(sizeof(fftw_plans_t)*threads);
      myfftw_num_plans_allocated = threads;
    } else if(myfftw_threadid < myfftw_num_plans_allocated) {
      /*else check whether there have been enough plans sets allocated*/
    } else {
      /* else grow the number of plan sets by 1*/
      //all_fftw_plans = (fftw_plans_t*) realloc(all_fftw_plans, sizeof(fftw_plans_t)*(myfftw_num_plans_allocated+1));
      //myfftw_num_plans_allocated++;
    }
    if (PID < myfftw_num_plans_allocated && myfftw_threadid < threads){
      all_fftw_plans[PID] =  make_fftw_plan_set(threads, dims, comm, local2d, local1d);
    }
    else{
      //all_fftw_plans[myfftw_threadid] =  make_fftw_plan_set(threads, dims, comm, local2d, local1d);
      //myfftw_planset = all_fftw_plans[myfftw_threadid];
    }
#else
    /* allocate one plan set*/
    if (PID ==0){
      all_fftw_plans = (fftw_plans_t*) malloc(sizeof(fftw_plans_t));
      myfftw_threadid = 0;
      all_fftw_plans[myfftw_threadid] =  make_fftw_plan_set(threads, dims, comm, local2d, local1d);
      myfftw_planset = all_fftw_plans[myfftw_threadid];
    }
    
#endif
    
    //all_fftw_plans[myfftw_threadid] =  make_fftw_plan_set(threads, dims, comm, local2d, local1d);
    //myfftw_planset = all_fftw_plans[myfftw_threadid];
    
#if MAKE_FFTW_THREADSAFE
    pthread_mutex_unlock(&plan_lock);
#endif

    //printf("  FFTInit: %d %d %d \n", myfftw_thread_counter, myfftw_threadid, PID); 
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
FFT2DLocalCols(ComplexPtr_t inout, int dir, int orientation, int *dims, int PID)
{
#if MAKE_FFTW_THREADSAFE
  fftw_plan	plan = all_fftw_plans[PID]->plans_1dcol[orientation][dir];
#else
  fftw_plan	plan = myfftw_planset->plans_1dcol[orientation][dir];
#endif  
  fftw_execute_dft(plan,FPTR(inout),FPTR(inout));
  return;
}

void
FFT2DLocalRow(ComplexPtr_t inout, int dir, int orientation, int *dims, int PID)
{
#if MAKE_FFTW_THREADSAFE
  fftw_plan     plan = all_fftw_plans[PID]->plans_1drow[orientation][dir];
#else
  fftw_plan     plan = myfftw_planset->plans_1drow[orientation][dir];
#endif
  //printf("  FFT2DLocalRow start: PID= %d \n", PID); 
  fftw_execute_dft(plan,FPTR(inout),FPTR(inout));
  //printf("  FFT2DLocalRow end: PID= %d \n", PID); 
  return;
}

void
FFT2DLocalRows(int nrows, ComplexPtr_t inout, int dir, int orientation, int *dims, int PID)
{
  int	i;
    
  for (i = 0; i < nrows; i++)
    FFT2DLocalRow(inout, dir, orientation, dims, PID);

  return;
}

void
FFT1DLocalTranspose(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation, int PID)
{
#if MAKE_FFTW_THREADSAFE
  fftw_plan     plan = all_fftw_plans[PID]->plans_1dfin2[orientation][dir];
#else 
  fftw_plan plan = myfftw_planset->plans_1dfin2[orientation][dir];
#endif
  int dims0, dims1, dims2;
  int dims0_n, dims1_n, dims2_n;
  int i,j,n;
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];

  //timer_update(T_FFT1DPOST, FT_TIME_BEGIN);
  for (i = 0; i < dims0/ft_threads; i++) {
    fftw_execute_dft(plan, FPTR(in+i*dims1), 
       FPTR(out+i*(dims1-CPAD_COLS)*(dims2+CPAD_COLS))); 
  }
  //timer_update(T_FFT1DPOST, FT_TIME_END);

  //printf("  FFT1DLocalTranspose: %d %d %d \n", myfftw_thread_counter, myfftw_threadid, PID); 
}

void
FFT1DLocal(ComplexPtr_t in, ComplexPtr_t out, int dir, int orientation, int PID)
{
  int dims0, dims1, dims2;
#if MAKE_FFTW_THREADSAFE
  fftw_plan plan = all_fftw_plans[PID]->plans_1dfin2[orientation][dir];
#else
  fftw_plan plan = myfftw_planset->plans_1dfin2[orientation][dir];
#endif
  int i;
  dims0 = ft_dims[3*orientation+0];
  dims1 = ft_dims[3*orientation+1];
  dims2 = ft_dims[3*orientation+2];

  //timer_update(T_FFT1DPOST, FT_TIME_BEGIN);
  for (i = 0; i < dims0/ft_threads; i++) {
    fftw_execute_dft(plan, 
	FPTR(in+i*dims1*dims2), 
	FPTR(out+i*(dims1-CPAD_COLS)*(dims2+CPAD_COLS))); 
  }
  //timer_update(T_FFT1DPOST, FT_TIME_END);
}


