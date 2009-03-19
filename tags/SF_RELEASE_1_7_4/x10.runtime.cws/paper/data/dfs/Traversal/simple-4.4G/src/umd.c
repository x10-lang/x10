#include <stdio.h>
#if 0
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>		/* struct timeval */
#include <sys/resource.h>
#include <math.h>
#include <string.h>

#include <sys/socket.h>
#include <netinet/in.h>
#include <netinet/tcp.h>
#include <netdb.h>
#include <fcntl.h>
#include "umd.h"

#include <unistd.h>
#if _NB_COMM
#include <pthread.h>
#endif /* _NB_COMM */ 

int     MYNODE, NODES;

#define DEFAULT_MACHFILENAME "machines.alpha.A"

#define DEFAULT_TCP_BASE   6357
#define DEFAULT_SENDWIN    (128*1024)
#define DEFAULT_RECVWIN    (128*1024)
#define DEFAULT_LISTEN     1
#define GS_TIME            0
#define TIME_BARRIER       0

#define DEFAULT_BLKSIZE    (1<<14)

#define DEBUG_TIMING       0
#define DEBUG_TIMING_REQ   0
#define DEBUG_CONNECT      0
#define DEBUG_WINDOW       0
#define DEBUG_VERBOSE      0
#define DEBUG_ERRNO        0
#define DEBUG_MPI          0
#define DEBUG              1

#if defined(_MP_MPI)
#define _MPI_TAG           0
pthread_mutex_t _mpi_lock_simple;
MPI_Status      mstat;
#endif /* _MP_MPI */

#if defined(_MP_UMD)
typedef struct sockaddr_in skt;
       
skt *sins;

int *sd;                	/* fd of network socket */
char **host;			/* ptr to name of hosts */
int verbose = 0;		/* 0=print basic info, 1=print cpu rate, proc
				 * resource usage. */
int window  = 1;		/* 0=use default   1=set to specified size*/
int one     = 1;                /* for 4.3 BSD style setsockopt() */
int nodelay = 1;		/* set TCP_NODELAY socket option */
int options = 0;		/* socket options ( |= SO_DEBUG ) */
extern int errno;
#endif /* _MP_UMD */

char *machfilename;             /* machine filename */
	 
#ifdef _NB_COMM_LOCK
pthread_mutex_t write_lock[MAXNODES];
pthread_mutex_t  recv_lock[MAXNODES];
#endif /* _NB_COMM_LOCK */

typedef struct {
  int brecv;
} UMD_stat;

#if DEBUG_TIMING_REQ
char stats[128];
#endif /* DEBUG_TIMING_REQ */
#if DEBUG_TIMING
int numCalls;		/* # of NRead/NWrite calls. */
#endif /* DEBUG_TIMING */


#if DEBUG_TIMING_REQ
void prep_timer();
double read_timer();
double cput, realt;		/* user, real time (seconds) */
#endif /* DEBUG_TIMING_REQ */



void
err(s, trans)
     char *s;
     int trans;
{
  fprintf(stderr,"PE%3d: fast%s: ", MYNODE, trans?"-t":"-r");
  perror(s);
  fprintf(stderr,"PE%3d: errno=%d\n",MYNODE, errno);
  exit(1);
}

void
mes(s, trans)
     char *s;
     int trans;
{
#if DEBUG_VERBOSE
  fprintf(stderr,"PE%3d: ttcp%s: %s\n", MYNODE, trans?"-t":"-r", s);
#endif /* DEBUG_VERBOSE */
  return;
}

void
pattern( cp, cnt )
     register char *cp;
     register int cnt;
{
  register char c;
  c = 0;
  while ( cnt-- > 0 )  {
    while ( !isprint((c&0x7F)) )  c++;
    *cp++ = (c++&0x7F);
  }
}

#if DEBUG_TIMING_REQ
static struct	timeval time0;	/* Time at which timing started */
static struct	rusage ru0;	/* Resource utilization at the start */

static void prusage();
static void tvadd();
static void tvsub();
static void psecs();

void prep_timer()
{
  gettimeofday(&time0, (struct timezone *)0);
  getrusage(RUSAGE_SELF, &ru0);
}

double read_timer(str,len)
     char *str;
{
  struct timeval timedol;
  struct rusage ru1;
  struct timeval td;
  struct timeval tend, tstart;
  char line[132];
  
  getrusage(RUSAGE_SELF, &ru1);
  gettimeofday(&timedol, (struct timezone *)0);
  prusage(&ru0, &ru1, &timedol, &time0, line);
  (void)strncpy( str, line, len );
  
  /* Get real time */
  tvsub( &td, &timedol, &time0 );
  realt = td.tv_sec + ((double)td.tv_usec) / 1000000;
  
  /* Get CPU time (user+sys) */
  tvadd( &tend, &ru1.ru_utime, &ru1.ru_stime );
  tvadd( &tstart, &ru0.ru_utime, &ru0.ru_stime );
  tvsub( &td, &tend, &tstart );
  cput = td.tv_sec + ((double)td.tv_usec) / 1000000;
  if ( cput < 0.00001 )  cput = 0.00001;
  return( cput );
}

static void
prusage(r0, r1, e, b, outp)
     register struct rusage *r0, *r1;
     struct timeval *e, *b;
     char *outp;
{
  struct timeval tdiff;
  register time_t t;
  register char *cp;
  register int i;
  int ms;
  
  t = (r1->ru_utime.tv_sec-r0->ru_utime.tv_sec)*100+
    (r1->ru_utime.tv_usec-r0->ru_utime.tv_usec)/10000+
    (r1->ru_stime.tv_sec-r0->ru_stime.tv_sec)*100+
    (r1->ru_stime.tv_usec-r0->ru_stime.tv_usec)/10000;
  ms =  (e->tv_sec-b->tv_sec)*100 + (e->tv_usec-b->tv_usec)/10000;
  
#define END(x)	{while (*x) x++;}
  cp = "%Uuser %Ssys %Ereal %P %Xi+%Dd %Mmaxrss %F+%Rpf %Ccsw";
  for (; *cp; cp++)  {
    if (*cp != '%')
      *outp++ = *cp;
    else if (cp[1]) switch(*++cp) {
      
    case 'U':
      tvsub(&tdiff, &r1->ru_utime, &r0->ru_utime);
      sprintf(outp,"%d.%01d", tdiff.tv_sec, tdiff.tv_usec/100000);
      END(outp);
      break;
      
    case 'S':
      tvsub(&tdiff, &r1->ru_stime, &r0->ru_stime);
      sprintf(outp,"%d.%01d", tdiff.tv_sec, tdiff.tv_usec/100000);
      END(outp);
      break;
      
    case 'E':
      psecs(ms / 100, outp);
      END(outp);
      break;
      
    case 'P':
      sprintf(outp,"%d%%", (int) (t*100 / ((ms ? ms : 1))));
      END(outp);
      break;
      
    case 'W':
      i = r1->ru_nswap - r0->ru_nswap;
      sprintf(outp,"%d", i);
      END(outp);
      break;
      
    case 'X':
      sprintf(outp,"%d", t == 0 ? 0 : (r1->ru_ixrss-r0->ru_ixrss)/t);
      END(outp);
      break;
      
    case 'D':
      sprintf(outp,"%d", t == 0 ? 0 :
	      (r1->ru_idrss+r1->ru_isrss-(r0->ru_idrss+r0->ru_isrss))/t);
      END(outp);
      break;
      
    case 'K':
      sprintf(outp,"%d", t == 0 ? 0 :
	      ((r1->ru_ixrss+r1->ru_isrss+r1->ru_idrss) -
	       (r0->ru_ixrss+r0->ru_idrss+r0->ru_isrss))/t);
      END(outp);
      break;
      
    case 'M':
      sprintf(outp,"%d", r1->ru_maxrss/2);
      END(outp);
      break;
      
    case 'F':
      sprintf(outp,"%d", r1->ru_majflt-r0->ru_majflt);
      END(outp);
      break;
      
    case 'R':
      sprintf(outp,"%d", r1->ru_minflt-r0->ru_minflt);
      END(outp);
      break;
      
    case 'I':
      sprintf(outp,"%d", r1->ru_inblock-r0->ru_inblock);
      END(outp);
      break;
      
    case 'O':
      sprintf(outp,"%d", r1->ru_oublock-r0->ru_oublock);
      END(outp);
      break;
    case 'C':
      sprintf(outp,"%d+%d", r1->ru_nvcsw-r0->ru_nvcsw,
	      r1->ru_nivcsw-r0->ru_nivcsw );
      END(outp);
      break;
    }
  }
  *outp = '\0';
}

