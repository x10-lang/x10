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

#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
#pragma weak NBC_Comm_neighbors_count=PNBC_Comm_neighbors_count
#define NBC_Comm_neighbors_count PNBC_Comm_neighbors_count
#endif

int NBC_Comm_neighbors_count(MPI_Comm comm, int rank, int *nneighbors) {
  int topo, res;

  res = MPI_Topo_test(comm, &topo);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Topo_test() (%i)\n", res); return res; }

  switch(topo) {
    case MPI_CART: /* cartesian */
      {
        int ndims, i, rpeer, speer;
        res = MPI_Cartdim_get(comm, &ndims)  ;
        if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Cartdim_get() (%i)\n", res); return res; }
        /* do the shift in every dimension to see if we really have
         * those neighbors (they're not MPI_PROC_NULL) */
        *nneighbors = 0;
        for(i = 0; i<ndims; i++) {
          res = MPI_Cart_shift(comm, i, 1, &rpeer, &speer);
          if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Cart_shift() (%i)\n", res); return res; }
          if(rpeer != MPI_PROC_NULL) (*nneighbors)++;
          if(speer != MPI_PROC_NULL) (*nneighbors)++;
        }
      }
      break;
    case MPI_GRAPH: /* graph */
      {
        res = MPI_Graph_neighbors_count(comm, rank, nneighbors);
        if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Graph_neighbors_count() (%i)\n", res); return res; }
      }
      break;
    case MPI_UNDEFINED:
      return NBC_INVALID_TOPOLOGY_COMM;
      break;
    default:
      return NBC_INVALID_PARAM;
      break;
  }
  return NBC_OK;
}

#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
#pragma weak NBC_Comm_neighbors=PNBC_Comm_neighbors
#define NBC_Comm_neighbors PNBC_Comm_neighbors
#endif

int NBC_Comm_neighbors(MPI_Comm comm, int rank, int maxneighbors, int *neighbors) {
  int topo, res, nneighbors;
  int index = 0;

  res = NBC_Comm_neighbors_count(comm, rank, &nneighbors);
  if(nneighbors > maxneighbors) return NBC_INVALID_PARAM; /* we want to return *all* neighbors */

  res = MPI_Topo_test(comm, &topo);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Topo_test() (%i)\n", res); return res; }

  switch(topo) {
    case MPI_CART: /* cartesian */
      {
        int ndims, i, rpeer, speer;
        res = MPI_Cartdim_get(comm, &ndims);
        if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Cartdim_get() (%i)\n", res); return res; }

        for(i = 0; i<ndims; i++) {
          res = MPI_Cart_shift(comm, i, 1, &rpeer, &speer);
          if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Cart_shift() (%i)\n", res); return res; }
          if(rpeer != MPI_PROC_NULL) neighbors[index++] = rpeer;
          if(speer != MPI_PROC_NULL) neighbors[index++] = speer;
        }
      }
      break;
    case MPI_GRAPH: /* graph */
      {
        res = MPI_Graph_neighbors(comm, rank, maxneighbors, neighbors);
        if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Graph_neighbors_count() (%i)\n", res); return res; }
      }
      break;
    case MPI_UNDEFINED:
      return NBC_INVALID_TOPOLOGY_COMM;
      break;
    default:
      return NBC_INVALID_PARAM;
      break;
  }

  return NBC_OK;
}

#ifdef NBC_CACHE_SCHEDULE
/* tree comparison function for schedule cache */
int NBC_Ineighbor_xchg_args_compare(NBC_Ineighbor_xchg_args *a, NBC_Ineighbor_xchg_args *b, void *param) {

	if( (a->sbuf == b->sbuf) && 
      (a->scount == b->scount) && 
      (a->stype == b->stype) &&
      (a->rbuf == b->rbuf) && 
      (a->rcount == b->rcount) && 
      (a->rtype == b->rtype) ) {
      return  0;
    }
	if( a->sbuf < b->sbuf ) {	
      return -1;
	}
	return +1;
}
#endif

