#ifndef _SIMPLE_H
#define _SIMPLE_H

#ifdef __cplusplus
extern "C" {
#endif 

#ifndef SMPONLY
#include "umd.h"
#endif /* !SMPONLY */
#include <pthread.h>
#include "mach_def.h"
#include "smp_node.h"
#include "types.h"
#include "timing.h"

#ifdef SPRNG
#include "sprng.h"
#endif

#ifdef _MACH_SCHED_I
#include _MACH_SCHED_I
#endif

#define DEFAULT_PRI (PRI_OTHER_MIN+PRI_OTHER_MAX)/2
#if defined(PTHREAD_USE_D4)
#define pNULL       ((pthread_addr_t)NULL)
#else
#define pNULL       (NULL)
#endif
#define SIMPLE_done(x) SIMPLE_Cleanup(x); pthread_exit(pNULL); return 0;

#define pthread_mb_np() asm("mb;")

#define MYTHREAD     (ti->mythread)
#define THREADED     uthread_info_t *ti
#define TH           ti
#define on_one_thread if (MYTHREAD == 0)
#define on_thread(k)  if (MYTHREAD == (k))
#ifndef SMPONLY
#define on_one        on_one_node on_one_thread
#else /* SMPONLY */
#define on_one        on_one_thread
#endif /* SMPONLY */
#define BIND_TH       0
#define ERR_TH        0
#define CACHELOG      7
#define NOSHARE(x)    ((x)<<CACHELOG)
#ifndef SMPONLY
#define ID           (ti->id)
#define TID          (ti->tid)
#endif /* !SMPONLY */

#define THARGC       (ti->argc)
#define THARGV       (ti->argv)
#define EXENAME      (ti->argv[0])

#define THRAND       (ti->rand)

#ifdef RRANDOM
typedef struct {
  long *randtbl;
  long *fptr;
  long *rptr;
  long *state;
  int rand_type;
  int rand_deg;
  int rand_sep;
  long *end_ptr;
} rrandom_info_t;
#endif

#ifdef SPRNG
#define THSPRNG     (ti->randgen)
#endif

#ifndef MAXTHREADS
extern int          MAXTHREADS;
#endif
extern int          THREADS;

struct thread_inf {
  int                 mythread;   /* Thread number */
  int                 argc;      
  char              **argv;
  int                 m1;         /* used in pardo */
  int                 m2;         /* used in pardo */
  int                 blk;        /* used in pardo */
#ifdef RRANDOM
  rrandom_info_t      rand;       /* used in rrandom_th */
  long                rbs;        /* used in random_bit, random_bitstring */
  short               rc;         /* used in random_bit, random_count */
#ifdef SOLARIS
  unsigned short      xi[3];      /* used in rrandom_th nrand48() */
#endif /* SOLARIS */
#endif /* RRANDOM */
#ifdef SPRNG
  int                *randgen;    /* SPRNG generator pointer */
#endif /* SPRNG */
  int                 id;         /* My thread index in cluster */
  int                 tid;        /* Total threads in cluster */
  int                 udata;      /* User data */
};

typedef struct thread_inf uthread_info_t;

extern uthread_info_t *uthread_info;

typedef pthread_t *btask_t;
btask_t create_btask(void *(*run_routine)(void *), void *run_arg);
void    waitfor_btask(btask_t the_btask);
void    kill_btask(btask_t the_btask, int sig);

#define node_partition_loop_across_threads(i,first,last,inc) \
        node_pardo((i),(first),(last),(inc))     
#define pardo(i,first,last,inc) \
        node_pardo((i),(first),(last),(inc))     
#define node_pardo(i,first,last,inc)                    \
        if (((first)==0)&&((last)==THREADS)) {          \
            ti->m1 = MYTHREAD;                          \
	    ti->m2 = ti->m1 + 1;                        \
	} else {                                        \
            ti->blk = ((last)-(first))/THREADS;         \
            if (ti->blk == 0) {    		        \
              ti->m1  = (first)+MYTHREAD;               \
              ti->m2  = (ti->m1) + 1;                   \
              if ((ti->m1) >= (last))                   \
                 ti->m1 = ti->m2;                       \
	    }                                           \
            else {                                      \
              ti->m1  = (ti->blk) * MYTHREAD + (first); \
	      if (MYTHREAD < THREADS-1)                 \
	          ti->m2 = (ti->m1)+(ti->blk);          \
	      else                                      \
	          ti->m2 = (last);                      \
            }                                           \
	}                                               \
        if ((inc)>1) {                            \
            while ((ti->m1-(first)) % (inc) > 0)  \
                ti->m1 += 1;                      \
        }                                         \
	for (i=ti->m1 ; i<ti->m2 ; i+=(inc))

#ifndef SMPONLY
#define all_pardo_cyclic(i,first,last)                  \
	ti->m1  = ID + (first);                         \
	for (i=ti->m1 ; i<(last) ; i+=TID)
#endif /* !SMPONLY */

#ifndef SMPONLY
#define all_pardo_block(i,first,last)                   \
        ti->blk = ((last)-(first))/TID + 1;             \
	ti->m1  = (ti->blk)*ID + (first);               \
	ti->m2  = (ti->m1)+(ti->blk);                   \
	if (ti->m2 > (last)) ti->m2 = (last);           \
	for (i=ti->m1 ; i<ti->m2 ; i++)
#endif /* !SMPONLY */

#define task_do(x)  (MYTHREAD == ((x) % THREADS))

#if (_MACH_BAR == _MACH_BAR_SYNC)
#define node_Barrier() node_Barrier_sync(TH)
#endif
#if (_MACH_BAR == _MACH_BAR_TREE)
#define node_Barrier() node_Barrier_tree(TH)
#endif
  
void   node_Barrier_tree(THREADED);
void   node_Barrier_sync(THREADED);
void   *node_malloc(int bytes, THREADED);
void   node_free(void *, THREADED);

int    node_mutex_init(pthread_mutex_t **, const pthread_mutexattr_t *, THREADED);
int    node_mutex_destroy(pthread_mutex_t *, THREADED);
#define node_mutex_lock(m)    pthread_mutex_lock(m)
#define node_mutex_trylock(m) pthread_mutex_trylock(m)
#define node_mutex_unlock(m)  pthread_mutex_unlock(m)

int    node_Bcast_i(int    myval, THREADED);
long   node_Bcast_l(long   myval, THREADED);
double node_Bcast_d(double myval, THREADED);
char   node_Bcast_c(char   myval, THREADED);
int    *node_Bcast_ip(int    *myval, THREADED);
long   *node_Bcast_lp(long   *myval, THREADED);
double *node_Bcast_dp(double *myval, THREADED);
char   *node_Bcast_cp(char   *myval, THREADED);
int    node_Reduce_i(int    myval, reduce_t op, THREADED);
long   node_Reduce_l(long   myval, reduce_t op, THREADED);
double node_Reduce_d(double myval, reduce_t op, THREADED);
int    node_Scan_i(int    myval, reduce_t op, THREADED);
long   node_Scan_l(long   myval, reduce_t op, THREADED);
double node_Scan_d(double myval, reduce_t op, THREADED);

#ifndef SMPONLY
void   all_Barrier(THREADED);

int    all_Reduce_i(int    val, reduce_t op, THREADED);
long   all_Reduce_l(long   val, reduce_t op, THREADED);
double all_Reduce_d(double val, reduce_t op, THREADED);

int    all_Bcast_i(int    myval, THREADED);
long   all_Bcast_l(long   myval, THREADED);
double all_Bcast_d(double myval, THREADED);
char   all_Bcast_c(char   myval, THREADED);

int    all_Allreduce_i(int    val, reduce_t op, THREADED);
long   all_Allreduce_l(long   val, reduce_t op, THREADED);
double all_Allreduce_d(double val, reduce_t op, THREADED);

int    all_Alltoall_c(char   *sendbuf, int block_size, char   *recvbuf,
		      THREADED);
int    all_Alltoall_i(int    *sendbuf, int block_size, int    *recvbuf,
		      THREADED);
int    all_Alltoall_l(long   *sendbuf, int block_size, long   *recvbuf,
		      THREADED);
int    all_Alltoall_d(double *sendbuf, int block_size, double *recvbuf,
		      THREADED);

int    all_Alltoallv_extent(char   *sendbuf, int *sendcnt, int *sendoff,
			    char   *recvbuf, int *recvcnt, int *recvoff,
			    int extent, THREADED);
int    all_Alltoallv_logextent(char   *sendbuf, int *sendcnt, int *sendoff,
			       char   *recvbuf, int *recvcnt, int *recvoff,
			       int logextent, THREADED);
int    all_Alltoallv_c(char   *sendbuf, int *sendcnt, int *sendoff,
		       char   *recvbuf, int *recvcnt, int *recvoff,
		       THREADED);
int    all_Alltoallv_i(int    *sendbuf, int *sendcnt, int *sendoff,
		       int    *recvbuf, int *recvcnt, int *recvoff,
		       THREADED);
int    all_Alltoallv_l(long   *sendbuf, int *sendcnt, int *sendoff,
		       long   *recvbuf, int *recvcnt, int *recvoff,
		       THREADED);
int    all_Alltoallv_d(double *sendbuf, int *sendcnt, int *sendoff,
		       double *recvbuf, int *recvcnt, int *recvoff,
		       THREADED);

int    all_Gather_c(char   *sendbuf, int block_size, char   *recvbuf,
		    THREADED);
int    all_Gather_i(int    *sendbuf, int block_size, int    *recvbuf,
		    THREADED);
int    all_Gather_l(long   *sendbuf, int block_size, long   *recvbuf,
		    THREADED);
int    all_Gather_d(double *sendbuf, int block_size, double *recvbuf,
		    THREADED);

int    all_Scatter_c(char   *sendbuf, int block_size, char   *recvbuf,
		     THREADED);
int    all_Scatter_i(int    *sendbuf, int block_size, int    *recvbuf,
		     THREADED);
int    all_Scatter_l(long   *sendbuf, int block_size, long   *recvbuf,
		     THREADED);
int    all_Scatter_d(double *sendbuf, int block_size, double *recvbuf,
		     THREADED);

int    all_ScatterX_i(int    *sendbuf, int array_size, int    *recvbuf,
		      THREADED);
#endif /* !SMPONLY */

void *SIMPLE_main(uthread_info_t *);
  
void SIMPLE_Init(int*, char***);
void SIMPLE_Finalize();
void SIMPLE_Cleanup(THREADED);

#define errprnt(msg)    { fprintf(stderr,"%s: %s\n",EXENAME,msg); exit(1); }

#if 1

#ifdef SOLARIS
#include <sys/isa_defs.h>
#if defined(__sparcv9)
#define sun_mb_mi_np() asm("membar #MemIssue ;")
#else
#define sun_mb_mi_np() 
#endif
#endif

#else

#ifdef SOLARIS
#define sun_mb_mi_np() asm("membar #MemIssue ;")
#endif

#endif


#ifdef __cplusplus
}
#endif 

  
#endif
