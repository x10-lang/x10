/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#if defined(__CYGWIN__) || defined(__FreeBSD__)
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin and FreeBSD
#endif

#include <cstdlib>
#include <cstdio>

#include <x10rt_front.h>
#include <x10rt_logical.h>
#include <x10rt_net.h>

static x10rt_msg_type counter = 0;

static bool run_as_library = false;

x10rt_error x10rt_preinit (char* connInfoBuffer, int connInfoBufferSize) {
	run_as_library = true;
	return x10rt_lgl_preinit(connInfoBuffer, connInfoBufferSize);
}

bool x10rt_run_as_library (void)
{ return run_as_library; }

const char *x10rt_error_msg (void) {
    return x10rt_lgl_error_msg();
}

x10rt_error x10rt_init (int *argc, char ***argv)
{ return x10rt_lgl_init(argc, argv, &counter); }

x10rt_msg_type x10rt_register_msg_receiver (x10rt_handler *cb,
                                            x10rt_cuda_pre *pre, x10rt_cuda_post *post,
                                            const char *cubin, const char *kernel_name)
{
    x10rt_lgl_register_msg_receiver(counter, cb); 
    if (pre!=NULL)
        x10rt_lgl_register_msg_receiver_cuda(counter, pre, post, cubin, kernel_name);
    return counter++;
}

x10rt_msg_type x10rt_register_get_receiver (x10rt_notifier *cb, x10rt_notifier *cuda_cb)
{
    x10rt_lgl_register_get_receiver(counter, cb);
    if (cuda_cb!=NULL)
        x10rt_lgl_register_get_receiver_cuda(counter, cuda_cb);
    return counter++;
}

x10rt_msg_type x10rt_register_put_receiver (x10rt_notifier *cb, x10rt_notifier *cuda_cb)
{
    x10rt_lgl_register_put_receiver(counter, cb);
    if (cuda_cb!=NULL)
        x10rt_lgl_register_put_receiver_cuda(counter, cuda_cb);
    return counter++;
}

void x10rt_registration_complete (void)
{ x10rt_lgl_registration_complete(); }

x10rt_place x10rt_nplaces (void)
{ return x10rt_lgl_nplaces(); }

x10rt_place x10rt_nhosts (void)
{ return x10rt_lgl_nhosts(); }

x10rt_place x10rt_ndead (void)
{ return x10rt_lgl_ndead(); }

bool x10rt_is_place_dead (x10rt_place p)
{ return x10rt_lgl_is_place_dead(p); }

x10rt_error x10rt_get_dead (x10rt_place *dead_places, x10rt_place len)
{ return x10rt_lgl_get_dead(dead_places, len); }

x10rt_place x10rt_here (void)
{ return x10rt_lgl_here(); }

bool x10rt_is_host (x10rt_place place)
{ return x10rt_lgl_type(place) == X10RT_LGL_HOST; }

bool x10rt_is_cuda (x10rt_place place)
{ return x10rt_lgl_type(place) == X10RT_LGL_CUDA; }

x10rt_place x10rt_parent (x10rt_place place)
{ return x10rt_lgl_parent(place); }

x10rt_place x10rt_nchildren (x10rt_place place)
{ return x10rt_lgl_nchildren(place); }

x10rt_place x10rt_child (x10rt_place host, x10rt_place index)
{ return x10rt_lgl_child(host, index); }

x10rt_place x10rt_child_index (x10rt_place child)
{ return x10rt_lgl_child_index(child); }


static uint32_t print_headers = getenv("X10RT_PRINT_MSG_HEADERS") != NULL
                              ? (uint32_t)strtoull(getenv("X10RT_PRINT_MSG_HEADERS"),NULL,10)
                              : 0xFFFFFFFF;
void x10rt_send_msg (x10rt_msg_params *p)
{
    if (p->len > print_headers) {
        ::fprintf(stderr,"p%llu --%llu--> p%llu (%llu bytes)\n",
                  (unsigned long long)x10rt_lgl_here(),
                  (unsigned long long)p->type,
                  (unsigned long long)p->dest_place,
                  (unsigned long long)p->len);
    }
    return x10rt_lgl_send_msg(p);
}


void x10rt_send_get (x10rt_msg_params *p, void *srcAddr, void*dstAddr, x10rt_copy_sz len)
{ return x10rt_lgl_send_get(p, srcAddr, dstAddr, len); }

