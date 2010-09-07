/********************************************************************************************
 * (c) Copyright IBM Corporation 2010
 * Written by Ben Herta for IBM, bherta@us.ibm.com, Septemer 2010
 * This code supports multi-place x10 programs running on one or more machines.
 * It uses SSH to spawn processes on remote machines.  A user should set up any hosts to have
 * an SSH daemon, and public/private keys configured so that there isn't any password prompt.
 * As long as you have SSH daemons, you shouldn't need any other system dependencies.
 *
 * The pgas_socket backend has its own buffers and it spawns threads to populate those buffers,
 * getting data out of the network as quickly as possible.  This implementation does not do this.
 * The network is read/written directly.
 **********************************************************************************************/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <errno.h>

#include <x10rt_net.h>
#include "Launcher.h"
#include "TCP.h"

// mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

enum MSGTYPE {CONTROL, STANDARD, PUT, GET, GET_COMPLETED};

struct x10SocketCallback
{
	handlerCallback handler;
	finderCallback finder;
	notifierCallback notifier;
};

struct x10SocketState
{
	x10rt_place numPlaces; // how many places we have.
	x10rt_place myPlaceId; // which place we're at.  Also used as the index to the array below. Local per-place memory is used.
	x10SocketCallback* callBackTable; // I'm told message ID's run from 0 to n, so a simple array using message indexes is the best solution for this table.  Local per-place memory is used.
	x10rt_msg_type callBackTableSize; // length of the above array
	int* socketLinks; // handles to each remote place.  Unconnected links have a value of 0.  The slot for my place holds my listen port.
} state;

/*********************************************
 *  utility methods
*********************************************/

void error(const char* message)
{
	fprintf(stderr, "Fatal Error: %s: %s\n", message, strerror(errno));
	abort();
}

/******************************************************
 *  Main API calls.  See x10rt_net.h for documentation
*******************************************************/
void x10rt_net_init (int * argc, char ***argv, x10rt_msg_type *counter)
{
	// If this is to be a launcher process, this method will not return.
	Launcher_Init(*argc, *argv);

	// determine the number of places
	char* NPROCS = getenv(X10LAUNCHER_NPROCS);
	if (NPROCS == NULL)
	{
		fprintf(stderr, "%s not set.  Assuming 1 place\n", X10LAUNCHER_NPROCS);
		state.numPlaces = 1;
	}
	else
		state.numPlaces = atol(NPROCS);

	// determine my place ID
	char* ID = getenv(X10LAUNCHER_MYID);
	if (ID == NULL)
		error("X10LAUNCHER_MYID not set!");
	else
		state.myPlaceId = atol(ID);

	state.socketLinks = new int[state.numPlaces];
	for (unsigned int i=0; i<state.numPlaces; i++)
		state.socketLinks[i] = 0;

	// open local listen port.
	// TODO: for now, use a well-known fixed port number.  This will be changed to dynamic before it's released.
	unsigned listenPort = 7000+state.myPlaceId;
	//unsigned listenPort = 0;
	state.socketLinks[state.myPlaceId] = TCP::listen(&listenPort, 10);
	if (state.socketLinks[state.myPlaceId] < 0)
		error("cannot create listener port");

	// TODO establish a link to the local launcher, and tell it our port.
	// TODO wait for the launcher to give us our link information.
	// TODO if not using lazy connections, establish links to other places.
}

void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *callback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10SocketCallback*)realloc(state.callBackTable, sizeof(struct x10SocketCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
		state.callBackTableSize = msg_type+1;
	}

	state.callBackTable[msg_type].handler = callback;
	state.callBackTable[msg_type].finder = NULL;
	state.callBackTable[msg_type].notifier = NULL;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered standard message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *finderCallback, x10rt_notifier *notifierCallback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10SocketCallback*)realloc(state.callBackTable, sizeof(struct x10SocketCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = finderCallback;
	state.callBackTable[msg_type].notifier = notifierCallback;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered put message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type, x10rt_finder *finderCallback, x10rt_notifier *notifierCallback)
{
	// register a pointer to methods that will handle specific message types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10SocketCallback*)realloc(state.callBackTable, sizeof(struct x10SocketCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = finderCallback;
	state.callBackTable[msg_type].notifier = notifierCallback;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered get message %u\n", state.myPlaceId, msg_type);
	#endif
}


void x10rt_net_internal_barrier (void)
{
	error("x10rt_net_internal_barrier not implemented");
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

void x10rt_net_send_msg (x10rt_msg_params *parameters)
{
	if (state.socketLinks[parameters->dest_place] == 0)
		// TODO: change to use remote hosts
		state.socketLinks[parameters->dest_place] = TCP::connect("localhost", 7000+parameters->dest_place, 0);

	// write out the x10SocketMessage data
	enum MSGTYPE m = STANDARD;
	TCP::write(state.socketLinks[parameters->dest_place], &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place], &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place], parameters->msg, parameters->len);
}

void x10rt_net_send_get (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
	if (state.socketLinks[parameters->dest_place] == 0)
		// TODO: change to use remote hosts
		state.socketLinks[parameters->dest_place] = TCP::connect("localhost", 7000+parameters->dest_place, 0);

	// write out the x10SocketMessage data
	enum MSGTYPE m = GET;
	TCP::write(state.socketLinks[parameters->dest_place], &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place], &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place], parameters->msg, parameters->len);

	// TODO set up receive buffer
}

void x10rt_net_send_put (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
	if (state.socketLinks[parameters->dest_place] == 0)
		// TODO: change to use remote hosts
		state.socketLinks[parameters->dest_place] = TCP::connect("localhost", 7000+parameters->dest_place, 0);

	// write out the x10SocketMessage data
	enum MSGTYPE m = PUT;
	TCP::write(state.socketLinks[parameters->dest_place], &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place], &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place], &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place], parameters->msg, parameters->len);
	TCP::write(state.socketLinks[parameters->dest_place], &bufferLen, sizeof(x10rt_copy_sz));
	if (bufferLen > 0)
		TCP::write(state.socketLinks[parameters->dest_place], buffer, bufferLen);
}

void x10rt_net_probe ()
{
}

void x10rt_net_finalize (void)
{
	#ifdef DEBUG
		printf("X10rt.Socket: shutting down place %lu\n", state.myPlaceId);
	#endif

	// TODO - close sockets

	free(state.socketLinks);
}

/*************************************************
 * TODO - talk to Cunningham to see if anything
 * needs to be done for these methods below.
 *************************************************/

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{
	error("x10rt_net_remote_op not implemented");
}

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{
	error("x10rt_net_register_mem not implemented");
	return NULL;
}


int x10rt_net_supports (x10rt_opt o)
{
    switch (o) {
        default: return 0;
    }
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

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
	error("x10rt_net_team_split not implemented");
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_barrier not implemented");
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_bcast not implemented");
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_alltoall not implemented");
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op,
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
	error("x10rt_net_allreduce not implemented");
}
