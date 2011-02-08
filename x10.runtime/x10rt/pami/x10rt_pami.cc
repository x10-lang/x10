/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010-2011.
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

enum MSGTYPE {STANDARD=1, PUT, GET, GET_COMPLETE}; // PAMI doesn't send messages with type=0... it just silently eats them.
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
    void* callbackPtr; // stores the header address for GET_COMPLETE
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
			fprintf(stderr, "Place %lu waiting on a partially delivered message %i, len=%u\n", state.myPlaceId, hdr->type, pipe_size);
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
 * This method is called at the receiving end of a PUT message, after all the data has arrived.
 * It sends a PUT_COMPLETE to the other end (if data was copied), and calls the x10 notifier
 */
static void put_handler_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("put_handler_complete discovered a communication error");

	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) cookie;
	#ifdef DEBUG
		fprintf(stderr, "(%lu) issuing put notifier callback, type=%i, msglen=%u, datalen=%u\n", state.myPlaceId, header->x10msg.type, header->x10msg.len, header->data_len);
	#endif
	notifierCallback ncb = state.callBackTable[header->x10msg.type].notifier;
	ncb(&header->x10msg, header->data_len);

	if (header->x10msg.len > 0)
		free(header->x10msg.msg);
	free(header);
}

/*
 * This method is called at the receiving end of a PUT message, before all the data has arrived.  It finds the buffers
 * that the incoming data should go into, and issues a RDMA get to pull that data in.  It registers put_handler_complete
 * to run after the data has been transmitted.
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
	struct x10rt_pami_header_data* localParameters = (struct x10rt_pami_header_data*)malloc(sizeof(struct x10rt_pami_header_data));
	struct x10rt_pami_header_data* incomingParameters = (struct x10rt_pami_header_data*) header_addr;
	localParameters->x10msg.dest_place = state.myPlaceId;
	localParameters->data_len = incomingParameters->data_len;
	localParameters->data_ptr = incomingParameters->data_ptr;
	localParameters->x10msg.type = incomingParameters->x10msg.type;
	localParameters->x10msg.len = pipe_size;
	if (pipe_size > 0)
	{
		localParameters->x10msg.msg = malloc(pipe_size);
		if (localParameters->x10msg.msg == NULL)
			error("Unable to malloc a buffer to hold incoming PUT data");
		memcpy(localParameters->x10msg.msg, pipe_addr, pipe_size); // save the message for later
	}
	else
		localParameters->x10msg.msg = NULL;

	state.recv_active = 1;

	#ifdef DEBUG
		fprintf(stderr, "Place %lu processing PUT message %i from %u, msglen=%u, len=%u, remote buf=%p, remote cookie=%p\n", state.myPlaceId, localParameters->x10msg.type, origin, localParameters->x10msg.len, localParameters->data_len, incomingParameters->data_ptr, incomingParameters->x10msg.msg);
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
		parameters.rma.bytes   = localParameters->data_len;
		parameters.rma.cookie  = localParameters;
		parameters.rma.done_fn = put_handler_complete;
		parameters.addr.local  = dest;
		parameters.addr.remote = localParameters->data_ptr;
		localParameters->data_ptr = incomingParameters->x10msg.msg; // the cookie from the other end
		localParameters->x10msg.dest_place = origin;
		if ((status = PAMI_Get (state.context[0], &parameters)) != PAMI_SUCCESS)
			error("Error sending data for PUT response");
	}
	else
		put_handler_complete(state.context[0], localParameters, PAMI_SUCCESS);
}

/*
 * This method is called at the receiving end of a GET message, after all the data has been sent.
 * It sends a GET_COMPLETE to the originator
 */
