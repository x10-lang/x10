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
#include <unistd.h> // sleep()
#include <errno.h> // for the strerror function
#include <sched.h> // for sched_yield()
#include <x10rt_net.h>
#include <pami.h>

//#define DEBUG 1

enum MSGTYPE {STANDARD=1, PUT, GET}; // PAMI doesn't send messages with type=0... it just silently eats them.
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

struct x10rt_pami_header_data
{
	x10rt_msg_params x10msg;
    uint32_t data_len;
    void* data_ptr;
};

struct x10PAMIState
{
	uint32_t numPlaces;
	uint32_t myPlaceId;
	x10rtCallback* callBackTable;
	x10rt_msg_type callBackTableSize;
	pami_client_t client; // the PAMI client instance used for this place
	// TODO associate a context with each worker thread
	pami_context_t context[1]; // PAMI context associated with the client (currently only 1 context is used)
	pami_send_hint_t standardHints; // hints that apply to this session
	volatile unsigned recv_active;
} state;


/*
 * The error method prints out serious errors, then immediately exits
 */
void error(const char* msg, ...)
{
	char buffer[1200];
	va_list ap;
	va_start(ap, msg);
	vsnprintf(buffer, sizeof(buffer), msg, ap);
	va_end(ap);
	strcat(buffer, "  ");
	int blen = strlen(buffer);
	PAMI_Error_text(buffer+blen, 1199-blen);
	fprintf(stderr, "X10 PAMI error: %s\n", buffer);
	if (errno != 0)
		fprintf(stderr, "X10 PAMI errno: %s\n", strerror(errno));

	fflush(stderr);
	exit(EXIT_FAILURE);
}

/*
 * This small method is used to hold-up some calls until the data transmission is complete, by simply decrementing a counter
 */
static void cookie_decrement (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	unsigned * value = (unsigned *) cookie;
	#ifdef DEBUG
		fprintf(stderr, "(%zu) decrement() cookie = %p, %d => %d\n", state.myPlaceId, cookie, *value, *value-1);
	#endif
	--*value;
}

/*
 * When a standard message is too large to fit in one transmission block, it gets broken up.
 * This method handles the final part of these messages (local_msg_dispatch handles the first part)
 */
static void std_msg_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	struct x10rt_msg_params *hdr = (struct x10rt_msg_params*) cookie;
	#ifdef DEBUG
		fprintf(stderr, "Place %lu processing delayed standard message %i, len=%u\n", state.myPlaceId, hdr->type, hdr->len);
	#endif
	handlerCallback hcb = state.callBackTable[hdr->type].handler;
	hcb(hdr);

	if (hdr->len > 0) // will always be true, but check anyway
		free(hdr->msg);
	free(hdr);
}

/*
 * This is the standard message handler.  It gets called on the receiving end for all standard messages
 */
static void local_msg_dispatch (
	    pami_context_t        context,      /**< IN: PAMI context */
	    void               * cookie,       /**< IN: dispatch cookie */
	    const void         * header_addr,  /**< IN: header address */
	    size_t               header_size,  /**< IN: header size */
	    const void         * pipe_addr,    /**< IN: address of PAMI pipe buffer */
	    size_t               pipe_size,    /**< IN: size of PAMI pipe buffer */
	    pami_endpoint_t      origin,
	    pami_recv_t         * recv)        /**< OUT: receive message structure */
{
	pami_result_t status = PAMI_ERROR;

	if (recv) // not all of the data is here yet, so we need to tell PAMI what to run when it's all here.
	{
		struct x10rt_msg_params *hdr = (struct x10rt_msg_params *)malloc(sizeof(struct x10rt_msg_params));
		hdr->dest_place = state.myPlaceId;
		hdr->len = pipe_size; // this is going to be large-ish, otherwise recv would be null
		hdr->msg = malloc(pipe_size);
		if (hdr->msg == NULL)
			error("Unable to allocate a msg_dispatch buffer of size %u", pipe_size);
		hdr->type = *((x10rt_msg_type*)header_addr);
		#ifdef DEBUG
			fprintf(stderr, "Place %lu waiting on a partially delivered message %i, len=%u\n", state.myPlaceId, hdr.type, pipe_size);
		#endif
		recv->local_fn = std_msg_complete;
		recv->cookie   = hdr;
		recv->type     = PAMI_BYTE;
		recv->addr     = hdr->msg;
		recv->offset   = 0;
		recv->data_fn  = PAMI_DATA_COPY;
		state.recv_active = 1;
	}
	else
	{	// all the data is available, and ready to process
		x10rt_msg_params mp;
		mp.dest_place = state.myPlaceId;
		mp.type = *((x10rt_msg_type*)header_addr);
		mp.len = pipe_size;
		if (mp.len > 0)
		{
			mp.msg = alloca(pipe_size);
			if (mp.msg == NULL)
				error("Unable to alloca a msg buffer of size %u", pipe_size);
			memcpy(mp.msg, pipe_addr, pipe_size);
		}
		else
			mp.msg = NULL;

		state.recv_active = 1;

		#ifdef DEBUG
			fprintf(stderr, "Place %lu processing standard message %i, len=%u\n", state.myPlaceId, mp.type, mp.len);
		#endif
		handlerCallback hcb = state.callBackTable[mp.type].handler;
		hcb(&mp);
	}
}

