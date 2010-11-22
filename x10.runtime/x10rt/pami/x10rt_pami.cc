#include <cstdlib>
#include <stdio.h>
#include <x10rt_net.h>
#include <string.h>
#include <errno.h> // for the strerror function


void error(const char* message)
{
	if (errno)
		fprintf(stderr, "Fatal Error: %s: %s\n", message, strerror(errno));
	else
		fprintf(stderr, "Fatal Error: %s\n", message);
	fflush(stderr);
	abort();
}


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
void x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter){}

/** Register handlers for a plain message.
 *
 * \see #x10rt_lgl_register_msg_receiver
 *
 * \param msg_type s in x10rt_lgl_register_msg_receiver
 *
 * \param cb As in x10rt_lgl_register_msg_receiver
 */
void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb){}


/** Register handlers for a get message.
 *
 * \see #x10rt_lgl_register_get_receiver
 *
 * \param msg_type As in x10rt_lgl_register_get_receiver
 *
 * \param cb1 As in x10rt_lgl_register_get_receiver
 * \param cb2 As in x10rt_lgl_register_get_receiver
 */
void x10rt_net_register_get_receiver (x10rt_msg_type msg_type,
                                              x10rt_finder *cb1, x10rt_notifier *cb2){}


/** Register handlers for a put message.
 *
 * \see #x10rt_lgl_register_put_receiver
 *
 * \param msg_type As in x10rt_lgl_register_put_receiver
 *
 * \param cb1 As in x10rt_lgl_register_put_receiver
 * \param cb2 As in x10rt_lgl_register_put_receiver
 */
void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2){}

/** \see #x10rt_lgl_nhosts */
x10rt_place x10rt_net_nhosts ()
{
	return 1;
}

/** \see #x10rt_lgl_here */
x10rt_place x10rt_net_here ()
{
	return 0;
}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_msg (x10rt_msg_params *p){}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len){}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len){}

/** Handle any oustanding message from the network by calling the registered callbacks.  \see #x10rt_lgl_probe
 */
void x10rt_net_probe(){}

/** Shut down the network layer.  \see #x10rt_lgl_finalize
 */
void x10rt_net_finalize(){}

int x10rt_net_supports (x10rt_opt o)
{
    return 0;
}

/** A single-threaded SPMD host barrier. \deprecated
 */
void x10rt_net_internal_barrier (){}

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{
	error("x10rt_net_remote_op not implemented");
}

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{
	error("x10rt_net_register_mem not implemented");
	return NULL;
}

void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
	error("x10rt_net_team_new not implemented");
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_team_del not implemented");
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
	error("x10rt_net_team_sz not implemented");
    return 0;
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role, x10rt_place color,
		x10rt_place new_role, x10rt_completion_handler2 *ch, void *arg)
{
	error("x10rt_net_team_split not implemented");
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role, x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_barrier not implemented");
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_bcast not implemented");
}

void x10rt_net_scatter (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_scatter not implemented");
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_alltoall not implemented");
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		x10rt_red_op_type op, x10rt_red_type dtype, size_t count, x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_allreduce not implemented");
}
// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