static void get_handler_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	x10rt_msg_params* header = (x10rt_msg_params*) cookie;

	#ifdef DEBUG
		fprintf(stderr, "(%lu) running get_handler_complete, dest=%u type=%i, remote cookie=%p\n", state.myPlaceId, header->dest_place, header->type, header->msg);
	#endif

	// send a GET_COMPLETE to the originator
	pami_result_t   status = PAMI_ERROR;

	volatile unsigned send_active = 1;
	pami_send_t parameters;
	parameters.send.dispatch        = GET_COMPLETE;
	parameters.send.header.iov_base = &header->msg; // sending the origin pointer back
	parameters.send.header.iov_len  = sizeof(void *);
	parameters.send.data.iov_base   = NULL;
	parameters.send.data.iov_len    = 0;
	parameters.send.dest 			= header->dest_place;
	memset(&parameters.send.hints, 0, sizeof(pami_send_hint_t));
	parameters.events.cookie        = (void *) &send_active;
	parameters.events.local_fn      = cookie_decrement;
	parameters.events.remote_fn     = NULL;

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a GET_COMPLETE message from %u to %u: %i\n", state.myPlaceId, header->dest_place, status);

	#ifdef DEBUG
		fprintf(stderr, "(%zu) get_handler_complete before advance\n", state.myPlaceId);
	#endif
	// hold up this thread until the message buffers have been copied out by PAMI
	while (send_active)
		PAMI_Context_advance(state.context[0], 100);
	#ifdef DEBUG
		fprintf(stderr, "(%zu) get_handler_complete after advance\n", state.myPlaceId);
	#endif
	free(header);
}

/*
 * This is called at the receiving end of a GET message.  It finds the data that is requested locally, and issues
 * a RDMA put to push that data over to the requester (the end that sent the GET).  It then sends a GET_COMPLETE to
 * the originator
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
	x10rt_msg_params* localParameters = (x10rt_msg_params*) malloc(sizeof(x10rt_msg_params));
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) header_addr;
	localParameters->dest_place = state.myPlaceId;
	localParameters->type = header->x10msg.type;
	localParameters->msg = (void*)pipe_addr;
	localParameters->len = pipe_size;

	// issue a put to the originator
	#ifdef DEBUG
		fprintf(stderr, "Place %lu processing GET message %i, datalen=%u, remote cookie=%p\n", state.myPlaceId, header->x10msg.type, header->data_len, header->x10msg.msg);
	#endif

	finderCallback fcb = state.callBackTable[localParameters->type].finder;
	void* src = fcb(localParameters, header->data_len);
	if (src == NULL)
		error("invalid buffer provided for the source of a GET");

	state.recv_active = 1;

	localParameters->dest_place = origin;
	localParameters->msg = header->callbackPtr; // cookie for the other side

	if (header->data_len > 0) // PAMI doesn't like it if we try to RDMA zero sized messages
	{
		pami_put_simple_t parameters;
		memset(&parameters, 0, sizeof (parameters));
		parameters.rma.dest    = origin;
		parameters.rma.bytes   = header->data_len;
		parameters.rma.cookie  = localParameters;
		parameters.rma.done_fn = get_handler_complete;
		parameters.addr.local  = src;
		parameters.addr.remote = header->data_ptr;
		if ((status = PAMI_Put (state.context[0], &parameters)) != PAMI_SUCCESS)
			error("Error sending data for GET response");
		#ifdef DEBUG
			fprintf(stderr, "Place %lu pushing out %u bytes of GET message data\n", state.myPlaceId, header->data_len);
		#endif
	}
	else
		get_handler_complete(context, localParameters, PAMI_SUCCESS);
}

/*
 * This method runs at the sending end of a GET, after the data has been transferred.  It runs the notifier
 */
