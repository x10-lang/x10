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

#include <cstdlib>
#include <cstdio>
#include <string.h>
#include <errno.h>

#include <x10rt_net.h>
#include "Launcher.h"

// mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

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
} state;

/*********************************************
 *  utility methods
*********************************************/

void error(const char* message)
{
	printf("Fatal Error: %s: %s\n", message, strerror(errno));
	abort();
}

static void stub (void)
{
    fprintf(stderr,"Not implemented yet!\n");
    abort();
}


void x10rt_net_init (int *, char ***, x10rt_msg_type *)
{
	// TODO call the launcher.
	// If this is to be a launcher process, this method will not return.

	// determine the number of places (processes) to create, using an environment variable
	char* NPROCS = getenv(X10LAUNCHER_NPROCS);
	if (NPROCS == NULL)
	{
		fprintf(stderr, "%s not set.  Assuming 1 place\n", X10LAUNCHER_NPROCS);
		state.numPlaces = 1;
	}
	else
		state.numPlaces = atol(NPROCS);

}

void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb)
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

	state.callBackTable[msg_type].handler = cb;
	state.callBackTable[msg_type].finder = NULL;
	state.callBackTable[msg_type].notifier = NULL;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered standard message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
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
	state.callBackTable[msg_type].finder = cb1;
	state.callBackTable[msg_type].notifier = cb2;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered put message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
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
	state.callBackTable[msg_type].finder = cb1;
	state.callBackTable[msg_type].notifier = cb2;

	#ifdef DEBUG
		printf("X10rt.Socket: place %lu registered get message %u\n", state.myPlaceId, msg_type);
	#endif
}


void x10rt_net_internal_barrier (void)
{
	stub();
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

void x10rt_net_send_msg (x10rt_msg_params *)
{ stub(); }

void x10rt_net_send_get (x10rt_msg_params *, void *, x10rt_copy_sz )
{ stub(); }

void x10rt_net_send_put (x10rt_msg_params *, void *, x10rt_copy_sz)
{ stub(); }

void x10rt_net_probe ()
{ }

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{ stub(); }

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{ return NULL; }

void x10rt_net_finalize (void)
{
	#ifdef DEBUG
		printf("X10rt.Socket: shutting down place %lu\n", state.myPlaceId);
	#endif
		// TODO
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
    stub();
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
    stub();
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
    stub();
    return 0;
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    stub();
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
    stub();
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op,
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    stub();
}
