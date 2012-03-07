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
 *
 * This code supports multi-place x10 programs running on one or more machines.
 * It uses SSH to spawn processes on remote machines.  A user should set up any hosts to have
 * an SSH daemon, and public/private keys configured so that there isn't any password prompt.
 * As long as you have SSH daemons, you shouldn't need any other system dependencies.
 *
 * The pgas_socket backend has its own buffers and it spawns threads to populate those buffers,
 * getting data out of the network as quickly as possible.  This implementation does not do this.
 * The network is read/written directly.
 **********************************************************************************************/

#ifdef __CYGWIN__
#undef __STRICT_ANSI__ // Strict ANSI mode is too strict in Cygwin
#endif

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <unistd.h> // for close() and sleep()
#include <errno.h> // for the strerror function
#include <sys/socket.h> // for sockets
#include <sys/param.h>
#include <pthread.h> // for locks on the sockets
#include <poll.h> // for poll()
#ifndef BSD
#include <alloca.h> // for alloca()
#endif
#include <fcntl.h>

#include <x10rt_net.h>
#include <x10rt_internal.h>
#include "Launcher.h"
#include "DebugHelper.h"
#include "TCP.h"

// mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED};
#define COPY_PUT_GET_BUFFER false // if the network is full, and a message needs to be sent in chunks, should the put/get buffer arg be copied, or reused?
//#define DEBUG 1
//#define DEBUG_MESSAGING 1

struct x10SocketCallback
{
	handlerCallback handler;
	finderCallback finder;
	notifierCallback notifier;
};

struct x10SocketDataToWrite
{
	char* data;
	unsigned size;
	unsigned remainingToWrite;
	unsigned place;
	bool deleteBufferWhenComplete;
	struct x10SocketDataToWrite* next;
};

struct x10SocketState
{
	x10rt_place numPlaces; // how many places we have.
	x10rt_place myPlaceId; // which place we're at.  Also used as the index to the array below. Local per-place memory is used.
	x10SocketCallback* callBackTable; // I'm told message ID's run from 0 to n, so a simple array using message indexes is the best solution for this table.  Local per-place memory is used.
	x10rt_msg_type callBackTableSize; // length of the above array
	char* myhost; // my own hostname, so I can detect places that are on the same machine and use localhost instead.
	bool yieldAfterProbe; // a little flag that adds sched_yield() after probe, for better performance when there are more workers than processors on a machine (or when debugging).
	bool linkAtStartup; // this flag tells us that we should establish all our connections at startup, not on-demand.  It gets flipped after all links are up.
	pthread_mutex_t readLock; // a lock to prevent overlapping reads on each socket
	uint32_t nextSocketToCheck; // this is used in the socket read loop so that we don't give preference to the low-numbered places
	struct pollfd* socketLinks; // the file descriptors for each socket to other places
	pthread_mutex_t* writeLocks; // a lock to prevent overlapping writes on each socket
	// special case for index=myPlaceId on the above three.  The socket link is the local listen socket,
	// the read lock is used for listen socket handling and write lock for launcher communication
	bool useNonblockingLinks; // flag to enable/disable buffered writes.  True by default
	struct x10SocketDataToWrite* pendingWrites;
	pthread_mutex_t pendingWriteLock;
} state;

bool probe (bool onlyProcessAccept);

/*********************************************
 *  utility methods
*********************************************/

void error(const char* message)
{
	if (errno)
		fprintf(stderr, "Fatal Error at place %u: %s: %s\n", state.myPlaceId, message, strerror(errno));
	else
		fprintf(stderr, "Fatal Error at place %u: %s\n", state.myPlaceId, message);
	fflush(stderr);
	abort();
}

/*
 * This method determines if we should use a specific port number, or ask the OS
 * for one.  It looks at the X10_FORCEPORTS environment variable, which can take two forms.
 * Either it has a comma-separated list of numbers, one per place, or it has a single number,
 * and each place will use that port number + their place ID.
 */
int getPortEnv(unsigned int whichPlace)
{
	int lp = 0;
	char* p = getenv(X10_FORCEPORTS);
	if (p != NULL)
	{
		// find our port number in the list
		char * start = p;
		char * end = strchr(start, ',');

		if (end == NULL)
			lp = atoi(start)+whichPlace;
		else
		{
			for (unsigned int i=1; i<=whichPlace; i++)
			{
				if (end == NULL)
					error("Not enough ports defined in "X10_FORCEPORTS);
				start = end+1;
				end = strchr(start, ',');
			}
			if (end == NULL)
				lp = atoi(start);
			else
			{
				char port[16];
				strncpy(port, start, end-start);
				port[end-start]='\0';
				lp = atoi(port);
			}
		}
		#ifdef DEBUG
		if (whichPlace == state.myPlaceId)
			fprintf(stderr, "Place %u forced to port %i\n", whichPlace, lp);
		#endif
	}
	return lp;
}

/*
 * returns true if there is data remaining to flush
 */
