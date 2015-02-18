
#ifndef MPI_API_H
#define MPI_API_H

#include <stdint.h>

#ifdef jniport_h
typedef long long blas_long;
#else
typedef int64_t blas_long;
#endif

void mpi_new_comm();
void mpi_get_name_maxlen(int* ml);
void mpi_get_comm_pid(int* rk);
void mpi_get_comm_nproc(int* np);
void mpi_get_proc_info(int* rk, int* np, int* hlen, int* hstr);
void mpi_get_statue_memsize(int* sl);
void mpi_get_request_memsize(int* sl);

//---------------------
// Blocking double P2P
void mpi_send_double(double* buf, int off, int cnt, int dst, int tag);
void mpi_recv_double(double* buf, int off, int cnt, int src, int tag);

void mpi_send_long(blas_long* buf, int off, int cnt, int dst, int tag);
void mpi_recv_long(blas_long* buf, int off, int cnt, int src, int tag);

//---------------------
// Non-blocking double P2P
void mpi_Isend_double(double* buf, int off, int cnt, int dst, int tag, void* req);
void mpi_Irecv_double(double* buf, int off, int cnt, int src, int tag, void* req);

void mpi_Isend_long(blas_long* buf, int off, int cnt, int dst, int tag, void* req);
void mpi_Irecv_long(blas_long* buf, int off, int cnt, int src, int tag, void* req);
//---------------------
// Bcast
void mpi_bcast_long(blas_long* buf, int off, int cnt, int root);
void mpi_bcast_double(double* buf, int off, int cnt, int root);
//---------------------
//Gatherv
void mpi_gatherv_double(double* sendbuf, int sendoff, int sendcnt, 
						double* recvbuf, int recvoff, int* recvcnts, int* displs, int root);
void mpi_gatherv_long(blas_long* sendbuf, int sendoff, int sendcnt, 
					  blas_long* recvbuf, int recvoff, int* recvcnts, int* displs, int root);
//---------------------
//Scatterrv
void mpi_scatterv_double(double* sendbuf, int* sendcnts, int* displs,
						 double* recvbuf, int recvcnt,  int root);
void mpi_scatterv_long(blas_long* sendbuf, int* sendcnts, int* displs,
					   blas_long* recvbuf, int recvcnt,  int root);

// Allgatherv
void mpi_allgatherv_double(double* sendbuf, int sendoff, int sendcnt, 
						   double* recvbuf, int recvoff, int* recvcnts, int* displs);
//---------------------
// reduce sum
void mpi_reduce_sum_long(blas_long* sendbuf, int soff, int* recvbuf, int roff,  int cnt, int root);
void mpi_reduce_sum_double(double* sendbuf, int soff, double* recvbuf, int roff,  int cnt, int root);
//---------------------
// all reduce sum
void mpi_allreduce_sum_long(blas_long*sendbuf, int soff, int* recvbuf, int roff, int cnt);
void mpi_allreduce_sum_double(double*sendbuf, int soff, double* recvbuf, int roff, int cnt);
//---------------------
// Request waiting
void mpi_wait_request(void* req);
void mpi_test_request(void* req, int* flag);
//---------------------

#endif