/*
 * This method is called at the receiving end of a PUT message, after all the data has arrived
 */
static void put_handler_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) cookie;

	notifierCallback ncb = state.callBackTable[header->x10msg.type].notifier;
	ncb(&header->x10msg, header->data_len);

	if (header->x10msg.len > 0)
		free(header->x10msg.msg);
	free(header);
	#ifdef DEBUG
		fprintf(stderr, "(%lu) processed PUT message type %i\n", state.myPlaceId, mp.type);
	#endif
}

/*
 * This method is called at the receiving end of a PUT message, before all the data has arrived.  It finds the buffers
 * that the incoming data should go into, and issues a RDMA get to pull that data in.
 */
static void local_put_dispatch (
	    pami_context_t        context,      /**< IN: PAMI context */
	    void               * cookie,       /**< IN: dispatch cookie */
	    const void         * header_addr,  /**< IN: header address */
	    size_t               header_size,  /**< IN: header size */
	    const void         * pipe_addr,    /**< IN: address of PAMI pipe buffer */
	    size_t               pipe_size,    /**< IN: size of PAMI pipe buffer */
	    pami_endpoint_t      origin,
	    pami_recv_t         * recv)        /**< OUT: receive message structure */
{
	pami_result_t status = PAMI_ERROR;

	if (recv) // not all of the data is here yet, so we need to tell PAMI what to run when it's all here.
		error("non-immediate dispatch not yet implemented");

	// else, all the data is available, and ready to process
//	#ifdef DEBUG
//		volatile size_t * value = (volatile size_t *) cookie;
//		fprintf(stderr, "(%zu) local_put_dispatch() short recv:  cookie %p = %d\n", state.myPlaceId, cookie, *value);
//	#endif

	struct x10rt_pami_header_data* localParameters = (struct x10rt_pami_header_data*)malloc(sizeof(struct x10rt_pami_header_data));
	struct x10rt_msg_params* incomingParameters = (struct x10rt_msg_params*) header_addr;
	localParameters->x10msg.dest_place = state.myPlaceId;
	localParameters->data_len = incomingParameters->len;
	localParameters->data_ptr = incomingParameters->msg;
	localParameters->x10msg.type = incomingParameters->type;
	if (pipe_size > 0)
	{
		localParameters->x10msg.msg = malloc(pipe_size);
		if (localParameters->x10msg.msg == NULL)
			error("Unable to malloc a buffer to hold incoming PUT data");
		memcpy(localParameters->x10msg.msg, pipe_addr, pipe_size); // save the message for later
	}
	else
		localParameters->x10msg.msg = NULL;
	localParameters->x10msg.len = pipe_size;

	state.recv_active = 1;

	#ifdef DEBUG
		fprintf(stderr, "Place %lu processing PUT message %i\n", state.myPlaceId, mp.type);
//		fprintf (stderr, ">> 'put' dispatch function.  cookie = %p (value: %zu), header_size = %zu, pipe_size = %zu, recv = %p\n", cookie, *value, header_size, pipe_size, recv);
//		fprintf (stderr, "   'put' dispatch function.  origin = 0x%08x, rts->bytes = %zu, rts->source = %p\n", origin, header->data_len, header->data_ptr);
	#endif

	finderCallback fcb = state.callBackTable[localParameters->x10msg.type].finder;
	void* dest = fcb(&localParameters->x10msg, localParameters->data_len); // get the pointer to the destination location
	if (dest == NULL)
		error("invalid buffer provided for a PUT");

	if (localParameters->data_len > 0) // PAMI doesn't like zero sized messages
	{
		pami_get_simple_t parameters;
		memset(&parameters, 0, sizeof (parameters));
		parameters.rma.dest    = origin;
		parameters.rma.hints   = state.standardHints;
		parameters.rma.bytes   = localParameters->data_len;
		parameters.rma.cookie  = localParameters;
		parameters.rma.done_fn = put_handler_complete;
		parameters.addr.local  = dest;
		parameters.addr.remote = localParameters->data_ptr;
		if ((status = PAMI_Get (state.context[0], &parameters)) != PAMI_SUCCESS)
			error("Error sending data for PUT response");
	}
	else
		put_handler_complete(state.context[0], localParameters, PAMI_SUCCESS);
}

