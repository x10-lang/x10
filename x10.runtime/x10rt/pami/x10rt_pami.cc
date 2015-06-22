/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010-2015.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 */

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <unistd.h> // sleep()
#include <errno.h> // for the strerror function
#include <pthread.h> // for lock on the team mapping table, and context opening thread
#include <x10rt_net.h>
#include <x10rt_internal.h>
#include <pami.h>
#if !defined(__bgq__)
#include <pami_ext_hfi.h>
#endif

//#define DEBUG 1
//#define DEBUG_MESSAGING 1

// locally defined environment variables
#define X10RT_PAMI_ASYNC_PROGRESS "X10RT_PAMI_ASYNC_PROGRESS"
#define X10RT_PAMI_NUM_CONTEXTS "X10RT_PAMI_NUM_CONTEXTS" // if set, limits the number of parallel contexts we open.
#define X10RT_PAMI_DISABLE_HFI "X10RT_PAMI_DISABLE_HFI"
#define X10RT_PAMI_BARRIER_ALG "X10RT_PAMI_BARRIER_ALG"
#define X10RT_PAMI_BCAST_ALG "X10RT_PAMI_BCAST_ALG"
#define X10RT_PAMI_SCATTER_ALG "X10RT_PAMI_SCATTER_ALG"
#define X10RT_PAMI_ALLTOALL_ALG "X10RT_PAMI_ALLTOALL_ALG"
#define X10RT_PAMI_ALLTOALL_CHUNKS "X10RT_PAMI_ALLTOALL_CHUNKS"
#define X10RT_PAMI_REDUCE_ALG "X10RT_PAMI_REDUCE_ALG"
#define X10RT_PAMI_ALLREDUCE_ALG "X10RT_PAMI_ALLREDUCE_ALG"
#define X10RT_PAMI_ALLGATHER_ALG "X10RT_PAMI_ALLGATHER_ALG"
#define X10_NUM_IMMEDIATE_THREADS "X10_NUM_IMMEDIATE_THREADS"

#if defined(__bgq__) || defined(_ARCH_PPC) || defined(__PPC__)
#define POSTMESSAGES 1
#endif

enum MSGTYPE {STANDARD=1, PUT, GET, GET_COMPLETE, NEW_TEAM}; // PAMI doesn't send messages with type=0... it just silently eats them.

//mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*teamCallback2)(x10rt_team, void *);
typedef void (*teamCallback)(void *);

// definitions for PAMI async progress
#define PAMIX_CLIENT_ASYNC_GUARANTEE 1016
typedef enum
{
	PAMIX_ASYNC_ALL =    0,
	PAMIX_ASYNC_EXT = 1000
} pamix_async_t;
typedef void (* pamix_async_function) (pami_context_t context, void *cookie);
typedef pami_result_t (* async_progress_register_function) (pami_context_t context, pamix_async_function progress_fn,
		pamix_async_function suspend_fn, pamix_async_function resume_fn, void* cookie);
typedef pami_result_t (* async_progress_enable_function) (pami_context_t context, pamix_async_t event_type);
typedef pami_result_t (* async_progress_disable_function) (pami_context_t context, pamix_async_t event_type);

// the values for pami_dt are mapped to the indexes of x10rt_red_type
pami_type_t DATATYPE_CONVERSION_TABLE[] = {PAMI_TYPE_UNSIGNED_CHAR, PAMI_TYPE_SIGNED_CHAR, PAMI_TYPE_SIGNED_SHORT, PAMI_TYPE_UNSIGNED_SHORT, PAMI_TYPE_SIGNED_INT,
                                           PAMI_TYPE_UNSIGNED_INT, PAMI_TYPE_SIGNED_LONG_LONG, PAMI_TYPE_UNSIGNED_LONG_LONG, PAMI_TYPE_DOUBLE, PAMI_TYPE_FLOAT,
                                           PAMI_TYPE_LOC_DOUBLE_INT, PAMI_TYPE_DOUBLE_COMPLEX, PAMI_TYPE_LOGICAL1};
size_t DATATYPE_MULTIPLIER_TABLE[] = {1,1,2,2,4,4,8,8,8,4,12,16,1}; // the number of bytes used for each entry in the table above.
// values for pami_op are mapped to indexes of x10rt_red_op_type
pami_data_function OPERATION_CONVERSION_TABLE[] = {PAMI_DATA_SUM, PAMI_DATA_PROD, PAMI_DATA_NOOP, PAMI_DATA_BAND, PAMI_DATA_BOR, PAMI_DATA_BXOR, PAMI_DATA_MAX, PAMI_DATA_MIN};
// values of x10rt_op_type are mapped to pami_atomic_t.
// The x10rt_op_type values correspond to the HFI values, not the PAMI_Rmw() values, so we need to convert when not using HFI.
// The conversion table assumes HFI values: enum x10rt_op_type={X10RT_OP_ADD = 0x00, X10RT_OP_AND = 0x01, X10RT_OP_OR  = 0x02, X10RT_OP_XOR = 0x03}
pami_atomic_t REMOTE_MEMORY_OP_CONVERSION_TABLE[] = {PAMI_ATOMIC_ADD, PAMI_ATOMIC_AND, PAMI_ATOMIC_OR, PAMI_ATOMIC_XOR};

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

struct x10rt_pami_team_create
{
	teamCallback2 cb2;
	void *arg;
	x10rt_place *colors;
	uint32_t teamIndex;
	x10rt_place parent_role;
	bool member;
};

struct x10rt_pami_team_callback
{
	teamCallback tcb;
	void *arg;
	pami_xfer_t operation;
	pami_work_t work;
};

struct x10rt_pami_team
{
	pami_geometry_t geometry; // abstract geometry ID
	pami_algorithm_t algorithm[PAMI_XFER_COUNT]; // which algorithm to use with each collective.  We only set values for the algorithms used here
	uint32_t size; // number of members in the team
	pami_task_t *places; // list of team members
};

struct x10rt_pami_team_destroy
{
	x10rt_completion_handler *tch;
	void *arg;
	int teamid;
};

struct x10rt_pami_internal_alltoall
{
	x10rt_completion_handler *tch;
	void *arg;
	const void *sbuf;
	void *dbuf;
	int teamid;
	size_t dataSize;
	size_t chunksize;
	size_t currentChunkOffset;
	size_t currentPlaceOffset;
	pami_put_simple_t parameters;
	pami_work_t work;
};

struct x10rt_buffered_data
{
	void* header;
	void* data;
};

struct x10rt_post_send
{
	pami_work_t work;
	pami_send_t parameters;
};

#if !defined(__bgq__)
struct x10rt_post_hfi_update
{
	pami_work_t work;
	hfi_remote_update_info_t remote_info;
};

struct x10rt_post_hfi_updates
{
	pami_work_t work;
	size_t numOps;
	hfi_remote_update_info_t *remote_infos;
};
#endif

struct x10rt_post_rmw
{
	pami_work_t work;
	pami_rmw_t operation;
};

struct x10rt_post_general
{
	pami_work_t work;
	void *ptr;
	size_t val;
};

struct x10rt_post_collective_create
{
	pami_work_t work;
	pami_xfer_t operation;
};

struct x10rt_pami_state
{
	pami_task_t numPlaces;
	pami_task_t myPlaceId;
	pami_endpoint_t *endpoints; // today we only support sending data to a single remote context per place
	x10rtCallback* callBackTable;
	x10rt_msg_type callBackTableSize;
	pami_client_t client; // the PAMI client instance used for this place
	pthread_key_t contextLookupTable; // thread local storage to map a worker thread to a context.
	pami_context_t context; // PAMI context associated with the client.  This is created and owned by the first immediate thread

	x10rt_pami_team *teams;
	uint32_t lastTeamIndex;
	size_t a2achunks;
	pthread_mutex_t stateLock; // used when creating a new context or a new team
#if !defined(__bgq__)
	pami_extension_t hfi_extension;
	hfi_remote_update_fn hfi_update;
#endif
	pami_extension_t async_extension; // for async progress
	pami_task_t *stepOrder; // this array is allocated and used only when the internal all-to-all collective has been specified
	char errorMessageBuffer[1200]; // buffer to hold the most recent error message
} state;

static void local_msg_dispatch (pami_context_t context, void* cookie, const void* header_addr, size_t header_size,
		const void * pipe_addr, size_t pipe_size, pami_endpoint_t origin, pami_recv_t* recv);
static void local_put_dispatch (pami_context_t context, void* cookie, const void* header_addr, size_t header_size,
		const void * pipe_addr, size_t pipe_size, pami_endpoint_t origin, pami_recv_t* recv);
static void local_get_dispatch (pami_context_t context, void* cookie, const void* header_addr, size_t header_size,
		const void * pipe_addr, size_t pipe_size, pami_endpoint_t origin, pami_recv_t* recv);
static void get_complete_dispatch (pami_context_t context, void* cookie, const void* header_addr, size_t header_size,
		const void * pipe_addr, size_t pipe_size, pami_endpoint_t origin, pami_recv_t* recv);
static void team_create_dispatch (pami_context_t context, void* cookie, const void* header_addr, size_t header_size,
		const void * pipe_addr, size_t pipe_size, pami_endpoint_t origin, pami_recv_t* recv);


/*
 * Encapsulate malloc, to allow for different alignment on different machines
 */
inline void * x10rt_malloc(size_t n)
{
#if defined(__bgq__)
    void *ptr;
    size_t alignment = 32; // 128 might be better since that ensures every heap allocation starts on a L2 cache-line boundary
    posix_memalign(&ptr, alignment, n);
    return ptr;
#else
    return malloc(n);
#endif
}


/*
 * The error method prints out serious errors, then immediately exits
 */
void error(const char* msg, ...)
{
	va_list ap;
	va_start(ap, msg);
	vsnprintf(state.errorMessageBuffer, sizeof(state.errorMessageBuffer), msg, ap);
	va_end(ap);
	strcat(state.errorMessageBuffer, "  ");
	int blen = strlen(state.errorMessageBuffer);
	PAMI_Error_text(state.errorMessageBuffer+blen, 1199-blen);
	fprintf(stderr, "X10 PAMI error: %s\n", state.errorMessageBuffer);
	if (errno != 0)
		fprintf(stderr, "X10 PAMI errno: %s\n", strerror(errno));

	fflush(stderr);
	exit(EXIT_FAILURE); // TODO - support the non-exit on error mode
}