void x10rt_send_put (x10rt_msg_params *p, void *srcAddr, void *dstAddr, x10rt_copy_sz len)
{ return x10rt_lgl_send_put(p, srcAddr, dstAddr, len); }

void x10rt_remote_alloc (x10rt_place place, x10rt_remote_ptr sz,
                         x10rt_completion_handler3 *ch, void *arg)
{ x10rt_lgl_remote_alloc(place, sz, ch, arg); }
void x10rt_remote_free (x10rt_place place, x10rt_remote_ptr ptr)
{ x10rt_lgl_remote_free(place, ptr); }


void x10rt_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                      x10rt_op_type type, unsigned long long value)
{ x10rt_lgl_remote_op(place, remote_addr, type, value); }

void x10rt_remote_ops (x10rt_remote_op_params *opv, size_t opc)
{ x10rt_lgl_remote_ops(opv, opc); }

void x10rt_register_mem (void *ptr, size_t len)
{ x10rt_lgl_register_mem(ptr, len); }

void x10rt_blocks_threads (x10rt_place d, x10rt_msg_type type, int dyn_shm,
                           int *blocks, int *threads, const int *cfg)
{ x10rt_lgl_blocks_threads (d, type, dyn_shm, blocks, threads, cfg); }

void x10rt_device_sync (x10rt_place d)
{ x10rt_lgl_device_sync (d); }

x10rt_error x10rt_probe (void)
{ return x10rt_lgl_probe(); }


bool x10rt_blocking_probe_support (void)
{ return x10rt_lgl_blocking_probe_support(); }

x10rt_error x10rt_blocking_probe (void)
{ return x10rt_lgl_blocking_probe(); }

x10rt_error x10rt_unblock_probe (void)
{ return x10rt_lgl_unblock_probe(); }

void x10rt_finalize (void)
{ x10rt_lgl_finalize(); }

x10rt_coll_type x10rt_coll_support () {
	return x10rt_lgl_coll_support();
}

void x10rt_team_new (x10rt_place placec, x10rt_place *placev,
                     x10rt_completion_handler2 *ch, void *arg)
{
    x10rt_lgl_team_new(placec, placev, ch, arg);
}

void x10rt_team_del (x10rt_team team, x10rt_place role,
                     x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_team_del(team, role, ch, arg);
}

x10rt_place x10rt_team_sz (x10rt_team team)
{
    return x10rt_lgl_team_sz(team);
}

void x10rt_team_split (x10rt_team parent, x10rt_place parent_role,
                       x10rt_place color, x10rt_place new_role,
                       x10rt_completion_handler2 *ch, void *arg)
{
    x10rt_lgl_team_split(parent, parent_role, color, new_role, ch, arg);
}

void x10rt_barrier (x10rt_team team, x10rt_place role,
                    x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_barrier(team, role, ch, arg);
}

void x10rt_bcast (x10rt_team team, x10rt_place role,
                  x10rt_place root, const void *sbuf, void *dbuf,
                  size_t el, size_t count,
                  x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_bcast(team, role, root, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_scatter (x10rt_team team, x10rt_place role,
                    x10rt_place root, const void *sbuf, void *dbuf,
                    size_t el, size_t count,
                    x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_scatter(team, role, root, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_alltoall (x10rt_team team, x10rt_place role,
                     const void *sbuf, void *dbuf,
                     size_t el, size_t count,
                     x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_alltoall(team, role, sbuf, dbuf, el, count, ch, arg);
}

void x10rt_reduce (x10rt_team team, x10rt_place role,
                    x10rt_place root, const void *sbuf, void *dbuf,
                    x10rt_red_op_type op, 
                    x10rt_red_type dtype,
                    size_t count,
                    x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_reduce(team, role, root, sbuf, dbuf, op, dtype, count, ch, arg);
}

void x10rt_allreduce (x10rt_team team, x10rt_place role,
                      const void *sbuf, void *dbuf,
                      x10rt_red_op_type op, 
                      x10rt_red_type dtype,
                      size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    x10rt_lgl_allreduce(team, role, sbuf, dbuf, op, dtype, count, ch, arg);
}


void x10rt_one_setter (void *arg)
{ *((int*)arg) = 1; }

void x10rt_team_setter (x10rt_team v, void *arg)
{ *((x10rt_team*)arg) = v; }


void x10rt_remote_ptr_setter (x10rt_remote_ptr v, void *arg)
{ *((x10rt_remote_ptr*)arg) = v; }