/*
 * This is called at the receiving end of a GET message.  It finds the data that is requested locally, and issues
 * a RDMA put to push that data over to the requester (the end that sent the GET).
 */
static void local_get_dispatch (
	    pami_context_t        context,      /**< IN: PAMI context */
	    void               * cookie,       /**< IN: dispatch cookie */
	    const void         * header_addr,  /**< IN: header address */
	    size_t               header_size,  /**< IN: header size */
	    const void         * pipe_addr,    /**< IN: address of PAMI pipe buffer */
	    size_t               pipe_size,    /**< IN: size of PAMI pipe buffer */
	    pami_endpoint_t      origin,
	    pami_recv_t         * recv)        /**< OUT: receive message structure */
{
	pami_result_t status = PAMI_ERROR;

	if (recv) // not all of the data is here yet, so we need to tell PAMI what to run when it's all here.
		error("non-immediate dispatch not yet implemented");

	// else, all the data is available, and ready to process
//	#ifdef DEBUG
//		volatile size_t * value = (volatile size_t *) cookie;
//		fprintf(stderr, "(%zu) local_get_dispatch() short recv:  cookie %p = %d\n", state.myPlaceId, cookie, *value);
//	#endif

	x10rt_msg_params mp;
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) header_addr;
	mp.dest_place = state.myPlaceId;
	mp.type = header->x10msg.type;
	mp.msg = (void*)pipe_addr;
	mp.len = pipe_size;

	// issue a put to the originator
	#ifdef DEBUG
		fprintf(stderr, "Place %lu processing GET message %i\n", state.myPlaceId, mp.type);
//		fprintf (stderr, ">> 'send' dispatch function.  cookie = %p (_done: %zu), header_size = %zu, pipe_size = %zu, recv = %p\n", cookie, *value, header_size, pipe_size, recv);
//		fprintf (stderr, "   'send' dispatch function.  origin = 0x%08x, header->dst = %p\n", origin, header->data_ptr);
	#endif

	finderCallback fcb = state.callBackTable[mp.type].finder;
	void* src = fcb(&mp, header->data_len);
	if (src == NULL)
		error("invalid buffer provided for the source of a GET");

	state.recv_active = 1;

	if (header->data_len > 0) // PAMI doesn't like zero sized messages
	{
		volatile unsigned put_active = 1;
		pami_put_simple_t parameters;
		memset(&parameters, 0, sizeof (parameters));
		parameters.rma.dest    = origin;
		parameters.rma.hints   = state.standardHints;
		parameters.rma.bytes   = header->data_len;
		parameters.rma.cookie  = (void *)&put_active;
		parameters.rma.done_fn = cookie_decrement;
		parameters.addr.local  = src;
		parameters.addr.remote = header->data_ptr;
		if ((status = PAMI_Put (state.context[0], &parameters)) != PAMI_SUCCESS)
			error("Error sending data for GET response");
		#ifdef DEBUG
			fprintf(stderr, "Place %lu pushing out %u bytes of GET message data\n", state.myPlaceId, header->data_len);
		#endif
		while (put_active)
			PAMI_Context_advance(state.context[0], 100);
	}
}

/*
 * This method is called on the issuing end of a GET request, after the get has been fulfilled.
 */
