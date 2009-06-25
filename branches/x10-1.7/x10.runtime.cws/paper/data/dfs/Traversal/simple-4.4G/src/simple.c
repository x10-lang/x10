#include "mach_def.h"

#if (_MACH_SYSINFO == TRUE)
#include <sys/sysinfo.h>
#endif /* _MACH_SYSINFO */

#if (_MACH_THREAD == _MACH_THREAD_SYS)
#include <machine/hal_sysinfo.h>
#endif /* _MACH_THREAD */

#include "simple.h"
#ifndef SMPONLY
#include "umd.h"
#endif /* !SMPONLY */
#include "alg_load.h"
#ifdef RRANDOM
#include "alg_random.h"
#endif /* RRANDOM */


#define  DEBUG  1
#define  INFO   1

#define MAXTHREADS_DEFAULT 64

#ifndef MAXTHREADS
int     MAXTHREADS = MAXTHREADS_DEFAULT;
#endif /* MAXTHREADS */
#define DEFAULT_THREADS 2
int     THREADS;
uthread_info_t *uthread_info;
pthread_t     *spawn_thread;

#define MAX_GATHER 2

int    _node_bcast_i;
long   _node_bcast_l;
double _node_bcast_d;
char   _node_bcast_c;
int    *_node_bcast_ip;
long   *_node_bcast_lp;
double *_node_bcast_dp;
char   *_node_bcast_cp;


#if defined(SOLARIS)&&defined(THREADMAP)
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <procfs.h>

int *_solaris_step;

void _solaris_report(THREADED) {
  int s;
  char lwpsinfoFname[MAXLEN];
  int fd_lwpsinfo;
  lwpsinfo_t lwpsinfo;

  sprintf(lwpsinfoFname, "/proc/self/lwp/%d/lwpsinfo", MYTHREAD+2);

  fd_lwpsinfo = open(lwpsinfoFname, O_RDONLY);
  if (fd_lwpsinfo == -1) {
    fprintf(stderr,"ERROR: Trying to open %s\n",lwpsinfoFname);
    perror("ERROR opening proc lwpsinfo");
  }
  s = read(fd_lwpsinfo, &lwpsinfo, sizeof(lwpsinfo_t));
  if (s != sizeof(lwpsinfo_t)) 
    fprintf(stderr,"ERROR: only read %d of %d bytes of lwpsinfo\n",
	    s,sizeof(lwpsinfo_t));
  close(fd_lwpsinfo);

  fprintf(outfile,"THREADMAP %12d\t%3d\t%3d\n",
	  _solaris_step[NOSHARE(MYTHREAD)]++,
	  MYTHREAD,lwpsinfo.pr_onpro);

  return;
}
#endif /* defined(SOLARIS)&&defined(THREADMAP) */

smp_barrier_t nbar;

int    node_mutex_init(pthread_mutex_t **mutex, const pthread_mutexattr_t *attr, THREADED) {
  int r;
  r = 0;
  *mutex = (pthread_mutex_t *)node_malloc(sizeof(pthread_mutex_t), TH);
  on_one_thread {
    r = pthread_mutex_init(*mutex, attr);
  }
  r = node_Bcast_i(r, TH);
  return r;
}

int    node_mutex_destroy(pthread_mutex_t *mutex, THREADED) {
  int r;
  r = 0;
  node_Barrier();
  on_one_thread {
    r = pthread_mutex_destroy(mutex);
    free (mutex);
  }
  r = node_Bcast_i(r, TH);
  return r;
}

void node_Barrier_sync_init() {
#if defined(SOLARIS)&&defined(THREADMAP)
  int i;
  _solaris_step = (int *)malloc(NOSHARE(THREADS)*sizeof(int));
  assert_malloc(_solaris_step);
  for (i=0 ; i<THREADS ; i++) 
    _solaris_step[NOSHARE(i)] = 0;
#endif /* defined(SOLARIS)&&defined(THREADMAP) */
  nbar = smp_barrier_init(THREADS);
}

void node_Barrier_sync_destroy() {
  smp_barrier_destroy(nbar);
}

void node_Barrier_sync(THREADED) {
#if defined(SOLARIS)&&defined(THREADMAP)
  _solaris_report(TH);
#endif /* defined(SOLARIS)&&defined(THREADMAP) */
  smp_barrier_wait(nbar);
}

static volatile int up_buf[NOSHARE(MAXTHREADS_DEFAULT)][2];
static volatile int down_buf[NOSHARE(MAXTHREADS_DEFAULT)];

void
node_Barrier_tree_init() {
  int i;

  for (i=0 ; i<THREADS ; i++) 
    up_buf[NOSHARE(i)][0] = up_buf[NOSHARE(i)][1] = down_buf[NOSHARE(i)] = 0;
  return;
}

void
node_Barrier_tree_destroy() { return; }

void
node_Barrier_tree_up(THREADED) {
    
  register int myidx  = MYTHREAD;
  register int parent = (MYTHREAD - 1) / 2;
  register int odd_child = 2 * MYTHREAD + 1;
  register int even_child = 2 * MYTHREAD + 2;
  register int parity = MYTHREAD & 1;

  if (MYTHREAD == 0) {
    if (THREADS != 1) 
      if (THREADS == 2) 
	while (up_buf[NOSHARE(myidx)][1] == 0) ;
      else 
	while (up_buf[NOSHARE(myidx)][0] == 0 ||
	       up_buf[NOSHARE(myidx)][1] == 0) ;
  } 
  else 
    if (odd_child >= THREADS) 
      up_buf[NOSHARE(parent)][parity]++;
    else 
      if (even_child >= THREADS) {
	while (up_buf[NOSHARE(myidx)][1] == 0) ;
	up_buf[NOSHARE(parent)][parity]++;
      } 
      else {
	while (up_buf[NOSHARE(myidx)][0] == 0 ||
	       up_buf[NOSHARE(myidx)][1] == 0) ;
	up_buf[NOSHARE(parent)][parity]++;
      }

  up_buf[NOSHARE(myidx)][0] = up_buf[NOSHARE(myidx)][1] = 0;
#ifdef SOLARIS
  sun_mb_mi_np();
#endif
  return;
}

