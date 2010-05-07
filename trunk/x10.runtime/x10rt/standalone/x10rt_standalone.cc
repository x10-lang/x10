/********************************************************************************************
 * (c) Copyright IBM Corporation 2010
 * Written be Ben Herta for IBM, bherta@us.ibm.com, April 2010
 * This code supports multi-place x10 programs running on a single machine.
 * This involves spawning a child process for each place, and merging them at the end.
 * Data transmission is done through shared memory.
 *
 * No threads are actually necessary in this implementation, since there is
 * no need to process actual incoming message streams into buffers.  Instead
 * the buffers are filled by the sending end, and then picked up directly by
 * the receiving end.
 *
 * OPEN QUESTIONS AND TODOS:
 * Can the callbacks throw exceptions that I should be catching?
 * The shared memory queues should become dynamic, not static, and grow as needed
 * thoroughly test for and fix any memory leaks, and file handle leaks
 **********************************************************************************************/
#include <cstdlib>
#include <cstdio>

#include <sys/types.h>
#include <sys/mman.h> // using memory mapping for our IPC
#include <fcntl.h>

#include <pthread.h> // for semaphores
#include <unistd.h> // for fork()
#include <string.h> // for memcpy
#include <sys/wait.h> // for the waitpid at shutdown time
#include <errno.h>

#include <x10rt_net.h>

//#define DEBUG // uncomment to turn on debug messages

#ifdef __CYGWIN__
#include "x10rt_standalone_cygwin.cc"
#else
typedef pthread_mutexattr_t pthread_mutexattr;
typedef pthread_mutex_t pthread_mutex;
#endif

#define X10RT_STANDALONE_NUMPLACES "X10RT_STANDALONE_NUMPLACES" // environment variable
#define X10RT_DATABUFFERSIZE 524300 // the size, in bytes, of the shared memory segment used for communications, per place


// mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED};
enum MSGSTATUS {COMPLETED, NEW, INPROCESS, WRAPPED};

struct x10StandaloneMessageQueueEntry
{
	enum MSGSTATUS status;
	enum MSGTYPE standaloneMessageType; // an identifier for the type of message this is (specific to this standalone back-end)
	x10rt_place from; // which place this message is from.  Needed for the get response.
	x10rt_msg_type type; // corresponds to the type field of x10rt_msg_params
	unsigned long msgLen; // corresponds to the len field of x10rt_msg_params
	unsigned long payloadLen; // the length of the data associated with the get/put. On a get, the payload also includes the original pointer at the front.
	// msg buffer next
	// payload buffer last
};

struct x10StandalonePlaceState
{
	pthread_mutex messageQueueLock; // lock is per place, so we have parallelism for all but the creation/destruction of the buffer entries
	unsigned int messageQueueHead; // the array index for the first non-blank slot in our queue.  Set to the buffer size when there are no messages.
	unsigned int messageQueueTail; // the array index for the first blank spot in our queue

	// this data buffer holds the messages in their movement from one place to the other.
	// it uses the pointers above to point to where a block of data starts in the buffer, and where it ends.
	// data goes into the buffer in order, and is removed in order.  If a message can't fit in the remaining space,
	// it restarts at the front of the buffer again.
	// TODO - make it growable.
	char dataBuffer[X10RT_DATABUFFERSIZE];
};

struct x10StandaloneCallback
{
	handlerCallback handler;
	finderCallback finder;
	notifierCallback notifier;
};

struct x10StandaloneState
{
	x10rt_place numPlaces; // how many places we have.
	x10rt_place myPlaceId; // which place we're at.  Also used as the index to the array below. Local per-place memory is used.
	x10StandaloneCallback* callBackTable; // I'm told message ID's run from 0 to n, so a simple array using message indexes is the best solution for this table.  Local per-place memory is used.
	x10rt_msg_type callBackTableSize; // length of the above array

	pthread_barrier_t *barrier; // a barrier, used in the x10rt_net_internal_barrier method.  Stored in shared-memory.
	x10StandalonePlaceState **perPlaceState; // Array of state information for each place, stored in shared memory.
} state;

