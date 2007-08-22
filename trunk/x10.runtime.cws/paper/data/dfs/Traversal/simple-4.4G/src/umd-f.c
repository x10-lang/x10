#if 0
#include "umd.h"

#ifdef __GNUC__
#define FFUN(x) x##_
#else
#define FFUN(x) x##_
#endif

extern reduce_t convert_freduce(int);

void FFUN(fusend)(int *dst, void *arr, int *len,
		  int *ret) {
  *ret = UMD_Send(*dst, arr, *len);
  return;
}

void FFUN(furecv)(int *src, void *arr, int *len,
		  int *ret) {
  *ret = UMD_Recv(*src, arr, *len);
  return;
}

void FFUN(fusendrecv)(int *dst, void *arr_out, int *len_t,
		      int *src, void *arr_in,  int *len_r,
		      int *ret) {
  *ret = UMD_Sendrecv(*dst, arr_out, *len_t,
		      *src, arr_in,  *len_r);
  return;
}

void FFUN(fusendrecvnb)(int *dst, void *arr_out, int *len_t,
		      int *src, void *arr_in,  int *len_r,
		      int *ret) {
  *ret = UMD_Sendrecv_nb(*dst, arr_out, *len_t,
			 *src, arr_in,  *len_r);
  return;
}

void FFUN(fubar)(int *ret) {
  *ret = UMD_Barrier();
  return;
}

void FFUN(fureducei)(int *myval, int *fop, int *ret) {
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = UMD_Reduce_i(*myval, op);
  return;
}

void FFUN(fureduced)(double *myval, int *fop, double *ret) {
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = UMD_Reduce_d(*myval, op);
  return;
}

void FFUN(fubcasti)(int *myval, int *ret) {
  *ret = UMD_Bcast_i(*myval);
  return;
}

void FFUN(fubcastd)(double *myval, double *ret) {
  *ret = UMD_Bcast_d(*myval);
  return;
}

void FFUN(fubcastc)(char *myval, char *ret) {
  *ret = UMD_Bcast_c(*myval);
  return;
}

void FFUN(fubcastbufi)(int *sendbuf, int *cnt, int *recvbuf, int *ret) {
  *ret = UMD_Bcast_buf_i(sendbuf, *cnt, recvbuf);
  return;
}

void FFUN(fubcastbufd)(double *sendbuf, int *cnt, double *recvbuf, int *ret) {
  *ret = UMD_Bcast_buf_d(sendbuf, *cnt, recvbuf);
  return;
}

void FFUN(fubcastbufc)(char *sendbuf, int *cnt, char *recvbuf, int *ret) {
  *ret = UMD_Bcast_buf_c(sendbuf, *cnt, recvbuf);
  return;
}

void FFUN(fuallreducei)(int *myval, int *fop, int *ret) {
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = UMD_Allreduce_i(*myval, op);
  return;
}

void FFUN(fuallreduced)(double *myval, int *fop, int *ret) {
  reduce_t op;
  op = convert_freduce(*fop);
  *ret = UMD_Allreduce_d(*myval, op);
  return;
}

void FFUN(fualltoalli)(int *sendbuf, int *block_size, int *recvbuf,
		       int *ret) {
  *ret = UMD_Alltoall_i(sendbuf, *block_size, recvbuf);
  return;
}

void FFUN(fualltoalld)(double *sendbuf, int *block_size, double *recvbuf,
		       int *ret) {
  *ret = UMD_Alltoall_d(sendbuf, *block_size, recvbuf);
  return;
}

void FFUN(fualltoallvi)(int *sendbuf, int *sendcnt, int *sendoff,
			int *recvbuf, int *recvcnt, int *recvoff,
			int *ret) {
  *ret = UMD_Alltoallv_i(sendbuf, sendcnt, sendoff,
			 recvbuf, recvcnt, recvoff);
  return;
}

void FFUN(fualltoallvd)(double *sendbuf, int *sendcnt, int *sendoff,
			double *recvbuf, int *recvcnt, int *recvoff,
			int *ret) {
  *ret = UMD_Alltoallv_d(sendbuf, sendcnt, sendoff,
			 recvbuf, recvcnt, recvoff);
  return;
}

void FFUN(fugatheri)(int *sendbuf, int *block_size, int *recvbuf,
		     int *ret) {
  *ret = UMD_Gather_i(sendbuf, *block_size, recvbuf);
  return;
}

void FFUN(fugatherd)(double *sendbuf, int *block_size, double *recvbuf,
		     int *ret) {
  *ret = UMD_Gather_d(sendbuf, *block_size, recvbuf);
  return;
}

void FFUN(fuscatteri)(int *sendbuf, int *block_size, int *recvbuf,
		      int *ret) {
  *ret = UMD_Scatter_i(sendbuf, *block_size, recvbuf);
  return;
}

void FFUN(fuscatterd)(double *sendbuf, int *block_size, double *recvbuf,
		      int *ret) {
  *ret = UMD_Scatter_d(sendbuf, *block_size, recvbuf);
  return;
}


#if 0

#if _NB_COMM
int UMD_ISend(int dst, void *arr, int len, msg_tag *tag);
int UMD_IRecv(int src, void *arr, int len, msg_tag *tag);
int UMD_Test(msg_tag tag);
int UMD_Wait(msg_tag tag);
#endif

long   UMD_Reduce_l(long   myval, reduce_t op); 
long   UMD_Bcast_l(long   myval);
char   *UMD_Bcast_cp(char   *myval);
int    *UMD_Bcast_ip(int    *myval);
long   *UMD_Bcast_lp(long   *myval);
double *UMD_Bcast_dp(double *myval);
int    UMD_Bcast_buf_l(long   *sendbuf, int cnt, long   *recvbuf);
long   UMD_Allreduce_l(long   myval, reduce_t op);
int    UMD_Alltoall_l(long   *sendbuf, int block_size, long   *recvbuf);
int    UMD_Alltoallv_l(long   *sendbuf, int *sendcnt, int *sendoff,
		       long   *recvbuf, int *recvcnt, int *recvoff);
int    UMD_Gather_l(long   *sendbuf, int block_size, long   *recvbuf);
int    UMD_Scatter_l(long   *sendbuf, int block_size, long   *recvbuf);

#endif

#endif