static void get_complete_dispatch (
	    pami_context_t        context,      /**< IN: PAMI context */
	    void               * cookie,       /**< IN: dispatch cookie */
	    const void         * header_addr,  /**< IN: header address */
	    size_t               header_size,  /**< IN: header size */
	    const void         * pipe_addr,    /**< IN: address of PAMI pipe buffer */
	    size_t               pipe_size,    /**< IN: size of PAMI pipe buffer */
	    pami_endpoint_t      origin,
	    pami_recv_t         * recv)        /**< OUT: receive message structure */
{
	struct x10rt_pami_header_data* header = *(struct x10rt_pami_header_data**)header_addr;

	#ifdef DEBUG
		fprintf(stderr, "Place %lu got GET_COMPLETE from %u, header=%p\n", state.myPlaceId, origin, header);
		fprintf(stderr, "     type=%i, datalen=%u. Calling notifier\n", header->x10msg.type, header->data_len);
	#endif

	notifierCallback ncb = state.callBackTable[header->x10msg.type].notifier;
	ncb(&header->x10msg, header->data_len);

	#ifdef DEBUG
		fprintf(stderr, "Place %lu finished GET message\n", state.myPlaceId);
	#endif
	if (header->x10msg.len > 0)
		free(header->x10msg.msg);
	free(header);
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
	
	pami_send_hint_t hints;
	memset(&hints, 0, sizeof(pami_send_hint_t));
	state.recv_active = 1;

	// set up our callback functions, which will convert PAMI messages to X10 callbacks
	pami_dispatch_callback_function fn;
	fn.p2p = local_msg_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], STANDARD, fn, (void *) &state.recv_active, hints)) != PAMI_SUCCESS)
		error("Unable to register standard dispatch handler");

	pami_dispatch_callback_function fn2;
	fn2.p2p = local_put_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], PUT, fn2, (void *) &state.recv_active, hints)) != PAMI_SUCCESS)
		error("Unable to register put dispatch handler");

	pami_dispatch_callback_function fn3;
	fn3.p2p = local_get_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], GET, fn3, (void *) &state.recv_active, hints)) != PAMI_SUCCESS)
		error("Unable to register get dispatch handler");

	pami_dispatch_callback_function fn4;
	fn4.p2p = get_complete_dispatch;
	if ((status = PAMI_Dispatch_set(state.context[0], GET_COMPLETE, fn4, (void *) &state.recv_active, hints)) != PAMI_SUCCESS)
		error("Unable to register get_complete_dispatch handler");
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
	memset(&parameters.send.hints, 0, sizeof(pami_send_hint_t));
	//parameters.send.hints.buffer_registered = 1;
	parameters.events.cookie        = (void *) &send_active;
	parameters.events.local_fn      = cookie_decrement;
	parameters.events.remote_fn     = NULL;

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	#ifdef DEBUG
		fprintf(stderr, "(%zu) send_msg Before advance\n", state.myPlaceId);
	#endif

	// hold up this thread until the message buffers have been copied out by PAMI
	while (send_active)
		PAMI_Context_advance(state.context[0], 100);
	#ifdef DEBUG
		fprintf(stderr, "(%zu) send_msg After advance\n", state.myPlaceId);
	#endif
}

/** \see #x10rt_lgl_send_msg
 * Important: This method returns control back to the caller after the message p has been
 * transmitted out, but BEFORE the data in buf has been transmitted.  Therefore the caller
 * can not delete or modify buf at return time.  It must do this later, through some other
 * callback.
 */