/*********************************************
 *  utility methods
*********************************************/

void error(const char* message)
{
	printf("Fatal Error: %s: %s\n", message, strerror(errno));
	abort();
}

// returns the size of an entry in the buffer
x10rt_copy_sz getTotalLength(x10StandaloneMessageQueueEntry* entry)
{
	x10rt_copy_sz entrySize = sizeof(struct x10StandaloneMessageQueueEntry) + entry->msgLen;
	if (entry->standaloneMessageType == GET)
		entrySize+=sizeof(void*);
	else
		entrySize+=entry->payloadLen;
	if (entry->standaloneMessageType == GET_COMPLETED)
		entrySize+=sizeof(void*);
	return entrySize;
}

// adds a new entry into the buffer of the receiving end.
void insertNewMessage(MSGTYPE mt, x10rt_msg_params *p, void *dataPtr, x10rt_copy_sz dataLen, void* origGetDataPtr)
{
	x10StandalonePlaceState *dest = state.perPlaceState[p->dest_place]; // pointer to make this more readable
	x10StandaloneMessageQueueEntry *entry = NULL;
	x10rt_copy_sz entrySize = sizeof(struct x10StandaloneMessageQueueEntry) + p->len;
	if (mt == GET)
		entrySize+=sizeof(void*);
	else
		entrySize+=dataLen;
	if (mt == GET_COMPLETED)
		entrySize+=sizeof(void*);

	// check to see if this is simply too big.
	if (entrySize > X10RT_DATABUFFERSIZE-sizeof(MSGSTATUS))
	{
		printf("Unable to send a message of size %lu through a buffer of size %d!!!!  ABORT\n", entrySize, X10RT_DATABUFFERSIZE);
		abort();
	}

	// find a free slot to put the message into
	while (entry == NULL)
	{
		// lock destination
		if (pthread_mutex_lock(&dest->messageQueueLock) != 0) error("Unable to lock the message queue to insert a message");

		// find a place in the destination queue
		if (dest->messageQueueTail < dest->messageQueueHead) // the buffer is empty, or has previously wrapped around
		{
			if (dest->messageQueueTail + entrySize < dest->messageQueueHead) // space in the buffer before the head
				entry = (x10StandaloneMessageQueueEntry *)(dest->dataBuffer + dest->messageQueueTail);
		}
		else if (dest->messageQueueTail + entrySize < (X10RT_DATABUFFERSIZE-sizeof(MSGSTATUS))) // the tail has not wrapped around, and there is space at the end
		{
			entry = (x10StandaloneMessageQueueEntry *)(dest->dataBuffer + dest->messageQueueTail);
		}
		else if (entrySize < dest->messageQueueHead) // no space at the end, try wrapping around
		{
			entry = (x10StandaloneMessageQueueEntry *)(dest->dataBuffer + dest->messageQueueTail);
			entry->status = WRAPPED; // leave a marker behind
			entry = (x10StandaloneMessageQueueEntry *)dest->dataBuffer;
			dest->messageQueueTail = 0;
		}

		if (entry == NULL)
		{
			// not enough room in the buffer to hold our message.
			// unlock the buffer, do a yield (to give the other place some CPU), and try again.
			// TODO: grow the buffer instead of blocking
			#ifdef DEBUG
				printf("X10rt.Standalone: place %lu's buffer is full!  Head=%u, Tail=%u\n", p->dest_place, dest->messageQueueHead, dest->messageQueueTail);
				fflush(stdout);
			#endif
			if (pthread_mutex_unlock(&dest->messageQueueLock) != 0) error("Unable to unlock the message queue after inserting a message");
			sched_yield();
		}
	}

	// fill in the shared memory with the data
	entry->status = NEW;
	entry->standaloneMessageType = mt;
	entry->from = state.myPlaceId;
	entry->type = p->type;
	entry->msgLen = p->len;
	entry->payloadLen = dataLen;
	if (p->len > 0)
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry), p->msg, p->len);
	if (mt == GET)
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len, &dataPtr, sizeof(void *));
	else if (mt == GET_COMPLETED)
	{
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len, origGetDataPtr, sizeof(void *));
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len+sizeof(void *), &dataPtr, dataLen);
	}
	else if (dataLen > 0)
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len, dataPtr, dataLen);

	// update our queue positions
	dest->messageQueueTail += entrySize;
	if (dest->messageQueueHead == X10RT_DATABUFFERSIZE)
		dest->messageQueueHead = 0;

	#ifdef DEBUG
		printf("X10rt.Standalone: Place %lu added a message of length %lu to place %lu's buffer.  Head=%u, Tail=%u\n", state.myPlaceId, entrySize, p->dest_place,
				dest->messageQueueHead, dest->messageQueueTail);
		fflush(stdout);
	#endif


	// unlock destination
	if (pthread_mutex_unlock(&dest->messageQueueLock) != 0) error("Unable to unlock the message queue after inserting a message");
}