bool flushPendingData()
{
	if (state.pendingWrites == NULL)
		return false;

	bool ableToFlush = true;
	bool dataRemains = false;

	pthread_mutex_lock(&state.pendingWriteLock);
	while (state.pendingWrites != NULL && ableToFlush)
	{
		if (pthread_mutex_trylock(&state.writeLocks[state.pendingWrites->place]) == 0)
		{
			char * src = (char *) state.pendingWrites->data + (state.pendingWrites->size - state.pendingWrites->remainingToWrite);
			while (state.pendingWrites->remainingToWrite > 0)
			{
				int rc = ::write(state.socketLinks[state.pendingWrites->place].fd, src, state.pendingWrites->remainingToWrite);
				if (rc == -1)
				{
					if (errno == EINTR) continue;
					if (errno == EAGAIN) break;
					fprintf(stderr, "flush errno=%i", errno);
					error("Unable to flush data");
				}
				if (rc == 0)
					error("Unable to flush data - socket closed");
				src += rc;
				state.pendingWrites->remainingToWrite -= rc;
			}
			pthread_mutex_unlock(&state.writeLocks[state.pendingWrites->place]);

			#ifdef DEBUG
				if (state.pendingWrites->size - state.pendingWrites->remainingToWrite > 0)
					fprintf(stderr, "Place %u flushed %u bytes of old data\n", state.myPlaceId, state.pendingWrites->size - state.pendingWrites->remainingToWrite);
			#endif

			if (state.pendingWrites->remainingToWrite > 0)
				ableToFlush = false;
			else
			{
				if (state.pendingWrites->deleteBufferWhenComplete)
					free(state.pendingWrites->data);
				void* deleteme = state.pendingWrites;
				state.pendingWrites = state.pendingWrites->next;
				free(deleteme);
			}
			dataRemains = (state.pendingWrites != NULL);
		}
		else
		{
			pthread_mutex_unlock(&state.pendingWriteLock);
			return true;
		}
	}
	pthread_mutex_unlock(&state.pendingWriteLock);
	return dataRemains;
}


/*
 * When useNonblockingLinks is not set, the sending of data will be throttled by the speed of the reader
 * at the other side.  It's therefore possible to get a deadlock.  When useNonblockingLinks is set, and the
 * network buffer is full, a new buffer will be created to hold the outgoing data, so the application can continue.
 * This may lead to large swings in memory usage.
 */
int nonBlockingWrite(int dest, void * p, unsigned cnt, bool copyBuffer=true)
{
	if (!state.useNonblockingLinks)
		return TCP::write(state.socketLinks[dest].fd, p, cnt);

	char * src = (char *) p;
	unsigned bytesleft = cnt;
	uint8_t allowConnResetTries = 10;
	if (state.pendingWrites == NULL)
	{
		while (bytesleft > 0)
		{
			int rc = ::write(state.socketLinks[dest].fd, src, bytesleft);
			if (rc == -1) /* !!!! read interrupted */
			{
				if (errno == EINTR) continue;
				if (errno == EAGAIN) break;
				if (errno == ECONNRESET && allowConnResetTries--)
					continue; // this seems to happen, every once in a great while.  We allow a few only.
				fprintf(stderr, "write errno=%i ", errno);
				return -1;
			}
			if (rc == 0) break;
			src += rc;
			bytesleft -= rc;
		}
	}

	if (bytesleft > 0)
	{
		#ifdef DEBUG
			fprintf(stderr, "Place %u network buffer is full.  Saving %u bytes of data to flush later.\n", state.myPlaceId, bytesleft);
		#endif
		// save the remaining data for later writing
		struct x10SocketDataToWrite* pendingData = (struct x10SocketDataToWrite *)malloc(sizeof(struct x10SocketDataToWrite));
		if (pendingData == NULL) error("Allocating memory for a pending write");
		pendingData->deleteBufferWhenComplete = copyBuffer;
		if (copyBuffer)
		{
			pendingData->data = (char *)malloc(bytesleft);
			if (pendingData->data == NULL) error("Allocating memory for pending write data");
			memcpy(pendingData->data, src, bytesleft);
		}
		else
			pendingData->data = src;
		pendingData->remainingToWrite = bytesleft;
		pendingData->size = bytesleft;
		pendingData->next = NULL;
		pendingData->place = dest;

		pthread_mutex_lock(&state.pendingWriteLock);
		if (state.pendingWrites == NULL)
			state.pendingWrites = pendingData;
		else
		{
			struct x10SocketDataToWrite* currentSlot = state.pendingWrites;
			while(currentSlot->next != NULL)
				currentSlot = currentSlot->next;
			currentSlot->next = pendingData;
		}
		pthread_mutex_unlock(&state.pendingWriteLock);
		if (state.yieldAfterProbe)
			sched_yield();
	}
	return cnt;
}

int nonBlockingRead(int fd, void * p, unsigned cnt)
{
	if (!state.useNonblockingLinks)
		return TCP::read(fd, p, cnt);

	flushPendingData();

	char * dst = (char *) p;
	unsigned bytesleft = cnt;

	while (bytesleft > 0)
	{
		int rc = ::recv(fd, dst, bytesleft, MSG_WAITALL);
		if (rc == -1) /* !!!! read interrupted */
		{
			if (errno == EINTR) continue;
			else if (errno == EAGAIN || errno == EWOULDBLOCK)
			{
				flushPendingData();
				continue;
			}
			fprintf(stderr, "ERRNO = %i\n", errno);
			return -1;
		}
		if (rc == 0)
		{
			if (bytesleft == cnt) // nothing has been read, and nothing to read.
				return 0;
			flushPendingData();
			continue;
		}

		dst += rc;
		bytesleft -= rc;
	}
	return cnt;
}

