/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <errno.h> // for the strerror function
#include <x10rt_net.h>
#include <pami.h>

//mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

struct x10rtCallback
{
	handlerCallback handler;
	finderCallback finder;
	notifierCallback notifier;
};

struct x10PAMIState
{
	x10rt_place numPlaces;
	x10rt_place myPlaceId;
	x10rtCallback* callBackTable;
	x10rt_msg_type callBackTableSize;
	pami_client_t client; // the PAMI client instance used for this place
} state;


void error(const char* msg, ...)
{
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, sizeof(buffer), msg, ap);
	va_end(ap);
	fprintf(stderr, "%s\n", buffer);
	if (errno != 0)
		fprintf(stderr, "%s\n", strerror(errno));
	exit(1);
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
void x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
	pami_context_t  context;
	pami_result_t   status = PAMI_ERROR;
	const char    *name = "X10";
	setenv("MP_MSG_API", name, 1); // workaround for a PAMI issue
	if ((status = PAMI_Client_create(name, &state.client, NULL, 0)) != PAMI_SUCCESS)
		error("Unable to initialize the PAMI client: %i\n", status);

	if ((status = PAMI_Context_createv(state.client, NULL, 0, &context, 1)) != PAMI_SUCCESS)
		error("Unable to initialize the PAMI context: %i\n", status);

	pami_configuration_t configuration;
	configuration.name = PAMI_CLIENT_TASK_ID;
	if ((status = PAMI_Client_query(state.client, &configuration, 1)) != PAMI_SUCCESS)
		error("Unable to query the PAMI_CLIENT_TASK_ID: %i\n", status);
	state.myPlaceId = configuration.value.intval;

	configuration.name = PAMI_CLIENT_NUM_TASKS;
	if ((status = PAMI_Client_query(state.client, &configuration, 1)) != PAMI_SUCCESS)
		error("Unable to query PAMI_CLIENT_NUM_TASKS: %i\n", status);
	state.numPlaces = configuration.value.intval;


	printf("Hello from process %u of %u\n", state.myPlaceId, state.numPlaces); // TODO - deleteme


	if ((status = PAMI_Context_destroyv(&context, 1)) != PAMI_SUCCESS)
		error("Unable to destroy the initial PAMI context: %i\n", status);
}


void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *callback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10rtCallback*)realloc(state.callBackTable, sizeof(struct x10rtCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
		state.callBackTableSize = msg_type+1;
	}

	state.callBackTable[msg_type].handler = callback;
	state.callBackTable[msg_type].finder = NULL;
	state.callBackTable[msg_type].notifier = NULL;
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *finderCallback, x10rt_notifier *notifierCallback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10rtCallback*)realloc(state.callBackTable, sizeof(struct x10rtCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = finderCallback;
	state.callBackTable[msg_type].notifier = notifierCallback;
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type, x10rt_finder *finderCallback, x10rt_notifier *notifierCallback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10rtCallback*)realloc(state.callBackTable, sizeof(struct x10rtCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = finderCallback;
	state.callBackTable[msg_type].notifier = notifierCallback;
}

x10rt_place x10rt_net_nhosts (void)
{
	// return the number of places that exist.
	return state.numPlaces;
}

x10rt_place x10rt_net_here (void)
{
	// return which place this is
	return state.myPlaceId;
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
void x10rt_net_finalize()
{
	pami_result_t status = PAMI_ERROR;
	if ((status = PAMI_Client_destroy(&state.client)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI client: %i\n", status);
}

int x10rt_net_supports (x10rt_opt o)
{
    return 0;
}


void x10rt_net_internal_barrier (){} // DEPRECATED

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