void
node_Barrier_tree_down(THREADED) {
    
  register int myidx  = MYTHREAD;
  register int left = 2 * MYTHREAD + 1;
  register int right = 2 * MYTHREAD + 2;

  if (MYTHREAD != 0) 
    while (down_buf[NOSHARE(myidx)] == 0) ;
  
  if (left < THREADS)
    down_buf[NOSHARE(left)]++;
  if (right < THREADS)
    down_buf[NOSHARE(right)]++;

  down_buf[NOSHARE(myidx)] = 0;
#ifdef SOLARIS
  sun_mb_mi_np();
#endif
  return;
}

void
node_Barrier_tree(THREADED) {
  node_Barrier_tree_up(TH);
  node_Barrier_tree_down(TH);
  return;
}

#ifndef SMPONLY
void all_Barrier(THREADED) {
  node_Barrier();
  on_one_thread UMD_Barrier();
  node_Barrier();
}
#endif /* !SMPONLY */

#ifdef SUNMMAP
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <sys/mman.h>
int _fdmmap = 0;
int _offmmap = 0;
#endif

void   *node_malloc(int bytes, THREADED) {
  void *ptr;
  ptr=NULL;
  on_one_thread {
#ifdef SUNMMAP
    if (!_fdmmap) {
      _fdmmap = open("/dev/zero", O_RDWR);
      if (_fdmmap < 0)
	perror("fdmmap error");
    }
    ptr = mmap((void *)0, bytes,
	       (PROT_READ | PROT_WRITE), MAP_PRIVATE, _fdmmap, _offmmap);
    _offmmap += bytes;
    if (ptr == MAP_FAILED)
      perror("mmap failed");
#else
    ptr = malloc(bytes);
    assert_malloc(ptr);
#endif
  }
  return(node_Bcast_cp(ptr, TH));
}

void   node_free(void *ptr, THREADED) {
  on_one_thread {
#ifdef SUNMMAP
    ptr = (void *)NULL;
#else
    free(ptr);
#endif
  }
}

int    node_Bcast_i(int    myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_i = myval;
  }

  node_Barrier();
  return (_node_bcast_i);
}

long   node_Bcast_l(long   myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_l = myval;
  }

  node_Barrier();
  return (_node_bcast_l);
}

double node_Bcast_d(double myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_d = myval;
  }

  node_Barrier();
  return (_node_bcast_d);
}

char   node_Bcast_c(char   myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_c = myval;
  }

  node_Barrier();
  return (_node_bcast_c);
}

int    *node_Bcast_ip(int    *myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_ip = myval;
  }

  node_Barrier();
  return (_node_bcast_ip);
}

long   *node_Bcast_lp(long   *myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_lp = myval;
  }

  node_Barrier();
  return (_node_bcast_lp);
}

double *node_Bcast_dp(double *myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_dp = myval;
  }

  node_Barrier();
  return (_node_bcast_dp);
}

char   *node_Bcast_cp(char   *myval, THREADED) {

  node_Barrier();

  on_one_thread {
    _node_bcast_cp = myval;
  }

  node_Barrier();
  return (_node_bcast_cp);
}

smp_reduce_d_t red_d;

double node_Reduce_d(double myval, reduce_t op, THREADED) {
  return (smp_reduce_d(red_d, myval, op));
}


smp_reduce_i_t red_i;

int node_Reduce_i(int myval, reduce_t op, THREADED) {
  return (smp_reduce_i(red_i,myval,op));
}

smp_reduce_l_t red_l;

long node_Reduce_l(long myval, reduce_t op, THREADED) {
  return (smp_reduce_l(red_l,myval,op));
}

smp_scan_i_t scan_i;

int node_Scan_i(int myval, reduce_t op, THREADED) {
  return(smp_scan_i(scan_i,myval,op,MYTHREAD));
}

smp_scan_l_t scan_l;

long node_Scan_l(long myval, reduce_t op, THREADED) {
  return(smp_scan_l(scan_l,myval,op,MYTHREAD));
}

smp_scan_d_t scan_d;

double node_Scan_d(double myval, reduce_t op, THREADED) {
  return(smp_scan_d(scan_d,myval,op,MYTHREAD));
}