void handleConnectionRequest()
{
	#ifdef DEBUG
		fprintf(stderr, "X10rt.Sockets: place %u handling a connection request.\n", state.myPlaceId);
	#endif
	int newFD = TCP::accept(state.socketLinks[state.myPlaceId].fd, true);
	if (newFD > 0)
	{
		struct ctrl_msg m;
		int r = TCP::read(newFD, &m, sizeof(struct ctrl_msg));
		if (r == sizeof(struct ctrl_msg))
		{
			uint32_t from = m.from;
			// the higher-numbered place always decides if this connection goes or stays
			if (from < state.myPlaceId)
			{
				if (state.socketLinks[from].fd > 0) // already connected.
				{
					m.type = GOODBYE;
					m.to = from;
					m.from = state.myPlaceId;
					m.datalen = 0;
					r = TCP::write(newFD, &m, sizeof(struct ctrl_msg));
					close(newFD);
					#ifdef DEBUG
						fprintf(stderr, "X10rt.Sockets: place %u got a redundant connection from place %u\n", state.myPlaceId, from);
					#endif
					return;
				}
				else
				{
					m.type = HELLO;
					m.to = from;
					m.from = state.myPlaceId;
					m.datalen = 0;
					r = TCP::write(newFD, &m, sizeof(struct ctrl_msg));
				}
			}
			#ifdef DEBUG
				fprintf(stderr, "X10rt.Sockets: place %u got a new connection from place %u\n", state.myPlaceId, from);
			#endif
			pthread_mutex_init(&state.writeLocks[from], NULL);
	    	state.socketLinks[from].fd = newFD;
			state.socketLinks[from].events = POLLIN | POLLPRI;
			// set SO_LINGER
			struct linger linger;
			linger.l_onoff = 1;
			linger.l_linger = 1;
			if (setsockopt(newFD, SOL_SOCKET, SO_LINGER, &linger, sizeof(linger)) < 0)
				error("Error setting SO_LINGER on incoming socket");
			if (state.useNonblockingLinks)
			{
				int flags = fcntl(newFD, F_GETFL, 0);
				fcntl(newFD, F_SETFL, flags | O_NONBLOCK);
			}
			return;
		}
	}
	#ifdef DEBUG
		fprintf(stderr, "X10rt.Sockets: place %u got a bad connection request\n", state.myPlaceId);
	#endif
}