static void get_complete_handler (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) cookie;

	#ifdef DEBUG
		fprintf(stderr, "Place %lu got GET data\n", state.myPlaceId);
	#endif

	notifierCallback ncb = state.callBackTable[header->x10msg.type].notifier;
	ncb(&header->x10msg, header->data_len);

	#ifdef DEBUG
		fprintf(stderr, "Place %lu finished GET message\n", state.myPlaceId);
	#endif
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
	pami_result_t   status = PAMI_ERROR;
	const char    *name = "X10";
	setenv("MP_MSG_API", name, 1); // workaround for a PAMI issue
	if ((status = PAMI_Client_create(name, &state.client, NULL, 0)) != PAMI_SUCCESS)
		error("Unable to initialize the PAMI client: %i\n", status);

	size_t ncontext = 1;
	if ((status = PAMI_Context_createv(state.client, NULL, 0, &state.context[0], ncontext)) != PAMI_SUCCESS)
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

	#ifdef DEBUG
		fprintf(stderr, "Hello from process %u of %u\n", state.myPlaceId, state.numPlaces); // TODO - deleteme
	#endif
	
	memset(&state.standardHints, 0, sizeof(state.standardHints));
	//state.standardHints.buffer_registered = 1;
	state.recv_active = 1;

	// set up our callback functions, which will convert PAMI messages to X10 callbacks
	pami_dispatch_callback_function fn;
	fn.p2p = local_msg_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], STANDARD, fn, (void *) &state.recv_active, state.standardHints)) != PAMI_SUCCESS)
		error("Unable to register standard dispatch handler");

	pami_dispatch_callback_function fn2;
	fn2.p2p = local_put_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], PUT, fn2, (void *) &state.recv_active, state.standardHints)) != PAMI_SUCCESS)
		error("Unable to register put dispatch handler");

	pami_dispatch_callback_function fn3;
	fn3.p2p = local_get_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], GET, fn3, (void *) &state.recv_active, state.standardHints)) != PAMI_SUCCESS)
		error("Unable to register get dispatch handler");
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

	#ifdef DEBUG
		fprintf(stderr, "Place %lu registered standard message handler %u\n", state.myPlaceId, msg_type);
	#endif
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

	#ifdef DEBUG
		fprintf(stderr, "Place %lu registered PUT message handler %u\n", state.myPlaceId, msg_type);
	#endif
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

	#ifdef DEBUG
		fprintf(stderr, "Place %lu registered GET message handler %u\n", state.myPlaceId, msg_type);
	#endif
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
void x10rt_net_send_msg (x10rt_msg_params *p)
{
	pami_endpoint_t target;
	pami_result_t   status = PAMI_ERROR;
	#ifdef DEBUG
		fprintf(stderr, "Preparing to send a message from place %lu to %lu\n", state.myPlaceId, p->dest_place);
	#endif
	if ((status = PAMI_Endpoint_create(state.client, p->dest_place, 0, &target)) != PAMI_SUCCESS)
		error("Unable to create a target endpoint for sending a message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	volatile unsigned send_active = 1;
	pami_send_t parameters;
	parameters.send.dispatch        = STANDARD;
	parameters.send.header.iov_base = &p->type;
	parameters.send.header.iov_len  = sizeof(p->type);
	parameters.send.data.iov_base   = p->msg;
	parameters.send.data.iov_len    = p->len;
	parameters.send.dest 			= target;
	parameters.send.hints			= state.standardHints;
	parameters.events.cookie        = (void *) &send_active;
	parameters.events.local_fn      = cookie_decrement;
	parameters.events.remote_fn     = NULL;

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

//	#ifdef DEBUG
//		fprintf(stderr, "(%zu) send_once() Before advance\n", state.myPlaceId);
//	#endif

	while (send_active) // send_active gets decremented by cookie_decrement
		PAMI_Context_advance(state.context[0], 100);
//	#ifdef DEBUG
//		fprintf(stderr, "(%zu) send_once() After advance\n", state.myPlaceId);
//	#endif
}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	pami_endpoint_t target;
	pami_result_t   status = PAMI_ERROR;
	#ifdef DEBUG
		fprintf(stderr, "Preparing to send a PUT message from place %lu to %lu\n", state.myPlaceId, p->dest_place);
	#endif

	if ((status = PAMI_Endpoint_create(state.client, p->dest_place, 0, &target)) != PAMI_SUCCESS)
		error("Unable to create a target endpoint for sending a PUT message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	x10rt_msg_params header; // reusing this structure, since it has the pieces we need
	header.type = p->type;
	header.len = len;
	header.msg = buf;

	volatile unsigned put_active = 1;
	pami_send_t parameters;
	parameters.send.dispatch        = PUT;
	parameters.send.header.iov_base = &header;
	parameters.send.header.iov_len  = sizeof(header);
	parameters.send.data.iov_base   = p->msg;
	parameters.send.data.iov_len    = p->len;
	parameters.send.dest 			= target;
	parameters.events.cookie        = (void *) &put_active;
	parameters.events.local_fn      = NULL;
	parameters.events.remote_fn     = cookie_decrement;

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	#ifdef DEBUG
		fprintf(stderr, "(%zu) PUT Before advance\n", state.myPlaceId);
	#endif
	while (put_active)
		PAMI_Context_advance(state.context[0], 100);
	#ifdef DEBUG
		fprintf(stderr, "(%zu) PUT After advance\n", state.myPlaceId);
	#endif
}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	// GET is implemented as a send msg, followed by a PUT
	pami_endpoint_t target;
	pami_result_t   status = PAMI_ERROR;
	#ifdef DEBUG
		fprintf(stderr, "Preparing to send a GET message from place %lu to %lu\n", state.myPlaceId, p->dest_place);
	#endif

	if ((status = PAMI_Endpoint_create(state.client, p->dest_place, 0, &target)) != PAMI_SUCCESS)
		error("Unable to create a target endpoint for sending a GET message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*)malloc(sizeof(struct x10rt_pami_header_data));
	header->data_len = len;
	header->data_ptr = buf;
	header->x10msg.type = p->type;
	header->x10msg.len = p->len;
	header->x10msg.dest_place = p->dest_place;
	if (p->len > 0)
	{
		header->x10msg.msg = malloc(p->len);
		if (header->x10msg.msg == NULL)
			error("Unable to allocate msg buffer for GET");
		memcpy(header->x10msg.msg, p->msg, p->len);
	}
	else
		header->x10msg.msg = NULL;

	pami_send_t parameters;
	parameters.send.dispatch        = GET;
	parameters.send.header.iov_base = header;
	parameters.send.header.iov_len  = sizeof(struct x10rt_pami_header_data);
	parameters.send.data.iov_base   = header->x10msg.msg;
	parameters.send.data.iov_len    = header->x10msg.len;
	parameters.send.dest 			= target;
	parameters.events.cookie        = header;
	parameters.events.local_fn      = NULL;
	parameters.events.remote_fn     = get_complete_handler; // drop to 0 after completed

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);
}

