#include <cstdlib>

#include <x10rt_types.h>

/** \file
 *
 * Core Networking Layer.  This layer is used to implement X10 inter-host communication
 * functionality on top of a network library.  \see \ref structure
 */


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
X10RT_C void x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter);

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


/** A single-threaded SPMD host barrier. \bug This should be non-blocking rather than blocking on
 * #x10rt_net_probe.
 *
 * \see #x10rt_lgl_internal_barrier
 *
 */
X10RT_C void x10rt_net_internal_barrier (void);

/** \see #x10rt_lgl_nhosts */
X10RT_C x10rt_place x10rt_net_nhosts (void);

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
X10RT_C void x10rt_net_probe (void);

/** \see #x10rt_lgl_remote_op
 * \param place As in #x10rt_lgl_remote_op
 * \param remote_addr As in #x10rt_lgl_remote_op
 * \param type As in #x10rt_lgl_remote_op
 * \param value As in #x10rt_lgl_remote_op
 */
X10RT_C void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr remote_addr,
                                  x10rt_op_type type, unsigned long long value);

/** \see #x10rt_lgl_register_mem
 * \param ptr As in #x10rt_lgl_register_mem
 * \param len As in #x10rt_lgl_register_mem
 * \returns As in #x10rt_lgl_register_mem
 */
X10RT_C x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len);

/** Shut down the network layer.  \see #x10rt_lgl_finalize
 */
X10RT_C void x10rt_net_finalize (void); 

/** Return whether the x10rt_net implementation supports a particular feature.
 */
X10RT_C int x10rt_net_supports (x10rt_opt o);

// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