int initLink(uint32_t remotePlace)
{
	if (remotePlace > state.numPlaces || remotePlace == state.myPlaceId)
		return -1;

	if (!state.linkAtStartup || state.socketLinks[remotePlace].fd <= 0)
		probe(true); // handle any incoming connection requests - we may be able to skip a lookup.

	if (state.socketLinks[remotePlace].fd <= 0)
	{
		#ifdef DEBUG
			fprintf(stderr, "X10rt.Sockets: Place %u looking up place %u for a new connection\n", state.myPlaceId, remotePlace);
		#endif
		char link[1024];
		pthread_mutex_lock(&state.writeLocks[state.myPlaceId]); // because the lookup isn't currently thread-safe

		int port = getPortEnv(remotePlace);
		if (port == 0)
		{
			int r = Launcher::lookupPlace(state.myPlaceId, remotePlace, link, sizeof(link));
			if (r <= 0)
			{
				pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
				return -1;
			}

			// check that the other end didn't connect to us while we were waiting for our lookup to complete.
			if (state.socketLinks[remotePlace].fd > 0)
			{
				pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
				return state.socketLinks[remotePlace].fd;
			}

			// break apart the link into host and port
			char * c = strchr(link, ':');
			if (c == NULL)
			{
				char* suicideNote = (char*)alloca(512);
				sprintf(suicideNote, "Unable to establish a connection to place %u because %s!", remotePlace, link);
				error(suicideNote);
			}
			c[0] = '\0';
			port = atoi(c + 1);
		}
		else
		{
			char* p = getenv(X10_HOSTLIST);
			if (p != NULL)
			{
				// find our port number in the list
				char * start = p;
				char * end = strchr(start, ',');
				for (unsigned int i=1; i<=remotePlace; i++)
				{
					if (end == NULL)
						error("Not enough hosts defined in "X10_HOSTLIST);

					start = end+1;
					end = strchr(start, ',');
				}
				if (end == NULL)
					strcpy(link, start);
				else
				{
					strncpy(link, start, end-start);
					link[end-start] = '\0';
				}
			}
			else
			{
				strcpy(link, "localhost\0");
				if (getenv(X10_HOSTFILE)) fprintf(stderr, "WARNING: "X10_HOSTFILE" is ignored when using "X10_FORCEPORTS);
			}
		}

		// check to see if the host is our host, and if so, change it to "localhost"
		// to take advantage of any localhost OS efficiencies
		bool noDelay;
		if (strcmp(state.myhost, link) == 0)
		{
			strcpy(link, "localhost\0");
			#ifdef DEBUG
				fprintf(stderr, "X10rt.Sockets: Place %u changed hostname for place %u to %s\n", state.myPlaceId, remotePlace, link);
			#endif
//			noDelay = true;
		}
// performance seems to be better when we disable nagle for everything.
//		else if (strcmp("localhost", link) == 0)
			noDelay = true;
//		else
//			noDelay = false;

		int newFD;
		if ((newFD = TCP::connect(link, port, 10, noDelay)) > 0)
		{
			struct ctrl_msg m;
			m.type = HELLO;
			m.to = remotePlace;
			m.from = state.myPlaceId;
			m.datalen = 0;
			int r = TCP::write(newFD, &m, sizeof(m));
			if (r != sizeof(m))
			{
				pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
				return -1;
			}
			// both ends may reach this same point at the same time, so we can't always block on a read.
			// if we are connecting to a *higher* number place, we block, waiting to hear if this is
			// redundant or not.  Otherwise, we set the FD and continue on, assuming that the connection is good.
			if (m.to > state.myPlaceId)
			{
				#ifdef DEBUG
					fprintf(stderr, "X10rt.Sockets: Place %u waiting for response from place %u\n", state.myPlaceId, remotePlace);
				#endif
				r = TCP::read(newFD, &m, sizeof(m));
				if (r != sizeof(m))
				{
					pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
					return -1;
				}
			}

			if (m.type == HELLO)
			{
				pthread_mutex_init(&state.writeLocks[remotePlace], NULL);
				state.socketLinks[remotePlace].fd = newFD;
				state.socketLinks[remotePlace].events = POLLIN | POLLPRI;

				// set SO_LINGER
				struct linger linger;
				linger.l_onoff = 1;
				linger.l_linger = 1;
				if (setsockopt(newFD, SOL_SOCKET, SO_LINGER, &linger, sizeof(linger)) < 0)
					error("Error setting SO_LINGER on outgoing socket");
				if (state.useNonblockingLinks)
				{
					int flags = fcntl(newFD, F_GETFL, 0);
					fcntl(newFD, F_SETFL, flags | O_NONBLOCK);
				}
				#ifdef DEBUG
					fprintf(stderr, "X10rt.Sockets: Place %u established a link to place %u\n", state.myPlaceId, remotePlace);
				#endif
				pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
			}
			else
			{
				pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
				#ifdef DEBUG
					fprintf(stderr, "X10rt.Sockets: Place %u did NOT establish a link to place %u\n", state.myPlaceId, remotePlace);
				#endif
				while (state.socketLinks[remotePlace].fd < 0) // there is a pending connection coming in.
					probe(true);
			}
		}
		else
		{ // failed to connect to the other end.
			pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
			return -1;
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
	Launcher::Setup(*argc, *argv);

	// give parallel debugger opportunity to attach...
	if (getenv(X10_DEBUGGER_ID))
		DebugHelper::attachDebugger();

	// determine the number of places
	char* NPROCS = getenv(X10_NPLACES);
	if (NPROCS == NULL)
	{
//		fprintf(stderr, "%s not set.  Assuming 1 place, running locally\n", X10_NPLACES);
		state.numPlaces = 1;
	}
	else
	{
		state.numPlaces = atol(NPROCS);
		if (state.numPlaces <= 0) // atol failed
			error(X10_NPLACES" is not set to a valid number of places!");
	}

	if (state.numPlaces == 1)
	{
		state.myPlaceId = 0;
		return; // If there is only 1 place, then there are no sockets to set up.
	}

	// determine my place ID
	char* ID = getenv(X10_LAUNCHER_PLACE);
	if (ID == NULL)
		error(X10_LAUNCHER_PLACE" not set!");
	else
		state.myPlaceId = atol(ID);

	state.yieldAfterProbe = !checkBoolEnvVar(getenv(X10_NOYIELD));
	state.linkAtStartup = !checkBoolEnvVar(getenv(X10_LAZYLINKS));
	state.useNonblockingLinks = !checkBoolEnvVar(getenv(X10_NOWRITEBUFFER));

	state.nextSocketToCheck = 0;
	pthread_mutex_init(&state.readLock, NULL);
	state.socketLinks = safe_malloc<pollfd>(state.numPlaces);
	state.writeLocks = safe_malloc<pthread_mutex_t>(state.numPlaces);
	for (unsigned int i=0; i<state.numPlaces; i++)
	{
		state.socketLinks[i].fd = -1;
		state.socketLinks[i].events = 0;
	}

	// open local listen port.
	unsigned listenPort = getPortEnv(state.myPlaceId);
	bool useLauncher = (listenPort == 0);
	state.socketLinks[state.myPlaceId].fd = TCP::listen(&listenPort, 10);
	if (state.socketLinks[state.myPlaceId].fd < 0)
		error("cannot create listener port");
	pthread_mutex_init(&state.writeLocks[state.myPlaceId], NULL);
	state.socketLinks[state.myPlaceId].events = POLLIN | POLLPRI;

	char portname[1024];
	TCP::getname(state.socketLinks[state.myPlaceId].fd, portname, sizeof(portname));
	if (useLauncher)
	{   // Tell our launcher our communication port number
		pthread_mutex_lock(&state.writeLocks[state.myPlaceId]);
		if (Launcher::setPort(state.myPlaceId, portname) < 0)
			error("failed to connect to the local runtime");
		pthread_mutex_unlock(&state.writeLocks[state.myPlaceId]);
	}

	// save our hostname for later
	char * c = strchr(portname, ':');
	c[0] = '\0';
	state.myhost = (char*)malloc(strlen(portname)+1);
	strcpy(state.myhost, portname);

	state.pendingWrites = NULL;
	if (state.useNonblockingLinks)
		pthread_mutex_init(&state.pendingWriteLock, NULL);
	#ifdef DEBUG
		fprintf(stderr, "X10rt.Sockets: place %u running on %s\n", state.myPlaceId, state.myhost);
	#endif
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

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u registered standard message %u\n", state.myPlaceId, msg_type);
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

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u registered put message %u\n", state.myPlaceId, msg_type);
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

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u registered get message %u\n", state.myPlaceId, msg_type);
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
    x10rt_lgl_stats.msg.messages_sent++ ;
    x10rt_lgl_stats.msg.bytes_sent += parameters->len;
	flushPendingData();
	if (initLink(parameters->dest_place) < 0)
		error("establishing a connection");
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u sending a %d byte message of type %d to place %u\n", state.myPlaceId, parameters->len, (int)parameters->type, parameters->dest_place);
	#endif
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u sending a %d byte message to place %u\n", state.myPlaceId, parameters->len, parameters->dest_place);
	#endif
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	// write out the x10SocketMessage data
	// Format: type, p.type, p.len, p.msg
	enum MSGTYPE m = STANDARD;
	if (nonBlockingWrite(parameters->dest_place, &m, sizeof(m)) < (int)sizeof(m))
		error("sending STANDARD type");
	if (nonBlockingWrite(parameters->dest_place, &parameters->type, sizeof(parameters->type)) < (int)sizeof(parameters->type))
		error("sending STANDARD x10rt_msg_params.type");
	if (nonBlockingWrite(parameters->dest_place, &parameters->len, sizeof(parameters->len)) < (int)sizeof(parameters->len))
		error("sending STANDARD x10rt_msg_params.len");
	if (parameters->len > 0)
		if (nonBlockingWrite(parameters->dest_place, parameters->msg, parameters->len) < (int)parameters->len)
			error("sending STANDARD msg");
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_send_get (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
    x10rt_lgl_stats.get.messages_sent++ ;
    x10rt_lgl_stats.get.bytes_sent += parameters->len;
	flushPendingData();
	if (initLink(parameters->dest_place) < 0)
		error("establishing a connection");
	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u sending a %d byte GET message with %d byte payload to place %u\n", state.myPlaceId, parameters->len, bufferLen, parameters->dest_place);
	#endif
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	// write out the x10SocketMessage data
	// Format: type, p.type, p.len, p.msg, bufferlen, bufferADDRESS
	enum MSGTYPE m = GET;
	if (nonBlockingWrite(parameters->dest_place, &m, sizeof(m)) < (int)sizeof(m))
		error("sending GET MSGTYPE");
	if (nonBlockingWrite(parameters->dest_place, &parameters->type, sizeof(parameters->type)) < (int)sizeof(parameters->type))
		error("sending GET x10rt_msg_params.type");
	if (nonBlockingWrite(parameters->dest_place, &parameters->len, sizeof(parameters->len)) < (int)sizeof(parameters->len))
		error("sending GET x10rt_msg_params.len");
	if (parameters->len > 0)
		if (nonBlockingWrite(parameters->dest_place, parameters->msg, parameters->len) < (int)parameters->len)
			error("sending GET x10rt_msg_params.msg");
	if (nonBlockingWrite(parameters->dest_place, &bufferLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
		error("sending GET bufferLen");
	if (bufferLen > 0)
		if (nonBlockingWrite(parameters->dest_place, &buffer, sizeof(void*), COPY_PUT_GET_BUFFER) < (int)sizeof(void*))
			error("sending GET buffer pointer");
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_send_put (x10rt_msg_params *parameters, void *buffer, x10rt_copy_sz bufferLen)
{
    x10rt_lgl_stats.put.messages_sent++ ;
    x10rt_lgl_stats.put.bytes_sent += parameters->len;
    x10rt_lgl_stats.put_copied_bytes_sent += bufferLen;
	flushPendingData();
	if (initLink(parameters->dest_place) < 0)
		error("establishing a connection");
	pthread_mutex_lock(&state.writeLocks[parameters->dest_place]);

	#ifdef DEBUG_MESSAGING
		fprintf(stderr, "X10rt.Sockets: place %u sending a %d byte PUT message with %d byte payload to place %u\n", state.myPlaceId, parameters->len, bufferLen, parameters->dest_place);
	#endif
	// write out the x10SocketMessage data
	// Format: type, p.type, p.len, p.msg, bufferlen, buffer contents
	enum MSGTYPE m = PUT;
	if (nonBlockingWrite(parameters->dest_place, &m, sizeof(m)) < (int)sizeof(m))
		error("sending PUT MSGTYPE");
	if (nonBlockingWrite(parameters->dest_place, &parameters->type, sizeof(parameters->type)) < (int)sizeof(parameters->type))
		error("sending PUT x10rt_msg_params.type");
	if (nonBlockingWrite(parameters->dest_place, &parameters->len, sizeof(parameters->len)) < (int)sizeof(parameters->len))
		error("sending PUT x10rt_msg_params.len");
	if (parameters->len > 0)
		if (nonBlockingWrite(parameters->dest_place, parameters->msg, parameters->len) < (int)parameters->len)
			error("sending PUT x10rt_msg_params.len");
	if (nonBlockingWrite(parameters->dest_place, &bufferLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
		error("sending PUT bufferLen");
	if (bufferLen > 0)
		if (nonBlockingWrite(parameters->dest_place, buffer, bufferLen, COPY_PUT_GET_BUFFER) < (int)bufferLen)
			error("sending PUT buffer");
	pthread_mutex_unlock(&state.writeLocks[parameters->dest_place]);
}

void x10rt_net_probe ()
{
	if (state.numPlaces == 1)
		sched_yield(); // why is the runtime calling probe() with only one place?  It looses its CPU as punishment. ;-)
	else if (state.linkAtStartup)
	{
		for (unsigned i=0; i<state.myPlaceId; i++)
			initLink(i); // connect to all lower places
		for (unsigned i=state.myPlaceId+1; i<state.numPlaces; i++)
			while (state.socketLinks[i].fd <= 0)
				probe(true); // wait for connections from all upper places
		state.linkAtStartup = false;
	}
	else 
		while (probe(false)) { }
}

// return T if data was processed, F if not
bool probe (bool onlyProcessAccept)
{
	if (pthread_mutex_lock(&state.readLock) < 0)
		return false;
	uint32_t whichPlaceToHandle = state.nextSocketToCheck;
	int ret = poll(state.socketLinks, state.numPlaces, state.linkAtStartup?100:0);
	if (ret > 0)
	{ // There is at least one socket with something interesting to look at

		// the listen port always gets priority
		if ((state.socketLinks[state.myPlaceId].revents & POLLIN) || (state.socketLinks[state.myPlaceId].revents & POLLPRI))
			whichPlaceToHandle = state.myPlaceId;
		else if (onlyProcessAccept)
		{
			pthread_mutex_unlock(&state.readLock);
			return false;
		}
		else
		{
			while(true)
			{
				if (state.socketLinks[whichPlaceToHandle].fd != -1 && state.socketLinks[whichPlaceToHandle].revents)
					break;

				whichPlaceToHandle++;
				if (whichPlaceToHandle == state.numPlaces)
					whichPlaceToHandle = 0;
				if (whichPlaceToHandle == state.nextSocketToCheck)
				{
					// we should never get here, because if we do, it means that poll said there is something to do (ret > 0), but we didn't find it
					pthread_mutex_unlock(&state.readLock);
					return false;
				}
			}

			// Set nextSocketToCheck
			if (whichPlaceToHandle == state.numPlaces-1)
				state.nextSocketToCheck = 0;
			else
				state.nextSocketToCheck = whichPlaceToHandle+1;
		}		
		state.socketLinks[whichPlaceToHandle].events = 0; // disable any further polls on this socket
		pthread_mutex_unlock(&state.readLock);

		if ((state.socketLinks[whichPlaceToHandle].revents & POLLIN) || (state.socketLinks[whichPlaceToHandle].revents & POLLPRI))
		{
			if (whichPlaceToHandle == state.myPlaceId) // special case.  This is an incoming connection request.
			{
				#ifdef DEBUG_MESSAGING
					fprintf(stderr, "X10rt.Sockets: place %u probe processing a connection request\n", state.myPlaceId);
				#endif
				handleConnectionRequest();
				pthread_mutex_lock(&state.readLock);
				state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
				pthread_mutex_unlock(&state.readLock);
			}
			else
			{
				#ifdef DEBUG_MESSAGING
					fprintf(stderr, "X10rt.Sockets: place %u probe processing a message from place %u\n", state.myPlaceId, whichPlaceToHandle);
				#endif
				// Format: type, p.type, p.len, p.msg
				enum MSGTYPE t;
				int r = nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &t, sizeof(enum MSGTYPE));
				if (r < (int)sizeof(enum MSGTYPE) || t > GET_COMPLETED) // closed connection
				{
					#ifdef DEBUG_MESSAGING
						fprintf(stderr, "X10rt.Sockets: Place %u detected a bad message from place %u (likely a closed socket)\n", state.myPlaceId, whichPlaceToHandle);
					#endif
					close(state.socketLinks[whichPlaceToHandle].fd);
					state.socketLinks[whichPlaceToHandle].fd = -1;
					return false;
				}
				#ifdef DEBUG_MESSAGING
					fprintf(stderr, "X10rt.Sockets: place %u picked up a message from place %u\n", state.myPlaceId, whichPlaceToHandle);
				#endif

				x10rt_msg_params mp;
				mp.dest_endpoint = 0;
				mp.dest_place = state.myPlaceId;
				if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &mp.type, sizeof(x10rt_msg_type)) < (int)sizeof(x10rt_msg_type))
					error("reading x10rt_msg_params.type");
				if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &mp.len, sizeof(uint32_t)) < (int)sizeof(uint32_t))
					error("reading x10rt_msg_params.len");
				#ifdef DEBUG_MESSAGING
					fprintf(stderr, "X10rt.Sockets: place %u decoded a message of type %d from place %u\n", state.myPlaceId, (int)mp.type, whichPlaceToHandle);
				#endif
				bool heapAllocated = false;
				if (mp.len > 0)
				{
					if (mp.len <= 1024)
						mp.msg = alloca(mp.len);
					else
						mp.msg = NULL;
					if (mp.msg == NULL) // stack allocation failed... try heap allocation
					{
						mp.msg = malloc(mp.len);
						if (mp.msg == NULL)
							error("unable to allocate memory for an incoming message");
						heapAllocated = true;
					}
					if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, mp.msg, mp.len) < (int)mp.len)
						error("reading x10rt_msg_params.msg");
				}
				else
					mp.msg = NULL;

				switch (t)
				{
					case STANDARD:
					{
						pthread_mutex_lock(&state.readLock);
						state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
						pthread_mutex_unlock(&state.readLock);
						handlerCallback hcb = state.callBackTable[mp.type].handler;
                        x10rt_lgl_stats.msg.messages_received++;
                        x10rt_lgl_stats.msg.bytes_received += mp.len;
						hcb(&mp);
					}
					break;
					case PUT:
					{
						x10rt_copy_sz dataLen;
						if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &dataLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
							error("reading PUT datalen");

						finderCallback fcb = state.callBackTable[mp.type].finder;
                        x10rt_lgl_stats.put.messages_received++;
                        x10rt_lgl_stats.put.bytes_received += mp.len;
						void* dest = fcb(&mp, dataLen); // get the pointer to the destination location
						if (dest == NULL)
							error("invalid buffer provided for a PUT");
						if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, dest, dataLen) < (int)dataLen)
							error("reading PUT data");
						pthread_mutex_lock(&state.readLock);
						state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
						pthread_mutex_unlock(&state.readLock);
						notifierCallback ncb = state.callBackTable[mp.type].notifier;
						ncb(&mp, dataLen);
                        x10rt_lgl_stats.put_copied_bytes_received += dataLen;
					}
					break;
					case GET:
					{
						// this is the request for data.
						// Format: type, p.type, p.len, p.msg, bufferlen, bufferADDRESS
						x10rt_copy_sz dataLen;
						void* remotePtr; // THIS IS A POINTER ON A REMOTE MACHINE.  NOT VALID HERE
						if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &dataLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
							error("reading GET dataLen");
						if (dataLen > 0)
							if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &remotePtr, sizeof(void*)) < (int)sizeof(void*))
								error("reading GET pointer");

						pthread_mutex_lock(&state.readLock);
						state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
						pthread_mutex_unlock(&state.readLock);

						finderCallback fcb = state.callBackTable[mp.type].finder;
                        x10rt_lgl_stats.get.messages_received++;
                        x10rt_lgl_stats.get.bytes_received += mp.len;
						void* src = fcb(&mp, dataLen);
                        x10rt_lgl_stats.get_copied_bytes_received += dataLen;

						// send the data to the other side (the link is good, because we just read from it)
						pthread_mutex_lock(&state.writeLocks[whichPlaceToHandle]);
						// Format: type, p.type, p.len, p.msg, bufferlen, bufferADDRESS, buffer
						enum MSGTYPE m = GET_COMPLETED;
						if (nonBlockingWrite(whichPlaceToHandle, &m, sizeof(m)) < (int)sizeof(m))
							error("sending GET_COMPLETED MSGTYPE");
						if (nonBlockingWrite(whichPlaceToHandle, &mp.type, sizeof(mp.type)) < (int)sizeof(mp.type))
							error("sending GET_COMPLETED x10rt_msg_params.type");
						if (nonBlockingWrite(whichPlaceToHandle, &mp.len, sizeof(mp.len)) < (int)sizeof(mp.len))
							error("sending GET_COMPLETED x10rt_msg_params.len");
						if (mp.len > 0)
							if (nonBlockingWrite(whichPlaceToHandle, mp.msg, mp.len) < (int)mp.len)
								error("sending GET_COMPLETED x10rt_msg_params.msg");
						if (nonBlockingWrite(whichPlaceToHandle, &dataLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
							error("sending GET_COMPLETED dataLen");
						if (dataLen > 0)
						{
							if (nonBlockingWrite(whichPlaceToHandle, &remotePtr, sizeof(void*)) < (int)sizeof(void*))
								error("sending GET_COMPLETED remotePtr");
							if (nonBlockingWrite(whichPlaceToHandle, src, dataLen) < (int)dataLen)
								error("sending GET_COMPLETED data");
						}
						pthread_mutex_unlock(&state.writeLocks[whichPlaceToHandle]);
					}
					break;
					case GET_COMPLETED:
					{
						x10rt_copy_sz dataLen;
						void* buffer;

						if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &dataLen, sizeof(x10rt_copy_sz)) < (int)sizeof(x10rt_copy_sz))
							error("reading GET_COMPLETED dataLen");
						if (dataLen > 0)
						{
							if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, &buffer, sizeof(void*)) < (int)sizeof(void*))
								error("reading GET_COMPLETED pointer");
							if (nonBlockingRead(state.socketLinks[whichPlaceToHandle].fd, buffer, dataLen) < (int)dataLen)
								error("reading GET_COMPLETED data");
						}
						pthread_mutex_lock(&state.readLock);
						state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
						pthread_mutex_unlock(&state.readLock);

						mp.dest_place = whichPlaceToHandle;
						notifierCallback ncb = state.callBackTable[mp.type].notifier;
						ncb(&mp, dataLen);
                        x10rt_lgl_stats.get_copied_bytes_sent += dataLen;
					}
					break;
					default: // this should never happen
						error("Unknown message type found");
					break;
				}
				if (heapAllocated)
					free(mp.msg);
			}
		}
		else if ((state.socketLinks[whichPlaceToHandle].revents & POLLHUP) || (state.socketLinks[whichPlaceToHandle].revents & POLLERR) || (state.socketLinks[whichPlaceToHandle].revents & POLLNVAL))
		{
			#ifdef DEBUG
				fprintf(stderr, "X10rt.Sockets: place %u detected a broken link to place %u!\n", state.myPlaceId, whichPlaceToHandle);
			#endif

			// link is broken.  Close it down.
			#ifdef DEBUG
            	int r = close(state.socketLinks[whichPlaceToHandle].fd);
				if (r < 0)
					fprintf(stderr, "X10rt.Sockets: place %u failed closing link to %u: %i\n", state.myPlaceId, whichPlaceToHandle, r);
			#else
				close(state.socketLinks[whichPlaceToHandle].fd);
			#endif
			state.socketLinks[whichPlaceToHandle].fd = -1;
			// TODO - notify the runtime of this?
		}
		else
		{
			// when the socket gets closed, we might get into this code here.
			#ifdef DEBUG_MESSAGING
				fprintf(stderr, "X10rt.Sockets: place %u got a dud message from place %u\n", state.myPlaceId, whichPlaceToHandle);
			#endif
			pthread_mutex_lock(&state.readLock);
			state.socketLinks[whichPlaceToHandle].events = POLLIN | POLLPRI;
			pthread_mutex_unlock(&state.readLock);
		}
		return true;
	}
	else
	{
		pthread_mutex_unlock(&state.readLock);
		flushPendingData();
		if (state.yieldAfterProbe) // This would be a good time for a yield in some systems.
			sched_yield();
		return false;
	}
}

