#include "simple.h"
#include "simple-f-defs.h"

#ifdef __GNUC__
#define FFUN(x) x##_
#else
#define FFUN(x) x##_
#endif

#define FTHREADED int *fth
#define FTH fth

#define _M1  FTH[5]
#define _M2  FTH[6]
#define _BLK FTH[7]

#define MAKETHREADED THREADED = (uthread_info_t*)FTH[0]

void FFUN(fnbar)(FTHREADED) {
  /* Fortran node barrier */
  MAKETHREADED;
  node_Barrier();
  return;
}

void FFUN(fnpardo)(int *first, int *last, int *inc, FTHREADED) {
  /* Fortran node pardo */
  MAKETHREADED;
  int F = *first - 1;
  int L = *last;
  int I = *inc;
  if ((F == 0) && (L == THREADS)) {
    _M1 = MYTHREAD;
    _M2 = _M1 + 1;
  } else {
    _BLK = (L - F)/THREADS;
    if (_BLK  ==  0) {
      _M1  = F + MYTHREAD;
      _M2  = _M1 + 1;
      if (_M1 >= L) {
	_M1 = _M2;
      }
    }
    else {
      _M1  = _BLK * MYTHREAD + F;
      if (MYTHREAD < THREADS-1) 
	_M2 = _M1 + _BLK;
      else 
	_M2 = L;
    }
  }
  if (I > 1) {
    while ((_M1 - F) % I > 0)
      _M1 += 1;
  }
  _M1 += 1;
  return;
}

#ifndef SMPONLY
void FFUN(facpardo)(int *first, int *last, FTHREADED) {
  /* Fortran all cycle pardo */
  MAKETHREADED;
  int F = *first - 1;
  int L = *last;
  _M1  = ID + F;
  /* for (i=_M1 ; i<L ; i+=TID) */
  _M1 += 1;
  return;
}
#endif /* !SMPONLY */

#ifndef SMPONLY
void FFUN(fabpardo)(int *first, int *last, FTHREADED) {
  /* Fortran all block pardo */
  MAKETHREADED;
  int F = *first - 1;
  int L = *last;
  _BLK = (L - F)/TID + 1;
  _M1  = _BLK*ID + F;
  _M2  = _M1 + _BLK; 
  if (_M2 > L)
    _M2 = L;
  /*for (i=_M1 ; i<_M2 ; i++) */
  _M1 += 1;
  return;
}
#endif /* !SMPONLY */

void FFUN(fnbcasti)(int *myval, int *ret, FTHREADED) {
  /* Fortran node broadcast integer */
  MAKETHREADED;
  *ret = node_Bcast_i(*myval, TH);
  return;
}

#if 0
long   node_Bcast_l(long   myval, THREADED);
int    *node_Bcast_ip(int    *myval, THREADED);
long   *node_Bcast_lp(long   *myval, THREADED);
double *node_Bcast_dp(double *myval, THREADED);
char   *node_Bcast_cp(char   *myval, THREADED);
#endif

void FFUN(fnbcastd)(double *myval, double *ret, FTHREADED) {
  /* Fortran node broadcast double */
  MAKETHREADED;
  *ret = node_Bcast_d(*myval, TH);
  return;
}

void FFUN(fnbcastc)(char *myval, char *ret, FTHREADED) {
  /* Fortran node broadcast char */
  MAKETHREADED;
  *ret = node_Bcast_c(*myval, TH);
  return;
}

reduce_t convert_freduce(int fop) {
  reduce_t op;
  switch(fop) {
  case OP_MAX: op = MAX; break;
  case OP_MIN: op = MIN; break;
  case OP_SUM: op = SUM; break;
  case OP_PROD: op = PROD; break;
  case OP_LAND: op = LAND; break;
  case OP_BAND: op = BAND; break;
  case OP_LOR: op = LOR; break;
  case OP_BOR: op = BOR; break;
  case OP_LXOR: op = LXOR; break;
  case OP_BXOR: op = BXOR; break;
  default:
    fprintf(stderr,
	    "ERROR: Convert Fortran Reduction Operator (%d) not known\n",
	    fop);
    op = -1;
  }
  return op;
}

void FFUN(fnreducei)(int *myval, int *fop, int *ret, FTHREADED) {
  /* Fortran node reduce integer */
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = node_Reduce_i(*myval, op, TH);
  return;
}

/* long   node_Reduce_l(long   myval, reduce_t op, THREADED); */

void FFUN(fnreduced)(double *myval, int *fop, double *ret, FTHREADED) {
  /* Fortran node reduce double */
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = node_Reduce_d(*myval, op, TH);
  return;
}

void FFUN(fnscani)(int *myval, int *fop, int *ret, FTHREADED) {
  /* Fortran node scan integer */
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = node_Scan_i(*myval, op, TH);
  return;
}

/* long   node_Scan_l(long   myval, reduce_t op, THREADED); */

void FFUN(fnscand)(double *myval, int *fop, double *ret, FTHREADED) {
  /* Fortran node scan double */
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = node_Scan_d(*myval, op, TH);
  return;
}


void FFUN(fmutexinit)(int *mutexkey, int *ierr, FTHREADED) {
  MAKETHREADED;
  pthread_mutex_t *mutex;
  *ierr = node_mutex_init(&mutex, NULL, TH);
  *mutexkey = (int)mutex;
  return;
}

