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
#include <unistd.h> // for close()
#include <errno.h> // for the strerror function
#include <sys/socket.h> // for sockets
#include <pthread.h> // for locks on the sockets
#include <poll.h> // for poll()

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
	struct pollfd* socketLinks; // the file descriptors for each socket to other places
	pthread_mutex_t* writeLocks; // a lock to prevent overlapping writes on each socket
	pthread_mutex_t* readLocks; // a lock to prevent overlapping reads on each socket
} state;

/*********************************************
 *  utility methods
*********************************************/

void error(const char* message)
{
	fprintf(stderr, "Fatal Error: %s: %s\n", message, strerror(errno));
	abort();
}

int initLink(int remotePlace)
{
	if (state.socketLinks[remotePlace].fd <= 0)
	{
		// TODO: change to use remote hosts
		if ((state.socketLinks[remotePlace].fd = TCP::connect("localhost", 7000+remotePlace, 0)) > 0)
		{
			state.socketLinks[remotePlace].events = POLLHUP | POLLERR | POLLIN | POLLPRI;
			pthread_mutex_init(&state.readLocks[remotePlace], NULL);
			pthread_mutex_init(&state.writeLocks[remotePlace], NULL);
		}
	}
	return state.socketLinks[remotePlace].fd;
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

	state.socketLinks = new struct pollfd[state.numPlaces];
	state.writeLocks = new pthread_mutex_t[state.numPlaces];
	state.readLocks = new pthread_mutex_t[state.numPlaces];
	for (unsigned int i=0; i<state.numPlaces; i++)
		state.socketLinks[i].fd = 0;

	// open local listen port.
	// TODO: for now, use a well-known fixed port number.  This will be changed to dynamic before it's released.
	unsigned listenPort = 7000+state.myPlaceId;
	//unsigned listenPort = 0;
	state.socketLinks[state.myPlaceId].fd = TCP::listen(&listenPort, 10);
	if (state.socketLinks[state.myPlaceId].fd < 0)
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
	// TODO: if we're not using lazy initialization, remove this line
	if (initLink(parameters->dest_place) <= 0)
		error("establishing a connection");
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	// write out the x10SocketMessage data
	// Format: type, source, p.type, p.len, p.msg
	enum MSGTYPE m = STANDARD;
	TCP::write(state.socketLinks[parameters->dest_place].fd, &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place].fd, parameters->msg, parameters->len);
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_send_get (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
	// TODO: if we're not using lazy initialization, remove this line
	if (initLink(parameters->dest_place) <= 0)
		error("establishing a connection");
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	// write out the x10SocketMessage data
	// Format: type, source, p.type, p.len, p.msg, bufferlen, bufferADDRESS
	enum MSGTYPE m = GET;
	TCP::write(state.socketLinks[parameters->dest_place].fd, &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place].fd, parameters->msg, parameters->len);
	TCP::write(state.socketLinks[parameters->dest_place].fd, &bufferLen, sizeof(x10rt_copy_sz));
	TCP::write(state.socketLinks[parameters->dest_place].fd, buffer, sizeof(void*));
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_send_put (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
	// TODO: if we're not using lazy initialization, remove this line
	if (initLink(parameters->dest_place) <= 0)
		error("establishing a connection");
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	// write out the x10SocketMessage data
	// Format: type, source, p.type, p.len, p.msg, bufferlen, buffer contents
	enum MSGTYPE m = PUT;
	TCP::write(state.socketLinks[parameters->dest_place].fd, &m, sizeof(enum MSGTYPE));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &state.myPlaceId, sizeof(x10rt_place));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->type, sizeof(x10rt_msg_type));
	TCP::write(state.socketLinks[parameters->dest_place].fd, &parameters->len, sizeof(uint32_t));
	if (parameters->len > 0)
		TCP::write(state.socketLinks[parameters->dest_place].fd, parameters->msg, parameters->len);
	TCP::write(state.socketLinks[parameters->dest_place].fd, &bufferLen, sizeof(x10rt_copy_sz));
	if (bufferLen > 0)
		TCP::write(state.socketLinks[parameters->dest_place].fd, buffer, bufferLen);
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_probe ()
{
	int ret = poll(state.socketLinks, state.numPlaces, 0);
	if (ret > 0)
	{
		/* An event on one of the fds has occurred. */
		// POLLHUP | POLLERR | POLLIN | POLLPRI;
	    for (int i=0; i<state.numPlaces; i++)
	    {
	    	if ((state.socketLinks[i].revents & POLLHUP) || (state.socketLinks[i].revents & POLLERR))
	    	{
	    		// TODO connection broken
	    	}
	    	if ((state.socketLinks[i].revents & POLLIN) || (state.socketLinks[i].revents & POLLPRI))
	    	{
	    		// TODO data to read
	    	}
	    }
	}
}

void x10rt_net_finalize (void)
{
	#ifdef DEBUG
		printf("X10rt.Socket: shutting down place %lu\n", state.myPlaceId);
	#endif

	for (unsigned int i=0; i<state.numPlaces; i++)
	{
		if (state.socketLinks[i].fd != 0)
		{
			close(state.socketLinks[i].fd);
			pthread_mutex_destroy(&state.readLocks[i]);
			pthread_mutex_destroy(&state.writeLocks[i]);
		}
	}
	free(state.socketLinks);
	free(state.readLocks);
	free(state.writeLocks);
}

/*************************************************
 * All of the stuff below is not used in the socket
 * backend, and we rely on the emulation layer to
 * convert these into messages for us.
 *************************************************/


void x10rt_net_internal_barrier (void){}

int x10rt_net_supports (x10rt_opt o)
{
    return 0;
}

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
