/*
 * Copyright (c) 2006 The Trustees of Indiana University and Indiana
 *                    University Research and Technology
 *                    Corporation.  All rights reserved.
 * Copyright (c) 2006 The Technical University of Chemnitz. All 
 *                    rights reserved.
 *
 * Author(s): Torsten Hoefler <htor@cs.indiana.edu>
 *
 */
#include "nbc_internal.h"

/* Dissemination implementation of MPI_Ibarrier */
#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
#pragma weak NBC_Ibarrier=PNBC_Ibarrier
#define NBC_Ibarrier PNBC_Ibarrier
#endif

int NBC_Ibarrier(MPI_Comm comm, NBC_Handle* handle) {
  int round, rank, p, maxround, res, recvpeer, sendpeer;
  NBC_Schedule *schedule;
  
  res = NBC_Init_handle(handle, comm);
  if(res != NBC_OK) { printf("Error in NBC_Init_handle(%i)\n", res); return res; }
  res = MPI_Comm_rank(comm, &rank);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Comm_rank() (%i)\n", res); return res; }
  res = MPI_Comm_size(comm, &p);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Comm_size() (%i)\n", res); return res; }
  
  handle->tmpbuf=NULL;

#ifdef NBC_CACHE_SCHEDULE
  /* there only one argument set per communicator -> hang it directly at
   * the tree-position, NBC_Dict_size[...] is 0 for not initialized and
   * 1 for initialized. NBC_Dict[...] is a pointer to the schedule in
   * this case */
  if(handle->comminfo->NBC_Dict_size[NBC_BARRIER] == 0) {
    /* we did not init it yet */
#endif
    schedule = (NBC_Schedule*)malloc(sizeof(NBC_Schedule));
    if (NULL == schedule) { printf("Error in malloc()\n"); return res; }
    
    round = -1;
    res = NBC_Sched_create(schedule);
    if(res != NBC_OK) { printf("Error in NBC_Sched_create (%i)\n", res); return res; }

    maxround = (int)ceil((log(p)/LOG2)-1);

    do {
      round++;
      sendpeer = (rank + (1<<round)) % p;
      /* add p because modulo does not work with negative values */
      recvpeer = ((rank - (1<<round))+p) % p;

      /* send msg to sendpeer */
      res = NBC_Sched_send(NULL, false, 0, MPI_BYTE, sendpeer, schedule);
      if (NBC_OK != res) { printf("Error in NBC_Sched_send() (%i)\n", res); return res; }

      /* recv msg from recvpeer */
      res = NBC_Sched_recv(NULL, false, 0, MPI_BYTE, recvpeer, schedule);
      if (NBC_OK != res) { printf("Error in NBC_Sched_recv() (%i)\n", res); return res; }
      /* end communication round */
      if(round < maxround){
        res = NBC_Sched_barrier(schedule);
        if (NBC_OK != res) { printf("Error in NBC_Sched_barrier() (%i)\n", res); return res; }
      }
    } while (round < maxround);

    res = NBC_Sched_commit(schedule);
    if (NBC_OK != res) { printf("Error in NBC_Sched_commit() (%i)\n", res); return res; }

#ifdef NBC_CACHE_SCHEDULE
    /* add it */
    handle->comminfo->NBC_Dict[NBC_BARRIER] = (hb_tree*)schedule;
    handle->comminfo->NBC_Dict_size[NBC_BARRIER] = 1;
  } else {
    /* we found it */
    schedule = (NBC_Schedule*)handle->comminfo->NBC_Dict[NBC_BARRIER];
  }
#endif

  res = NBC_Start(handle, schedule);
  if (NBC_OK != res) { printf("Error in NBC_Start() (%i)\n", res); return res; }
  
  return NBC_OK;
}


#ifdef __cplusplus
extern "C" {
#endif
/* Fortran bindings */
#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
NBC_F77_ALLFUNC_(nbc_ibarrier,NBC_IBARRIER,(int *fcomm, int *fhandle, int *ierr));
#pragma weak NBC_IBARRIER = nbc_ibarrier_f
#pragma weak nbc_ibarrier = nbc_ibarrier_f
#pragma weak nbc_ibarrier_ = nbc_ibarrier_f
#pragma weak nbc_ibarrier__ = nbc_ibarrier_f
#pragma weak PNBC_IBARRIER = nbc_ibarrier_f
#pragma weak pnbc_ibarrier = nbc_ibarrier_f
#pragma weak pnbc_ibarrier_ = nbc_ibarrier_f
#pragma weak pnbc_ibarrier__ = nbc_ibarrier_f
void nbc_ibarrier_f(int *fcomm, int *fhandle, int *ierr) {
#else
void NBC_F77_FUNC_(nbc_ibarrier,NBC_IBARRIER)(int *fcomm, int *fhandle, int *ierr);
void NBC_F77_FUNC_(nbc_ibarrier,NBC_IBARRIER)(int *fcomm, int *fhandle, int *ierr)  {
#endif
  MPI_Comm comm;
  NBC_Handle *handle;

  /* this is the only MPI-2 we need :-( */
  comm = MPI_Comm_f2c(*fcomm);

  /* create a new handle in handle table */
  NBC_Create_fortran_handle(fhandle, &handle);

  /* call NBC function */
  *ierr = NBC_Ibarrier(comm, handle);
}
#ifdef __cplusplus
}
#endif