// Query PAMI for the algorithm to use with a specific team and collective
void queryAvailableAlgorithms(x10rt_pami_team* team, pami_xfer_type_t collective, size_t indexToUse)
{
	pami_result_t status = PAMI_ERROR;

	// figure out how many different algorithms are available
	size_t num_algorithms[2] = {0,0}; // [0]=always works, and [1]=sometimes works lists
	status = PAMI_Geometry_algorithms_num(team->geometry, collective, num_algorithms);
	if (status != PAMI_SUCCESS || (num_algorithms[0]==0 && num_algorithms[1]==0)) error("Unable to query the algorithm counts for collective %i. num_algorithms[0]=%u, [1]=%u, Status=%i", collective, num_algorithms[0], num_algorithms[1], status);

	// query what the different algorithms are
	pami_algorithm_t *always_works_alg = (pami_algorithm_t*)alloca(sizeof(pami_algorithm_t)*num_algorithms[0]);
	pami_metadata_t *always_works_md = (pami_metadata_t*)alloca(sizeof(pami_metadata_t)*num_algorithms[0]);
	pami_algorithm_t *must_query_alg = (pami_algorithm_t*)alloca(sizeof(pami_algorithm_t)*num_algorithms[1]);
	pami_metadata_t *must_query_md = (pami_metadata_t*)alloca(sizeof(pami_metadata_t)*num_algorithms[1]);
	status = PAMI_Geometry_algorithms_query(team->geometry, collective, always_works_alg,
			always_works_md, num_algorithms[0], must_query_alg, must_query_md, num_algorithms[1]);
	if (status != PAMI_SUCCESS) error("Unable to query the supported algorithms for collective %i. num_algorithms[0]=%u, [1]=%u, Status=%i", collective, num_algorithms[0], num_algorithms[1], status);

	if (indexToUse >= num_algorithms[0]+num_algorithms[1])
		error("You requested index %i for collective algorithm %i, which is more than the number available", indexToUse, collective);

	if (indexToUse < num_algorithms[0])
		team->algorithm[collective] = always_works_alg[indexToUse];
	else
		team->algorithm[collective] = must_query_alg[indexToUse-num_algorithms[0]];
}

// For every collective we support, check if the user has set an override to the algorithm selection,
// and set the algorithm for the given team
void determineCollectiveAlgorithms(x10rt_pami_team* team)
{
	char* userChoice = getenv(X10RT_PAMI_BARRIER_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_BARRIER, userChoice?atoi(userChoice):0);

	userChoice = getenv(X10RT_PAMI_BCAST_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_BROADCAST, userChoice?atoi(userChoice):0);

	userChoice = getenv(X10RT_PAMI_SCATTER_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_SCATTER, userChoice?atoi(userChoice):0);

	// all-to-all has issues, so we have our own implementation available
	userChoice = getenv(X10RT_PAMI_ALLTOALL_ALG);
	int userChoiceInt = userChoice?atoi(userChoice):0;
	if (userChoiceInt < 0)
	{
		userChoice = getenv(X10RT_PAMI_ALLTOALL_CHUNKS);
		team->algorithm[PAMI_XFER_ALLTOALL] = PAMI_ALGORITHM_NULL;
		state.a2achunks = userChoice?atoi(userChoice):1; // default to 1 chunk
		// initialize random order array
		state.stepOrder = (pami_task_t *)x10rt_malloc(state.numPlaces*sizeof(pami_task_t));
		if (state.stepOrder == NULL) error("Unable to allocate memory for internal alltoall step order");
		srand(state.myPlaceId);
		for (uint32_t i=0; i<state.numPlaces; i++)
			state.stepOrder[i] = i;
		// shuffle values around the array randomly
		for (uint32_t i=state.numPlaces-1; i>0; --i) {
			int j=rand()%(i+1);
			int tmp = state.stepOrder[j];
			state.stepOrder[j] = state.stepOrder[i];
			state.stepOrder[i] = tmp;
		}
		#ifdef DEBUG
			fprintf(stderr, "Switching AllToAll to internal implementation, messages will be sent as %lu parallel chunks\n", state.a2achunks);
		#endif
	}
	else {
		queryAvailableAlgorithms(team, PAMI_XFER_ALLTOALL, userChoiceInt);
		state.a2achunks = 0;
		state.stepOrder = NULL;
	}

	userChoice = getenv(X10RT_PAMI_REDUCE_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_REDUCE, userChoice?atoi(userChoice):0);

	userChoice = getenv(X10RT_PAMI_ALLREDUCE_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_ALLREDUCE, userChoice?atoi(userChoice):0);

	// only used in the team split operation
	userChoice = getenv(X10RT_PAMI_ALLGATHER_ALG);
	queryAvailableAlgorithms(team, PAMI_XFER_ALLGATHER, userChoice?atoi(userChoice):0);
}


// small bit of code to extend the number of allocated teams.  Returns the original lastTeamIndex
unsigned expandTeams(unsigned numNewTeams)
{
	int r;
	pthread_mutex_lock(&state.stateLock);
		void* oldTeams = state.teams;
		int oldSize = (state.lastTeamIndex+1)*sizeof(x10rt_pami_team);
		int newSize = (state.lastTeamIndex+1+numNewTeams)*sizeof(x10rt_pami_team);
		void* newTeams = x10rt_malloc(newSize);
		if (newTeams == NULL) error("Unable to allocate memory to add more teams");
		memcpy(newTeams, oldTeams, oldSize);
		memset(((char*)newTeams)+oldSize, 0, newSize-oldSize);
		state.teams = (x10rt_pami_team*)newTeams;
		r = state.lastTeamIndex;
		state.lastTeamIndex+=numNewTeams;
	pthread_mutex_unlock(&state.stateLock);
	free(oldTeams);
	return r;
}

void registerHandlers(pami_context_t context)
{
	pami_result_t status = PAMI_ERROR;

	pami_dispatch_hint_t hints;
	memset(&hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
	hints.recv_contiguous = PAMI_HINT_ENABLE;

	// set up our callback functions, which will convert PAMI messages to X10 callbacks
	pami_dispatch_callback_function fn;
	fn.p2p = local_msg_dispatch;
	if ((status = PAMI_Dispatch_set(context, STANDARD, fn, NULL, hints)) != PAMI_SUCCESS)
		error("Unable to register standard dispatch handler");

	pami_dispatch_callback_function fn2;
	fn2.p2p = local_put_dispatch;
	if ((status = PAMI_Dispatch_set(context, PUT, fn2, NULL, hints)) != PAMI_SUCCESS)
		error("Unable to register put dispatch handler");

	pami_dispatch_callback_function fn3;
	fn3.p2p = local_get_dispatch;
	if ((status = PAMI_Dispatch_set(context, GET, fn3, NULL, hints)) != PAMI_SUCCESS)
		error("Unable to register get dispatch handler");

	pami_dispatch_callback_function fn4;
	fn4.p2p = get_complete_dispatch;
	if ((status = PAMI_Dispatch_set(context, GET_COMPLETE, fn4, NULL, hints)) != PAMI_SUCCESS)
		error("Unable to register get_complete_dispatch handler");

	pami_dispatch_callback_function fn5;
	fn5.p2p = team_create_dispatch;
	if ((status = PAMI_Dispatch_set(context, NEW_TEAM, fn5, NULL, hints)) != PAMI_SUCCESS)
		error("Unable to register team_create_dispatch handler");

	if (state.async_extension)
	{
		pami_configuration_t configuration;
		configuration.name = (pami_attribute_name_t)PAMIX_CLIENT_ASYNC_GUARANTEE;
		PAMI_Client_query (state.client, &configuration, 1);
		if (configuration.value.intval == 0)
		{
			#ifdef __GNUC__
			__extension__
			#endif
			async_progress_register_function PAMIX_Context_async_progress_register = (async_progress_register_function) PAMI_Extension_symbol (state.async_extension, "register");
			PAMIX_Context_async_progress_register (context, NULL, NULL, NULL, NULL);
		}

		#ifdef __GNUC__
		__extension__
		#endif
		async_progress_enable_function PAMIX_Context_async_progress_enable = (async_progress_enable_function) PAMI_Extension_symbol (state.async_extension, "enable");
		PAMIX_Context_async_progress_enable (context, PAMIX_ASYNC_ALL);

		#ifdef DEBUG
			fprintf(stderr, "ASYNC Progress enabled at place %u\n", state.myPlaceId);
		#endif
	}
}

static void cookie_free (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in cookie_free");
	free(cookie);
}

static void free_buffered_data (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in free_buffered_data");
	x10rt_buffered_data *bd = (x10rt_buffered_data*)cookie;
	free(bd->header);
	free(bd->data);
	free(bd);
}

static void free_header_data (pami_context_t   context,
        void          * cookie,
        pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
	error("Error detected in free_header_data");
	x10rt_pami_header_data *hd = (x10rt_pami_header_data*)cookie;
	if (hd->x10msg.len > 0)
		free(hd->x10msg.msg);
	free(hd);
}

/*
 * When a standard message is too large to fit in one transmission block, it gets broken up.
 * This method handles the final part of these messages (local_msg_dispatch handles the first part)
 */
static void std_msg_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in std_msg_complete");

	struct x10rt_msg_params *hdr = (struct x10rt_msg_params*) cookie;
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u processing delayed standard message %i, len=%u\n", state.myPlaceId, hdr->type, hdr->len);
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
		struct x10rt_msg_params *hdr = (struct x10rt_msg_params *)x10rt_malloc(sizeof(struct x10rt_msg_params));
		if (hdr == NULL) error("Unable to allocate memory for a msg_dispatch callback");
		hdr->dest_place = state.myPlaceId;
		hdr->len = pipe_size; // this is going to be large-ish, otherwise recv would be null
		hdr->msg = x10rt_malloc(pipe_size);
		if (hdr->msg == NULL) error("Unable to allocate a msg_dispatch buffer of size %u", pipe_size);
		hdr->type = *((x10rt_msg_type*)header_addr);
		#ifdef DEBUG_MESSAGING
			fprintf(stderr, "Place %u waiting on a partially delivered message %i, len=%lu\n", state.myPlaceId, hdr->type, pipe_size);
		#endif
		recv->local_fn = std_msg_complete;
		recv->cookie   = hdr;
		recv->type     = PAMI_TYPE_BYTE;
		recv->addr     = hdr->msg;
		recv->offset   = 0;
		recv->data_fn  = PAMI_DATA_COPY;
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
				error("Unable to allocate a msg buffer of size %u", pipe_size);
			memcpy(mp.msg, pipe_addr, pipe_size);
		}
		else
			mp.msg = NULL;

		#ifdef DEBUG_MESSAGING
			fprintf(stderr, "Place %u processing standard message %i, len=%u\n", state.myPlaceId, mp.type, mp.len);
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
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u issuing put notifier callback, type=%i, msglen=%u, datalen=%u\n", state.myPlaceId, header->x10msg.type, header->x10msg.len, header->data_len);
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
		error("non-immediate put dispatch not yet implemented");

	// else, all the data is available, and ready to process
	struct x10rt_pami_header_data* localParameters = (struct x10rt_pami_header_data*)x10rt_malloc(sizeof(struct x10rt_pami_header_data));
	if (localParameters == NULL) error("Unable to allocate memory for a local_put_dispatch header");
	struct x10rt_pami_header_data* incomingParameters = (struct x10rt_pami_header_data*) header_addr;
	localParameters->x10msg.dest_place = state.myPlaceId;
	localParameters->data_len = incomingParameters->data_len;
	localParameters->data_ptr = incomingParameters->data_ptr;
	localParameters->x10msg.type = incomingParameters->x10msg.type;
	localParameters->x10msg.len = pipe_size;
	if (pipe_size > 0)
	{
		localParameters->x10msg.msg = x10rt_malloc(pipe_size);
		if (localParameters->x10msg.msg == NULL) error("Unable to allocate a buffer to hold incoming PUT data");
		memcpy(localParameters->x10msg.msg, pipe_addr, pipe_size); // save the message for later
	}
	else
		localParameters->x10msg.msg = NULL;

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u processing PUT message %i from %u, msglen=%u, len=%u, remote buf=%p, remote cookie=%p\n", state.myPlaceId, localParameters->x10msg.type, origin, localParameters->x10msg.len, localParameters->data_len, incomingParameters->data_ptr, incomingParameters->x10msg.msg);
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
		if ((status = PAMI_Get (context, &parameters)) != PAMI_SUCCESS)
			error("Error sending data for PUT response");
	}
	else
		put_handler_complete(context, localParameters, PAMI_SUCCESS);
}