#ifndef SMPONLY
int    all_Reduce_i(int    val, reduce_t op, THREADED) {
  int i, t;
  i = node_Reduce_i(val, op, TH);
  on_one_thread 
    t = UMD_Reduce_i(i,op);
  return(t);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
long   all_Reduce_l(long   val, reduce_t op, THREADED) {
  long l, t;
  l = node_Reduce_l(val, op, TH);
  on_one_thread 
    t = UMD_Reduce_l(l,op);
  return(t);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
double all_Reduce_d(double val, reduce_t op, THREADED) {
  double d, t;
  d = node_Reduce_d(val, op, TH);
  on_one_thread 
    t = UMD_Reduce_d(d,op);
  return(t);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Bcast_i(int    myval, THREADED) {
  on_one_thread 
    myval = UMD_Bcast_i(myval);
  myval = node_Bcast_i(myval, TH);
  return(myval);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
long   all_Bcast_l(long   myval, THREADED) {
  on_one_thread 
    myval = UMD_Bcast_l(myval);
  myval = node_Bcast_l(myval, TH);
  return(myval);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
double all_Bcast_d(double myval, THREADED) {
  on_one_thread 
    myval = UMD_Bcast_d(myval);
  myval = node_Bcast_d(myval, TH);
  return(myval);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
char   all_Bcast_c(char   myval, THREADED) {
  on_one_thread 
    myval = UMD_Bcast_c(myval);
  myval = node_Bcast_c(myval, TH);
  return(myval);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Allreduce_i(int    val, reduce_t op, THREADED) {
  val = all_Reduce_i(val, op, TH);
  val = all_Bcast_i(val, TH);
  return(val);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
long   all_Allreduce_l(long   val, reduce_t op, THREADED) {
  val = all_Reduce_l(val, op, TH);
  val = all_Bcast_l(val, TH);
  return(val);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
double all_Allreduce_d(double val, reduce_t op, THREADED) {
  val = all_Reduce_d(val, op, TH);
  val = all_Bcast_d(val, TH);
  return(val);
}
#endif /* !SMPONLY */

#if 0
#ifndef SMPONLY
int    all_Alltoall_c(char *sendbuf, int block_size, char *recvbuf,
		      THREADED) {

  register int first, last, i, t, j, k;

  if (THREADS <= NODES) {
    t = NODES/THREADS;

    first      = MYTHREAD*t;
    last       = first + t;
    if (last == t*THREADS) last=NODES;

    if (first==0) {
      k = MYNODE*block_size;
      memcpy(recvbuf+k, sendbuf+k, block_size);
      first = 1;
    }
  
    for (i=first ; i<last ; i++) {
      j = MYNODE ^ i;
      k = j*block_size;
      UMD_Sendrecv_nb(j, sendbuf+k, block_size,
		      j, recvbuf+k, block_size);
    }
  }
  else {
    if (MYTHREAD < NODES) {
      if (MYTHREAD==0) {
	k = MYNODE*block_size;
	memcpy(recvbuf+k, sendbuf+k, block_size);
      }
      else {
	j = MYNODE ^ MYTHREAD;
	k = j*block_size;
	UMD_Sendrecv_nb(j, sendbuf+k, block_size,
			j, recvbuf+k, block_size);
      }
    }
  }
  return 0;
}
#endif /* !SMPONLY */
#else /* 1 */
#ifndef SMPONLY
int    all_Alltoall_c(char *sendbuf, int block_size, char *recvbuf,
		      THREADED) {

  register int first, last, i, t, j, k, thpart;
  char ts, tr;
  int sc = sizeof(char);
  
  if (THREADS <= NODES) {
    thpart = min(THREADS, MAX_GATHER);
    if (MYTHREAD < thpart) {
      t = NODES/thpart;

      first      = MYTHREAD*t;
      last       = first + t;
      if (last == t*thpart) last=NODES;

      if (first==0) {
	k = MYNODE*block_size;
	memcpy(recvbuf+k, sendbuf+k, block_size);
	first = 1;
      }
  
      for (i=first ; i<last ; i++) {
	j = MYNODE ^ i;
	k = j*block_size;
	UMD_Sendrecv_nb(j, &ts, sc,
			j, &tr, sc);
	UMD_Sendrecv_nb(j, sendbuf+k, block_size,
			j, recvbuf+k, block_size);
      }
    }
  }
  else {
    if (MYTHREAD < NODES) {
      if (MYTHREAD==0) {
	k = MYNODE*block_size;
	memcpy(recvbuf+k, sendbuf+k, block_size);
      }
      else {
	j = MYNODE ^ MYTHREAD;
	k = j*block_size;
	UMD_Sendrecv_nb(j, sendbuf+k, block_size,
			j, recvbuf+k, block_size);
      }
    }
  }
  return 0;
}
#endif /* !SMPONLY */
#endif /* 0 */

#ifndef SMPONLY
int    all_Alltoall_i(int    *sendbuf, int block_size, int    *recvbuf,
		      THREADED) {
  return all_Alltoall_c((char *)sendbuf, block_size*sizeof(int), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoall_l(long   *sendbuf, int block_size, long   *recvbuf,
		      THREADED) {
  return all_Alltoall_c((char *)sendbuf, block_size*sizeof(long), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoall_d(double *sendbuf, int block_size, double *recvbuf,
		      THREADED) {
  return all_Alltoall_c((char *)sendbuf, block_size*sizeof(double), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_extent(char   *sendbuf, int *sendcnt, int *sendoff,
			    char   *recvbuf, int *recvcnt, int *recvoff,
			    int extent, THREADED) {
  register int first, last, i, j, t;
  /* extent = bytes per element */

  if (THREADS <= NODES) {
    t = NODES/THREADS;      

    first      = MYTHREAD*t;
    last       = first + t;
    if (last == t*THREADS) last=NODES;

    if (first==0) {
#if DEBUG
      if (sendcnt[MYNODE] != recvcnt[MYNODE]) {
	fprintf(outfile,"PE%3d(%3d): ERROR: all_Alltoallv_extent()\n",
		MYNODE,MYTHREAD);
	fflush(outfile);
      }
#endif /* DEBUG */
      memcpy(recvbuf+(recvoff[MYNODE]*extent),
	     sendbuf+(sendoff[MYNODE]*extent),
	     sendcnt[MYNODE]*extent);
      first = 1;
    }
    for (i=first ; i<last ; i++) {
      j = MYNODE ^ i;
      UMD_Sendrecv_nb(j, sendbuf+(sendoff[j]*extent), sendcnt[j]*extent,
		      j, recvbuf+(recvoff[j]*extent), recvcnt[j]*extent);
    }
  }
  else {
    if (MYTHREAD<NODES) {
      if (MYTHREAD==0) {
	memcpy(recvbuf+(recvoff[MYNODE]*extent),
	       sendbuf+(sendoff[MYNODE]*extent),
	       sendcnt[MYNODE]*extent);
      }
      else {
	j = MYNODE ^ MYTHREAD;
	UMD_Sendrecv_nb(j, sendbuf+(sendoff[j]*extent), sendcnt[j]*extent,
			j, recvbuf+(recvoff[j]*extent), recvcnt[j]*extent);
      }
    }
  }
  return 0;
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_logextent(char   *sendbuf, int *sendcnt, int *sendoff,
			       char   *recvbuf, int *recvcnt, int *recvoff,
			       int logextent, THREADED) {
  register int first, last, i, j, t;
  /* logextent = log2( bytes per element ) */

  if (THREADS <= NODES) {
    t = NODES/THREADS;      

    first      = MYTHREAD*t;
    last       = first + t;
    if (last == t*THREADS) last=NODES;

    if (first==0) {
#if DEBUG
      if (sendcnt[MYNODE] != recvcnt[MYNODE]) {
	fprintf(outfile,"PE%3d(%3d): ERROR: all_Alltoallv_logextent()\n",
		MYNODE,MYTHREAD);
	fflush(outfile);
      }
#endif /* DEBUG */
      memcpy(recvbuf+(recvoff[MYNODE]<<logextent),
	     sendbuf+(sendoff[MYNODE]<<logextent),
	     sendcnt[MYNODE]<<logextent);
      first = 1;
    }
    for (i=first ; i<last ; i++) {
      j = MYNODE ^ i;
      UMD_Sendrecv_nb(j, sendbuf+(sendoff[j]<<logextent),
		      sendcnt[j]<<logextent,
		      j, recvbuf+(recvoff[j]<<logextent),
		      recvcnt[j]<<logextent);
    }
  }
  else {
    if (MYTHREAD<NODES) {
      if (MYTHREAD==0) {
	memcpy(recvbuf+(recvoff[MYNODE]<<logextent),
	       sendbuf+(sendoff[MYNODE]<<logextent),
	       sendcnt[MYNODE]<<logextent);
      }
      else {
	j = MYNODE ^ MYTHREAD;
	UMD_Sendrecv_nb(j, sendbuf+(sendoff[j]<<logextent),
			sendcnt[j]<<logextent,
			j, recvbuf+(recvoff[j]<<logextent),
			recvcnt[j]<<logextent);
      }
    }
  }
  return 0;
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_c(char   *sendbuf, int *sendcnt, int *sendoff,
		       char   *recvbuf, int *recvcnt, int *recvoff,
		       THREADED) {
  return all_Alltoallv_logextent((char *)sendbuf,sendcnt,sendoff,
				 (char *)recvbuf,recvcnt,recvoff,
				 ilog2(sizeof(char)), TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_i(int    *sendbuf, int *sendcnt, int *sendoff,
		       int    *recvbuf, int *recvcnt, int *recvoff,
		       THREADED) {
  return all_Alltoallv_logextent((char *)sendbuf,sendcnt,sendoff,
				 (char *)recvbuf,recvcnt,recvoff,
				 ilog2(sizeof(int)), TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_l(long   *sendbuf, int *sendcnt, int *sendoff,
		       long   *recvbuf, int *recvcnt, int *recvoff,
		       THREADED) {
  return all_Alltoallv_logextent((char *)sendbuf,sendcnt,sendoff,
				 (char *)recvbuf,recvcnt,recvoff,
				 ilog2(sizeof(long)), TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Alltoallv_d(double *sendbuf, int *sendcnt, int *sendoff,
		       double *recvbuf, int *recvcnt, int *recvoff,
		       THREADED) {
  return all_Alltoallv_logextent((char *)sendbuf,sendcnt,sendoff,
				 (char *)recvbuf,recvcnt,recvoff,
				 ilog2(sizeof(double)), TH);
}
#endif /* !SMPONLY */

#if 0
#ifndef SMPONLY
int    all_Gather_c(char   *sendbuf, int block_size, char   *recvbuf,
		    THREADED) {
  register int i, t, first, last;
  char *buf;

  if (MYNODE > 0) {
    on_one_thread
      UMD_Send(0, sendbuf, block_size);
  }
  else {

    if (THREADS <= NODES) {
      t = NODES/THREADS;     

      first      = MYTHREAD*t;
      last       = first + t;
      if (last == t*THREADS) last=NODES;

      on_one_thread {
	memcpy(recvbuf, sendbuf, block_size);
	first = 1;
      }
      buf = recvbuf + (block_size*first);
      for (i=first ; i<last ; i++) {
	UMD_Recv(i, buf, block_size);
	buf += block_size;
      }
    }
    else {
      if (MYTHREAD<NODES) {
	if (MYTHREAD==0) {
	  memcpy(recvbuf, sendbuf, block_size);
	}
	else {
	  buf = recvbuf + (block_size*MYTHREAD);
	  UMD_Recv(MYTHREAD, buf, block_size);
	}
      }
    }
  }
  /*  all_Barrier(TH); */
  return 0;
}
#endif /* !SMPONLY */
#endif /* 0 */
#if 0
#ifndef SMPONLY
int    all_Gather_c(char   *sendbuf, int block_size, char   *recvbuf,
		    THREADED) {
  register int i, t, first, last;
  char *buf;
  char ts, tr;
  int sc = sizeof(char);

  if (MYNODE > 0) {
    on_one_thread {
      UMD_Sendrecv_nb(0, &ts, sc, 0, &tr, sc);
      UMD_Send(0, sendbuf, block_size);
    }
  }
  else {

    if (THREADS <= NODES) {
      t = NODES/THREADS;     

      first      = MYTHREAD*t;
      last       = first + t;
      if (last == t*THREADS) last=NODES;

      on_one_thread {
	memcpy(recvbuf, sendbuf, block_size);
	first = 1;
      }
      buf = recvbuf + (block_size*first);
      for (i=first ; i<last ; i++) {
	UMD_Sendrecv_nb(i, &ts, sc, i, &tr, sc);
	UMD_Recv(i, buf, block_size);
	buf += block_size;
      }
    }
    else {
      if (MYTHREAD<NODES) {
	if (MYTHREAD==0) {
	  memcpy(recvbuf, sendbuf, block_size);
	}
	else {
	  buf = recvbuf + (block_size*MYTHREAD);
	  UMD_Sendrecv_nb(MYTHREAD, &ts, sc, MYTHREAD, &tr, sc);
	  UMD_Recv(MYTHREAD, buf, block_size);
	}
      }
    }
  }
  /*  all_Barrier(TH); */
  return 0;
}
#endif /* !SMPONLY */
#endif /* 0 */
#if 1
#ifndef SMPONLY
int    all_Gather_c(char   *sendbuf, int block_size, char   *recvbuf,
		    THREADED) {
  register int i, t, first, last, thpart;
  char *buf;
  char ts, tr;
  int sc = sizeof(char);

  if (MYNODE > 0) {
    on_one_thread {
      UMD_Sendrecv_nb(0, &ts, sc, 0, &tr, sc);
      UMD_Send(0, sendbuf, block_size);
    }
  }
  else {

    if (THREADS <= NODES) {
      thpart = min(MAX_GATHER, THREADS);     
      if (MYTHREAD < thpart) {
	t = NODES/thpart;

	first      = MYTHREAD*t;
	last       = first + t;
	if (last == t*thpart) last=NODES;

	on_one_thread {
	  memcpy(recvbuf, sendbuf, block_size);
	  first = 1;
	}
	buf = recvbuf + (block_size*first);
	for (i=first ; i<last ; i++) {
	  UMD_Sendrecv_nb(i, &ts, sc, i, &tr, sc);
	  UMD_Recv(i, buf, block_size);
	  buf += block_size;
	}
      }
    }
    else {
      if (MYTHREAD<NODES) {
	if (MYTHREAD==0) {
	  memcpy(recvbuf, sendbuf, block_size);
	}
	else {
	  buf = recvbuf + (block_size*MYTHREAD);
	  UMD_Sendrecv_nb(MYTHREAD, &ts, sc, MYTHREAD, &tr, sc);
	  UMD_Recv(MYTHREAD, buf, block_size);
	}
      }
    }
  }
  /*  all_Barrier(TH); */
  return 0;
}
#endif /* !SMPONLY */
#endif /* 1 */

#ifndef SMPONLY
int    all_Gather_i(int    *sendbuf, int block_size, int    *recvbuf,
		    THREADED) {
  return all_Gather_c((char *)sendbuf, block_size*sizeof(int), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Gather_l(long   *sendbuf, int block_size, long   *recvbuf,
		    THREADED) {
  return all_Gather_c((char *)sendbuf, block_size*sizeof(long), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Gather_d(double *sendbuf, int block_size, double *recvbuf,
		    THREADED) {
  return all_Gather_c((char *)sendbuf, block_size*sizeof(double), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Scatter_c(char   *sendbuf, int block_size, char   *recvbuf,
		     THREADED) {
  register int i, t, first, last;
  char *buf;

  if (MYNODE > 0) {
    on_one_thread 
      UMD_Recv(0, recvbuf, block_size);
  }
  else {

    if (THREADS <= NODES) {
      t = NODES/THREADS;     

      first      = MYTHREAD*t;
      last       = first + t;
      if (last == t*THREADS) last=NODES;

      on_one_thread {
	memcpy(recvbuf, sendbuf, block_size);
	first = 1;
      }
      buf = sendbuf + (block_size*first);
      for (i=first ; i<last ; i++) {
	UMD_Send(i, buf, block_size);
	buf += block_size;
      }
    }
    else {
      if (MYTHREAD<NODES) {
	if (MYTHREAD==0) {
	  memcpy(recvbuf, sendbuf, block_size);
	}
	else {
	  buf = sendbuf + (block_size*MYTHREAD);
	  UMD_Send(MYTHREAD, buf, block_size);
	}
      }
    }
  }
  /*  all_Barrier(TH); */
  return 0;
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Scatter_i(int    *sendbuf, int block_size, int    *recvbuf,
		     THREADED) {
  return all_Scatter_c((char *)sendbuf, block_size*sizeof(int), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Scatter_l(long   *sendbuf, int block_size, long   *recvbuf,
		     THREADED) {
  return all_Scatter_c((char *)sendbuf, block_size*sizeof(long), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_Scatter_d(double *sendbuf, int block_size, double *recvbuf,
		     THREADED) {
  return all_Scatter_c((char *)sendbuf, block_size*sizeof(double), (char *)recvbuf, TH);
}
#endif /* !SMPONLY */

#ifndef SMPONLY
int    all_ScatterX_i(int    *sendbuf, int array_size, int    *recvbuf,
		     THREADED) {
  register int i, s, t, first, last, block_size, rem;
  int *buf;

  block_size = array_size/NODES;
  rem = array_size - (block_size * NODES);
  if (MYNODE < rem)
    block_size++;
  
  s = block_size * sizeof(int);

  if (MYNODE > 0) {
    on_one_thread 
      UMD_Recv(0, recvbuf, s);
  }
  else {
    int b0, s0, b1, s1;
    if (rem > 0) {
      b1 = block_size;
      s1 = s;
      b0 = block_size-1;
      s0 = s - sizeof(int);
    }
    else {
      b0 = block_size;
      s0 = s;
      b1 = block_size+1;
      s1 = s + sizeof(int);
    }

    if (THREADS <= NODES) {
      t = NODES/THREADS;     
      
      first      = MYTHREAD*t;
      last       = first + t;
      if (last == t*THREADS) last=NODES;

      on_one_thread {
	if (rem > 0) {
	  s          = s1;
	  block_size = b1;
	}
	else {
	  s          = s0;
	  block_size = b0;
	}
	memcpy(recvbuf, sendbuf, s);
	first = 1;
      }
      if (first < rem)
	buf = sendbuf + (b1*first);
      else
	buf = sendbuf + (b1*rem) + (b0 * (first-rem));
      for (i=first ; i<last ; i++) {
	if (i < rem) {
	  UMD_Send(i, buf, s1);
	  buf += b1;
	}
	else {
	  UMD_Send(i, buf, s0);
	  buf += b0;
	}
      }
    }
    else {
      if (MYTHREAD < NODES) {
	if (MYTHREAD==0) {
	  memcpy(recvbuf, sendbuf, s);
	}
	else {
	  if (MYTHREAD < rem) {
	    buf = sendbuf + (b1*MYTHREAD);
	    UMD_Send(MYTHREAD, buf, s1);
	  }
	  else {
	    buf = sendbuf + (b1*rem) + (b0 * (MYTHREAD-rem));
	    UMD_Send(MYTHREAD, buf, s0);
	  }
	}
      }
    }
  }
  /*  all_Barrier(TH); */

  return(block_size);
}
#endif /* !SMPONLY */

/************************************************************/
btask_t create_btask(void *(*run_routine)(void *), void *run_arg) {
  int rc;
#if !(defined(PTHREAD_USE_D4))
  pthread_attr_t pattr;
#if ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
     (_MACH_SCHED == _MACH_SCHED_1003_1b))
  struct sched_param psched;
#endif /* ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
	  (_MACH_SCHED == _MACH_SCHED_1003_1b)) */
#endif /* !(defined(PTHREAD_USE_D4)) */

  btask_t the_btask;
  the_btask = (btask_t)malloc(sizeof(pthread_t));
  assert_malloc(the_btask);

#if !(defined(AIX)||defined(SOLARIS)||defined(LINUX))
#if !(defined(PTHREAD_USE_D4))

  rc = pthread_attr_init(&pattr);
  if (rc)
    perror("pthread_attr_init");

#if ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
     (_MACH_SCHED == _MACH_SCHED_1003_1b))
      
   rc = pthread_attr_setschedpolicy(&pattr, SCHED_FIFO);
   if (rc)
     perror("pthread_attr_setschedpolicy");
    
#if (_MACH_SCHED == _MACH_SCHED_1003_1b)
   psched.sched_priority = sched_get_priority_min(SCHED_FIFO);
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_1b) */
#if (_MACH_SCHED == _MACH_SCHED_1003_1c)
   psched.sched_priority = PRI_FIFO_MIN;
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_1c) */
    
   rc = pthread_attr_setschedparam(&pattr, &psched);
   if (rc)
     perror("pthread_attr_isetschedparam");
    
   rc = pthread_attr_setinheritsched(&pattr, PTHREAD_EXPLICIT_SCHED);
   if (rc)
     perror("pthread_attr_setinheritsched");
#endif /* ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
	  (_MACH_SCHED == _MACH_SCHED_1003_1b)) */
#if (_MACH_SCHED == _MACH_SCHED_1003_4a)
   rc = pthread_attr_setsched(&pattr, SCHED_FIFO);
   if (rc)
     perror("pthread_attr_setsched");

   rc = pthread_attr_setprio(&pattr, /*101*/  50 /*MIN_PRIORITY*/ );
   if (rc)
     perror("pthread_attr_setprio");

   rc = pthread_attr_setinheritsched(&pattr, PTHREAD_INHERIT_SCHED);
   if (rc)
     perror("pthread_attr_setinheritsched");
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_4a) */
    
#endif /* !(defined(PTHREAD_USE_D4)) */
#endif /* !(defined(AIX)||defined(SOLARIS)||defined(LINUX)) */
   
  pthread_create((pthread_t *)the_btask,
#if !(defined(AIX)||defined(SOLARIS)||defined(LINUX))
		 &pattr,
#else /* defined(AIX)||defined(SOLARIS)||defined(LINUX) */
		 NULL,
#endif /* !(defined(AIX)||defined(SOLARIS)||defined(LINUX)) */
		 run_routine, run_arg);
  return((btask_t)the_btask);
}

void waitfor_btask(btask_t the_btask) {
  int run_arg;
  pthread_join((pthread_t)*the_btask, (void *)&run_arg);
  return;
}

void kill_btask(btask_t the_btask, int sig) {
  pthread_kill((pthread_t)*the_btask, sig);
  return;
}
/************************************************************/
     

#if 0
SIMPLE_get_args(int argc, char **argv) {
  char
    *s,**argvv = argv;

#if 0
  int i;
  printf("SIMPLE argc: %d\n",argc);
  for (i=0 ; i <argc ; i++)
    printf("SIMPLE argv[%2d]: %s\n",i,argv[i]);
#endif /* 0 */
    
#if 0
  while (--argc > 0 && (*++argvv)[0] == '-')
    for (s=argvv[0]+1; *s != '\0'; s++) {}
#endif /* 0 */

  while (--argc > 0) 
    if ((*++argvv)[0] == '-')
      for (s=argvv[0]+1; *s != '\0'; s++) 
	switch (*s) {
	case 't':
	  if (argc <= 1) 
	    perror("number of threads per node expected after -t");
	  argc--;
	  THREADS = atoi(*++argvv);
#if 0
	  fprintf(outfile,"SIMPLE_get_args: THREADS: %d\n",THREADS);
	  fflush(outfile);
#endif /* 0 */
	  break;
	case 'h':
	  fprintf(outfile,"SIMPLE Options:\n");
	  fprintf(outfile," -t <number of threads per node>\n");
	  fprintf(outfile,"\n\n");
	  fflush(outfile);
	  break;
/*	default: perror("illegal option");  */
	}

  return 0;
}
#endif /* 0 */

SIMPLE_get_args(int *argc, char* **argv) {
  int numarg = *argc;
  int done = 0;
  char
    *s,**argvv = *argv;

#if 0
  int i;
  printf("SIMPLE argc: %d\n",*argc);
  for (i=0 ; i <numarg ; i++)
    printf("SIMPLE argv[%2d]: %s\n",i,(*argv)[i]);
#endif /* 0 */
    
#if 0
  while (--numarg > 0 && (*++argvv)[0] == '-')
    for (s=argvv[0]+1; *s != '\0'; s++) {}
#endif /* 0 */

  while ((--numarg > 0) && !done)
    if ((*++argvv)[0] == '-')
      for (s=argvv[0]+1; *s != '\0'; s++)
	if (*s == '-')
	  done = 1;
	else {
	  switch (*s) {
	  case 't':
	    if (numarg <= 1) 
	      perror("number of threads per node expected after -t");
	    numarg--;
	    THREADS = atoi(*++argvv);
#if 0
	    fprintf(outfile,"SIMPLE_get_args: THREADS: %d\n",THREADS);
	    fflush(outfile);
#endif /* 0 */
	    break;
	  case 'h':
	    fprintf(outfile,"SIMPLE Options:\n");
	    fprintf(outfile," -t <number of threads per node>\n");
	    fprintf(outfile,"\n\n");
	    fflush(outfile);
	    break;
	    /*	default: perror("illegal option");  */
	  }
	}
  if (done) {
    *argc = numarg;
    *argv = ++argvv;
  }
  else {
    *argc = 0;
    *argv = NULL;
  }
    
#if 0
  printf("XXSIMPLE argc: %d\n",*argc);
  for (i=0 ; i <numarg ; i++)
    printf("XXSIMPLE argv[%2d]: %s\n",i,(*argv)[i]);
  printf("XXSIMPLE done\n");
#endif /* 0 */

  return 0;
}

void *SIMPLE_mainstub(void *vti) {

#if defined(SOLARIS)
#define _LOADSIZE 100
#define _LOADLOOP 5
  int i,j;
  int *array;
  array = (int *)malloc(_LOADSIZE*sizeof(int));
  if (array == (int*)NULL) {
    fprintf(stderr,"ERROR: could not initialize (SIMPLE_mainstub)\n");
    exit(-1);
  }
  for (j=0 ; j<_LOADLOOP ; j++)
    for (i=0 ; i<_LOADSIZE ; i++) 
      array[i] = i;
  free(array);
#endif /* defined(SOLARIS) */

  return SIMPLE_main((uthread_info_t *)vti);
}

void SIMPLE_Init(argc,argv)
int* argc;
char* **argv;
{

    int _syscpu, _sysclk;
    int i, rc;
    uthread_info_t *ti;

#if !(defined(PTHREAD_USE_D4))
    pthread_attr_t pattr;
#if ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
     (_MACH_SCHED == _MACH_SCHED_1003_1b))

    struct sched_param psched;

#endif /* ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
	  (_MACH_SCHED == _MACH_SCHED_1003_1b)) */
#endif /* !(defined(PTHREAD_USE_D4)) */

#if (_MACH_THREAD == _MACH_THREAD_SYS)
    getsysinfo(GSI_CPUS_IN_BOX, (caddr_t)&_syscpu, sizeof(int), 0, NULL);
    getsysinfo(GSI_CLK_TCK, (caddr_t)&_sysclk, sizeof(int), 0, NULL);
#endif /* (_MACH_THREAD == _MACH_THREAD_SYS) */

#if INFO
#ifndef SMPONLY
    on_one_node {
      fprintf(outfile,"NODES: %d\n", NODES);
      fflush(outfile);
    }
#endif /* !SMPONLY */
#if (_MACH_THREAD == _MACH_THREAD_SYS)
#ifndef SMPONLY
    fprintf(outfile,
	    "PE%3d: Machine: %d CPUS running at %d clk/s (load: %6.3f)\n",
	    MYNODE,_syscpu,_sysclk, get_load());
#else /* SMPONLY */
    fprintf(outfile,
	    "Machine: %d CPUS running at %d clk/s (load: %6.3f)\n",
	    _syscpu,_sysclk, get_load());
#endif /* SMPONLY */
    fflush(outfile);
#endif /* (_MACH_THREAD == _MACH_THREAD_SYS) */
#endif /* INFO */

#if (_MACH_THREAD == _MACH_THREAD_SYS)
    THREADS       = _syscpu;
#elif (_MACH_THREAD == _MACH_THREAD_DEF)
    THREADS       = DEFAULT_THREADS;
#endif /* _MACH_THREAD */
    
    SIMPLE_get_args(argc, argv);
    
#if INFO
#ifndef SMPONLY
    on_one_node
    {
      fprintf(outfile,"THREADS: %d\n", THREADS);
      fflush(outfile);
    }
#else /* SMPONLY */
    fprintf(outfile,"THREADS: %d\n", THREADS);
    fflush(outfile);
#endif /* SMPONLY */
#endif /*INFO */

    /*********************************/
    /* ON ONE THREAD INITIALIZATION  */
    /*********************************/

    node_Barrier_sync_init();
    node_Barrier_tree_init();

    red_i = smp_reduce_init_i(THREADS);
    red_l = smp_reduce_init_l(THREADS);
    red_d = smp_reduce_init_d(THREADS);

    scan_i = smp_scan_init_i(THREADS);
    scan_l = smp_scan_init_l(THREADS);
    scan_d = smp_scan_init_d(THREADS);

#if !(defined(AIX)||defined(LINUX)||defined(FREEBSD))
#if !(defined(PTHREAD_USE_D4))

#if 0
    /*********************/
    /* PROCESS SCHEDULER */
    /*********************/
    rc = setpriority(PRIO_PROCESS, 0, -20);
    if (rc)
      perror("setpriority");
#endif /* 0 */

    /*********************/
    /* THREAD  SCHEDULER */
    /*********************/
    rc = pthread_attr_init(&pattr);
    if (rc)
      perror("pthread_attr_init");

#if ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
     (_MACH_SCHED == _MACH_SCHED_1003_1b))

#if 0
    rc = pthread_attr_setscope(&pattr, PTHREAD_SCOPE_PROCESS);
    if (rc)
      perror("pthread_attr_setscope");
#endif

    rc = pthread_attr_setschedpolicy(&pattr, SCHED_FIFO);
    if (rc)
      perror("pthread_attr_setschedpolicy");
    
#if (_MACH_SCHED == _MACH_SCHED_1003_1b)
    psched.sched_priority = sched_get_priority_max(SCHED_FIFO);
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_1b) */
#if (_MACH_SCHED == _MACH_SCHED_1003_1c)
    psched.sched_priority = PRI_FIFO_MAX;
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_1c) */
    
    rc = pthread_attr_setschedparam(&pattr, &psched);
    if (rc)
      perror("pthread_attr_isetschedparam");
    
    rc = pthread_attr_setinheritsched(&pattr, PTHREAD_EXPLICIT_SCHED);
    if (rc)
      perror("pthread_attr_setinheritsched");
#endif /* ((_MACH_SCHED == _MACH_SCHED_1003_1c) || \
	  (_MACH_SCHED == _MACH_SCHED_1003_1b)) */

#if (_MACH_SCHED == _MACH_SCHED_1003_4a)
    rc = pthread_attr_setsched(&pattr, SCHED_FIFO);
    if (rc)
      perror("pthread_attr_setsched");

    rc = pthread_attr_setprio(&pattr, 101 /* MAX_PRIORITY */);
    if (rc)
      perror("pthread_attr_setprio");

    rc = pthread_attr_setinheritsched(&pattr, PTHREAD_INHERIT_SCHED);
    if (rc)
      perror("pthread_attr_setinheritsched");
#endif /* (_MACH_SCHED == _MACH_SCHED_1003_4a) */
    
#endif /* !(defined(PTHREAD_USE_D4)) */
#endif /* !(defined(AIX)||defined(LINUX)||defined(FREEBSD)) */

#if (defined(SOLARIS))
    rc = pthread_setconcurrency(THREADS+1);
    if (rc)
      perror("pthread_setconcurrency");
#endif /* defined(SOLARIS) */
    
    spawn_thread = (pthread_t *)malloc(NOSHARE(THREADS)*
				       sizeof(pthread_t));
    assert_malloc(spawn_thread);
    uthread_info = (uthread_info_t *)malloc(NOSHARE(THREADS)*
					  sizeof(uthread_info_t));
    assert_malloc(uthread_info);

    ti = uthread_info;

    for (i=0 ; i<THREADS ; i++) {
      ti->mythread   = i;

      ti->argc       = *argc;
      ti->argv       = *argv;

#ifndef SMPONLY
      ti->id         = (MYNODE*THREADS)+i;
      ti->tid        = NODES*THREADS;
#endif /* !SMPONLY */

#ifdef RRANDOM
      rrandom_init_th(ti);
#endif /* RRANDOM */

#ifdef SPRNG
      THSPRNG        = (int *)NULL;
#endif
      
#if defined(PTHREAD_USE_D4)
      rc = pthread_create(spawn_thread + NOSHARE(i),
			  pthread_attr_default,
			  (pthread_startroutine_t)SIMPLE_mainstub,
			  ti);
#else /* !defined(PTHREAD_USE_D4) */
      rc = pthread_create(spawn_thread + NOSHARE(i),
#if !(defined(AIX)||defined(LINUX)||defined(FREEBSD))
			  &pattr,
#else /* defined(AIX)||defined(LINUX)||defined(FREEBSD) */
			  NULL,
#endif /* !(defined(AIX)||defined(LINUX)||defined(FREEBSD)) */
			  SIMPLE_mainstub,
			  ti);
#endif /* defined(PTHREAD_USE_D4) */
      ti += NOSHARE(1);
    }
}

void SIMPLE_Finalize() {
  int i;
#if defined(PTHREAD_USE_D4)
  pthread_addr_t parg;
#else /* !defined(PTHREAD_USE_D4) */
  int *parg;
#endif /* defined(PTHREAD_USE_D4) */

  for (i=0 ; i<THREADS ; i++)
#if defined(PTHREAD_USE_D4)    
    pthread_join(spawn_thread[NOSHARE(i)], &parg);
#else /* !defined(PTHREAD_USE_D4) */
    pthread_join(spawn_thread[NOSHARE(i)], (void *)&parg);
#endif /* defined(PTHREAD_USE_D4) */

  /*********************************/
  /* ONE ONE THREAD DESTRUCTION    */
  /*********************************/

  smp_reduce_destroy_i(red_i);
  smp_reduce_destroy_l(red_l);
  smp_reduce_destroy_d(red_d);
  smp_scan_destroy_i(scan_i);
  smp_scan_destroy_l(scan_l);
  smp_scan_destroy_d(scan_d);

  node_Barrier_sync_destroy();
  node_Barrier_tree_destroy();
  
  free(uthread_info);
  free(spawn_thread);
}

void SIMPLE_Cleanup(THREADED) {
#ifdef RRANDOM
  rrandom_destroy_th(TH);
#endif /* RRANDOM */
  return;
}

/****************************************/
/****************************************/
main(argc,argv)
int argc;
char **argv;
{
#ifndef SMPONLY
  UMD_Init(&argc,&argv);
#else /* SMPONLY */
  main_get_args(argc, argv);
#endif
  SIMPLE_Init(&argc,&argv);
  
  SIMPLE_Finalize();
#ifndef SMPONLY
  UMD_Finalize();
#endif /* !SMPONLY */
  exit(0);
}
/****************************************/
/****************************************/