/* this is a new collective operation defined on a topo communicator.
 * This operation communicates with all neighbors in a topology
 * communicator.  This operation is comparable to an Alltoall on a
 * communicator that spans the neighbors. An example in a 2d cartesian
 * grid:
 *
 *  0    1    2    3    4    5          ^  1st dim
 *  6    7    8    9    10   11         -> 2nd dim        
 *  12   13   14   15   16   17
 *
 * Case of Cartesian Topology:
 * ndims is two in this case and every rank has a maximum of 2*ndims
 * neighbors! Thus send and receive arrays are arranged as in the
 * Alltoall case, i.e., they must offer space for 2*ndims*count elements
 * of the supplied type. The order of nodes is first along the first
 * dimension, in displacement -1 then +1, then along the second
 * dimension and so on ... on our example, the order for rank 8 is:
 *  2, 14, 7, 9. It can be calculated with the local function
 *  MPI_Cart_shift().
 *
 * Case of Graph Topology:
 *  A graph topology is more complicated because it might be irregular,
 *  i.e., different nodes have different numbers of neighbors. The
 *  arrays are defined similarly to Alltoall again. However, the size of
 *  the arrays depends on the number of neighbors and might be different
 *  on different nodes. The local function MPI_Graph_neighbors_count()
 *  returns the array size and the local function MPI_Graph_neigbors()
 *  returns the actual neigbors. The array must have enough space for
 *  nneighbor*count elements of the supplied datatype and the data is
 *  ordered as MPI_Graph_neigbors() returns. 
 *
 * Implementation ideas:
 * - check if this is a topo comm (MPI_TOPO_TEST?)
 *     -> MPI_Topo_test(comm, &stat);
 *         if(stat == MPI_UNDEFINED) return error;
 * 
 * if(stat == MPI_GRAPH)
 *     MPI_graph_neighbors_count(comm, rank, &nneighbors)
 *     neigbor_array = malloc(sizeof(int)*nneighbors);
 *     use MPI_Graph_neighbors(comm, rank, nneighbors, &neigbor_array)
 *     send and receive to them all non-blocking ...
 * 
 * if(stat == MPI_CART)
 *     MPI_Cartdim_get(comm, &ndims);
 *     for(i=0;i<ndims;i++) {
 *         MPI_Cart_shift(comm, i, -1, &rpeer, &speer);
 *         if(rpeer != MPI_PROC_NULL) Sched_recv ....
 *         if(speer != MPI_PROC_NULL) Sched_send ....
 *     }
 * 
 */