/*
 * This method is called at the receiving end of a GET message, after all the data has been sent.
 * It sends a GET_COMPLETE to the originator
 */
static void get_handler_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in get_handler_complete");

	x10rt_msg_params* header = (x10rt_msg_params*) cookie;

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u running get_handler_complete, dest=%u type=%i, remote cookie=%p\n", state.myPlaceId, header->dest_place, header->type, header->msg);
	#endif

	// send a GET_COMPLETE to the originator
	pami_result_t   status = PAMI_ERROR;

	pami_send_t parameters;
	parameters.send.dispatch        = GET_COMPLETE;
	parameters.send.header.iov_base = &header->msg; // sending the origin pointer back
	parameters.send.header.iov_len  = sizeof(void *);
	parameters.send.data.iov_base   = NULL;
	parameters.send.data.iov_len    = 0;
	parameters.send.dest 			= header->dest_place;
	memset(&parameters.send.hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
	parameters.send.hints.buffer_registered = PAMI_HINT_ENABLE;
	parameters.events.cookie        = cookie;
	parameters.events.local_fn      = cookie_free;
	parameters.events.remote_fn     = NULL;

	if ((status = PAMI_Send(context, &parameters)) != PAMI_SUCCESS)
		error("Unable to send a GET_COMPLETE message from %u to %u: %i\n", state.myPlaceId, header->dest_place, status);

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "(%u) get_handler_complete\n", state.myPlaceId);
	#endif
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
		error("non-immediate get dispatch not yet implemented");

	// else, all the data is available, and ready to process
	x10rt_msg_params* localParameters = (x10rt_msg_params*) x10rt_malloc(sizeof(x10rt_msg_params));
	if (localParameters == NULL) error("Unable to allocate memory for a local_get_dispatch header");
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*) header_addr;
	localParameters->dest_place = state.myPlaceId;
	localParameters->type = header->x10msg.type;
	localParameters->msg = (void*)pipe_addr;
	localParameters->len = pipe_size;

	// issue a put to the originator
	#ifdef DEBUG
		fprintf(stderr, "Place %u processing GET message %i, datalen=%u, remote cookie=%p\n", state.myPlaceId, header->x10msg.type, header->data_len, header->x10msg.msg);
	#endif

	finderCallback fcb = state.callBackTable[localParameters->type].finder;
	void* src = fcb(localParameters, header->data_len);
	if (src == NULL)
		error("invalid buffer provided for the source of a GET");

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
		if ((status = PAMI_Put (context, &parameters)) != PAMI_SUCCESS)
			error("Error sending data for GET response");
		#ifdef DEBUG_MESSAGING
			fprintf(stderr, "Place %u pushing out %u bytes of GET message data\n", state.myPlaceId, header->data_len);
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

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u got GET_COMPLETE from %u, header=%p\n", state.myPlaceId, origin, (void*)header);
		fprintf(stderr, "     type=%i, datalen=%u. Calling notifier\n", header->x10msg.type, header->data_len);
	#endif

	notifierCallback ncb = state.callBackTable[header->x10msg.type].notifier;
	ncb(&header->x10msg, header->data_len);

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u finished GET message\n", state.myPlaceId);
	#endif
	if (header->x10msg.len > 0)
		free(header->x10msg.msg);
	free(header);
}

static void team_creation_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in team_creation_complete");

	if (cookie) // should always be true
	{
		x10rt_pami_team_create *team = (x10rt_pami_team_create*)cookie;
		#ifdef DEBUG
			fprintf(stderr, "New team %u created at place %u, member=%s\n", team->teamIndex, state.myPlaceId, team->member?"true":"false");
		#endif
		if (team->member) determineCollectiveAlgorithms(&state.teams[team->teamIndex]);
		team->cb2(team->teamIndex, team->arg);
		free(team);
	}
}

static void team_creation_complete_nocallback (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in team_creation_complete_nocallback");

	x10rt_pami_team* team = (x10rt_pami_team *)cookie;
	for (int i=0; i<team->size; i++)
	{
		if (team->places[i] == state.myPlaceId)
		{
			determineCollectiveAlgorithms(team);
			break;
		}
	}
	#ifdef DEBUG
		fprintf(stderr, "New Team created via team_new at place %u\n", state.myPlaceId);
	#endif
}

static void team_create_dispatch_part2 (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in team_create_dispatch_part2");

	uint32_t newTeamId = *((uint32_t*)cookie);

	pami_configuration_t config;
	config.name = PAMI_GEOMETRY_OPTIMIZE;
	config.value.intval = 1;

	#ifdef DEBUG
		fprintf(stderr, "Creating a new team %u at place %u of size %u\n", newTeamId, state.myPlaceId, state.teams[newTeamId].size);
	#endif

	pami_result_t   status = PAMI_ERROR;
	status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, &state.teams[newTeamId].geometry, state.teams[0].geometry, newTeamId, state.teams[newTeamId].places, state.teams[newTeamId].size, context, team_creation_complete_nocallback, &state.teams[newTeamId]);
	if (status != PAMI_SUCCESS) error("Unable to create a new team");

	free(cookie);
}

/*
 * This method is used to create a new team
 */
static void team_create_dispatch (
	    pami_context_t       context,      /**< IN: PAMI context */
	    void               * cookie,       /**< IN: dispatch cookie */
	    const void         * header_addr,  /**< IN: header address */
	    size_t               header_size,  /**< IN: header size */
	    const void         * pipe_addr,    /**< IN: address of PAMI pipe buffer */
	    size_t               pipe_size,    /**< IN: size of PAMI pipe buffer */
	    pami_endpoint_t      origin,
	    pami_recv_t         * recv)        /**< OUT: receive message structure */
{
	pami_task_t newTeamId = *((pami_task_t*)header_addr);
	if (newTeamId <= state.lastTeamIndex)
		error("Place %u attempted to join team %u, but it is already a member of that team.  A place can not be in the same team more than once.", state.myPlaceId, newTeamId);

	unsigned previousLastTeam = expandTeams(1);
	if (previousLastTeam+1 != newTeamId) error("misalignment detected in team_create_dispatch");

	// save the members of the new team
	state.teams[newTeamId].size = pipe_size/(sizeof(pami_task_t));
	state.teams[newTeamId].places = (pami_task_t*)x10rt_malloc(pipe_size);
	if (state.teams[newTeamId].places == NULL) error("unable to allocate memory for holding the places in team_create_dispatch");

	if (recv)
	{
		#ifdef DEBUG
			fprintf(stderr, "Place %u waiting on a partially delivered team creation message, len=%lu\n", state.myPlaceId, pipe_size);
		#endif

		recv->local_fn = team_create_dispatch_part2;
		recv->cookie   = x10rt_malloc(sizeof(uint32_t));
		memcpy(recv->cookie, &newTeamId, sizeof(uint32_t));
		recv->type     = PAMI_TYPE_BYTE;
		recv->addr     = state.teams[newTeamId].places;
		recv->offset   = 0;
		recv->data_fn  = PAMI_DATA_COPY;
	}
	else
	{
		memcpy(state.teams[newTeamId].places, pipe_addr, pipe_size);

		pami_configuration_t config;
		config.name = PAMI_GEOMETRY_OPTIMIZE;
		config.value.intval = 1;

		// check to see if we are a member of the geometry or not
		bool member = false;
		pami_result_t   status = PAMI_ERROR;

		for (int i=0; i<state.teams[newTeamId].size; i++)
		{
			if (state.teams[newTeamId].places[i] == state.myPlaceId)
			{
				member = true;
				break;
			}
		}

		#ifdef DEBUG
			fprintf(stderr, "creating a new team %u at place %u of size %u, member=%s\n", newTeamId, state.myPlaceId, state.teams[newTeamId].size, member?"true":"false");
		#endif

		if (!member)
		{
			state.teams[newTeamId].geometry = PAMI_GEOMETRY_NULL;
			status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, NULL, state.teams[0].geometry, newTeamId, state.teams[newTeamId].places, state.teams[newTeamId].size, context, NULL, NULL);
		}
		else
			status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, &state.teams[newTeamId].geometry, state.teams[0].geometry, newTeamId, state.teams[newTeamId].places, state.teams[newTeamId].size, context, team_creation_complete_nocallback, &state.teams[newTeamId]);

		if (status != PAMI_SUCCESS) error("Unable to create a new team");
	}
}

