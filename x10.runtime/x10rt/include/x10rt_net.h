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

#ifndef X10RT_NET_H
#define X10RT_NET_H

#include <cstdlib>

#include <x10rt_types.h>

/** \file
 *
 * Core Networking Layer.  This layer is used to implement X10 inter-host communication
 * functionality on top of a network library.  \see \ref structure
 */

/** Partially initialize the X10RT API logical layer.
 *
 * \see #x10rt_lgl_preinit
 *
 * \param connInfoBuffer As in x10rt_lgl_preinit.
 *
 * \param connInfoBufferSize As in x10rt_lgl_preinit.
 */
X10RT_C x10rt_error x10rt_net_preinit(char* connInfoBuffer, int connInfoBufferSize);

/** Initialize the X10RT API logical layer.  
 *
 * \see #x10rt_lgl_init
 *
 * \param argc As in x10rt_lgl_init.
 *
 * \param argv As in x10rt_lgl_init.
 *
 * \param counter As in x10rt_lgl_init.
 */
X10RT_C x10rt_error x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter);

/** Get a detailed user-readable error about the fatal error that has rendered X10RT inoperable. 
 * \returns Text describing the error, or NULL if no error has occured.
 */
X10RT_C const char *x10rt_net_error_msg (void);

/** Register handlers for a plain message.
 *
 * \see #x10rt_lgl_register_msg_receiver
 *
 * \param msg_type s in x10rt_lgl_register_msg_receiver
 *
 * \param cb As in x10rt_lgl_register_msg_receiver
 */
X10RT_C void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb);


/** Register handlers for a get message.
 *
 * \see #x10rt_lgl_register_get_receiver
 *
 * \param msg_type As in x10rt_lgl_register_get_receiver
 *
 * \param cb1 As in x10rt_lgl_register_get_receiver
 * \param cb2 As in x10rt_lgl_register_get_receiver
 */
X10RT_C void x10rt_net_register_get_receiver (x10rt_msg_type msg_type,
                                              x10rt_finder *cb1, x10rt_notifier *cb2);


/** Register handlers for a put message.
 *
 * \see #x10rt_lgl_register_put_receiver
 *
 * \param msg_type As in x10rt_lgl_register_put_receiver
 *
 * \param cb1 As in x10rt_lgl_register_put_receiver
 * \param cb2 As in x10rt_lgl_register_put_receiver
 */
X10RT_C void x10rt_net_register_put_receiver (x10rt_msg_type msg_type,
                                              x10rt_finder *cb1, x10rt_notifier *cb2);


/** A single-threaded SPMD host barrier. \deprecated
 */
X10RT_C void x10rt_net_internal_barrier (void);

/** \see #x10rt_lgl_nhosts */
X10RT_C x10rt_place x10rt_net_nhosts (void);

/** \see #x10rt_ndead */
X10RT_C x10rt_place x10rt_net_ndead (void);

/** \see #x10rt_is_place_dead */
X10RT_C bool x10rt_net_is_place_dead (x10rt_place p);

/** \see #x10rt_get_dead */
X10RT_C x10rt_error x10rt_net_get_dead (x10rt_place *dead_places, x10rt_place len);

/** \see #x10rt_lgl_here */
X10RT_C x10rt_place x10rt_net_here (void);

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 */
X10RT_C void x10rt_net_send_msg (x10rt_msg_params *p);

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
X10RT_C void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len);

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
X10RT_C void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len);

/** Handle any oustanding message from the network by calling the registered callbacks.  \see #x10rt_lgl_probe
 */
X10RT_C x10rt_error x10rt_net_probe (void);

/**
 * Check to see if a call to blocking_probe has been implemented, or if it's just a wrapper for probe
 * Returns true if blocking_probe is real, or false if it will always return immediately
 */
X10RT_C bool x10rt_net_blocking_probe_support (void);

/** Handle any oustanding message from the network by calling the registered callbacks, blocking if nothing is available.
 * \see #x10rt_lgl_probe
 */
X10RT_C x10rt_error x10rt_net_blocking_probe (void);

/** Unblock anything stuck in blocking_probe  \see #x10rt_unblock_probe
 */
X10RT_C x10rt_error x10rt_net_unblock_probe (void);

/** \see #x10rt_lgl_remote_op
 * \param place As in #x10rt_lgl_remote_op
 * \param remote_addr As in #x10rt_lgl_remote_op
 * \param type As in #x10rt_lgl_remote_op
 * \param value As in #x10rt_lgl_remote_op
 */
X10RT_C void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                                  x10rt_op_type type, unsigned long long value);

/** \see #x10rt_lgl_remote_ops
 * \param ops As in #x10rt_lgl_remote_ops
 * \param num_ops As in #x10rt_lgl_remote_ops
 */
X10RT_C void x10rt_net_remote_ops (x10rt_remote_op_params *ops, size_t num_ops);


/** \see #x10rt_lgl_register_mem
 * \param ptr As in #x10rt_lgl_register_mem
 * \param len As in #x10rt_lgl_register_mem
 */
X10RT_C void x10rt_net_register_mem (void *ptr, size_t len);

/** Shut down the network layer.  \see #x10rt_lgl_finalize
 */
X10RT_C void x10rt_net_finalize (void); 

/** Return what level of collectives the network transport supports
 */
X10RT_C x10rt_coll_type x10rt_net_coll_support ();

/** Return whether the x10rt_net implementation supports remote operations.
 */
X10RT_C bool x10rt_net_remoteop_support ();