void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	pami_endpoint_t target;
	pami_result_t   status = PAMI_ERROR;
	if ((status = PAMI_Endpoint_create(state.client, p->dest_place, 0, &target)) != PAMI_SUCCESS)
		error("Unable to create a target endpoint for sending a PUT message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	struct x10rt_pami_header_data header;
	header.x10msg.type = p->type;
	header.x10msg.dest_place = p->dest_place;
	header.x10msg.len = p->len;
	header.data_len = len;
	header.data_ptr = buf;

	volatile unsigned put_active = 1;
	pami_send_t parameters;
	parameters.send.dispatch        = PUT;
	parameters.send.header.iov_base = &header;
	parameters.send.header.iov_len  = sizeof(struct x10rt_pami_header_data);
	parameters.send.data.iov_base   = p->msg;
	parameters.send.data.iov_len    = p->len;
	parameters.send.dest 			= target;
	memset(&parameters.send.hints, 0, sizeof(pami_send_hint_t));
	parameters.events.cookie		= (void*)&put_active;
	parameters.events.local_fn		= cookie_decrement;
	parameters.events.remote_fn     = NULL;

	#ifdef DEBUG
		fprintf(stderr, "Preparing to send a PUT message from place %lu to %lu, type=%i, msglen=%u, len=%u, buf=%p\n", state.myPlaceId, p->dest_place, p->type, p->len, len, buf);
	#endif

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a PUT message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	#ifdef DEBUG
		fprintf(stderr, "(%zu) PUT Before advance\n", state.myPlaceId);
	#endif
	// hold up until the message and data has been copied out
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

	if ((status = PAMI_Endpoint_create(state.client, p->dest_place, 0, &target)) != PAMI_SUCCESS)
		error("Unable to create a target endpoint for sending a GET message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*)malloc(sizeof(struct x10rt_pami_header_data));
	header->data_len = len;
	header->data_ptr = buf;
	header->x10msg.type = p->type;
	header->x10msg.dest_place = p->dest_place;
	header->x10msg.len = p->len;
	// save the msg data for the notifier
	if (p->len > 0)
	{
		header->x10msg.msg = malloc(p->len);
		if (header->x10msg.msg == NULL)
			error("Unable to malloc msg space for a GET");
		memcpy(header->x10msg.msg, p->msg, p->len);
	}
	else
		header->x10msg.msg = NULL;
	header->callbackPtr = header; // senging this along with the data

	#ifdef DEBUG
		fprintf(stderr, "Preparing to send a GET message from place %lu to %lu, len=%u, buf=%p, cookie=%p\n", state.myPlaceId, p->dest_place, len, buf, header);
	#endif

	volatile unsigned get_active = 1;
	pami_send_t parameters;
	parameters.send.dispatch        = GET;
	parameters.send.header.iov_base = header;
	parameters.send.header.iov_len  = sizeof(struct x10rt_pami_header_data);
	parameters.send.data.iov_base   = p->msg;
	parameters.send.data.iov_len    = p->len;
	parameters.send.dest 			= target;
	memset(&parameters.send.hints, 0, sizeof(pami_send_hint_t));
	parameters.events.cookie        = (void*)&get_active;
	parameters.events.local_fn      = cookie_decrement;
	parameters.events.remote_fn     = NULL;

	if ((status = PAMI_Send(state.context[0], &parameters)) != PAMI_SUCCESS)
		error("Unable to send a GET message from %u to %u: %i\n", state.myPlaceId, p->dest_place, status);

	// hold up until the message has been copied out
	while (get_active)
		PAMI_Context_advance(state.context[0], 100);

	#ifdef DEBUG
		fprintf(stderr, "GET message sent from place %lu to %lu, len=%u, buf=%p, cookie=%p\n", state.myPlaceId, p->dest_place, len, buf, header);
	#endif
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
//	if (o == X10RT_OPT_COLLECTIVES)
//		return 1;
	return 0;
}

void x10rt_net_internal_barrier (){} // DEPRECATED

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{
	error("x10rt_net_remote_op not implemented");
}

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{
	// TODO PAMI_Memregion_create
	// need to call PAMI_Memregion_destroy at shutdown
	error("x10rt_net_register_mem not implemented");
	return NULL;
}

void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
	// issue a call to PAMI_Geometry_world, followed by PAMI_Geometry_update to adjust it
	pami_result_t status = PAMI_ERROR;
	pami_geometry_t world;
	status = PAMI_Geometry_world(state.client, &world);
	if (status != PAMI_SUCCESS) error("Unable to create the world geometry");

	pami_configuration_t config;

	//status = PAMI_Geometry_create_tasklist(state.client, );
	if (status != PAMI_SUCCESS) error("Unable to reconfigure the world geometry");

	error("x10rt_net_team_new not implemented");
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Geometry_destroy
	error("x10rt_net_team_del not implemented");
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
	// TODO issue PAMI_Geometry_query to get the number of members
	error("x10rt_net_team_sz not implemented");
    return 0;
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role, x10rt_place color,
		x10rt_place new_role, x10rt_completion_handler2 *ch, void *arg)
{
	// TODO PAMI_Geometry_update
	error("x10rt_net_team_split not implemented");
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role, x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Collective
	error("x10rt_net_barrier not implemented");
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Collective
	error("x10rt_net_bcast not implemented");
}

void x10rt_net_scatter (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Collective
	error("x10rt_net_scatter not implemented");
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Collective
	error("x10rt_net_alltoall not implemented");
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		x10rt_red_op_type op, x10rt_red_type dtype, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// TODO PAMI_Collective
	error("x10rt_net_allreduce not implemented");
}
// vim: tabstop=4:shiftwidth=4:expandtab:textwidth=100