static void team_destroy_complete (pami_context_t context, void* cookie, pami_result_t result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in team_destroy_complete");

	x10rt_pami_team_destroy* ptd = (x10rt_pami_team_destroy*)cookie;

	state.teams[ptd->teamid].size = 0;
	free(state.teams[ptd->teamid].places);
	state.teams[ptd->teamid].places = NULL;
	ptd->tch(ptd->arg);
	free(cookie);
}


x10rt_error x10rt_net_preinit(char* connInfoBuffer, int connInfoBufferSize) {
	return X10RT_ERR_UNSUPPORTED;
}

x10rt_error x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
	// TODO - return proper error codes upon failure, in place of calling the error() method.
	pami_result_t   status = PAMI_ERROR;
	setenv("MP_MSG_API", "X10", 0);
	const char *name = getenv("MP_MSG_API");

	pami_configuration_t config;
	config.name = PAMI_GEOMETRY_OPTIMIZE;
	config.value.intval = 1;

	// Check if we want to enable async progress
	if (checkBoolEnvVar(getenv(X10RT_PAMI_ASYNC_PROGRESS)))
	{
		if ((status = PAMI_Client_create(name, &state.client, &config, 1)) != PAMI_SUCCESS)
			error("Unable to initialize the PAMI client: %i\n", status);

		status = PAMI_Extension_open(state.client, "EXT_async_progress", &state.async_extension);
		if (status != PAMI_SUCCESS)
			error("ASYNC progress requested but unavailable at place %u because PAMI_Extension_open status=%u\n", state.myPlaceId, status);

		// the extension is enabled for each context in the registerHandlers method.
	}
	else
	{
		state.async_extension = NULL;
		setenv("MP_POLLING_INTERVAL", "2147483647", 0);
		if ((status = PAMI_Client_create(name, &state.client, &config, 1)) != PAMI_SUCCESS)
			error("Unable to initialize the PAMI client: %i\n", status);
	}

	pami_configuration_t configuration[2];
	configuration[0].name = PAMI_CLIENT_TASK_ID;
	configuration[1].name = PAMI_CLIENT_NUM_TASKS;

	if ((status = PAMI_Client_query(state.client, configuration, 2)) != PAMI_SUCCESS)
		error("Unable to query the PAMI_CLIENT: %i\n", status);
	state.myPlaceId = configuration[0].value.intval;
	state.numPlaces = configuration[1].value.intval;
	if (pthread_mutex_init(&state.stateLock, NULL) != 0) error("Unable to initialize the state lock");

#ifdef POSTMESSAGES
	// if multi-threaded, we require at least one immediate thread
	// set the num immediate threads environment variable if not already set
	// this overrides the default used by Configuration.x10, which would otherwise choose 0 because we don't support blocking probe
	setenv(X10_NUM_IMMEDIATE_THREADS, "1", 0);
	// check the value, to ensure it's at least one
	if (atoi(getenv(X10_NUM_IMMEDIATE_THREADS)) < 1) {
		// no immediate threads.  But a single thread is ok too.  Check.
		char* sthreads = getenv("X10_STATIC_THREADS");
		char* nthreads = getenv("X10_NTHREADS");
		if ( !(checkBoolEnvVar(sthreads) && nthreads && atoi(nthreads) == 1)) {
			// not a valid configuration
			if (state.myPlaceId == 0)
				printf("Configuration error: when using PAMI with multiple threads, you must have at least one immediate thread.  Please change or unset "X10_NUM_IMMEDIATE_THREADS"\n");
			exit(1);
		}
	}
#endif

	#ifdef DEBUG
		fprintf(stderr, "Place %u init called from thread %lu\n", state.myPlaceId, pthread_self());
	#endif
	
	if ((status = PAMI_Context_createv(state.client, NULL, 0, &state.context, 1)) != PAMI_SUCCESS)
		error("Unable to initialize PAMI context: %i\n", status);
	registerHandlers(state.context);

	// preallocate a single endpoint for each destination
	state.endpoints = (pami_endpoint_t *) x10rt_malloc(sizeof(pami_endpoint_t) * state.numPlaces);
	for (pami_task_t i=0; i<state.numPlaces; i++) {
		if ((status = PAMI_Endpoint_create(state.client, i, 0, &state.endpoints[i])) != PAMI_SUCCESS)
			error("Unable to create target endpoint %u for sending a message from %u to %u: %i\n", 0, state.myPlaceId, i, status);
	}
	
	// associate the context with this thread
	if (pthread_key_create(&state.contextLookupTable, NULL) != 0)
		error("Unable to allocate the thread-local-storage context lookup table");
	pthread_setspecific(state.contextLookupTable, state.context);

	// create the world geometry
	state.teams = (x10rt_pami_team*)x10rt_malloc(sizeof(x10rt_pami_team));
	if (state.teams == NULL) error("Unable to allocate memory for teams data");
	state.lastTeamIndex = 0;
	state.teams[0].size = state.numPlaces;
	state.teams[0].places = NULL;
	status = PAMI_Geometry_world(state.client, &state.teams[0].geometry);
	if (status != PAMI_SUCCESS) error("Unable to create the world geometry");
	determineCollectiveAlgorithms(state.teams);

	#ifdef DEBUG
        fprintf(stderr, "Hello from process %u of %u\n", state.myPlaceId, state.numPlaces);
    #endif

#if !defined(__bgq__)
	state.hfi_update = NULL;
#if defined(_ARCH_PPC) || defined(__PPC__)
    // see if HFI should be used
	if (!checkBoolEnvVar(getenv(X10RT_PAMI_DISABLE_HFI)))
	{
		if (sizeof(x10rt_remote_op_params)!=sizeof(hfi_remote_update_info_t))
			fprintf(stderr, "HFI present but the structures don't match at place %u\n", state.myPlaceId);
		else
		{
			status = PAMI_Extension_open (state.client, "EXT_hfi_extension", &state.hfi_extension);
			if (status == PAMI_SUCCESS)
			{
				#ifdef __GNUC__
				__extension__
				#endif
				state.hfi_update = (hfi_remote_update_fn) PAMI_Extension_symbol(state.hfi_extension, "hfi_remote_update"); // This may succeed even if HFI is not available
			}
			else
				fprintf(stderr, "HFI present but disabled at place %u because PAMI_Extension_open status=%u\n", state.myPlaceId, status);
		}
	}
#endif // power CPU
#endif // not BlueGeneQ

	return X10RT_ERR_OK;
}

const char *x10rt_net_error_msg (void)
{
	return state.errorMessageBuffer;
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

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Place %u registered standard message handler %u\n", state.myPlaceId, msg_type);
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
		fprintf(stderr, "Place %u registered PUT message handler %u\n", state.myPlaceId, msg_type);
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
		fprintf(stderr, "Place %u registered GET message handler %u\n", state.myPlaceId, msg_type);
	#endif
}

x10rt_place x10rt_net_ndead (void) {
	return 0; // place failure is not handled by this implementation.
}

bool x10rt_net_is_place_dead (x10rt_place p) {
	return false; // place failure is not handled by this implementation.
}