void FFUN(fmutexdestroy)(int *mutexkey, int *ierr, FTHREADED) {
  MAKETHREADED;
  pthread_mutex_t *mutex;
  mutex = (pthread_mutex_t *)(*mutexkey);
  *ierr = node_mutex_destroy(mutex, TH);
  return;
}

void FFUN(fmutexlock)(int *mutexkey, FTHREADED) {
  MAKETHREADED;
  pthread_mutex_t *mutex;
  mutex = (pthread_mutex_t *)(*mutexkey);
  node_mutex_lock(mutex);
  return;
}

void FFUN(fmutextrylock)(int *mutexkey, FTHREADED) {
  MAKETHREADED;
  pthread_mutex_t *mutex;
  mutex = (pthread_mutex_t *)(*mutexkey);
  node_mutex_trylock(mutex);
  return;
}

void FFUN(fmutexunlock)(int *mutexkey, FTHREADED) {
  MAKETHREADED;
  pthread_mutex_t *mutex;
  mutex = (pthread_mutex_t *)(*mutexkey);
  node_mutex_unlock(mutex);
  return;
}

#ifndef SMPONLY
void FFUN(fabar)(FTHREADED) {
  MAKETHREADED;
  all_Barrier(TH);
  return;
}
#endif


#ifndef SMPONLY
void FFUN(fareducei)(int *val, int *fop, int *ret, FTHREADED) {
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = all_Reduce_i(*val, op, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fareduced)(double *val, int *fop, double *ret, FTHREADED) {
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = all_Reduce_d(*val, op, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fabcasti)(int *val, int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Bcast_i(*val,TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fabcastd)(double *val, double *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Bcast_d(*val,TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fabcastc)(char *val, char *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Bcast_c(*val,TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faallreducei)(int *val, int *fop, int *ret, FTHREADED) {
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = all_Allreduce_i(*val, op, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faallreduced)(double *val, int *fop, double *ret, FTHREADED) {
  MAKETHREADED;
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = all_Allreduce_d(*val, op, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoalli)(int *sendbuf, int *block_size, int *recvbuf,
		       int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoall_i(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoalld)(double *sendbuf, int *block_size, double *recvbuf,
		       int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoall_d(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallc)(char *sendbuf, int *block_size, char *recvbuf,
		       int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoall_c(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallvextent)(char *sendbuf, int *sendcnt, int *sendoff,
			     char *recvbuf, int *recvcnt, int *recvoff,
			     int *extent,  int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoallv_extent(sendbuf, sendcnt, sendoff,
			      recvbuf, recvcnt, recvoff,
			      *extent, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallvlogextent)(char *sendbuf, int *sendcnt, int *sendoff,
				char *recvbuf, int *recvcnt, int *recvoff,
				int *logextent,  int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoallv_logextent(sendbuf, sendcnt, sendoff,
				 recvbuf, recvcnt, recvoff,
				 *logextent, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallvi)(int *sendbuf, int *sendcnt, int *sendoff,
			int *recvbuf, int *recvcnt, int *recvoff,
			int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoallv_i(sendbuf, sendcnt, sendoff,
			 recvbuf, recvcnt, recvoff, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallvd)(double *sendbuf, int *sendcnt, int *sendoff,
			double *recvbuf, int *recvcnt, int *recvoff,
			int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoallv_d(sendbuf, sendcnt, sendoff,
			 recvbuf, recvcnt, recvoff, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(faalltoallvc)(char *sendbuf, int *sendcnt, int *sendoff,
			char *recvbuf, int *recvcnt, int *recvoff,
			int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Alltoallv_c(sendbuf, sendcnt, sendoff,
			 recvbuf, recvcnt, recvoff, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fagatheri)(int *sendbuf, int *block_size, int *recvbuf,
		     int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Gather_i(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fagatherd)(double *sendbuf, int *block_size, double *recvbuf,
		     int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Gather_d(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fagatherc)(char *sendbuf, int *block_size, char *recvbuf,
		     int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Gather_c(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fascatteri)(int *sendbuf, int *block_size, int *recvbuf,
		      int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Scatter_i(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fascatterd)(double *sendbuf, int *block_size, double *recvbuf,
		      int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Scatter_d(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fascatterc)(char *sendbuf, int *block_size, char *recvbuf,
		      int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_Scatter_c(sendbuf, *block_size, recvbuf, TH);
  return;
}
#endif

#ifndef SMPONLY
void FFUN(fascatterxi)(int *sendbuf, int *array_size, int *recvbuf,
		       int *ret, FTHREADED) {
  MAKETHREADED;
  *ret = all_ScatterX_i(sendbuf, *array_size, recvbuf, TH);
  return;
}
#endif


#if 0
#ifndef SMPONLY
long   all_Reduce_l(long   val, reduce_t op, THREADED);
long   all_Bcast_l(long   myval, THREADED);
long   all_Allreduce_l(long   val, reduce_t op, THREADED);
int    all_Alltoall_l(long   *sendbuf, int block_size, long   *recvbuf,
		      THREADED);
int    all_Alltoallv_l(long   *sendbuf, int *sendcnt, int *sendoff,
		       long   *recvbuf, int *recvcnt, int *recvoff,
		       THREADED);
int    all_Gather_l(long   *sendbuf, int block_size, long   *recvbuf,
		    THREADED);
int    all_Scatter_l(long   *sendbuf, int block_size, long   *recvbuf,
		     THREADED);
#endif /* !SMPONLY */
#endif