/** Handle any oustanding message from the network by calling the registered callbacks.  \see #x10rt_lgl_probe
 */
void x10rt_net_probe()
{
//	#ifdef DEBUG
//		fprintf(stderr, "Place %lu trying a probe\n", state.myPlaceId);
//	#endif
//	pami_result_t status = PAMI_ERROR;
	// TODO remove this lock when we move to endpoints, or when X10_NTHREADS=1
//	if ((status = PAMI_Context_lock(state.context[0])) != PAMI_SUCCESS)
//		error("Unable to lock context");

//	#ifdef DEBUG
//		fprintf(stderr, "Place %lu advancing context\n", state.myPlaceId);
//	#endif
/*	while (state.recv_active)
		PAMI_Context_advance (state.context[0], 100);

	state.recv_active = 1;
*/
	if (state.recv_active)
	{
		pami_result_t status = PAMI_ERROR;
		status = PAMI_Context_advance(state.context[0], 100);
		if (status == PAMI_EAGAIN)
		{
//			#ifdef DEBUG
//				fprintf(stderr, "Place %lu found nothing to do\n", state.myPlaceId);
//			#endif
//			if ((status = PAMI_Context_unlock(state.context[0])) != PAMI_SUCCESS)
//				error("Unable to unlock context");
			sched_yield();
		}
//		#ifdef DEBUG
//		else if (status == PAMI_SUCCESS)
//			fprintf(stderr, "Place %lu finished advancing a context\n", state.myPlaceId);
//		#endif
//		else if ((status = PAMI_Context_unlock(state.context[0])) != PAMI_SUCCESS)
//			error("Unable to unlock context");
	}
	else
	{
//		#ifdef DEBUG
//			fprintf(stderr, "Place %lu not advancing because receive is deactivated\n", state.myPlaceId);
//		#endif
		sched_yield();
	}
}

/** Shut down the network layer.  \see #x10rt_lgl_finalize
 */
void x10rt_net_finalize()
{
	pami_result_t status = PAMI_ERROR;

	#ifdef DEBUG
		fprintf(stderr, "Place %lu shutting down\n", state.myPlaceId);
	#endif
	if ((status = PAMI_Context_destroyv(&state.context[0], 1)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI context: %i\n", status);

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