x10rt_error x10rt_net_get_dead (x10rt_place *dead_places, x10rt_place len) {
	return X10RT_ERR_UNSUPPORTED; // place failure is not handled by this implementation.
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

pami_result_t post_send(pami_context_t context, void * cookie) {
	pami_result_t   status = PAMI_ERROR;
	if ((status = PAMI_Send(context, &((x10rt_post_send *)cookie)->parameters)) != PAMI_SUCCESS)
		error("Unable to send an immediate message from %u: %i\n", state.myPlaceId, status);
	free(cookie);
	return PAMI_SUCCESS;
}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_msg (x10rt_msg_params *p)
{
	pami_result_t   status = PAMI_ERROR;
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Preparing to send a message from place %u to %u, endpoint %u\n", state.myPlaceId, p->dest_place, 0);
	#endif

	x10rt_post_send *post = (x10rt_post_send *)x10rt_malloc(sizeof(x10rt_post_send));
	post->parameters.send.dispatch        = STANDARD;
	post->parameters.send.header.iov_len  = sizeof(p->type);
	post->parameters.send.data.iov_len    = p->len;
	post->parameters.send.dest 			= state.endpoints[p->dest_place];
	memset(&post->parameters.send.hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
	post->parameters.send.hints.buffer_registered = PAMI_HINT_ENABLE;
	post->parameters.events.remote_fn     = NULL;

	x10rt_buffered_data *bd = (x10rt_buffered_data *)x10rt_malloc(sizeof(x10rt_buffered_data));
	bd->header = x10rt_malloc(sizeof(p->type));
	memcpy(bd->header, &p->type, sizeof(p->type));
	bd->data = x10rt_malloc(p->len);
	memcpy(bd->data, p->msg, p->len);

	post->parameters.send.header.iov_base = bd->header;
	post->parameters.send.data.iov_base   = bd->data;
	post->parameters.events.cookie        = bd;
	post->parameters.events.local_fn      = free_buffered_data;

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "(%u) send_msg, headerlen=%u, datalen=%u\n", state.myPlaceId, post->parameters.send.header.iov_len, post->parameters.send.data.iov_len);
	#endif

#ifdef POSTMESSAGES
	status = PAMI_Context_post(state.context, &post->work, post_send, (void *)post);
#else
	PAMI_Context_lock(state.context);
	status = post_send(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a send message");
}

/** \see #x10rt_lgl_send_msg
 * Important: This method returns control back to the caller after the message p has been
 * transmitted out, but BEFORE the data in buf has been transmitted.  Therefore the caller
 * can not delete or modify buf at return time.  It must do this later, through some other
 * callback.
 */
void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	pami_result_t   status = PAMI_ERROR;

	struct x10rt_pami_header_data *header = (struct x10rt_pami_header_data *)x10rt_malloc(sizeof(struct x10rt_pami_header_data));
	if (header == NULL) error("Unable to allocate memory for a PUT header");
	if (p->len > 0)
	{
		header->x10msg.msg = x10rt_malloc(p->len);
		if (header->x10msg.msg == NULL) error("Unable to allocate memory for a PUT header message");
		memcpy(header->x10msg.msg, p->msg, p->len);
	}
	else
		header->x10msg.msg = NULL;
	header->x10msg.type = p->type;
	header->x10msg.dest_place = p->dest_place;
	header->x10msg.len = p->len;
	header->data_len = len;
	header->data_ptr = buf;

	x10rt_post_send *post = (x10rt_post_send *)x10rt_malloc(sizeof(x10rt_post_send));
	post->parameters.send.dispatch        = PUT;
	post->parameters.send.header.iov_base = header;
	post->parameters.send.header.iov_len  = sizeof(struct x10rt_pami_header_data);
	post->parameters.send.data.iov_base   = header->x10msg.msg;
	post->parameters.send.data.iov_len    = header->x10msg.len;
	post->parameters.send.dest 			= state.endpoints[p->dest_place];
	memset(&post->parameters.send.hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
	post->parameters.send.hints.buffer_registered = PAMI_HINT_ENABLE;
	post->parameters.events.cookie		= (void*)header;
	post->parameters.events.local_fn		= free_header_data;
	post->parameters.events.remote_fn     = NULL;

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Preparing to send a PUT message from place %u to %u, endpoint %u, type=%i, msglen=%u, len=%u, buf=%p\n", state.myPlaceId, p->dest_place, 0, p->type, p->len, len, buf);
	#endif

#ifdef POSTMESSAGES
	status = PAMI_Context_post(state.context, &post->work, post_send, (void *)post);
#else
	PAMI_Context_lock(state.context);
	status = post_send(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a put message");
}

/** \see #x10rt_lgl_send_msg
 * \param p As in x10rt_lgl_send_msg.
 * \param buf As in x10rt_lgl_send_msg.
 * \param len As in x10rt_lgl_send_msg.
 */
void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	// GET is implemented as a send msg, followed by a PUT
	pami_result_t   status = PAMI_ERROR;

	// note: this allocation gets freed when the response comes in
	struct x10rt_pami_header_data* header = (struct x10rt_pami_header_data*)x10rt_malloc(sizeof(struct x10rt_pami_header_data));
	if (header == NULL) error("Unable to allocate memory for a send_get header");
	header->data_len = len;
	header->data_ptr = buf;
	header->x10msg.type = p->type;
	header->x10msg.dest_place = p->dest_place;
	header->x10msg.len = p->len;
	// save the msg data for the notifier
	if (p->len > 0)
	{
		header->x10msg.msg = x10rt_malloc(p->len);
		if (header->x10msg.msg == NULL) error("Unable to allocate msg space for a GET");
		memcpy(header->x10msg.msg, p->msg, p->len);
	}
	else
		header->x10msg.msg = NULL;
	header->callbackPtr = header; // sending this along with the data

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "Preparing to send a GET message from place %u to %u endpoint %u, len=%u, buf=%p, cookie=%p\n", state.myPlaceId, p->dest_place, 0, len, buf, (void*)header);
	#endif

	x10rt_post_send *post = (x10rt_post_send *)x10rt_malloc(sizeof(x10rt_post_send));
	post->parameters.send.dispatch        = GET;
	post->parameters.send.header.iov_base = header;
	post->parameters.send.header.iov_len  = sizeof(struct x10rt_pami_header_data);
	post->parameters.send.data.iov_base   = header->x10msg.msg;
	post->parameters.send.data.iov_len    = header->x10msg.len;
	post->parameters.send.dest 			= state.endpoints[p->dest_place];
	memset(&post->parameters.send.hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
	post->parameters.send.hints.buffer_registered = PAMI_HINT_ENABLE;
	post->parameters.events.cookie        = NULL;
	post->parameters.events.local_fn      = NULL;
	post->parameters.events.remote_fn     = NULL;

#ifdef POSTMESSAGES
	status = PAMI_Context_post(state.context, &post->work, post_send, (void *)post);
#else
	PAMI_Context_lock(state.context);
	status = post_send(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a get message");
}

/** Handle any oustanding message from the network by calling the registered callbacks.  \see #x10rt_lgl_probe
 */
x10rt_error x10rt_net_probe()
{
	pami_result_t status = PAMI_ERROR;

#ifdef POSTMESSAGES
	if (pthread_getspecific(state.contextLookupTable)) {
#else
	PAMI_Context_lock(state.context);
#endif
		#if defined(__bgq__)
			// Temporary workaround observed behavior on BG/Q.
			// PAMI_Context_advance seems to always return PAMI_SUCCESS
			// So convert SUCCESS to EAGAIN and rely on higher-level looping to drain the network
			status = PAMI_Context_advance(state.context, 100);
		#else
//			fprintf(stderr, "Place %u probed\n", state.myPlaceId);
			do { status = PAMI_Context_advance(state.context, 1);
			} while (status == PAMI_SUCCESS); // PAMI_Context_advance returns PAMI_EAGAIN when no messages were processed
		#endif
		if (status == PAMI_ERROR) error ("Problem advancing the context");
#ifdef POSTMESSAGES
	}
#else
	PAMI_Context_unlock(state.context);
#endif
	return X10RT_ERR_OK;
}

bool x10rt_net_blocking_probe_support(void)
{
	return false;
}

x10rt_error x10rt_net_blocking_probe (void)
{
	// TODO: make this blocking.  For now, just call probe.
	return x10rt_net_probe();
}

x10rt_error x10rt_net_unblock_probe (void)
{
	// TODO: once blocking_probe is implemented, this needs to do something.  Fine for now.
	return X10RT_ERR_OK;
}

/** Shut down the network layer.  \see #x10rt_lgl_finalize
 */
void x10rt_net_finalize()
{
	pami_result_t status = PAMI_ERROR;

	#ifdef DEBUG
		fprintf(stderr, "Place %u shutting down via thread %lu\n", state.myPlaceId, pthread_self());
	#endif
	
	// flush out any pending work
	x10rt_net_probe();

#if !defined(__bgq__)
	if (state.hfi_update != NULL)
	{
		PAMI_Extension_close (state.hfi_extension);
		state.hfi_update = NULL;
	}
#endif

	if (state.async_extension != NULL)
	{
		#ifdef __GNUC__
		__extension__
		#endif
		async_progress_disable_function PAMIX_Context_async_progress_disable = (async_progress_disable_function) PAMI_Extension_symbol (state.async_extension, "disable");
		PAMIX_Context_async_progress_disable (state.context, PAMIX_ASYNC_ALL);
		PAMI_Extension_close (state.async_extension);
		state.async_extension = NULL;
	}

#if !defined(__bgq__)
	if ((status = PAMI_Context_destroyv(&state.context, 1)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI context: %i\n", status);
#endif

	if ((status = PAMI_Client_destroy(&state.client)) != PAMI_SUCCESS)
		fprintf(stderr, "Error closing PAMI client: %i\n", status);

	// wipe out any leftover teams
	for (unsigned int i=1; i<=state.lastTeamIndex; i++)
		if (state.teams[i].places != NULL)
			free(state.teams[i].places);

	free(state.teams);
	if (state.stepOrder != NULL)
		free(state.stepOrder);

	pthread_mutex_destroy(&state.stateLock);
}

x10rt_coll_type x10rt_net_coll_support () {
	return X10RT_COLL_ALLNONBLOCKINGCOLLECTIVES;
}

bool x10rt_net_remoteop_support () {
#if defined(__bgq__) || !(defined(_ARCH_PPC) || defined(__PPC__))
	return false; // No hardware support for remote memory operations on BG/Q; best to use emulated layer
#else
	return true;
#endif
}

#if !defined(__bgq__)
pami_result_t post_hfi_update (pami_context_t context, void* cookie) {
	pami_result_t status = state.hfi_update(context, 1, &((x10rt_post_hfi_update*)cookie)->remote_info);
	if (status != PAMI_SUCCESS)
		error("Unable to execute the remote hfi update");
	free(cookie);
	return PAMI_SUCCESS;
}

pami_result_t post_hfi_updates (pami_context_t context, void* cookie) {
	pami_result_t status = state.hfi_update(context, ((x10rt_post_hfi_updates*)cookie)->numOps, ((x10rt_post_hfi_updates*)cookie)->remote_infos);
	if (status != PAMI_SUCCESS)
		error("Unable to execute the remote operations");
	free(cookie);
	return PAMI_SUCCESS;
}
#endif


pami_result_t post_rmw (pami_context_t context, void* cookie) {
	pami_result_t status = PAMI_Rmw(context, &((x10rt_post_rmw *)cookie)->operation);
	if (status != PAMI_SUCCESS)
		error("Unable to execute the remote operation");
	free(cookie);
	return PAMI_SUCCESS;
}

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{
#if !defined(__bgq__)
	if (state.hfi_update != NULL)
	{
		// use HFI remote operations
		x10rt_post_hfi_update* post = (x10rt_post_hfi_update*)x10rt_malloc(sizeof(x10rt_post_hfi_update));
		post->remote_info.dest = place;
		post->remote_info.op = type;
		post->remote_info.atomic_operand = value;
		post->remote_info.dest_buf = victim;
		#ifdef DEBUG
			fprintf(stderr, "Place %u executing a remote operation %u on %p at place %u using HFI\n", state.myPlaceId, type, (void*)victim, place);
		#endif

#ifdef POSTMESSAGES
		pami_result_t status = PAMI_Context_post(state.context, &post->work, post_hfi_update, (void *)post);
#else
		PAMI_Context_lock(state.context);
		pami_result_t status = post_hfi_update(state.context, post);
		PAMI_Context_unlock(state.context);
#endif
		if (status != PAMI_SUCCESS) error("Unable to post a hfi_update");
	}
	else
#endif
	{
		x10rt_post_rmw* post = (x10rt_post_rmw *)x10rt_malloc(sizeof(x10rt_post_rmw));
		memset(&post->operation, 0, sizeof(pami_rmw_t));
		post->operation.dest = state.endpoints[place];
		post->operation.hints.buffer_registered = PAMI_HINT_ENABLE;
		post->operation.remote = (void *)victim;
		post->operation.value = &value;
		post->operation.operation = (pami_atomic_t)REMOTE_MEMORY_OP_CONVERSION_TABLE[type];
		post->operation.type = PAMI_TYPE_UNSIGNED_LONG_LONG;
		#ifdef DEBUG
			fprintf(stderr, "Place %u executing a remote operation %u on %p at place %u\n", state.myPlaceId, type, post->operation.remote, place);
		#endif

#ifdef POSTMESSAGES
		pami_result_t status = PAMI_Context_post(state.context, &post->work, post_rmw, (void *)post);
#else
		PAMI_Context_lock(state.context);
		pami_result_t status = post_rmw(state.context, post);
		PAMI_Context_unlock(state.context);
#endif
		if (status != PAMI_SUCCESS) error("Unable to post a rmw");
	}
}

void x10rt_net_remote_ops (x10rt_remote_op_params *ops, size_t numOps)
{
#if !defined(__bgq__)
	if (state.hfi_update != NULL)
	{
		// use HFI remote operations
		#ifdef DEBUG
			fprintf(stderr, "Place %u executing a remote %lu operations using HFI\n", state.myPlaceId, numOps);
		#endif

		x10rt_post_hfi_updates* post = (x10rt_post_hfi_updates*)x10rt_malloc(sizeof(x10rt_post_hfi_updates));
		post->numOps = numOps;
		post->remote_infos = (hfi_remote_update_info_t*)ops;

#ifdef POSTMESSAGES
		pami_result_t status = PAMI_Context_post(state.context, &post->work, post_hfi_updates, (void *)post);
#else
		PAMI_Context_lock(state.context);
		pami_result_t status = post_hfi_updates(state.context, post);
		PAMI_Context_unlock(state.context);
#endif
		if (status != PAMI_SUCCESS) error("Unable to post a hfi_update");
	}
	else
#endif
	{
		#ifdef DEBUG
			fprintf(stderr, "Place %u executing %u remote operations\n", state.myPlaceId, numOps);
		#endif
		for (size_t i=0; i<numOps; i++)
		{
			x10rt_post_rmw* post = (x10rt_post_rmw *)x10rt_malloc(sizeof(x10rt_post_rmw));
			memset(&post->operation, 0, sizeof(pami_rmw_t));
			post->operation.hints.buffer_registered = PAMI_HINT_ENABLE;
			post->operation.type = PAMI_TYPE_UNSIGNED_LONG_LONG;
			post->operation.dest = state.endpoints[ops[i].dest];
			post->operation.remote = (void*)ops[i].dest_buf;
			post->operation.value = &ops[i].value;
			post->operation.operation = (pami_atomic_t)REMOTE_MEMORY_OP_CONVERSION_TABLE[ops[i].op];
#ifdef POSTMESSAGES
			pami_result_t status = PAMI_Context_post(state.context, &post->work, post_rmw, (void *)post);
#else
			PAMI_Context_lock(state.context);
			pami_result_t status = post_rmw(state.context, post);
			PAMI_Context_unlock(state.context);
#endif
			if (status != PAMI_SUCCESS) error("Unable to post a rmw");
        }
	}
}

pami_result_t post_register_mem (pami_context_t context, void* cookie) {
	pami_memregion_t registration;
	size_t registeredSize;

	pami_result_t status = PAMI_Memregion_create(context, ((x10rt_post_general *)cookie)->ptr, ((x10rt_post_general *)cookie)->val, &registeredSize, &registration);
	if (status != PAMI_SUCCESS)
		error("Unable to register memory for remote access");
	if (registeredSize < ((x10rt_post_general *)cookie)->val)
		error("Only able to allocate %u out of %lu requested bytes for remote access", registeredSize, ((x10rt_post_general *)cookie)->val);
	#ifdef DEBUG
		fprintf(stderr, "Place %u registered %lu bytes at %p for remote operations\n", state.myPlaceId, ((x10rt_post_general *)cookie)->val, ((x10rt_post_general *)cookie)->ptr);
	#endif
	free(cookie);
	return PAMI_SUCCESS;
}

void x10rt_net_register_mem (void *ptr, size_t len)
{
	x10rt_post_general* post = (x10rt_post_general *)x10rt_malloc(sizeof(x10rt_post_general));
	post->ptr = ptr;
	post->val = len;

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &post->work, post_register_mem, (void *)post);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_register_mem(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a memory registration");
}

pami_result_t post_geometry_create (pami_context_t context, void* cookie) {
	pami_configuration_t config;
	pami_result_t status = PAMI_ERROR;

	x10rt_post_general *post = (x10rt_post_general *)cookie;
	x10rt_pami_team_create *cbd = (x10rt_pami_team_create *) post->ptr;

	config.name = PAMI_GEOMETRY_OPTIMIZE;
	config.value.intval = 1;

	#ifdef DEBUG
		fprintf(stderr, "creating a new team %u at place %u of size %u, member=%s\n", cbd->teamIndex, state.myPlaceId, state.teams[cbd->teamIndex].size, cbd->member?"true":"false");
	#endif

	if (!cbd->member)
	{
		state.teams[cbd->teamIndex].geometry = PAMI_GEOMETRY_NULL;
		status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, NULL, state.teams[0].geometry, cbd->teamIndex, state.teams[cbd->teamIndex].places, state.teams[cbd->teamIndex].size, context, team_creation_complete, post->ptr);
	}
	else
		status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, &state.teams[cbd->teamIndex].geometry, state.teams[0].geometry, cbd->teamIndex, state.teams[cbd->teamIndex].places, state.teams[cbd->teamIndex].size, context, team_creation_complete, post->ptr);

	if (status != PAMI_SUCCESS) error("Unable to create a new geometry");

	free(cookie);
	return PAMI_SUCCESS;
}

void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
	pami_result_t status = PAMI_ERROR;

	// create a definition for the new team
	uint32_t newTeamId = expandTeams(1)+1;
	state.teams[newTeamId].size = placec;
	state.teams[newTeamId].places = (pami_task_t*)x10rt_malloc(placec*sizeof(pami_task_t));
	if (state.teams[newTeamId].places == NULL) error("unable to allocate memory for holding the places in x10rt_net_team_new");
	memcpy(state.teams[newTeamId].places, placev, placec*sizeof(pami_task_t));

	x10rt_pami_team_create *cookie = (x10rt_pami_team_create*)x10rt_malloc(sizeof(x10rt_pami_team_create));
	if (cookie == NULL) error("Unable to allocate memory for a team_new header");
	cookie->cb2 = ch;
	cookie->arg = arg;
	cookie->teamIndex = newTeamId;

	for (unsigned i=0; i<state.numPlaces; i++)
	{
		if (i != state.myPlaceId)
		{
			x10rt_post_send *post = (x10rt_post_send*)x10rt_malloc(sizeof(x10rt_post_send));
			post->parameters.send.dispatch        = NEW_TEAM;
			post->parameters.send.header.iov_base = &cookie->teamIndex;
			post->parameters.send.header.iov_len  = sizeof(cookie->teamIndex);
			post->parameters.send.data.iov_base   = state.teams[newTeamId].places; // team members
			post->parameters.send.data.iov_len    = placec*sizeof(pami_task_t);
			memset(&post->parameters.send.hints, PAMI_HINT_DEFAULT, sizeof(pami_send_hint_t));
			post->parameters.send.hints.buffer_registered = PAMI_HINT_ENABLE;
			post->parameters.events.cookie        = NULL;
			post->parameters.events.local_fn      = NULL;
			post->parameters.events.remote_fn     = NULL;
			post->parameters.send.dest = state.endpoints[i];
#ifdef POSTMESSAGES
			status = PAMI_Context_post(state.context, &post->work, post_send, (void *)post);
#else
			PAMI_Context_lock(state.context);
			status = post_send(state.context, post);
			PAMI_Context_unlock(state.context);
#endif
			if (status != PAMI_SUCCESS) error("Unable to post a send message");

			#ifdef DEBUG
				fprintf(stderr, "Place %u sending a NEW_TEAM message to place %u\n", state.myPlaceId, i);
			#endif
		}
	}

	// at this point, all the places that are to be a part of this new team have been sent a message to join it.  We need to join too
	// check to see if we are a member of the geometry or not
	cookie->member = false;
	for (int i=0; i<placec; i++)
	{
		if (placev[i] == state.myPlaceId)
		{
			cookie->member = true;
			break;
		}
	}

	x10rt_post_general *post = (x10rt_post_general*)x10rt_malloc(sizeof(x10rt_post_general));
	post->ptr = cookie;
#ifdef POSTMESSAGES
	status = PAMI_Context_post(state.context, &post->work, post_geometry_create, (void *)post);
#else
	PAMI_Context_lock(state.context);
	status = post_geometry_create(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post creation of a new team");
}

static void split_stage2 (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in split_stage2");

	// at this point, we have completed our all-to-all, and know which members will be in which new teams
	x10rt_pami_team_create *cbd = (x10rt_pami_team_create *)cookie;

	unsigned parentTeamSize = x10rt_net_team_sz(cbd->teamIndex);

	// find how many new teams we're creating
	unsigned numNewTeams = 0;
	unsigned myNewTeamSize = 0;
	for (unsigned i=0; i<parentTeamSize; i++)
	{
		if (cbd->colors[i] > numNewTeams)
			numNewTeams = cbd->colors[i];
		if (cbd->colors[i] == cbd->colors[cbd->parent_role])
			myNewTeamSize++;
	}
	numNewTeams++; // values in colors run from 0 to N

	// save the members of the team that matches my color.  Skip the other teams
	unsigned myNewTeamIndex = expandTeams(numNewTeams)+cbd->colors[cbd->parent_role]+1;
	state.teams[myNewTeamIndex].size = myNewTeamSize;
	state.teams[myNewTeamIndex].places = (pami_task_t*)x10rt_malloc(myNewTeamSize*sizeof(pami_task_t));
	if (state.teams[myNewTeamIndex].places == NULL) error("Unable to allocate memory to hold the team member list");
	int index = 0;
	for (unsigned i=0; i<parentTeamSize; i++)
	{   // each team member was a member in the parent.  Copy the ID's over, preserving the order.
		if (cbd->colors[i] == cbd->colors[cbd->parent_role]) // if this member is in my new team
		{
			if (cbd->teamIndex == 0) // world geometry doesn't use the places array
				state.teams[myNewTeamIndex].places[index] = i;
			else
				state.teams[myNewTeamIndex].places[index] = state.teams[cbd->teamIndex].places[i];
			index++;
		}
	}
	#ifdef DEBUG
		fprintf(stderr, "Place %u creating new split team %u from team %u, with %u members: %u", state.myPlaceId, myNewTeamIndex, cbd->teamIndex, myNewTeamSize, state.teams[myNewTeamIndex].places[0]);
		for (unsigned i=1; i<myNewTeamSize; i++)
			fprintf(stderr, ",%u", state.teams[myNewTeamIndex].places[i]);
		fprintf(stderr, ".\n");
	#endif

	pami_configuration_t config;
	config.name = PAMI_GEOMETRY_OPTIMIZE;
	config.value.intval = 1;

	pami_result_t   status = PAMI_ERROR;
	pami_geometry_t parentGeometry = state.teams[cbd->teamIndex].geometry;
	cbd->teamIndex = myNewTeamIndex;
	status = PAMI_Geometry_create_tasklist(state.client, 0, &config, 1, &state.teams[myNewTeamIndex].geometry, parentGeometry, myNewTeamIndex, state.teams[myNewTeamIndex].places, myNewTeamSize, state.context, team_creation_complete, cbd);
	if (status != PAMI_SUCCESS) error("Unable to create a new team");
}

pami_result_t post_collective_create (pami_context_t context, void* cookie) {
	pami_result_t status = PAMI_Collective(context, &((x10rt_post_collective_create *)cookie)->operation);
	if (status != PAMI_SUCCESS) error("Unable to execute collective operation");
	free(cookie);
	return PAMI_SUCCESS;
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role, x10rt_place color,
		x10rt_place new_role, x10rt_completion_handler2 *ch, void *arg)
{
	// we need to determine how many new teams are getting created (# of colors), and who is in each team.
	// we learn this through an all-to-all

	#ifdef DEBUG
		fprintf(stderr, "Place %u splitting team %u, new color=%u\n", state.myPlaceId, parent, color);
	#endif
	unsigned parentTeamSize = x10rt_net_team_sz(parent);
	// allocate a buffer to hold the color for each place
	x10rt_place *colors = (x10rt_place*) x10rt_malloc(sizeof(x10rt_place) * parentTeamSize);
	if (colors == NULL) error("Unable to allocate memory for the team colors buffer");
	colors[parent_role] = color;

	// determine an algorithm for the all-to-all

	// Issue the collective
	x10rt_pami_team_create *cbd = (x10rt_pami_team_create *)x10rt_malloc(sizeof(x10rt_pami_team_create));
	if (cbd == NULL) error("Unable to allocate memory for a team split structure");
	cbd->cb2 = ch;
	cbd->arg = arg;
	cbd->colors = colors;
	cbd->teamIndex = parent;
	cbd->parent_role = parent_role;
	cbd->member = true;

	x10rt_post_collective_create *post = (x10rt_post_collective_create*)x10rt_malloc(sizeof(x10rt_post_collective_create));
	post->operation.cb_done = split_stage2;
	post->operation.cookie = cbd;
	post->operation.algorithm = state.teams[parent].algorithm[PAMI_XFER_ALLGATHER];
	post->operation.cmd.xfer_allgather.rcvbuf = (char*)colors;
	post->operation.cmd.xfer_allgather.rtype = PAMI_TYPE_BYTE;
	post->operation.cmd.xfer_allgather.rtypecount = sizeof(x10rt_place);// *parentTeamSize;
	post->operation.cmd.xfer_allgather.sndbuf = (char*)&color;
	post->operation.cmd.xfer_allgather.stype = PAMI_TYPE_BYTE;
	post->operation.cmd.xfer_allgather.stypecount = sizeof(x10rt_place);

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &post->work, post_collective_create, (void *)post);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective_create(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post an allgather for team_split");
}

pami_result_t post_geometry_destroy (pami_context_t context, void* cookie) {
	x10rt_post_general *post = (x10rt_post_general *)cookie;

	pami_result_t status = PAMI_Geometry_destroy(state.client, &state.teams[post->val].geometry, context, team_destroy_complete, post->ptr);
	if (status != PAMI_SUCCESS) error("Unable to destroy geometry");

	free(cookie);
	return PAMI_SUCCESS;
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
	x10rt_pami_team_destroy* ptd = (x10rt_pami_team_destroy*)x10rt_malloc(sizeof(x10rt_pami_team_destroy));
	ptd->teamid = team;
	ptd->arg = arg;
	ptd->tch = ch;

	x10rt_post_general *post = (x10rt_post_general*)x10rt_malloc(sizeof(x10rt_post_general));
	post->val = team;
	post->ptr = ptd;

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &post->work, post_geometry_destroy, (void *)post);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_geometry_destroy(state.context, post);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a geometry destroy");
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
	if (team > state.lastTeamIndex)
		return 0;
	return state.teams[team].size;
}

static void collective_operation_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in collective_operation_complete");

	x10rt_pami_team_callback *cbd = (x10rt_pami_team_callback*)cookie;
	#ifdef DEBUG
		fprintf(stderr, "Place %u completed collective operation. cookie=%p\n", state.myPlaceId, cookie);
	#endif
	cbd->tcb(cbd->arg);
	free(cookie);
}

static void internal_alltoall_complete (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in internal_alltoall_complete");

	x10rt_pami_internal_alltoall *cbd = (x10rt_pami_internal_alltoall*)cookie;

	#ifdef DEBUG
		fprintf(stderr, "Place %u completed remote updates for internal alltoall. cookie=%p\n", state.myPlaceId, cookie);
	#endif
	// Done!  block on a barrier, followed by the original x10-level callback

	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a barrier callback header");
	tcb->tcb = cbd->tch;
	tcb->arg = cbd->arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[cbd->teamid].algorithm[PAMI_XFER_BARRIER];
	pami_result_t status = PAMI_Collective(context, &tcb->operation);
	if (status != PAMI_SUCCESS) error("Unable to issue post-alltoall barrier on team %u", cbd->teamid);
	free(cookie);
}

static void internal_alltoall_step (pami_context_t   context,
                       void          * cookie,
                       pami_result_t    result)
{
	if (result != PAMI_SUCCESS)
		error("Error detected in internal_alltoall_step");

	pami_result_t status = PAMI_ERROR;
	x10rt_pami_internal_alltoall *cbd = (x10rt_pami_internal_alltoall*)cookie;
	// no need to lock the context in here, as it was already locked by the surrounding callback

	//int64_t remotePlace = (state.myPlaceId + cbd->currentPlaceOffset) % state.teams[cbd->teamid].size; // shift the place we start with
	int64_t remotePlace = state.stepOrder[cbd->currentPlaceOffset];
	cbd->parameters.rma.dest = remotePlace;
	cbd->parameters.addr.local = (void*)((char*)(cbd->sbuf) + (remotePlace * cbd->dataSize) + cbd->currentChunkOffset);
	cbd->parameters.addr.remote = (void*)((char*)(cbd->dbuf) + (state.myPlaceId * cbd->dataSize) + cbd->currentChunkOffset);

	cbd->currentPlaceOffset++;
	if (cbd->currentPlaceOffset >= state.teams[cbd->teamid].size)
	{
		cbd->currentPlaceOffset = 0;
		cbd->currentChunkOffset+=cbd->chunksize;
		if (cbd->currentChunkOffset >= cbd->dataSize)
			cbd->parameters.rma.done_fn = internal_alltoall_complete;
	}

	status = PAMI_Put(context, &cbd->parameters);
	if (status != PAMI_SUCCESS) error("Error with the remote Put in internal all-to-all %u\n", status);
}

pami_result_t post_collective (pami_context_t context, void* cookie) {
	pami_result_t status = PAMI_Collective(context, &((x10rt_pami_team_callback*)cookie)->operation);
	if (status != PAMI_SUCCESS) error("Unable to execute a collective operation");
	// do not free the cookie, as it's used (and freed) later
	return PAMI_SUCCESS;
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role, x10rt_completion_handler *ch, void *arg)
{
	// Issue the collective
	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a barrier callback header");
	tcb->tcb = ch;
	tcb->arg = arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_BARRIER];
	#ifdef DEBUG
		fprintf(stderr, "Place %u, role %u executing barrier via thread %u. cookie=%p\n", state.myPlaceId, role, pthread_self(), (void*)tcb);
	#endif

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective(state.context, tcb);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a barrier on team %u", team);
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	#ifdef DEBUG
		fprintf(stderr, "Place %u executing broadcast of %lu %lu-byte elements on team %u, with role=%u, root=%u\n", state.myPlaceId, count, el, team, role, root);
	#endif

	// Issue the collective
	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a broadcast callback header");
	tcb->tcb = ch;
	tcb->arg = arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_BROADCAST];
	tcb->operation.cmd.xfer_broadcast.type = PAMI_TYPE_BYTE;
	tcb->operation.cmd.xfer_broadcast.typecount = count*el;
	if (team == 0)
		tcb->operation.cmd.xfer_broadcast.root = root;
	else
		tcb->operation.cmd.xfer_broadcast.root = state.teams[team].places[root];

	if (role == root)
		tcb->operation.cmd.xfer_broadcast.buf = (char*)sbuf;
	else
		tcb->operation.cmd.xfer_broadcast.buf = (char*)dbuf;

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective(state.context, tcb);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a broadcast on team %u", team);

	// copy the data for the root separately.  PAMI does not do this for us.
	if (role == root)
		memcpy(dbuf, sbuf, count*el);
}