/** \see #x10rt_lgl_team_new
 * \param placec As in #x10rt_lgl_team_new
 * \param placev As in #x10rt_lgl_team_new
 * \param ch As in #x10rt_lgl_team_new
 * \param arg As in #x10rt_lgl_team_new
 */
X10RT_C void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                                 x10rt_completion_handler2 *ch, void *arg);

/** \see #x10rt_lgl_team_del
 * \param team As in #x10rt_lgl_team_del
 * \param role As in #x10rt_lgl_team_del
 * \param ch As in #x10rt_lgl_team_del
 * \param arg As in #x10rt_lgl_team_del
 */
X10RT_C void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                                 x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_team_sz
 * \param team As in #x10rt_lgl_team_sz
 * \returns As in #x10rt_lgl_team_sz
 */
X10RT_C x10rt_place x10rt_net_team_sz (x10rt_team team);

/** \see #x10rt_lgl_team_split
 * \param parent As in #x10rt_lgl_team_split
 * \param parent_role As in #x10rt_lgl_team_split
 * \param color As in #x10rt_lgl_team_split
 * \param new_role As in #x10rt_lgl_team_split
 * \param ch As in #x10rt_lgl_team_split
 * \param arg As in #x10rt_lgl_team_split
 */
X10RT_C void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role,
                                   x10rt_place color, x10rt_place new_role,
                                   x10rt_completion_handler2 *ch, void *arg);

/** \see #x10rt_lgl_barrier
 * \param team As in #x10rt_lgl_barrier
 * \param role As in #x10rt_lgl_barrier
 * \param ch As in #x10rt_lgl_barrier
 * \param arg As in #x10rt_lgl_barrier
 */
X10RT_C void x10rt_net_barrier (x10rt_team team, x10rt_place role,
                                x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_bcast
 * \param team As in #x10rt_lgl_bcast
 * \param role As in #x10rt_lgl_bcast
 * \param root As in #x10rt_lgl_bcast
 * \param sbuf As in #x10rt_lgl_bcast
 * \param dbuf As in #x10rt_lgl_bcast
 * \param el As in #x10rt_lgl_bcast
 * \param count As in #x10rt_lgl_bcast
 * \param ch As in #x10rt_lgl_bcast
 * \param arg As in #x10rt_lgl_bcast
 */
X10RT_C void x10rt_net_bcast (x10rt_team team, x10rt_place role,
                              x10rt_place root, const void *sbuf, void *dbuf,
                              size_t el, size_t count,
                              x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_scatter
 * \param team As in #x10rt_lgl_scatter
 * \param role As in #x10rt_lgl_scatter
 * \param root As in #x10rt_lgl_scatter
 * \param sbuf As in #x10rt_lgl_scatter
 * \param dbuf As in #x10rt_lgl_scatter
 * \param el As in #x10rt_lgl_scatter
 * \param count As in #x10rt_lgl_scatter
 * \param ch As in #x10rt_lgl_scatter
 * \param arg As in #x10rt_lgl_scatter
 */
X10RT_C void x10rt_net_scatter (x10rt_team team, x10rt_place role,
                                x10rt_place root, const void *sbuf, void *dbuf,
                                size_t el, size_t count,
                                x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_alltoall
 * \param team As in #x10rt_lgl_alltoall
 * \param role As in #x10rt_lgl_alltoall
 * \param sbuf As in #x10rt_lgl_alltoall
 * \param dbuf As in #x10rt_lgl_alltoall
 * \param el As in #x10rt_lgl_alltoall
 * \param count As in #x10rt_lgl_alltoall
 * \param ch As in #x10rt_lgl_alltoall
 * \param arg As in #x10rt_lgl_alltoall
 */
X10RT_C void x10rt_net_alltoall (x10rt_team team, x10rt_place role,
                                 const void *sbuf, void *dbuf,
                                 size_t el, size_t count,
                                 x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_reduce
 * \param team As in #x10rt_lgl_reduce
 * \param role As in #x10rt_lgl_reduce
 * \param root As in #x10rt_lgl_reduce
 * \param sbuf As in #x10rt_lgl_reduce
 * \param dbuf As in #x10rt_lgl_reduce
 * \param el As in #x10rt_lgl_reduce
 * \param count As in #x10rt_lgl_reduce
 * \param ch As in #x10rt_lgl_reduce
 * \param arg As in #x10rt_lgl_reduce
 */
X10RT_C void x10rt_net_reduce (x10rt_team team, x10rt_place role,
                                x10rt_place root, const void *sbuf, void *dbuf,
                                x10rt_red_op_type op,
                                x10rt_red_type dtype,
                                size_t count,
                                x10rt_completion_handler *ch, void *arg);

/** \see #x10rt_lgl_allreduce
 * \param team As in #x10rt_lgl_allreduce
 * \param role As in #x10rt_lgl_allreduce
 * \param sbuf As in #x10rt_lgl_allreduce
 * \param dbuf As in #x10rt_lgl_allreduce
 * \param op As in #x10rt_lgl_allreduce
 * \param dtype As in #x10rt_lgl_allreduce
 * \param count As in #x10rt_lgl_allreduce
 * \param ch As in #x10rt_lgl_allreduce
 * \param arg As in #x10rt_lgl_allreduce
 */
X10RT_C void x10rt_net_allreduce (x10rt_team team, x10rt_place role,
                                  const void *sbuf, void *dbuf,
                                  x10rt_red_op_type op,
                                  x10rt_red_type dtype,
                                  size_t count,
                                  x10rt_completion_handler *ch, void *arg);

/** Counters exposed to the backend for direct (i.e. fast) manipulation.
 */
extern x10rt_stats x10rt_lgl_stats;

#endif

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
