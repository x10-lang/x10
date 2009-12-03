/*
 * (c) Copyright IBM Corporation 2009
 * 
 * $Id$
 *
 * This file is part of X10 Runtime on MPI layer implementation.
 */

x10rt_net_comm_split(unsigned comm, unsigned new_comm, unsigned color, unsigned new_rank) {
}

bool x10rt_net_is_coll_op_done(void * collop) {
    return false;
}

void * x10rt_net_barrier_start(unsigned comm) {
    return NULL;
}

void * x10rt_net_allreduce_start(unsigned comm,  void * sendbuf, void * recvbuf, 
        x10rt_net_op op, x10rt_net_dtype dtype, unsigned nelems) {
    return NULL;
}

void * x10rt_net_broadcast_start(unsigned comm, int root,
        void * sendbuf, void * recvbuf, x10rt_net_dtype dtype, unsigned nelems) {
    return NULL;
}