/******************************************************
 *  Main API calls.  See x10rt_net.h for documentation
*******************************************************/

void x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
	// determine the number of places (processes) to create, using an environment variable
	char* NPROCS = getenv(X10RT_STANDALONE_NUMPLACES);
	if (NPROCS == NULL) 
	{
		fprintf(stderr, "X10RT_STANDALONE_NUMPLACES not set.  Assuming 1 place\n");
		state.numPlaces = 1;
	}
	else
		state.numPlaces = atol(NPROCS);

	// allocate the shared memory regions which hold the barrier and pointers to buffers between processes
	int sharedStateHandle = open("/dev/zero", O_RDWR|O_EXCL, S_IRWXU);
	if (sharedStateHandle < 0) error("Unable to open the initial shared memory region");
//	if (ftruncate(sharedStateHandle, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces)) < 0) error("Unable to truncate shared memory region"); // set the shared memory size
	state.barrier = (pthread_barrier_t*)mmap(NULL, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces), PROT_READ|PROT_WRITE, MAP_SHARED, sharedStateHandle, 0);
	if (state.barrier == MAP_FAILED) error("Unable to mmap the initial shared memory region");
	state.perPlaceState = (x10StandalonePlaceState **)state.barrier+sizeof(pthread_barrier_t);
	if (close(sharedStateHandle) < 0) error("Unable to close the initial shared memory handle");

	// initialize our barrier to the number of places
	pthread_barrierattr_t barrier_attr;
	if (pthread_barrierattr_init(&barrier_attr) != 0) error("Unable to initialize the synchronizarion barrier attributes");
	if (pthread_barrierattr_setpshared(&barrier_attr, PTHREAD_PROCESS_SHARED) != 0) error("Unable to set the synchronizarion barrier to shared");
	if (pthread_barrier_init(state.barrier, &barrier_attr, state.numPlaces) != 0) error("Unable to initialize the synchronizarion barrier");
	if (pthread_barrierattr_destroy(&barrier_attr) != 0) error("Unable to initialize the synchronizarion barrier attributes");

	pthread_mutexattr mta;
	if (pthread_mutexattr_init(&mta) != 0) error("Unable to initialize the mutex attributes");
	if (pthread_mutexattr_setpshared(&mta, PTHREAD_PROCESS_SHARED) != 0) error("Unable to initialize the mutex attributes to shared");
	// initialize structures for each individual place
	for (x10rt_place i=0; i<state.numPlaces; i++)
	{
		// allocate the shared memory data buffer and associates structures
		int placeStateHandle = open("/dev/zero", O_RDWR|O_EXCL, S_IRWXU);
		if (placeStateHandle < 0) error("Unable to open the place-specific buffer");
//		if (ftruncate(placeStateHandle, sizeof(struct x10StandalonePlaceState))) error("Unable to truncate a place-specific buffer"); // set the shared memory size
		state.perPlaceState[i] = (x10StandalonePlaceState *)mmap(NULL, sizeof(struct x10StandalonePlaceState), PROT_READ|PROT_WRITE, MAP_SHARED, placeStateHandle, 0);
		if (state.perPlaceState[i] == MAP_FAILED) error("Unable to mmap the place-specific buffer");
		if (close(placeStateHandle) < 0) error("Unable to close the place-specific buffer handle");

		if(pthread_mutex_init(&state.perPlaceState[i]->messageQueueLock, &mta) != 0) error("Unable to initialize the mutex for a place");
		state.perPlaceState[i]->messageQueueHead = X10RT_DATABUFFERSIZE;
		state.perPlaceState[i]->messageQueueTail = 0;
	}
	if (pthread_mutexattr_destroy(&mta) != 0) error("Unable to destroy the mutex attributes"); // done with the mutex attributes object

	state.callBackTable = NULL;
	state.callBackTableSize = 0;
	state.myPlaceId = 0;

	// spawn off the child processes
	for (x10rt_place i=1; i<state.numPlaces; i++)
	{
		pid_t id = fork();
		if (id < 0) error("Unable to fork a child process");
		else if (id == 0) // child process
		{
			#ifdef __CYGWIN__
				sleep(1);
			#endif
			state.myPlaceId = i;
			break; // out of the spawning for loop
		}
	}
}