void x10rt_net_scatter (x10rt_team team, x10rt_place role, x10rt_place root, const void *sbuf,
		void *dbuf, size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// Issue the collective
	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a scatter callback header");
	tcb->tcb = ch;
	tcb->arg = arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_SCATTER];
	tcb->operation.cmd.xfer_scatter.rcvbuf = (char*)dbuf;
	if (team == 0)
		tcb->operation.cmd.xfer_scatter.root = root;
	else
		tcb->operation.cmd.xfer_scatter.root = state.teams[team].places[root];
	tcb->operation.cmd.xfer_scatter.rtype = PAMI_TYPE_BYTE;
	tcb->operation.cmd.xfer_scatter.rtypecount = el*count;
	tcb->operation.cmd.xfer_scatter.sndbuf = (char*)sbuf;
	tcb->operation.cmd.xfer_scatter.stype = PAMI_TYPE_BYTE;
	tcb->operation.cmd.xfer_scatter.stypecount = el*count;

	#ifdef DEBUG
		fprintf(stderr, "Place %u executing scatter: role=%u, root=%u\n", state.myPlaceId, role, root);
	#endif

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective(state.context, tcb);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a scatter on team %u", team);
    // The local copy is not needed.  PAMI handles this for us.
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		size_t el, size_t count, x10rt_completion_handler *ch, void *arg)
{
	if (state.teams[team].algorithm[PAMI_XFER_ALLTOALL] == PAMI_ALGORITHM_NULL) // use our own algorithm, not PAMI's.  The value is -1*chunksize
	{
		// TODO - the code below only works with world, and only when the src and dst arrays are symmetric!
		if (team != 0) error("Internal implementation of ALLTOALL only works with world\n");

		#ifdef DEBUG
			fprintf(stderr, "Place %u, role %u executing internal AllToAll with team %u. chunksize=%lu\n", state.myPlaceId, role, team, el*count/state.a2achunks);
		#endif
		x10rt_pami_internal_alltoall *tcb = (x10rt_pami_internal_alltoall *)x10rt_malloc(sizeof(x10rt_pami_internal_alltoall));
		if (tcb == NULL) error("Unable to allocate memory for the all-to-all cookie");
		tcb->tch = ch;
		tcb->arg = arg;
		tcb->sbuf = sbuf;
		tcb->dbuf = dbuf;
		tcb->teamid = team;
		tcb->dataSize = el*count;
		tcb->chunksize = tcb->dataSize / state.a2achunks;
		tcb->currentChunkOffset = 0;
		tcb->currentPlaceOffset = 0;
		memset(&tcb->parameters, 0, sizeof (tcb->parameters));
		tcb->parameters.rma.bytes   = tcb->chunksize;
		tcb->parameters.rma.cookie  = tcb;
		tcb->parameters.rma.done_fn = internal_alltoall_step;
		tcb->parameters.rma.hints.buffer_registered = PAMI_HINT_ENABLE;

		pami_xfer_t operation;
		memset(&operation, 0, sizeof(operation));
		operation.cb_done = internal_alltoall_step;
		operation.cookie = tcb;
		operation.algorithm = state.teams[team].algorithm[PAMI_XFER_BARRIER];
		#ifdef DEBUG
			fprintf(stderr, "Place %u executing pre-alltoall barrier. cookie=%p\n", state.myPlaceId, (void*)tcb);
		#endif

#ifdef POSTMESSAGES
		pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
		PAMI_Context_lock(state.context);
		pami_result_t status = post_collective(state.context, tcb);
		PAMI_Context_unlock(state.context);
#endif
		if (status != PAMI_SUCCESS) error("Unable to post a pre-alltoall barrier on team %u", team);
	}
	else
	{
		// Issue the PAMI collective
		x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
		if (tcb == NULL) error("Unable to allocate memory for the all-to-all cookie");
		tcb->tcb = ch;
		tcb->arg = arg;
		memset(&tcb->operation, 0, sizeof (tcb->operation));
		tcb->operation.cb_done = collective_operation_complete;
		tcb->operation.cookie = tcb;
		tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_ALLTOALL];
		tcb->operation.cmd.xfer_alltoall.rcvbuf = (char*)dbuf;
		tcb->operation.cmd.xfer_alltoall.rtype = PAMI_TYPE_BYTE;
		tcb->operation.cmd.xfer_alltoall.rtypecount = el*count;
		tcb->operation.cmd.xfer_alltoall.sndbuf = (char*)sbuf;
		tcb->operation.cmd.xfer_alltoall.stype = PAMI_TYPE_BYTE;
		tcb->operation.cmd.xfer_alltoall.stypecount = el*count;

		#ifdef DEBUG
			fprintf(stderr, "Place %u, role %u executing AllToAll with team %u. cookie=%p\n", state.myPlaceId, role, team, (void*)tcb);
		#endif

#ifdef POSTMESSAGES
		pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
		PAMI_Context_lock(state.context);
		pami_result_t status = post_collective(state.context, tcb);
		PAMI_Context_unlock(state.context);
#endif
		if (status != PAMI_SUCCESS) error("Unable to issue an all-to-all on team %u", team);
	    // The local copy is not needed.  PAMI handles this for us.
	}
}