static void
tvadd(tsum, t0, t1)
     struct timeval *tsum, *t0, *t1;
{
  
  tsum->tv_sec = t0->tv_sec + t1->tv_sec;
  tsum->tv_usec = t0->tv_usec + t1->tv_usec;
  if (tsum->tv_usec > 1000000)
    tsum->tv_sec++, tsum->tv_usec -= 1000000;
}

static void
tvsub(tdiff, t1, t0)
     struct timeval *tdiff, *t1, *t0;
{
  
  tdiff->tv_sec = t1->tv_sec - t0->tv_sec;
  tdiff->tv_usec = t1->tv_usec - t0->tv_usec;
  if (tdiff->tv_usec < 0)
    tdiff->tv_sec--, tdiff->tv_usec += 1000000;
}

static void
psecs(l,cp)
     long l;
     register char *cp;
{
  register int i;
  
  i = l / 3600;
  if (i) {
    sprintf(cp,"%d:", i);
    END(cp);
    i = l % 3600;
    sprintf(cp,"%d%d", (i/60) / 10, (i/60) % 10);
    END(cp);
  } else {
    i = l;
    sprintf(cp,"%d", i / 60);
    END(cp);
  }
  i %= 60;
  *cp++ = ':';
  sprintf(cp,"%d%d", i / 10, i % 10);
}

delay(us)
{
  struct timeval tv;
  
  tv.tv_sec = 0;
  tv.tv_usec = us;
  (void)select( 1, (char *)0, (char *)0, (char *)0, &tv );
  return(1);
}
#endif /* DEBUG_TIMING_REQ */