void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb)
{
	// register a pointer to methods that will handle specific mesage types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10StandaloneCallback*)realloc(state.callBackTable, sizeof(struct x10StandaloneCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
		state.callBackTableSize = msg_type+1;
	}

	state.callBackTable[msg_type].handler = cb;
	state.callBackTable[msg_type].finder = NULL;
	state.callBackTable[msg_type].notifier = NULL;

	#ifdef DEBUG
		printf("X10rt.Standalone: place %lu regestered standard message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
{
	// register a pointer to methods that will handle specific mesage types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10StandaloneCallback*)realloc(state.callBackTable, sizeof(struct x10StandaloneCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = cb1;
	state.callBackTable[msg_type].notifier = cb2;

	#ifdef DEBUG
		printf("X10rt.Standalone: place %lu regestered put message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
{ 
	// register a pointer to methods that will handle specific mesage types.
	// add an entry to our type/handler table

	// there are more efficient ways to do this, but this is not in our critical path of execution, so we do it the easy way
	if (msg_type >= state.callBackTableSize)
	{
		state.callBackTable = (x10StandaloneCallback*)realloc(state.callBackTable, sizeof(struct x10StandaloneCallback)*(msg_type+1));
		if (state.callBackTable == NULL) error("Unable to allocate space for the callback table");
	}

	state.callBackTable[msg_type].handler = NULL;
	state.callBackTable[msg_type].finder = cb1;
	state.callBackTable[msg_type].notifier = cb2;

	#ifdef DEBUG
		printf("X10rt.Standalone: place %lu regestered get message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_internal_barrier (void)
{
	// this is called by all places, and blocks until all places have reached it
	#ifdef DEBUG
		printf("X10rt.Standalone: place %lu reached barrier\n", state.myPlaceId);
		fflush(stdout);
	#endif
	int ret = pthread_barrier_wait(state.barrier);
	if (ret != PTHREAD_BARRIER_SERIAL_THREAD && ret != 0)
		error("Unable to block on the barrier");
	#ifdef DEBUG
		printf("X10rt.Standalone: place %lu left barrier\n", state.myPlaceId);
		fflush(stdout);
		sched_yield(); // simply a way to help keep the stdout messages across places in-line
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

void *x10rt_net_msg_realloc (void *old, size_t old_sz, size_t new_sz)
{
	void* ret = realloc(old, new_sz);
	if (ret == NULL && new_sz != 0)
		error("Unable to realloc a message");
	return ret;
}

void *x10rt_net_get_realloc (void *old, size_t old_sz, size_t new_sz)
{
	void* ret = realloc(old, new_sz);
	if (ret == NULL && new_sz != 0)
		error("Unable to realloc a GET message");
	return ret;
}

void *x10rt_net_put_realloc (void *old, size_t old_sz, size_t new_sz)
{
	void* ret = realloc(old, new_sz);
	if (ret == NULL && new_sz != 0)
		error("Unable to realloc a PUT message");
	return ret;
}

void x10rt_net_send_msg (x10rt_msg_params *p)
{
	// originating place calls this method, to send something? to a remote place.  It returns once the data transfer is complete.
	// There is not really anything to do here except put the pointer to the message into the receivers buffer
	insertNewMessage(STANDARD, p, NULL, 0, NULL);
	if (p->len > 0)
		free(p->msg);
}

void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
	// The local place uses this method to bring in data from a remote place
	insertNewMessage(GET, p, buf, len, NULL);
	if (p->len > 0)
		free(p->msg);
}

void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ 
	// originating place calls this method, to transfer data to a remote place.  It returns once the data transfer is complete.
	insertNewMessage(PUT, p, buf, len, NULL);
	if (p->len > 0)
		free(p->msg);
}

void x10rt_net_remote_xor (x10rt_place place, x10rt_remote_ptr addr, long long update)
{ 
	// not implemented
}

void x10rt_net_remote_op_fence (void)
{
	// not implemented
}

void x10rt_net_probe (void)
{
	// the receiving side calls this regularly, to see if messages have come in to be processed.  This is
	// a thread that's part of the receiving end, and this is the thread that will begin execution of the function registered

	x10StandalonePlaceState *myPlace = state.perPlaceState[state.myPlaceId]; // pointer to make this more readable

	if (pthread_mutex_lock(&myPlace->messageQueueLock) != 0) error("Unable to lock the message queue to get a message");

	while(true) // loop as long as we have incoming messages to process in the buffer
	{
		// check the buffer to see if we have a message in it
		if (myPlace->messageQueueHead == X10RT_DATABUFFERSIZE) // empty queue
		{
			if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
			return;
		}

		x10StandaloneMessageQueueEntry *entry = (x10StandaloneMessageQueueEntry *)(myPlace->dataBuffer + myPlace->messageQueueHead);
		int skippedMsgs = 0;
		x10rt_copy_sz entrySize = getTotalLength(entry);

		// if we have multiple worker threads, this slot may already be in process
		while (entry->status != NEW)
		{			
			if (entry->status == WRAPPED)
				skippedMsgs = myPlace->messageQueueHead * -1;
			else
				skippedMsgs += entrySize;

			entry = (x10StandaloneMessageQueueEntry *)(myPlace->dataBuffer + myPlace->messageQueueHead + skippedMsgs);

			// check to see if we're at the tail.  If so, there's nothing to do
			if (myPlace->messageQueueTail == myPlace->messageQueueHead + skippedMsgs)
			{
				if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
				return;
			}

			entrySize = getTotalLength(entry);
		}

		// mark the message as in-process
		entry->status = INPROCESS;

		#ifdef DEBUG
			printf("X10rt.Standalone: place %lu picked up a message from place %lu with type=%d len=%lu and payloadLen=%lu, poistion=%u Head=%u, Tail=%u\n", state.myPlaceId, entry->from, entry->standaloneMessageType, entrySize, entry->payloadLen, myPlace->messageQueueHead + skippedMsgs, myPlace->messageQueueHead, myPlace->messageQueueTail);
			fflush(stdout);
		#endif

		// we've reserved the entry, and can unlock the buffer.
		if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after reserving a message");

		// reconstruct the x10rt_msg_params structure
		x10rt_msg_params mp;
		mp.dest_place = state.myPlaceId;
		mp.type = entry->type;
		mp.len = entry->msgLen;
		if (entry->msgLen > 0)
			mp.msg = (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry);
		else
			mp.msg = NULL;

		switch (entry->standaloneMessageType)
		{
			case STANDARD:
			{
				handlerCallback hcb = state.callBackTable[mp.type].handler;
				hcb(&mp);
			}
			break;
			case PUT:
			{
				finderCallback fcb = state.callBackTable[mp.type].finder;
				void* dest = fcb(&mp, entry->payloadLen); // get the pointer to the destination location
				memcpy(dest, (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen, entry->payloadLen); // copy the data to the destination
				notifierCallback ncb = state.callBackTable[mp.type].notifier;
				ncb(&mp, entry->payloadLen);
			}
			break;
			case GET:
			{
				// this is the request for data.
				finderCallback fcb = state.callBackTable[mp.type].finder;
				void* src = fcb(&mp, entry->payloadLen);

				// send the data to the other side
				mp.dest_place = entry->from;
				insertNewMessage(GET_COMPLETED, &mp, src, entry->payloadLen, (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen);
			}
			break;
			case GET_COMPLETED:
			{
				// copy over the contents of the shared memory
				memcpy((void *)((char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen), (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen+sizeof(void *), entry->payloadLen);

				notifierCallback ncb = state.callBackTable[mp.type].notifier;
				ncb(&mp, entry->payloadLen);
			}
			break;
			default: // this should never happen
				error("Unknown message type found");
			break;
		}

		if (pthread_mutex_lock(&myPlace->messageQueueLock) != 0) error("Unable to lock the message queue after processing a message");

		entry->status = COMPLETED;

		// move the head pointer up in the array, to clear out this message for the next insert
		if (skippedMsgs > 0)
		{
			#ifdef DEBUG
				printf("X10rt.Standalone: Place %lu finished processing a message of size %lu. Not moving head. Head=%u, Tail=%u\n", state.myPlaceId, entrySize, myPlace->messageQueueHead, myPlace->messageQueueTail);
				fflush(stdout);
			#endif

			// this message was not the previous head.  We're done.
			if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after processing a message");
			return;
		}

		// skip the head along to the next used message position
		while (entry->status == COMPLETED)
		{
			entrySize = getTotalLength(entry);
			myPlace->messageQueueHead += entrySize;

			// check to see if we're at the tail.  If so, reset the buffer.
			if (myPlace->messageQueueHead == myPlace->messageQueueTail)
			{
				myPlace->messageQueueHead = X10RT_DATABUFFERSIZE;
				myPlace->messageQueueTail = 0;
				#ifdef DEBUG
					printf("X10rt.Standalone: Place %lu finished processing a message of size %lu. Buffer empty. Head=%u, Tail=%u\n", state.myPlaceId, entrySize,
							myPlace->messageQueueHead, myPlace->messageQueueTail);
					fflush(stdout);
				#endif
				if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
				return;
			}

			entry = (x10StandaloneMessageQueueEntry *)(myPlace->dataBuffer + myPlace->messageQueueHead);

			if (entry->status == WRAPPED)
			{
				myPlace->messageQueueHead = 0;
				entry = (x10StandaloneMessageQueueEntry *)myPlace->dataBuffer;
			}
		}

		#ifdef DEBUG
			printf("X10rt.Standalone: Place %lu finished processing a message of size %lu. Head=%u, Tail=%u\n", state.myPlaceId, entrySize,
					myPlace->messageQueueHead, myPlace->messageQueueTail);
			fflush(stdout);
		#endif
	}
}

void x10rt_net_finalize (void)
{
	#ifdef DEBUG
		printf("X10rt.Standalone: shutting down place %lu\n", state.myPlaceId);
	#endif

	// if we're the parent process, clean up shared memory buffers, wait for other places to exit, and return.
	if (state.myPlaceId == 0) // we're the parent thread.  We need to do the cleanup of shared stuff.
	{
		int childStatus;
		for (x10rt_place i=1; i<state.numPlaces; i++)
			wait(&childStatus);

		for (x10rt_place i=0; i<state.numPlaces; i++)
		{
			pthread_mutex_destroy(&state.perPlaceState[i]->messageQueueLock);
			munmap(state.perPlaceState[i], sizeof(struct x10StandalonePlaceState));
		}
		pthread_barrier_destroy(state.barrier);
		munmap(state.barrier, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces));
	}

	free(state.callBackTable);
}

int x10rt_net_supports (x10rt_opt o)
{
    switch (o)
    {
        default: return 0;
    }
}