void x10rt_net_reduce (x10rt_team team, x10rt_place role,
                        x10rt_place root, const void *sbuf, void *dbuf,
                        x10rt_red_op_type op, 
                        x10rt_red_type dtype,
                        size_t count,
                        x10rt_completion_handler *ch, void *arg)
{
	// Issue the collective
	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a reduce callback header");
	tcb->tcb = ch;
	tcb->arg = arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_REDUCE];
	tcb->operation.cmd.xfer_reduce.sndbuf = (char*)sbuf;
	tcb->operation.cmd.xfer_reduce.stype = DATATYPE_CONVERSION_TABLE[dtype];
	tcb->operation.cmd.xfer_reduce.stypecount = count;
	tcb->operation.cmd.xfer_reduce.rcvbuf = (char*)dbuf;
	tcb->operation.cmd.xfer_reduce.rtype = DATATYPE_CONVERSION_TABLE[dtype];
	tcb->operation.cmd.xfer_reduce.rtypecount = count;
	if (dtype == X10RT_RED_TYPE_DBL_S32)
	{   // operations on LOC datatypes are different from regular types
		if (OPERATION_CONVERSION_TABLE[op] == PAMI_DATA_MAX)
			tcb->operation.cmd.xfer_reduce.op = PAMI_DATA_MAXLOC;
		else if (OPERATION_CONVERSION_TABLE[op] == PAMI_DATA_MIN)
			tcb->operation.cmd.xfer_reduce.op = PAMI_DATA_MINLOC;
		else
			error("Unknown operation type %i", op);
	}
	else
		tcb->operation.cmd.xfer_reduce.op = OPERATION_CONVERSION_TABLE[op];
	tcb->operation.cmd.xfer_reduce.data_cookie = NULL;
	tcb->operation.cmd.xfer_reduce.commutative = 1;
	if (team == 0)
		tcb->operation.cmd.xfer_reduce.root = root;
	else
		tcb->operation.cmd.xfer_reduce.root = state.teams[team].places[root];
	#ifdef DEBUG
		fprintf(stderr, "Place %u executing reduce, with type=%u and op=%u\n", state.myPlaceId, dtype, op);
	#endif

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective(state.context, tcb);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post a reduce on team %u", team);
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role, const void *sbuf, void *dbuf,
		x10rt_red_op_type op, x10rt_red_type dtype, size_t count, x10rt_completion_handler *ch, void *arg)
{
	// Issue the collective
	x10rt_pami_team_callback *tcb = (x10rt_pami_team_callback *)x10rt_malloc(sizeof(x10rt_pami_team_callback));
	if (tcb == NULL) error("Unable to allocate memory for a allreduce callback header");
	tcb->tcb = ch;
	tcb->arg = arg;
	memset(&tcb->operation, 0, sizeof (tcb->operation));
	tcb->operation.cb_done = collective_operation_complete;
	tcb->operation.cookie = tcb;
	tcb->operation.algorithm = state.teams[team].algorithm[PAMI_XFER_ALLREDUCE];
	tcb->operation.cmd.xfer_allreduce.sndbuf = (char*)sbuf;
	tcb->operation.cmd.xfer_allreduce.stype = DATATYPE_CONVERSION_TABLE[dtype];
	tcb->operation.cmd.xfer_allreduce.stypecount = count;
	tcb->operation.cmd.xfer_allreduce.rcvbuf = (char*)dbuf;
	tcb->operation.cmd.xfer_allreduce.rtype = DATATYPE_CONVERSION_TABLE[dtype];
	tcb->operation.cmd.xfer_allreduce.rtypecount = count;
	if (dtype == X10RT_RED_TYPE_DBL_S32)
	{   // operations on LOC datatypes are different from regular types
		if (OPERATION_CONVERSION_TABLE[op] == PAMI_DATA_MAX)
			tcb->operation.cmd.xfer_allreduce.op = PAMI_DATA_MAXLOC;
		else if (OPERATION_CONVERSION_TABLE[op] == PAMI_DATA_MIN)
			tcb->operation.cmd.xfer_allreduce.op = PAMI_DATA_MINLOC;
		else
			error("Unknown operation type %i", op);
	}
	else
		tcb->operation.cmd.xfer_allreduce.op = OPERATION_CONVERSION_TABLE[op];	
	tcb->operation.cmd.xfer_allreduce.data_cookie = NULL;
	tcb->operation.cmd.xfer_allreduce.commutative = 1;
	#ifdef DEBUG
		fprintf(stderr, "Place %u executing allreduce, with type=%u and op=%u\n", state.myPlaceId, dtype, op);
	#endif

#ifdef POSTMESSAGES
	pami_result_t status = PAMI_Context_post(state.context, &tcb->work, post_collective, (void *)tcb);
#else
	PAMI_Context_lock(state.context);
	pami_result_t status = post_collective(state.context, tcb);
	PAMI_Context_unlock(state.context);
#endif
	if (status != PAMI_SUCCESS) error("Unable to post an allreduce on team %u", team);
}