void x10rt_net_finalize (void)
{
	if (state.numPlaces == 1)
		return;

	#ifdef DEBUG
		fprintf(stderr, "X10rt.Sockets: shutting down place %u\n", state.myPlaceId);
	#endif

	if (state.useNonblockingLinks)
	{
		while(flushPendingData()){}
		pthread_mutex_destroy(&state.pendingWriteLock);
	}

	for (unsigned int i=0; i<state.numPlaces; i++)
	{
		if (state.socketLinks[i].fd != -1)
		{
			pthread_mutex_lock(&state.writeLocks[i]);
			#ifdef DEBUG
				int r = close(state.socketLinks[i].fd);
				if (r < 0)
					fprintf(stderr, "X10rt.Sockets: runtime %u failed closing link to %u: %i\n", state.myPlaceId, i, r);
			#else
				close(state.socketLinks[i].fd);
			#endif
			pthread_mutex_unlock(&state.writeLocks[i]);
			pthread_mutex_destroy(&state.writeLocks[i]);
		}
	}

	if (Launcher::_parentLauncherControlLink != -1)
	{
		#ifdef DEBUG
			int r = close(Launcher::_parentLauncherControlLink);
			if (r < 0)
				fprintf(stderr, "X10rt.Sockets: runtime %u failed closing link to parent launcher: %i\n", state.myPlaceId, r);
		#else
			close(Launcher::_parentLauncherControlLink);
		#endif
	}
	pthread_mutex_destroy(&state.readLock);
	free(state.myhost);
	free(state.socketLinks);
	free(state.writeLocks);
}

/*************************************************
 * All of the stuff below is not used in the socket
 * backend, and we rely on the emulation layer to
 * convert these into messages for us.
 *************************************************/


void x10rt_net_internal_barrier (void)
{
	#ifdef DEBUG
		fprintf(stderr, "X10rt.Sockets internal barrier called at place %u\n", state.myPlaceId);
	#endif
}

int x10rt_net_supports (x10rt_opt o)
{
    return 0;
}

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim, x10rt_op_type type, unsigned long long value)
{
	error("x10rt_net_remote_op not implemented");
}

void x10rt_net_remote_ops (x10rt_remote_op_params *ops, size_t numOps)
{
	error("x10rt_net_remote_ops not implemented");
}

x10rt_remote_ptr x10rt_net_register_mem (void *ptr, size_t len)
{
    // assume remote ops will be handled by regular x10rt_send_msg so
    // no special work to do here
    return (x10rt_remote_ptr)(size_t)ptr;
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