#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
#pragma weak NBC_Ineighbor_xchg=PNBC_Ineighbor_xchg
#define NBC_Ineighbor_xchg PNBC_Ineighbor_xchg
#endif
int NBC_Ineighbor_xchg(void *sbuf, int scount, MPI_Datatype stype,
        void *rbuf, int rcount, MPI_Datatype rtype, MPI_Comm comm, NBC_Handle* handle) {
  int rank, res;
  MPI_Aint sndext, rcvext;
  char inplace;
  NBC_Schedule *schedule;
#ifdef NBC_CACHE_SCHEDULE
  NBC_Ineighbor_xchg_args *args, *found, search;
#endif

  NBC_IN_PLACE(sbuf, rbuf, inplace);
  
  res = NBC_Init_handle(handle, comm);
  if(res != NBC_OK) { printf("Error in NBC_Init_handle(%i)\n", res); return res; }
  res = MPI_Comm_rank(comm, &rank);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Comm_rank() (%i)\n", res); return res; }
  res = MPI_Type_extent(stype, &sndext);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Type_extent() (%i)\n", res); return res; }
  res = MPI_Type_extent(rtype, &rcvext);
  if (MPI_SUCCESS != res) { printf("MPI Error in MPI_Type_extent() (%i)\n", res); return res; }
  
  handle->tmpbuf=NULL;

#ifdef NBC_CACHE_SCHEDULE
  /* search schedule in communicator specific tree */
  search.sbuf=sbuf;
  search.scount=scount;
  search.stype=stype;
  search.rbuf=rbuf;
  search.rcount=rcount;
  search.rtype=rtype;
  found = (NBC_Ineighbor_xchg_args*)hb_tree_search((hb_tree*)handle->comminfo->NBC_Dict[NBC_NEIGHBOR_XCHG], &search);
  if(found == NULL) {
#endif
    schedule = (NBC_Schedule*)malloc(sizeof(NBC_Schedule));
    
    res = NBC_Sched_create(schedule);
    if(res != NBC_OK) { printf("Error in NBC_Sched_create, res = %i\n", res); return res; }

    {
      int nneighbors, *neighbors, i;
      res = NBC_Comm_neighbors_count(comm, rank, &nneighbors);
      if(res != NBC_OK) return res;
      neighbors = (int*)malloc(sizeof(int)*nneighbors);
      res = NBC_Comm_neighbors(comm, rank, nneighbors, neighbors);
      if(res != NBC_OK) return res;
    

      if(inplace) { /* we need an extra buffer to be deadlock-free */
        handle->tmpbuf = malloc(nneighbors*sndext*scount);

        for(i = 0; i < nneighbors; i++) {
          res = NBC_Sched_recv((char*)0+i*rcount*rcvext, true, rcount, rtype, neighbors[i], schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_recv() (%i)\n", res); return res; }
          res = NBC_Sched_send((char*)sbuf+i*scount*sndext, false, scount, stype, neighbors[i], schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_send() (%i)\n", res); return res; }
        }
        /* unpack from buffer */
        for(i = 0; i < nneighbors; i++) {
          res = NBC_Sched_barrier(schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_barrier() (%i)\n", res); return res; }
          res = NBC_Sched_copy((char*)0+i*rcount*rcvext, true, rcount, rtype, (char*)rbuf+i*rcount*rcvext, false, rcount, rtype, schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_copy() (%i)\n", res); return res; }
        }
      } else { /* non INPLACE case */
        /* simply loop over neighbors and post send/recv operations */
        for(i = 0; i < nneighbors; i++) {
          res = NBC_Sched_recv((char*)rbuf+i*rcount*rcvext, false, rcount, rtype, neighbors[i], schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_recv() (%i)\n", res); return res; }
          res = NBC_Sched_send((char*)sbuf+i*scount*sndext, false, scount, stype, neighbors[i], schedule);
          if (NBC_OK != res) { printf("Error in NBC_Sched_send() (%i)\n", res); return res; }
        }
      }
    }
    
    res = NBC_Sched_commit(schedule);
    if (NBC_OK != res) { printf("Error in NBC_Sched_commit() (%i)\n", res); return res; }
#ifdef NBC_CACHE_SCHEDULE
    /* save schedule to tree */
    args = (NBC_Ineighbor_xchg_args*)malloc(sizeof(NBC_Ineighbor_xchg_args));
    args->sbuf=sbuf;
    args->scount=scount;
    args->stype=stype;
    args->rbuf=rbuf;
    args->rcount=rcount;
    args->rtype=rtype;
    args->schedule=schedule;
	  res = hb_tree_insert ((hb_tree*)handle->comminfo->NBC_Dict[NBC_NEIGHBOR_XCHG], args, args, 0);
    if(res != 0) printf("error in dict_insert() (%i)\n", res);
    /* increase number of elements for A2A */
    if(++handle->comminfo->NBC_Dict_size[NBC_NEIGHBOR_XCHG] > NBC_SCHED_DICT_UPPER) {
      NBC_SchedCache_dictwipe((hb_tree*)handle->comminfo->NBC_Dict[NBC_NEIGHBOR_XCHG], &handle->comminfo->NBC_Dict_size[NBC_NEIGHBOR_XCHG]);
    }
  } else {
    /* found schedule */
    schedule=found->schedule;
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
NBC_F77_ALLFUNC_(nbc_comm_neighbors,NBC_COMM_NEIGHBORS,(int *fcomm, int *rank, int *maxneighbors, int *neighbors, int *ierr));
#pragma weak NBC_COMM_NEIGHBORS = nbc_comm_neighbors_f
#pragma weak nbc_comm_neighbors = nbc_comm_neighbors_f
#pragma weak nbc_comm_neighbors_ = nbc_comm_neighbors_f
#pragma weak nbc_comm_neighbors__ = nbc_comm_neighbors_f
#pragma weak PNBC_COMM_NEIGHBORS = nbc_comm_neighbors_f
#pragma weak pnbc_comm_neighbors = nbc_comm_neighbors_f
#pragma weak pnbc_comm_neighbors_ = nbc_comm_neighbors_f
#pragma weak pnbc_comm_neighbors__ = nbc_comm_neighbors_f
void nbc_comm_neighbors_f(int *fcomm, int *rank, int *maxneighbors, int *neighbors, int *ierr) {
#else
void NBC_F77_FUNC_(nbc_comm_neighbors,NBC_COMM_NEIGHBORS)(int *fcomm, int *rank, int *maxneighbors, int *neighbors, int *ierr);
void NBC_F77_FUNC_(nbc_comm_neighbors,NBC_COMM_NEIGHBORS)(int *fcomm, int *rank, int *maxneighbors, int *neighbors, int *ierr) {
#endif
  MPI_Comm comm;
  comm = MPI_Comm_f2c(*fcomm);

  *ierr = NBC_Comm_neighbors(comm, *rank, *maxneighbors, neighbors);
}

#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
NBC_F77_ALLFUNC_(nbc_comm_neighbors_count,NBC_COMM_NEIGHBORS_COUNT,(int *fcomm, int *rank, int *nneighbors, int *ierr));
#pragma weak NBC_COMM_NEIGHBORS_COUNT = nbc_comm_neighbors_count_f
#pragma weak nbc_comm_neighbors_count = nbc_comm_neighbors_count_f
#pragma weak nbc_comm_neighbors_count_ = nbc_comm_neighbors_count_f
#pragma weak nbc_comm_neighbors_count__ = nbc_comm_neighbors_count_f
#pragma weak PNBC_COMM_NEIGHBORS_COUNT = nbc_comm_neighbors_count_f
#pragma weak pnbc_comm_neighbors_count = nbc_comm_neighbors_count_f
#pragma weak pnbc_comm_neighbors_count_ = nbc_comm_neighbors_count_f
#pragma weak pnbc_comm_neighbors_count__ = nbc_comm_neighbors_count_f
void nbc_comm_neighbors_count_f(int *fcomm, int *rank, int *nneighbors, int *ierr) {
#else
void NBC_F77_FUNC_(nbc_comm_neighbors_count,NBC_COMM_NEIGHBORS_COUNT)(int *fcomm, int *rank, int *nneighbors, int *ierr);
void NBC_F77_FUNC_(nbc_comm_neighbors_count,NBC_COMM_NEIGHBORS_COUNT)(int *fcomm, int *rank, int *nneighbors, int *ierr) {
#endif
  MPI_Comm comm;
  comm = MPI_Comm_f2c(*fcomm);

  *ierr = NBC_Comm_neighbors_count(comm, *rank, nneighbors);
}

#ifdef HAVE_SYS_WEAK_ALIAS_PRAGMA
NBC_F77_ALLFUNC_(nbc_ineighbor_xchg,NBC_INEIGHBOR_XCHG,(void *sbuf, int *scount, int *stype, void *rbuf, int *rcount,
        int *rtype, int *fcomm, int *fhandle, int *ierr));
#pragma weak NBC_INEIGHBOR_XCHG = nbc_ineighbor_xchg_f
#pragma weak nbc_ineighbor_xchg = nbc_ineighbor_xchg_f
#pragma weak nbc_ineighbor_xchg_ = nbc_ineighbor_xchg_f
#pragma weak nbc_ineighbor_xchg__ = nbc_ineighbor_xchg_f
#pragma weak PNBC_INEIGHBOR_XCHG = nbc_ineighbor_xchg_f
#pragma weak pnbc_ineighbor_xchg = nbc_ineighbor_xchg_f
#pragma weak pnbc_ineighbor_xchg_ = nbc_ineighbor_xchg_f
#pragma weak pnbc_ineighbor_xchg__ = nbc_ineighbor_xchg_f
void nbc_ineighbor_xchg_f(void *sbuf, int *scount, int *stype, void *rbuf, int *rcount,
        int *rtype, int *fcomm, int *fhandle, int *ierr) {
#else
void NBC_F77_FUNC_(nbc_ineighbor_xchg,NBC_INEIGHBOR_XCHG)(void *sbuf, int *scount, int *stype, void *rbuf, int *rcount,
        int *rtype, int *fcomm, int *fhandle, int *ierr);
void NBC_F77_FUNC_(nbc_ineighbor_xchg,NBC_INEIGHBOR_XCHG)(void *sbuf, int *scount, int *stype, void *rbuf, int *rcount,
        int *rtype, int *fcomm, int *fhandle, int *ierr) {
#endif
  MPI_Datatype sdtype, rdtype;
  MPI_Comm comm;
  NBC_Handle *handle;

  /* this is the only MPI-2 we need :-( */
  sdtype = MPI_Type_f2c(*stype);
  rdtype = MPI_Type_f2c(*rtype);
  comm = MPI_Comm_f2c(*fcomm);

  /* create a new handle in handle table */
  NBC_Create_fortran_handle(fhandle, &handle);

  /* call NBC function */
  *ierr = NBC_Ineighbor_xchg(sbuf, *scount, sdtype, rbuf, *rcount,
           rdtype, comm, handle);
}
#ifdef __cplusplus
}
#endif
