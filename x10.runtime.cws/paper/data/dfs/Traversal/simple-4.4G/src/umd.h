#ifndef _UMD_H
#define _UMD_H

#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/time.h>		/* struct timeval */
#include <sys/resource.h>
#include <math.h>
#include <string.h>
#include "mpi.h"
#include "mach_def.h"
#include "types.h"

#if 0
#define _ZCOMM
#endif
#ifdef _ZCOMM
#include "zlib.h"
#endif

#define MAXNODES 1024

typedef int msg_tag;

int UMD_Init(int* argc, char* **argv);
int UMD_Finalize();
int UMD_Send(int dst, void *arr, int len);
int UMD_Recv(int src, void *arr, int len);
int UMD_Sendrecv(int dst, void *arr_out, int len_t,
		  int src, void *arr_in, int len_r);
int UMD_Sendrecv_nb(int dst, void *arr_out, int len_t,
		  int src, void *arr_in, int len_r);

#ifdef _ZCOMM
int UMD_ZSend(int dst, void *arr, int len);
int UMD_ZRecv(int src, void *arr, int len);
#endif

/* NON-BLOCKING */
#define _NB_COMM      1    /* Use ISend() and IRecv() non-blocking comm */
#define _NB_COMM_LOCK 0    /* Atomic lock of write/recv Send()/Recv() */

#if _NB_COMM
#define DONE TRUE
int UMD_ISend(int dst, void *arr, int len, msg_tag *tag);
int UMD_IRecv(int src, void *arr, int len, msg_tag *tag);
int UMD_Test(msg_tag tag);
int UMD_Wait(msg_tag tag);
#endif

#if defined(_MP_MPI)
#define _MPI_WRAP(x) pthread_mutex_lock(&_mpi_lock_simple); \
                     x; \
		     pthread_mutex_unlock(&_mpi_lock_simple)
#endif /* _MP_MPI */

#if defined(_MP_UMD)
#define UMD_Barrier() UMD_Barrier3()
int UMD_Barrier1();
int UMD_Barrier2();
int UMD_Barrier3();
#endif /* _MP_UMD */

#if defined(_MP_MPI)
int UMD_Barrier();
#endif /* _MP_MPI */

int    UMD_Reduce_i(int    myval, reduce_t op); 
long   UMD_Reduce_l(long   myval, reduce_t op); 
double UMD_Reduce_d(double myval, reduce_t op);
	  
char   UMD_Bcast_c(char   myval);
int    UMD_Bcast_i(int    myval);
long   UMD_Bcast_l(long   myval);
double UMD_Bcast_d(double myval);

char   *UMD_Bcast_cp(char   *myval);
int    *UMD_Bcast_ip(int    *myval);
long   *UMD_Bcast_lp(long   *myval);
double *UMD_Bcast_dp(double *myval);

int    UMD_Bcast_buf_c(char   *sendbuf, int cnt, char   *recvbuf);
int    UMD_Bcast_buf_i(int    *sendbuf, int cnt, int    *recvbuf);
int    UMD_Bcast_buf_l(long   *sendbuf, int cnt, long   *recvbuf);
int    UMD_Bcast_buf_d(double *sendbuf, int cnt, double *recvbuf);

int    UMD_Allreduce_i(int    myval, reduce_t op);
long   UMD_Allreduce_l(long   myval, reduce_t op);
double UMD_Allreduce_d(double myval, reduce_t op);

int    UMD_Alltoall_i(int    *sendbuf, int block_size, int    *recvbuf);
int    UMD_Alltoall_l(long   *sendbuf, int block_size, long   *recvbuf);
int    UMD_Alltoall_d(double *sendbuf, int block_size, double *recvbuf);

int    UMD_Alltoallv_i(int    *sendbuf, int *sendcnt, int *sendoff,
		       int    *recvbuf, int *recvcnt, int *recvoff);
int    UMD_Alltoallv_l(long   *sendbuf, int *sendcnt, int *sendoff,
		       long   *recvbuf, int *recvcnt, int *recvoff);
int    UMD_Alltoallv_d(double *sendbuf, int *sendcnt, int *sendoff,
		       double *recvbuf, int *recvcnt, int *recvoff);

int    UMD_Gather_i(int    *sendbuf, int block_size, int    *recvbuf);
int    UMD_Gather_l(long   *sendbuf, int block_size, long   *recvbuf);
int    UMD_Gather_d(double *sendbuf, int block_size, double *recvbuf);

int    UMD_Scatter_i(int    *sendbuf, int block_size, int    *recvbuf);
int    UMD_Scatter_l(long   *sendbuf, int block_size, long   *recvbuf);
int    UMD_Scatter_d(double *sendbuf, int block_size, double *recvbuf);

extern int MYNODE;
extern int NODES;

#define on_one_node if (MYNODE==0)
#define on_node(a)  if (MYNODE==(a))

#endif