#if defined(_MP_UMD)
int UMD_Send(int dst, void *arr, int len) {
  int cnt;
  
#if GS_TIME
  double secs = get_seconds();
#endif /* GS_TIME */

#if DEBUG_TIMING_REQ
  prep_timer();
#endif /* DEBUG_TIMING_REQ */
  
  errno = 0;

#if _NB_COMM_LOCK
  pthread_mutex_lock(&write_lock[dst]);
#endif /* _NB_COMM_LOCK */
  cnt = write( sd[dst], arr, len);
#if _NB_COMM_LOCK
  pthread_mutex_unlock(&write_lock[dst]);
#endif /* _NB_COMM_LOCK */
  if (cnt != len)
    perror("ERROR: write()");

#if DEBUG_ERRNO
  if(errno) err("IO",1);
#endif /* DEBUG_ERRNO */

#if GS_TIME
  secs = get_seconds() - secs;
  if ( secs <= 0.0 )  secs = 0.000001;
  fprintf(outfile,
	  "PE%3d: ttcp-t: %9ld bytes in %.6f secs = %7.2f MB/sec = %8.4f Mb/s\n",
	  MYNODE, len, secs, (((double)len)/1000000.0)/secs,
	  (((double)len)/128000.0)/secs);
  fflush(outfile);
#endif /* GS_TIME */

#if DEBUG_TIMING
  numCalls++;
#endif /* DEBUG_TIMING */
#if DEBUG_TIMING_REQ  
  (void)read_timer(stats,sizeof(stats));
  
  if ( cput <= 0.0 )  cput = 0.001;
  if ( realt <= 0.0 )  realt = 0.000001;
#endif /* DEBUG TIMING_REQ */
#if DEBUG_TIMING_REQ
  fprintf(outfile,
	  "PE%3d: ttcp-t: %9ld bytes in %.6f real seconds = %7.2f MB/sec = %8.4f Mb/s\n",
	  MYNODE,len, realt, (((double)len)/1000000.0)/realt,
	  (((double)len)/128000.0)/realt);
  fflush(outfile);
#endif /* DEBUG TIMING_REQ */
#if DEBUG_TIMING
  if (verbose) {
    fprintf(outfile,
	    "PE%3d: ttcp-t: %ld bytes in %.2f CPU seconds = %.2f KB/cpu sec\n",
	    MYNODE,len, cput, ((double)len)/cput/1024 );
    fflush(outfile);
  }
  fprintf(outfile,
	  "PE%3d: ttcp-t: %d I/O calls, msec/call = %.2f, calls/sec = %.2f\n",
	  MYNODE, numCalls,
	  1024.0 * realt/((double)numCalls),
	  ((double)numCalls)/realt);

  fprintf(outfile,"PE%3d: ttcp-t: %s\n",MYNODE, stats);
  fflush(outfile);
#endif /* DEBUG TIMING */
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Send(int dst, void *arr, int len) {
  _MPI_WRAP(MPI_Send(arr, len, MPI_BYTE, dst, _MPI_TAG, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int UMD_Recv(int src, void *arr, int len) {
  char *buf;
  register int cnt;
  register int xbytes;

#if GS_TIME
  double secs = get_seconds();
#endif /* GS_TIME */

#if DEBUG_TIMING_REQ
  prep_timer();
#endif /* DEBUG_TIMING_REQ */

#if DEBUG_ERRNO
  errno = 0;
#endif /* DEBUG_ERRNO */

  xbytes = len;
  buf = (char *)arr;
#if _NB_COMM_LOCK
  pthread_mutex_lock(&recv_lock[src]);
#endif /* _NB_COMM_LOCK */
  while (xbytes > 0) {
    cnt = recv( sd[src], buf, xbytes, 0);
#if DEBUG_TIMING
    numCalls++;
#endif /* DEBUG_TIMING */
    xbytes -= cnt;
    buf    += cnt;
  }
#if _NB_COMM_LOCK
  pthread_mutex_unlock(&recv_lock[src]);
#endif /* _NB_COMM_LOCK */

#if DEBUG_ERRNO
  if (errno) err("IO",0);
#endif /* DEBUG_ERRNO */

#if GS_TIME
  secs = get_seconds() - secs;
  if ( secs <= 0.0 )  secs = 0.000001;
  fprintf(outfile,
	  "PE%3d: ttcp-r: %9ld bytes in %.6f secs = %7.2f MB/sec = %8.4f Mb/s\n",
	  MYNODE, len, secs, (((double)len)/1000000.0)/secs,
	  (((double)len)/128000.0)/secs);
  fflush(outfile);
#endif /* GS_TIME */
#if DEBUG_TIMING_REQ
  (void)read_timer(stats,sizeof(stats));

  if ( cput <= 0.0 )  cput = 0.001;
  if ( realt <= 0.0 )  realt = 0.000001; 
#endif /* DEBUG_TIMING_REQ */
#if DEBUG_TIMING_REQ
  fprintf(outfile,
	  "PE%3d: ttcp-r: %9ld bytes in %.6f real seconds = %7.2f MB/sec = %8.4f Mb/s\n",
	  MYNODE, len, realt, (((double)len)/1000000.0)/realt,
	  (((double)len)/128000.0)/realt);
  fflush(outfile);
#endif /* DEBUG_TIMING_REQ */
#if DEBUG_TIMING_REQ
  fprintf(outfile,
	  "PE%3d: ttcp-r: %9ld bytes in %.6f real seconds = %7.2f MB/sec = %8.4f Mb/s\n",
	  MYNODE, len, realt, (((double)len)/1000000.0)/realt,
	  (((double)len)/128000.0)/realt);
  fflush(outfile);
#endif /* DEBUG_TIMING_REQ */
#if DEBUG_TIMING
  if (verbose) {
    fprintf(outfile,
	    "PE%3d: ttcp-r: %ld bytes in %.2f CPU seconds = %.2f KB/cpu sec\n",
	    MYNODE, len, cput, ((double)len)/cput/1024 );
    fflush(outfile);
  }
  fprintf(outfile,
	  "PE%3d: ttcp-r: %d I/O calls, msec/call = %.2f, calls/sec = %.2f\n",
	  MYNODE, numCalls,
	  1024.0 * realt/((double)numCalls),
	  ((double)numCalls)/realt);
  
  fprintf(outfile,"PE%3d: ttcp-r: %s\n",MYNODE, stats);
  fflush(outfile);
#endif /* DEBUG_TIMING */
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Recv(int src, void *arr, int len) {
  _MPI_WRAP(MPI_Recv(arr, len, MPI_BYTE, src, _MPI_TAG, MPI_COMM_WORLD, &mstat));
  return 0;
}
#endif /* _MP_MPI */

#ifdef _ZCOMM

#define ZCHECK_ERR(err, msg) { \
    if (err != Z_OK) { \
        fprintf(stderr, "%s error: %d\n", msg, err); \
        exit(1); \
    } \
}

#if defined(_MP_UMD)
int UMD_ZSend(int dst, void *arr, int len) {
  int cnt;
  unsigned long clen;
  int err;
  char *carr;

  clen = len + (len/10) + 12;
  carr = (char *)malloc(clen*sizeof(char));
  assert_malloc(carr);

  err = compress(carr, &clen, arr, len);
  ZCHECK_ERR(err, "compress");
  
  write(sd[dst], &clen, sizeof(unsigned long));
  
  cnt = write( sd[dst], carr, clen);
  if (cnt != clen)
    perror("ERROR: write()");

  free(carr);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_ZSend(int dst, void *arr, int len) {
  int cnt;
  unsigned long clen;
  int err;
  char *carr;

  clen = len + (len/10) + 12;
  carr = (char *)malloc(clen*sizeof(char));
  assert_malloc(carr);

  err = compress(carr, &clen, arr, len);
  ZCHECK_ERR(err, "compress");

  UMD_Send(dst, &clen, sizeof(unsigned long));
  UMD_Send(dst, carr, clen);

  free(carr);
  return 0;

}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int UMD_ZRecv(int src, void *arr, int len) {
  char *buf;
  register int cnt;
  register int xbytes;
  unsigned long clen, ulen;
  char *carr;
  int err;

  recv(sd[src], &clen, sizeof(unsigned long), MSG_WAITALL);
  carr = (char *)malloc(clen*sizeof(char));
  assert_malloc(carr);
  
  xbytes = clen;
  buf = carr;
  while (xbytes > 0) {
    cnt = recv( sd[src], buf, xbytes, 0);
    xbytes -= cnt;
    buf    += cnt;
  }

  ulen = len;
  err = uncompress(arr, &ulen, carr, clen);
  ZCHECK_ERR(err, "uncompress");

  if (ulen != (unsigned long)len)
    fprintf(stderr,"ulen != len\n");

  free(carr);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_ZRecv(int src, void *arr, int len) {
  unsigned long clen, ulen;
  char *carr;
  int err;

  UMD_Recv(src, &clen, sizeof(unsigned long));
  carr = (char *)malloc(clen*sizeof(char));
  assert_malloc(carr);

  UMD_Recv(src, carr, clen);

  ulen = len;
  err = uncompress(arr, &ulen, carr, clen);
  ZCHECK_ERR(err, "uncompress");

  if (ulen != (unsigned long)len)
    fprintf(stderr,"ulen != len\n");

  free(carr);
  return 0;
}
#endif /* _MP_MPI */

#endif /* _ZCOMM */

#if _NB_COMM
typedef struct {
  int node;
  void *arr;
  int len;
  msg_tag tag;
} msg_info_t;

typedef struct {
  int send;        /* 1 = send, 0 = recv */
  int idx;         /* index into *stats  */
} tag_info_t;

#define MIN_TAG 0
#define MAX_TAG MAXNODES

#define COMM_SLOTS (MAX_TAG << 1)

int ISend_tagcnt = MIN_TAG;     
int IRecv_tagcnt = MIN_TAG; 

int IComm_stats[COMM_SLOTS]; /* first half are Send, second half are Recv */
msg_info_t msg_info[COMM_SLOTS];

pthread_t ISend_thread[MAX_TAG];
pthread_t IRecv_thread[MAX_TAG];

pthread_mutex_t ISend_use_lock[MAXNODES];
pthread_mutex_t IRecv_use_lock[MAXNODES];
pthread_mutex_t ISend_cnt_lock;
pthread_mutex_t IRecv_cnt_lock;

void *UMD_ISend_thread(void  *m_arg) {
  msg_info_t *msg_info = (msg_info_t *)m_arg;
  UMD_Send(msg_info->node, msg_info->arr, msg_info->len);
  pthread_mutex_unlock(&ISend_use_lock[msg_info->node]);
  IComm_stats[msg_info->tag]  = DONE;
  return ((void *)NULL);
}

int UMD_ISend(int dst, void *arr, int len, msg_tag *tag) {
  msg_info_t *minfo;
  int idx;
  int rc;

  pthread_mutex_lock(&ISend_cnt_lock);
  idx = ISend_tagcnt++;
  if (idx >= MAX_TAG)
    ISend_tagcnt = (idx = MIN_TAG) + 1;
  *tag = idx;

  pthread_mutex_lock(&ISend_use_lock[dst]);
  
  pthread_mutex_unlock(&ISend_cnt_lock);

  IComm_stats[*tag] = !DONE;

  minfo       = &msg_info[*tag];
  minfo->node = dst;
  minfo->arr  = arr;
  minfo->len  = len;
  minfo->tag  = *tag;
  
  rc = pthread_create(&ISend_thread[idx], NULL, UMD_ISend_thread, minfo);
  return rc;
}

void *UMD_IRecv_thread(void  *m_arg) {
  msg_info_t *msg_info = (msg_info_t *)m_arg;
  UMD_Recv(msg_info->node, msg_info->arr, msg_info->len);
  pthread_mutex_unlock(&IRecv_use_lock[msg_info->node]);
  IComm_stats[msg_info->tag] = DONE;
  return ((void *)NULL);
}

int UMD_IRecv(int src, void *arr, int len, msg_tag *tag) {
  msg_info_t *minfo;
  int idx;
  int rc;

  pthread_mutex_lock(&IRecv_cnt_lock);
  idx = IRecv_tagcnt++;
  if (idx >= MAX_TAG) 
    IRecv_tagcnt = (idx = MIN_TAG) + 1;
  *tag = idx + MAX_TAG;

  pthread_mutex_lock(&IRecv_use_lock[src]);
  
  pthread_mutex_unlock(&IRecv_cnt_lock);

  IComm_stats[*tag] = !DONE;

  minfo       = &msg_info[*tag];
  minfo->node = src;
  minfo->arr  = arr;
  minfo->len  = len;
  minfo->tag  = *tag;
  
  rc = pthread_create(&IRecv_thread[idx], NULL, UMD_IRecv_thread, minfo);
  return rc;
}

int UMD_Test(msg_tag tag) {
  return IComm_stats[tag];
}

int UMD_Wait(msg_tag tag) {
  int parg;
  if (tag >= MAX_TAG)
    pthread_join(IRecv_thread[tag-MAX_TAG], (void *)&parg);
  else
    pthread_join(ISend_thread[tag], (void *)&parg);

  return 0;
}
#endif /* _NB_COMM */

int UMD_Sendrecv(int dst, void *arr_out, int len_t,
		  int src, void *arr_in, int len_r) {
  if (src==dst)
    if (MYNODE < src) {
      UMD_Send(src,arr_out,len_t);
      UMD_Recv(src,arr_in,len_r);
    }
    else {
      UMD_Recv(src,arr_in,len_r);
      UMD_Send(src,arr_out,len_t);
    }
  else
    if (dst < src) {
      UMD_Send(dst,arr_out,len_t);
      UMD_Recv(src,arr_in,len_r);
    }
    else {
      UMD_Recv(src,arr_in,len_r);
      UMD_Send(dst,arr_out,len_t);
    }
  return 0;
}

#if defined(_MP_UMD)
int UMD_Sendrecv_nb(int dst, void *arr_out, int len_t,
		     int src, void *arr_in, int len_r) {
  char *buf_out, *buf_in;
  register int
    blk,
    done_send = 0,
    done_recv = 0,
    cnt,
    bytes_to_send,
    bytes_to_recv;


#if DEBUG_ERRNO
  errno=0;
#endif /* DEBUG_ERRNO */
  
  blk = DEFAULT_BLKSIZE;

  buf_out       = (char *)arr_out;
  bytes_to_send = len_t;
  buf_in        = (char *)arr_in;
  bytes_to_recv = len_r;

  while (!(done_send&&done_recv)) {
    if (!done_send) {
      cnt = write( sd[dst], buf_out, min(bytes_to_send,blk));
#if DEBUG_ERRNO
      if(errno) err("IO",1);
#endif /* DEBUG_ERRNO */
      bytes_to_send -= cnt;
      buf_out       += cnt;
      if (bytes_to_send <= 0)
	done_send = 1;
    }

    if (!done_recv) {
      cnt            = recv( sd[src], buf_in, min(bytes_to_recv,blk), 0);
#if DEBUG_ERRNO
      if(errno) err("IO",1);
#endif /* DEBUG_ERRNO */
      bytes_to_recv -= cnt;
      buf_in        += cnt;
      if (bytes_to_recv <= 0)
	done_recv = 1;
    }
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Sendrecv_nb(int dst, void *arr_out, int len_t,
		     int src, void *arr_in, int len_r) {
  _MPI_WRAP(MPI_Sendrecv(arr_out, len_t, MPI_BYTE, dst, _MPI_TAG,
			 arr_in,  len_r, MPI_BYTE, src, _MPI_TAG,
			 MPI_COMM_WORLD, &mstat));
  return 0;
}
#endif /* _MP_MPI */

int UMD_Barrier1()
{
  int  N2_prev, surfeit;
  int  d, dst, src;
  char buf1, buf2;

#if TIME_BARRIER
  double secs = get_seconds();
#endif /* TIME_BARRIER */

  if ( NODES > 1 ) {

    N2_prev = (int)pow(2.0,floor(log2((double)(NODES-1))));
    surfeit = NODES - N2_prev;
    
    if (MYNODE < N2_prev) {
      if (MYNODE < surfeit) {
        /* get the fanin letter from the upper "half" process: */
        dst = N2_prev + MYNODE;
	UMD_Recv(dst,&buf2,1);
      }

      /* combine on embedded N2_prev power-of-two processes */
      for (d = 1; d < N2_prev; d <<= 1) {
        dst = (MYNODE ^ d);
	UMD_Sendrecv(dst,&buf1,1, dst,&buf2,1);
      }

      /* fanout data to nodes above N2_prev... */
      if ( MYNODE < surfeit ) {
        dst = N2_prev + MYNODE;
	UMD_Send(dst,&buf1,1);
      }
    } 
    else {
      /* fanin data to power of 2 subset */
      src = MYNODE - N2_prev;
      UMD_Send(src,&buf1,1);
      UMD_Recv(src,&buf2,1);
    }
  } 
#if TIME_BARRIER
  secs = get_seconds() - secs;
  fprintf(outfile,"PE%3d: Barrier: %9.6f\n",MYNODE,secs);
  fflush(outfile);
#endif /* TIME_BARRIER */
  return 0;
}


int UMD_Barrier2() {
  int i, d;
  char buf1, buf2;

  if (NODES > 1) {
    for (i=1 ; i<NODES ; i++) {
      d = MYNODE ^ i;
      UMD_Sendrecv(d,&buf1,1,
		    d,&buf2,1);
    }
  }
  return 0;
}

int UMD_Barrier3() {
  int i;
  char buf1;
  if (NODES > 1) {
    if (MYNODE>0) {
      UMD_Send(0,&buf1,1);
      UMD_Recv(0,&buf1,1);
    }
    else {
      for (i=1 ; i<NODES ; i++)
	UMD_Recv(i,&buf1,1);
      for (i=1 ; i<NODES ; i++)
	UMD_Send(i,&buf1,1);
    }
  }
  return 0;
}

#if defined(_MP_MPI)
int UMD_Barrier() {
  _MPI_WRAP(MPI_Barrier(MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if 0
void
UMD_get_args(int argc, char **argv) {
  char
    *outfilename = NULL,
    *s,**argvv = argv;

  machfilename = NULL;
  
  while (--argc > 0 && (*++argvv)[0] == '-')
    for (s=argvv[0]+1; *s != '\0'; s++) 
      switch (*s) {
      case 'o':
	if (argc <= 1) 
	  perror("output filename expected after -o (e.g. -o filename)");
	argc--;
	outfilename = (char *)malloc(MAXLEN*sizeof(char));
	strcpy(outfilename, *++argvv); 
#if 0
	printf("UMD_get_args: outfile: %s\n",outfilename);
#endif /* 0 */
	break;
      case 'm':
	if (argc <= 1) 
	  perror("machinefile filename expected after -m (e.g. -m filename)");
	argc--;
	machfilename = (char *)malloc(MAXLEN*sizeof(char));
	assert_malloc(machfilename);
	strcpy(machfilename, *++argvv);
#if 0
	printf("UMD_get_args: machinefile: %s\n",machfilename);
#endif /* 0 */
	break;
      case 'h':
	fprintf(stdout,"UMD Options:\n");
	fprintf(stdout," -m machinefilename\n");
	fprintf(stdout," -o outfilename\n");
	fprintf(stdout,"\n\n");
	break;
/*    default: perror("illegal option"); */
      }

  if (outfilename == NULL)
    outfile = stdout;
  else
    outfile = fopen(outfilename,"a+");
  
  if (machfilename == NULL)
    machfilename = DEFAULT_MACHFILENAME;
}
#endif


void
UMD_get_args(int argc, char **argv) {
  char
    *s,**argvv = argv;

  machfilename = NULL;

  main_get_args(argc, argv);

  while (--argc > 0 && (*++argvv)[0] == '-')
    for (s=argvv[0]+1; *s != '\0'; s++) 
      switch (*s) {
      case 'm':
	if (argc <= 1) 
	  perror("machinefile filename expected after -m (e.g. -m filename)");
	argc--;
	machfilename = (char *)malloc(MAXLEN*sizeof(char));
	assert_malloc(machfilename);
	strcpy(machfilename, *++argvv);
#if 0
	printf("UMD_get_args: machinefile: %s\n",machfilename);
#endif /* 0 */
	break;
      case 'h':
	fprintf(stdout,"UMD Options:\n");
	fprintf(stdout," -m machinefilename\n");
	fprintf(stdout,"\n\n");
	break;
/*    default: perror("illegal option"); */
      }

  if (machfilename == NULL)
    machfilename = DEFAULT_MACHFILENAME;
}


int UMD_Init(int* argc, char* **argv) {

#if defined(_MP_UMD)
  int i, src, dst;
  int rc;
  int user_offset_seed;
  short user_offset;
  short port_num;
  FILE *machfd;
  skt *sin_srv;
  skt *sin_clt;
#endif /* _MP_UMD */
  
  /****************************/
  /* Start MPI Environment    */
  /****************************/

#if DEBUG_MPI
  fprintf(stderr,"UMD_Init() Starting MPI... \n");
#endif /* DEBUG_MPI */


  MPI_Init(argc, argv);
  MPI_Comm_size(MPI_COMM_WORLD, &NODES);
  MPI_Comm_rank(MPI_COMM_WORLD, &MYNODE);

#if DEBUG_MPI
  MPI_Barrier(MPI_COMM_WORLD);
  fprintf(stderr,"PE%2d(%d): UMD_Init() MPI Started \n",MYNODE,NODES);
  MPI_Barrier(MPI_COMM_WORLD);
#endif /* DEBUG_MPI */

  UMD_get_args(*argc, *argv);

#if defined(_MP_MPI)
#if defined(PTHREAD_USE_D4)
  pthread_mutex_init(&_mpi_lock_simple, pthread_mutexattr_default);
#else 
  pthread_mutex_init(&_mpi_lock_simple, NULL);
#endif /* PTHREAD_USE_D4 */
#endif /* _MP_MPI */

#if defined(_MP_UMD)
  user_offset_seed = (int)getuid();
  srandom(user_offset_seed);
  user_offset = (short)((int)random() % 2000);
  
#if DEBUG_MPI
  MPI_Barrier(MPI_COMM_WORLD);
  fprintf(stderr,"PE%2d(%d): UMD_Init() user_offset_seed: %d user_offset: %d\n",MYNODE,NODES,user_offset_seed, user_offset);
  MPI_Barrier(MPI_COMM_WORLD);
#endif /* DEBUG_MPI */

  /****************************/
  /* Allocate communication   */
  /****************************/

  sins = (skt *)malloc(NODES * sizeof(skt));
  assert_malloc(sins);

  sd = (int *)malloc(NODES * sizeof(int));
  assert_malloc(sd);

  host = (char **)malloc(NODES*sizeof(char *));
  assert_malloc(host);
    
  /****************************/
  /* Get resource file        */
  /****************************/

  machfd = fopen(machfilename,"r");
  if (machfd == (FILE *)NULL)
    perror("ERROR: trying to open machine file");
  for (i=0 ; i<NODES ; i++) {
    host[i] = (char *)malloc(MAXLEN * sizeof(char));
    assert_malloc(host[i]);
    fscanf(machfd,"%s",host[i]);
  }
  fclose(machfd);


  /****************************/
  /* BUILD SERVER SOCKETS     */
  /****************************/

  for (src=0 ; src<MYNODE ; src++)
  {
    sin_srv = sins + src;

    bzero((char *)sin_srv, sizeof(skt));
    sin_srv->sin_family      = AF_INET;
    /* rcvr */
    port_num = DEFAULT_TCP_BASE + user_offset + src + (MYNODE*NODES);
    sin_srv->sin_port =  htons(port_num);

#if DEBUG_CONNECT    
    fprintf(outfile,"PE%3d: ttcp-r: port=%d %s\n",MYNODE, port_num, "tcp");
    fflush(outfile);
#endif /* DEBUG_CONNECT */
    
    if ((sd[src] = socket(AF_INET, SOCK_STREAM, 0)) < 0)
      err("socket",0);
    mes("socket",0);

#if 0
    if (bind(sd[src], sin_srv, sizeof(skt)) < 0)
      err("bind",0);
#else /* 1 */
    rc = bind(sd[src], (struct sockaddr *)sin_srv, sizeof(skt));
    while (rc < 0) {
      close(sd[src]);
      sleep(1);
      if ((sd[src] = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	err("socket",0);
      rc = bind(sd[src], (struct sockaddr *)sin_srv, sizeof(skt));
    }
#endif /* 0 */

    listen(sd[src], DEFAULT_LISTEN);   /* allow a queue of 5 */

    if(options)  {
      if( setsockopt(sd[src], SOL_SOCKET, options,
		     (void *)&one, sizeof(one)) < 0)
	err("setsockopt",0);
    }

#if DEBUG_CONNECT
    fprintf(outfile,"PE%3d: Built sd[%d] with port=%5d\n",
	    MYNODE,src,port_num);
    fflush(outfile);
#endif /* DEBUG_CONNECT */
  }

  /*********************************/
  /* Wait for all servers to build */
  /*********************************/
  {
    struct timeval tv;
  
    tv.tv_sec = 1;
    tv.tv_usec = 0;
#if (_MACH_SELECT == _MACH_SELECT_FD)
    select(1, (fd_set *)0, (fd_set *)0, (fd_set *)0, &tv );
#elif (_MACH_SELECT == _MACH_SELECT_CHAR)
    select(1, (char *)0, (char *)0, (char *)0, &tv );
#endif /* _MACH_SELECT */
  }
  
  /******************************/
  /* BUILD CLIENT SOCKETS       */
  /******************************/

  for (dst=MYNODE+1 ; dst<NODES ; dst++)
  {
    unsigned long addr_tmp;
    struct hostent *addr;

    /*  nick code  */
    int sendwin = DEFAULT_SENDWIN;
    int rcvwin  = DEFAULT_RECVWIN;
    int optlen  = sizeof(sendwin);
    /* end of nick code  */

    sin_clt = sins + dst;

    /* xmitr */
    bzero((char *)sin_clt, sizeof(skt));

    if (atoi(host[dst]) > 0 )  {
      /* Numeric */
      sin_clt->sin_family = AF_INET;
      sin_clt->sin_addr.s_addr = inet_addr(host[dst]);
    } else {
      if ((addr=gethostbyname(host[dst])) == NULL)
	err("bad hostname",1);
      sin_clt->sin_family = addr->h_addrtype;
      bcopy(addr->h_addr, &addr_tmp, addr->h_length);
      sin_clt->sin_addr.s_addr = addr_tmp;
    }

    port_num = DEFAULT_TCP_BASE + user_offset + MYNODE + (dst*NODES);
    sin_clt->sin_port = htons(port_num);
    
#if DEBUG_CONNECT
    fprintf(outfile,"PE%3d: ttcp-t: port=%d %s  -> %s\n",
	    MYNODE, port_num, "tcp", host[dst]); 
    fflush(outfile);
#endif /* DEBUG_CONNECT */
  
    if ((sd[dst] = socket(AF_INET, SOCK_STREAM, 0)) < 0)
      err("socket",1);
    mes("socket",1);

    if(window){
      if( setsockopt(sd[dst], SOL_SOCKET, SO_SNDBUF,
		     (void *)&sendwin, sizeof(sendwin)) < 0)
	err("setsockopt",1);
      if( setsockopt(sd[dst], SOL_SOCKET, SO_RCVBUF,
		     (void *)&rcvwin, sizeof(rcvwin)) < 0)
	err("setsockopt",1);
    }
    if (nodelay) {
      struct protoent *p;
      p = getprotobyname("tcp");
      if( p && setsockopt(sd[dst], p->p_proto, TCP_NODELAY,
			  (void *)&one, sizeof(one)) < 0)
	err("setsockopt: nodelay",1);
      mes("nodelay",1);
    }

#if 0
    if (connect(sd[dst], sin_clt, sizeof(skt) ) < 0)
      err("connect",1);
    mes("connect",1);
#else /* 1 */
    rc = connect(sd[dst], (struct sockaddr *)sin_clt, sizeof(skt));
    while (rc < 0) {
      close(sd[dst]);
      sleep(5);
      if ((sd[dst] = socket(AF_INET, SOCK_STREAM, 0)) < 0)
	err("socket2",1);
      mes("socket2",1);
      
      if(window){
	if( setsockopt(sd[dst], SOL_SOCKET, SO_SNDBUF,
		       (void *)&sendwin, sizeof(sendwin)) < 0)
	  err("setsockopt2",1);
	if( setsockopt(sd[dst], SOL_SOCKET, SO_RCVBUF,
		       (void *)&rcvwin, sizeof(rcvwin)) < 0)
	  err("setsockopt2",1);
      }
      if (nodelay) {
	struct protoent *p;
	p = getprotobyname("tcp");
	if( p && setsockopt(sd[dst], p->p_proto, TCP_NODELAY,
			    (void *)&one, sizeof(one)) < 0)
	  err("setsockopt2: nodelay",1);
	mes("nodelay2",1);
      }

      rc = connect(sd[dst], (struct sockaddr *)sin_clt, sizeof(skt));
    }
    mes("connect",1);
#endif /* 0 */

    if (getsockopt(sd[dst], SOL_SOCKET, SO_SNDBUF, (void *)&sendwin, (size_t *) &optlen) < 0) {
      fprintf(outfile,"PE%3d: get send window size didn't work\n",MYNODE);
      fflush(outfile);
    } else {
#if DEBUG_WINDOW
      fprintf(outfile,"PE%3d: send window size = %d\n",MYNODE, sendwin);
      fflush(outfile);
#endif /* DEBUG_WINDOW */
    }
    if (getsockopt(sd[dst], SOL_SOCKET, SO_RCVBUF, (void *)&rcvwin, (size_t *) &optlen) < 0) {
      fprintf(outfile,"PE%3d: Get recv. window size didn't work\n",MYNODE);
      fflush(outfile);
    } else {
#if DEBUG_WINDOW
      fprintf(outfile,"PE%3d: receive window size = %d\n",MYNODE, rcvwin);
      fflush(outfile);
#endif /* DEBUG_WINDOW */
    }
  }

  /********************************/
  /* SERVERS ACCEPT CONNECTIONS   */
  /********************************/

  for (src=0 ; src<MYNODE ; src++) {
    skt frominet;
    int fromlen = sizeof(skt);

    /* nick code */
    int sendwin = DEFAULT_SENDWIN;
    int rcvwin  = DEFAULT_RECVWIN;
    int optlen  = sizeof(sendwin);
    /* end of nick code  */

    if ((sd[src]=accept(sd[src], (struct sockaddr *)&frominet, (size_t *) &fromlen) ) < 0)
      err("accept",0);
    {
      skt peer;
      int peerlen = sizeof(peer);
      if (getpeername(sd[src], (struct sockaddr *)&peer, (size_t *) &peerlen) < 0) {
	err("getpeername",0);
      }
      if(window){
	if( setsockopt(sd[src], SOL_SOCKET, SO_SNDBUF,
		       (void *)&sendwin, sizeof(sendwin)) < 0)
	  err("setsockopt",0);
	if( setsockopt(sd[src], SOL_SOCKET, SO_RCVBUF,
		       (void *)&rcvwin, sizeof(rcvwin)) < 0)
	  err("setsockopt",0);
      }
#if DEBUG_CONNECT
      fprintf(stderr,"ttcp-r: accept from %s\n", inet_ntoa(peer.sin_addr));
#endif /* DEBUG_CONNECT */
    }
    
    if (getsockopt(sd[src], SOL_SOCKET, SO_SNDBUF, (void *)&sendwin, (size_t *) &optlen) < 0) {
      fprintf(outfile,"PE%3d: get send window size didn't work\n",MYNODE);
      fflush(outfile);
    } else {
#if DEBUG_WINDOW
      fprintf(outfile,"PE%3d: send window size = %d\n",MYNODE, sendwin);
      fflush(outfile);
#endif /* DEBUG_WINDOW */
    }
    if (getsockopt(sd[src], SOL_SOCKET, SO_RCVBUF, (void *)&rcvwin, (size_t *) &optlen) < 0) {
      fprintf(outfile,"PE%3d: Get recv. window size didn't work\n",MYNODE);
      fflush(outfile);
    } else {
#if DEBUG_WINDOW
      fprintf(outfile,"PE%3d: receive window size = %d\n",MYNODE, rcvwin);
      fflush(outfile);
#endif /* DEBUG_WINDOW */
    }
  }
#if _NB_COMM
#if defined(PTHREAD_USE_D4)
  pthread_mutex_init(&ISend_cnt_lock, pthread_mutexattr_default);
  pthread_mutex_init(&IRecv_cnt_lock, pthread_mutexattr_default);
#else 
  pthread_mutex_init(&ISend_cnt_lock, NULL);
  pthread_mutex_init(&IRecv_cnt_lock, NULL);
#endif /* PTHREAD_USE_D4 */
  for (i=0 ; i<NODES ; i++) {
#if _NB_COMM_LOCK
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&write_lock[i], pthread_mutexattr_default);
    pthread_mutex_init(& recv_lock[i], pthread_mutexattr_default);
#else
    pthread_mutex_init(&write_lock[i], NULL);
    pthread_mutex_init(& recv_lock[i], NULL);
#endif /* PTHREAD_USE_D4 */
#endif /* _NB_COMM_LOCK */
#if defined(PTHREAD_USE_D4)
    pthread_mutex_init(&ISend_use_lock[i], pthread_mutexattr_default);
    pthread_mutex_init(&IRecv_use_lock[i], pthread_mutexattr_default);
#else
    pthread_mutex_init(&ISend_use_lock[i], NULL);
    pthread_mutex_init(&IRecv_use_lock[i], NULL);
#endif /* PTHREAD_USE_D4 */
  }
#endif /* _NB_COMM */

#endif /* _MP_UMD */

  return 0;
}

int
UMD_Finalize() {
  int i;

#if defined(_MP_UMD)
  for (i=0 ; i<NODES ; i++)
    if (i != MYNODE)
      close(sd[i]);

  for (i=0 ; i<NODES ; i++)
    free(host[i]);
  free(host);
  free(sd);
  free(sins);
#endif /* _MP_UMD */

#if defined(_MP_MPI)
  pthread_mutex_destroy(&_mpi_lock_simple);
#endif /* _MP_MPI */
  
  fclose(outfile);
  MPI_Finalize();
  return 0;
}


#if defined(_MP_MPI)
MPI_Op UMD_MPI_OP(reduce_t op) {
  MPI_Op mop;
  switch (op) {
  case MIN:  mop = MPI_MIN;  break;
  case MAX:  mop = MPI_MAX;  break;
  case SUM:  mop = MPI_SUM;  break;
  case PROD: mop = MPI_PROD; break;
  case LAND: mop = MPI_LAND; break;
  case BAND: mop = MPI_BAND; break;
  case LOR:  mop = MPI_LOR;  break;
  case BOR:  mop = MPI_BOR;  break;
  case LXOR: mop = MPI_LXOR; break;
  case BXOR: mop = MPI_BXOR; break;
  }
  return (mop);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int UMD_Reduce_i(int    myval, reduce_t op) {
  int parent, child1, child2;
  int
    r,
    tmp;
  int s;

  s      = sizeof(int);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  r = myval;

  if (child1 < NODES) {
    UMD_Recv(child1, &tmp, s);
    switch (op) {
    case MIN:  r  = min(r,tmp);                break;
    case MAX:  r  = max(r,tmp);                break;
    case SUM:  r += tmp;                       break;
    case PROD: r *= tmp;                       break;
    case LAND: r  = r && tmp;                  break;
    case BAND: r &= tmp;                       break;
    case LOR:  r  = r || tmp;                  break;
    case BOR:  r |= tmp;                       break;
    case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
    case BXOR: r ^= tmp;                       break;
    default: perror("Bad reduction operator");
    }
    if (child2 < NODES) {
      UMD_Recv(child2, &tmp, s);
      switch (op) {
      case MIN:  r  = min(r,tmp);                break;
      case MAX:  r  = max(r,tmp);                break;
      case SUM:  r += tmp;                       break;
      case PROD: r *= tmp;                       break;
      case LAND: r  = r && tmp;                  break;
      case BAND: r &= tmp;                       break;
      case LOR:  r  = r || tmp;                  break;
      case BOR:  r |= tmp;                       break;
      case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
      case BXOR: r ^= tmp;                       break;
      default: perror("Bad reduction operator");
      }
    }
  }
  if (MYNODE > 0) 
    UMD_Send(parent, &r, s);

  return (r);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Reduce_i(int    myval, reduce_t op) {
  int val;
  _MPI_WRAP(MPI_Reduce(&myval, &val, 1, MPI_INT, UMD_MPI_OP(op), 0, MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
long UMD_Reduce_l(long    myval, reduce_t op) {
  int parent, child1, child2;
  long
    r,
    tmp;
  int s;

  s      = sizeof(long);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  r = myval;

  if (child1 < NODES) {
    UMD_Recv(child1, &tmp, s);
    switch (op) {
    case MIN:  r  = min(r,tmp);                break;
    case MAX:  r  = max(r,tmp);                break;
    case SUM:  r += tmp;                       break;
    case PROD: r *= tmp;                       break;
    case LAND: r  = r && tmp;                  break;
    case BAND: r &= tmp;                       break;
    case LOR:  r  = r || tmp;                  break;
    case BOR:  r |= tmp;                       break;
    case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
    case BXOR: r ^= tmp;                       break;
    default: perror("Bad reduction operator");
    }
    if (child2 < NODES) {
      UMD_Recv(child2, &tmp, s);
      switch (op) {
      case MIN:  r  = min(r,tmp);                break;
      case MAX:  r  = max(r,tmp);                break;
      case SUM:  r += tmp;                       break;
      case PROD: r *= tmp;                       break;
      case LAND: r  = r && tmp;                  break;
      case BAND: r &= tmp;                       break;
      case LOR:  r  = r || tmp;                  break;
      case BOR:  r |= tmp;                       break;
      case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
      case BXOR: r ^= tmp;                       break;
      default: perror("Bad reduction operator");
      }
    }
  }
  if (MYNODE > 0) 
    UMD_Send(parent, &r, s);

  return (r);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
long UMD_Reduce_l(long   myval, reduce_t op) {
  long val;

  _MPI_WRAP(MPI_Reduce(&myval, &val, 1, MPI_LONG, UMD_MPI_OP(op), 0, MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
double UMD_Reduce_d(double myval, reduce_t op) {
  int parent, child1, child2;
  double
    r,
    tmp;
  int s;

  s      = sizeof(double);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  r = myval;

  if (child1 < NODES) {
    UMD_Recv(child1, &tmp, s);
    switch (op) {
    case MIN:  r  = min(r,tmp);                break;
    case MAX:  r  = max(r,tmp);                break;
    case SUM:  r += tmp;                       break;
    case PROD: r *= tmp;                       break;
    case LAND: r  = r && tmp;                  break;
    case BAND: perror("Reduce_d BAND");        break;
    case LOR:  r  = r || tmp;                  break;
    case BOR:  perror("Reduce_d BOR");         break;
    case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
    case BXOR: perror("Reduce_d BXOR");        break;
    default: perror("Bad reduction operator");
    }
    if (child2 < NODES) {
      UMD_Recv(child2, &tmp, s);
      switch (op) {
      case MIN:  r  = min(r,tmp);                break;
      case MAX:  r  = max(r,tmp);                break;
      case SUM:  r += tmp;                       break;
      case PROD: r *= tmp;                       break;
      case LAND: r  = r && tmp;                  break;
      case BAND: perror("Reduce_d BAND");        break;
      case LOR:  r  = r || tmp;                  break;
      case BOR:  perror("Reduce_d BOR");         break;
      case LXOR: r  = (r && !tmp)||(!r && tmp);  break;
      case BXOR: perror("Reduce_d BXOR");        break;
      default: perror("Bad reduction operator");
      }
    }
  }
  if (MYNODE > 0) 
    UMD_Send(parent, &r, s);

  return (r);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
double UMD_Reduce_d(double myval, reduce_t op) {
  double val;
  _MPI_WRAP(MPI_Reduce(&myval, &val, 1, MPI_DOUBLE, UMD_MPI_OP(op), 0, MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
char   UMD_Bcast_c(char myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(char);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
char   UMD_Bcast_c(char myval) {
  _MPI_WRAP(MPI_Bcast(&myval, 1, MPI_CHAR, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Bcast_i(int myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(int);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Bcast_i(int myval) {
  _MPI_WRAP(MPI_Bcast(&myval, 1, MPI_INT, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
long    UMD_Bcast_l(long myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(long);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
long   UMD_Bcast_l(long myval) {
  _MPI_WRAP(MPI_Bcast(&myval, 1, MPI_LONG, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
double UMD_Bcast_d(double myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(double);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
double UMD_Bcast_d(double myval) {
  _MPI_WRAP(MPI_Bcast(&myval, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
char   *UMD_Bcast_cp(char *myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(char *);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
char   *UMD_Bcast_cp(char *myval) {
  _MPI_WRAP(MPI_Bcast(&myval, sizeof(char *), MPI_BYTE, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    *UMD_Bcast_ip(int *myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(int *);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    *UMD_Bcast_ip(int  *myval) {
  _MPI_WRAP(MPI_Bcast(&myval, sizeof(int *), MPI_BYTE, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
long    *UMD_Bcast_lp(long *myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(long *);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
long   *UMD_Bcast_lp(long *myval) {
  _MPI_WRAP(MPI_Bcast(&myval, sizeof(long *), MPI_BYTE, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
double *UMD_Bcast_dp(double *myval) {
  int parent, child1, child2;
  int s;

  s      = sizeof(double *);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, &myval, s);
    if (child2 < NODES)
      UMD_Send(child2, &myval, s);
  }
  else {
    UMD_Recv(parent, &myval, s);
    if (child1 < NODES) {
      UMD_Send(child1, &myval, s);
      if (child2 < NODES)
	UMD_Send(child2, &myval, s);
    }
  }
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
double *UMD_Bcast_dp(double *myval) {
  _MPI_WRAP(MPI_Bcast(&myval, sizeof(double *), MPI_BYTE, 0, MPI_COMM_WORLD));
  return (myval);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Bcast_buf_c(char *sendbuf, int cnt, char *recvbuf) {
  int parent, child1, child2;
  int s;

  s      = cnt*sizeof(char);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, sendbuf, s);
    if (child2 < NODES)
      UMD_Send(child2, sendbuf, s);
    if (sendbuf != recvbuf)
      memcpy(recvbuf, sendbuf, s);
  }
  else {
    UMD_Recv(parent, recvbuf, s);
    if (child1 < NODES) {
      UMD_Send(child1, recvbuf, s);
      if (child2 < NODES)
	UMD_Send(child2, recvbuf, s);
    }
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Bcast_buf_c(char *sendbuf, int cnt, char *recvbuf) {
  _MPI_WRAP(MPI_Bcast(sendbuf, cnt, MPI_CHAR, 0, MPI_COMM_WORLD));
  memcpy(recvbuf, sendbuf, cnt*sizeof(char));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Bcast_buf_i(int *sendbuf, int cnt, int *recvbuf) {
  int parent, child1, child2;
  int s;

  s      = cnt*sizeof(int);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, sendbuf, s);
    if (child2 < NODES)
      UMD_Send(child2, sendbuf, s);
    if (sendbuf != recvbuf)
      memcpy(recvbuf, sendbuf, s);
  }
  else {
    UMD_Recv(parent, recvbuf, s);
    if (child1 < NODES) {
      UMD_Send(child1, recvbuf, s);
      if (child2 < NODES)
	UMD_Send(child2, recvbuf, s);
    }
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Bcast_buf_i(int *sendbuf, int cnt, int *recvbuf) {
  _MPI_WRAP(MPI_Bcast(sendbuf, cnt, MPI_INT, 0, MPI_COMM_WORLD));
  memcpy(recvbuf, sendbuf, cnt*sizeof(int));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Bcast_buf_l(long *sendbuf, int cnt, long *recvbuf) {
  int parent, child1, child2;
  int s;

  s      = cnt*sizeof(long);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, sendbuf, s);
    if (child2 < NODES)
      UMD_Send(child2, sendbuf, s);
    if (sendbuf != recvbuf)
      memcpy(recvbuf, sendbuf, s);
  }
  else {
    UMD_Recv(parent, recvbuf, s);
    if (child1 < NODES) {
      UMD_Send(child1, recvbuf, s);
      if (child2 < NODES)
	UMD_Send(child2, recvbuf, s);
    }
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Bcast_buf_l(long *sendbuf, int cnt, long *recvbuf) {
  _MPI_WRAP(MPI_Bcast(sendbuf, cnt, MPI_LONG, 0, MPI_COMM_WORLD));
  memcpy(recvbuf, sendbuf, cnt*sizeof(long));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Bcast_buf_d(double *sendbuf, int cnt, double *recvbuf) {
  int parent, child1, child2;
  int s;

  s      = cnt*sizeof(double);
  parent = (MYNODE-1)/2;
  child1 = (MYNODE<<1)+1;
  child2 = child1+1;

  if (MYNODE == 0) {
    if (child1 < NODES)
      UMD_Send(child1, sendbuf, s);
    if (child2 < NODES)
      UMD_Send(child2, sendbuf, s);
    if (sendbuf != recvbuf)
      memcpy(recvbuf, sendbuf, s);
  }
  else {
    UMD_Recv(parent, recvbuf, s);
    if (child1 < NODES) {
      UMD_Send(child1, recvbuf, s);
      if (child2 < NODES)
	UMD_Send(child2, recvbuf, s);
    }
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Bcast_buf_d(double *sendbuf, int cnt, double *recvbuf) {
  _MPI_WRAP(MPI_Bcast(sendbuf, cnt, MPI_DOUBLE, 0, MPI_COMM_WORLD));
  memcpy(recvbuf, sendbuf, cnt*sizeof(double));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Allreduce_i(int    myval, reduce_t op) {
  myval = UMD_Reduce_i(myval, op);
  myval = UMD_Bcast_i(myval);
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Allreduce_i(int    myval, reduce_t op) {
  int val;
  _MPI_WRAP(MPI_Allreduce(&myval, &val, 1, MPI_INT, UMD_MPI_OP(op), MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
long   UMD_Allreduce_l(long   myval, reduce_t op) {
  myval = UMD_Reduce_l(myval, op);
  myval = UMD_Bcast_l(myval);
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
long   UMD_Allreduce_l(long   myval, reduce_t op) {
  long val;
  _MPI_WRAP(MPI_Allreduce(&myval, &val, 1, MPI_LONG, UMD_MPI_OP(op), MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
double UMD_Allreduce_d(double myval, reduce_t op) {
  myval = UMD_Reduce_d(myval, op);
  myval = UMD_Bcast_d(myval);
  return (myval);
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
double UMD_Allreduce_d(double myval, reduce_t op) {
  double val;
  _MPI_WRAP(MPI_Allreduce(&myval, &val, 1, MPI_DOUBLE, UMD_MPI_OP(op), MPI_COMM_WORLD));
  return(val);
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Alltoall_i(int *sendbuf, int block_size, int *recvbuf) {
  register int i, j, k, s;
  
  s = block_size * sizeof(int);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    k = j*block_size;
    UMD_Sendrecv_nb(j, sendbuf+k, s,
		    j, recvbuf+k, s);
  }
  k = MYNODE * block_size;
  memcpy(recvbuf+k, sendbuf+k, s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Alltoall_i(int *sendbuf, int block_size, int *recvbuf) {
  _MPI_WRAP(MPI_Alltoall(sendbuf, block_size, MPI_INT,
			 recvbuf, block_size, MPI_INT,
			 MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Alltoall_l(long *sendbuf, int block_size, long *recvbuf) {
  register int i, j, k, s;
  
  s = block_size * sizeof(long);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    k = j*block_size;
    UMD_Sendrecv_nb(j, sendbuf+k, s,
		    j, recvbuf+k, s);
  }
  k = MYNODE * block_size;
  memcpy(recvbuf+k, sendbuf+k, s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Alltoall_l(long *sendbuf, int block_size, long *recvbuf) {
  _MPI_WRAP(MPI_Alltoall(sendbuf, block_size, MPI_LONG,
			 recvbuf, block_size, MPI_LONG,
			 MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int UMD_Alltoall_d(double *sendbuf, int block_size, double *recvbuf) {
  register int i, j, k, s;

  s = block_size * sizeof(double);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    k = j*block_size;
    UMD_Sendrecv(j, sendbuf + k, s,
		 j, recvbuf + k, s);
  }
  k = MYNODE * block_size;
  memcpy(recvbuf+k, sendbuf+k, s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Alltoall_d(double *sendbuf, int block_size, double *recvbuf) {
  _MPI_WRAP(MPI_Alltoall(sendbuf, block_size, MPI_DOUBLE,
			 recvbuf, block_size, MPI_DOUBLE,
			 MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Alltoallv_i(int *sendbuf, int *sendcnt, int *sendoff,
		       int *recvbuf, int *recvcnt, int *recvoff)
{
  register int i, j, s;

  s = sizeof(int);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    UMD_Sendrecv_nb(j, sendbuf+sendoff[j], sendcnt[j]*s,
		    j, recvbuf+recvoff[j], recvcnt[j]*s);
  }

#if DEBUG
  if (sendcnt[MYNODE] != recvcnt[MYNODE]) {
    perror("ERROR: Alltoallv_i");
    exit(1);
  }
#endif /* DEBUG */
  memcpy(recvbuf+recvoff[MYNODE], sendbuf+sendoff[MYNODE], sendcnt[MYNODE]*s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Alltoallv_i(int *sendbuf, int *sendcnt, int *sendoff,
		       int *recvbuf, int *recvcnt, int *recvoff) {
  _MPI_WRAP(MPI_Alltoallv(sendbuf, sendcnt, sendoff, MPI_INT,
			  recvbuf, recvcnt, recvoff, MPI_INT,
			  MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Alltoallv_l(long *sendbuf, int *sendcnt, int *sendoff,
		       long *recvbuf, int *recvcnt, int *recvoff)
{
  register int i, j, s;

  s = sizeof(long);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    UMD_Sendrecv_nb(j, sendbuf+sendoff[j], sendcnt[j]*s,
		    j, recvbuf+recvoff[j], recvcnt[j]*s);
  }

#if DEBUG
  if (sendcnt[MYNODE] != recvcnt[MYNODE]) {
    perror("ERROR: Alltoallv_i");
    exit(1);
  }
#endif /* DEBUG */
  memcpy(recvbuf+recvoff[MYNODE], sendbuf+sendoff[MYNODE], sendcnt[MYNODE]*s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Alltoallv_l(long *sendbuf, int *sendcnt, int *sendoff,
		       long *recvbuf, int *recvcnt, int *recvoff) {
  _MPI_WRAP(MPI_Alltoallv(sendbuf, sendcnt, sendoff, MPI_LONG,
			  recvbuf, recvcnt, recvoff, MPI_LONG,
			  MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Alltoallv_d(double *sendbuf, int *sendcnt, int *sendoff,
		       double *recvbuf, int *recvcnt, int *recvoff) {
  register int i, j, s;

  s = sizeof(double);

  for (i=1 ; i<NODES ; i++) {
    j = MYNODE ^ i;
    UMD_Sendrecv_nb(j, sendbuf+sendoff[j], sendcnt[j]*s,
		    j, recvbuf+recvoff[j], recvcnt[j]*s);
  }

#if DEBUG
  if (sendcnt[MYNODE] != recvcnt[MYNODE]) {
    perror("ERROR: Alltoallv_d");
    exit(1);
  }
#endif /* DEBUG */
  memcpy(recvbuf+recvoff[MYNODE], sendbuf+sendoff[MYNODE], sendcnt[MYNODE]*s);
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Alltoallv_d(double *sendbuf, int *sendcnt, int *sendoff,
		       double *recvbuf, int *recvcnt, int *recvoff) {
  _MPI_WRAP(MPI_Alltoallv(sendbuf, sendcnt, sendoff, MPI_DOUBLE,
			  recvbuf, recvcnt, recvoff, MPI_DOUBLE,
			  MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Gather_i(int *sendbuf, int block_size, int *recvbuf) {
  register int i, s;
  int *buf;

  s = block_size * sizeof(int);

  if (MYNODE > 0)
    UMD_Send(0, sendbuf, s);
  else {
    memcpy(recvbuf, sendbuf, s);
    buf = recvbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Recv(i, (buf+=block_size), s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Gather_i(int *sendbuf, int block_size, int *recvbuf) {
  _MPI_WRAP(MPI_Gather(sendbuf, block_size, MPI_INT,
		       recvbuf, block_size, MPI_INT,
		       0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Gather_l(long *sendbuf, int block_size, long *recvbuf) {
  register int i, s;
  long *buf;

  s = block_size * sizeof(long);

  if (MYNODE > 0)
    UMD_Send(0, sendbuf, s);
  else {
    memcpy(recvbuf, sendbuf, s);
    buf = recvbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Recv(i, (buf+=block_size), s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Gather_l(long *sendbuf, int block_size, long *recvbuf) {
  _MPI_WRAP(MPI_Gather(sendbuf, block_size, MPI_LONG,
		       recvbuf, block_size, MPI_LONG,
		       0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Gather_d(double *sendbuf, int block_size, double *recvbuf) {
  register int i, s;
  double *buf;

  s = block_size * sizeof(double);

  if (MYNODE > 0)
    UMD_Send(0, sendbuf, s);
  else {
    memcpy(recvbuf, sendbuf, s);
    buf = recvbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Recv(i, (buf+=block_size), s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Gather_d(double *sendbuf, int block_size, double *recvbuf) {
  _MPI_WRAP(MPI_Gather(sendbuf, block_size, MPI_DOUBLE,
		       recvbuf, block_size, MPI_DOUBLE,
		       0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Scatter_i(int *sendbuf, int block_size, int *recvbuf) {
  register int i, s;
  int *buf;

  s = block_size * sizeof(int);

  if (MYNODE > 0)
    UMD_Recv(0, recvbuf, s);
  else {
    buf = sendbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Send(i, (buf+=block_size), s);
    memcpy(recvbuf, sendbuf, s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Scatter_i(int *sendbuf, int block_size, int *recvbuf) {
  _MPI_WRAP(MPI_Scatter(sendbuf, block_size, MPI_INT,
			recvbuf, block_size, MPI_INT,
			0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Scatter_l(long *sendbuf, int block_size, long *recvbuf) {
  register int i, s;
  long *buf;

  s = block_size * sizeof(long);

  if (MYNODE > 0)
    UMD_Recv(0, recvbuf, s);
  else {
    buf = sendbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Send(i, (buf+=block_size), s);
    memcpy(recvbuf, sendbuf, s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Scatter_l(long *sendbuf, int block_size, long *recvbuf) {
  _MPI_WRAP(MPI_Scatter(sendbuf, block_size, MPI_LONG,
			recvbuf, block_size, MPI_LONG,
			0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

#if defined(_MP_UMD)
int    UMD_Scatter_d(double *sendbuf, int block_size, double *recvbuf) {
  register int i, s;
  double *buf;

  s = block_size * sizeof(double);

  if (MYNODE > 0)
    UMD_Recv(0, recvbuf, s);
  else {
    buf = sendbuf;
    for (i=1 ; i<NODES ; i++) 
      UMD_Send(i, (buf+=block_size), s);
    memcpy(recvbuf, sendbuf, s);
  }
  return 0;
}
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int    UMD_Scatter_d(double *sendbuf, int block_size, double *recvbuf) {
  _MPI_WRAP(MPI_Scatter(sendbuf, block_size, MPI_DOUBLE,
			recvbuf, block_size, MPI_DOUBLE,
			0, MPI_COMM_WORLD));
  return 0;
}
#endif /* _MP_MPI */

int log_2(int number)
{
  int cumulative = 1;
  int out = 0;
  int done = 0;

  while ((cumulative < number) && (!done) && (out < 50)) {
    if (cumulative == number) {
      done = 1;
    } else {
      cumulative = (cumulative << 1);
      out ++;
    }
  }
  if (cumulative == number) {
    return(out);
  } else {
    return(-1);
  }
}

int ilog2(int a) {
  /* a is a power of two */
  int b=0;
  while (a>1) {
    a >>=1;
    b++;
  }
  return (b);
}
#endif

