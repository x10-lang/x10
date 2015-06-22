/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 *
 *  This file was written by Ben Herta for IBM: bherta@us.ibm.com
 *
 * This code supports multi-place x10 programs running on a single machine.
 * This involves spawning a child process for each place, and merging them at the end.
 * Data transmission is done through shared memory.
 */
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
#define X10_NPLACES "X10_NPLACES" // environment variable
#define X10RT_DATABUFFERSIZE 10000000 // the size, in bytes, of the shared memory segment used for communications, per place


// mechanisms for the callback functions used in the register and probe methods
typedef void (*handlerCallback)(const x10rt_msg_params *);
typedef void *(*finderCallback)(const x10rt_msg_params *, x10rt_copy_sz);
typedef void (*notifierCallback)(const x10rt_msg_params *, x10rt_copy_sz);

enum MSGTYPE {STANDARD, PUT, GET, GET_COMPLETED};
enum MSGSTATUS {COMPLETED, NEW, INPROCESS, WRAPPED};

#if defined(__APPLE__)
// Mac OS X doesn't support cross-process pthread mutexes, or pthread barriers,
// so these are over-ridden using named semaphores

#include <semaphore.h>

#define pthread_barrier_t struct barrier_t
#define pthread_barrierattr_t char
#define pthread_mutexattr_t char
#define pthread_mutexattr_init(b) doNothing()
#define pthread_mutexattr_destroy(b) doNothing()
#define pthread_mutexattr_setpshared(b,a) doNothing()
#define pthread_mutex_t sem_t*
#define pthread_mutex_init(s,a) mysem_open(s)
#define pthread_mutex_destroy(s) mysem_close(s)
#define pthread_mutex_lock(a) mysem_wait(a)
#define pthread_mutex_unlock(a) mysem_post(a)
#define pthread_barrierattr_init(b) doNothing()
#define pthread_barrierattr_setpshared(b,a) doNothing()
#define pthread_barrierattr_destroy(b) doNothing()
#define pthread_barrier_init(b,a,n) barrier_init(b,n)
#define pthread_barrier_destroy(b) barrier_destroy(b)
#define pthread_barrier_wait(b) barrier_wait(b)
#define PTHREAD_BARRIER_SERIAL_THREAD 1

int mutexCounter = 0;

struct barrier_t
{
    int numPlaces;
    int numWaiting;
    sem_t *mutex;
    sem_t *barrier;
};

void macError(const char* message)
{
	printf("Fatal MacOS Error: %s: %s\n", message, strerror(errno));
	abort();
}

int doNothing(){return 0;}

int mysem_open(pthread_mutex_t *s)
{
	char myName[50];
	myName[0] = '\0';
	sprintf(myName, "/X10RT.STANDALONE.%d", mutexCounter++);
	*s = sem_open(myName, O_CREAT|O_EXCL, S_IRUSR|S_IWUSR, 1);
	if (SEM_FAILED == *s) macError("Failed to create a semaphore");
	#ifdef DEBUG
		printf("X10rt.Standalone: created mutex %s\n", myName);
		fflush(stdout);
	#endif
	sem_unlink(myName); // this causes the semaphore to be freed when the close happens later.
	return 0;
}

int mysem_close(pthread_mutex_t *s)
{
	return sem_close(*s);
}

int mysem_wait(pthread_mutex_t *s)
{
	return sem_wait(*s);
}

int mysem_post(pthread_mutex_t *s)
{
	return sem_post(*s);
}

int barrier_init(pthread_barrier_t *barrier, int numPlaces)
{
    barrier->numPlaces = numPlaces;
    barrier->numWaiting = 0;

    barrier->barrier = sem_open("/X10RT.STANDALONE.BARRIER\0", O_CREAT|O_EXCL, S_IRUSR|S_IWUSR, 0);
    if (SEM_FAILED == barrier->barrier) macError("Failed to create a barrier");
    sem_unlink("/X10RT.STANDALONE.BARRIER\0");

    barrier->mutex = sem_open("/X10RT.STANDALONE.B_LOCK\0", O_CREAT|O_EXCL, S_IRUSR|S_IWUSR, 1);
    if (SEM_FAILED == barrier->mutex) macError("Failed to create a barrier semaphore");
    sem_unlink("/X10RT.STANDALONE.B_LOCK\0");

	#ifdef DEBUG
		printf("X10rt.Standalone: barrier initialized with %i places\n", numPlaces);
		fflush(stdout);
	#endif

    return 0;
}

int barrier_destroy(pthread_barrier_t *barrier)
{
	sem_close(barrier->barrier);
	return sem_close(barrier->mutex);
}

int barrier_wait(pthread_barrier_t *barrier)
{

	if (sem_wait(barrier->mutex) != 0) macError("Unable to lock barrier mutex");
	barrier->numWaiting++;
	if (barrier->numWaiting == barrier->numPlaces)
	{
		// everyone is here.  Signal the other places to go, and release the mutex
		barrier->numWaiting = 0;
		if (sem_post(barrier->mutex) != 0) macError("Unable to unlock barrier mutex");
		for (int i=1; i<barrier->numPlaces; i++)
			if (sem_post(barrier->barrier) != 0) macError("Unable to free programs from the barrier");
	}
	else // release the mutex and wait for the barrier event
	{
		if (sem_post(barrier->mutex) != 0) macError("unable to unlock barrier mutex");
		if (sem_wait(barrier->barrier) != 0) macError("Unable to lock barrier");
	}

	return 0;
}

#elif defined(__CYGWIN__)
// cygwin doesn't support barriers or cross-process mutexes in their pthread implementation,
// so these components are reworked using windows native APIs.

#include <windows.h>
#define pthread_mutexattr_t SECURITY_ATTRIBUTES
#define pthread_mutex_t HANDLE
typedef char pthread_barrierattr_t;
typedef struct barrier_t pthread_barrier_t;
#define pthread_barrierattr_init(b) doNothing()
#define pthread_barrierattr_setpshared(b,a) doNothing()
#define pthread_barrierattr_destroy(b) doNothing()
#define pthread_barrier_init(b,a,n) barrier_init(b,n)
#define pthread_barrier_destroy(b) barrier_destroy(b)
#define pthread_barrier_wait(b) barrier_wait(b)
#define PTHREAD_BARRIER_SERIAL_THREAD 1

int mutexCounter = 0;

struct barrier_t
{
    int numPlaces;
    int numWaiting;
    HANDLE mutex;
    HANDLE barrierReleaseEvent;
};

int doNothing(){return 0;}

void winError(const char* message)
{
	LPVOID lpMsgBuf;
	DWORD dw = GetLastError();

	FormatMessage(
		FORMAT_MESSAGE_ALLOCATE_BUFFER |
		FORMAT_MESSAGE_FROM_SYSTEM |
		FORMAT_MESSAGE_IGNORE_INSERTS,
		NULL,
		dw,
		MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT),
		(LPTSTR) &lpMsgBuf,
		0, NULL );

	// Display the error message and exit the process
	printf("Fatal WIN32 Error: %s: %s\n", message, (char *)lpMsgBuf);
	LocalFree(lpMsgBuf);
	ExitProcess(dw);
}

// mutex stuff
int pthread_mutexattr_init(pthread_mutexattr_t *attr)
{
	attr->nLength = sizeof(SECURITY_ATTRIBUTES);
	attr->lpSecurityDescriptor = NULL;
	attr->bInheritHandle = true;
	return 0;
}

int pthread_mutexattr_setpshared(pthread_mutexattr_t *attr, int b)
{
//	if (b == PTHREAD_PROCESS_SHARED)
//		attr->bInheritHandle = true;
	return 0;
}

int pthread_mutexattr_destroy(pthread_mutexattr_t *attr)
{
	return 0;
}

int pthread_mutex_init(pthread_mutex_t *pthrd_mutex, pthread_mutexattr_t *attr)
{
	char myName[50];
	sprintf(myName, "Local\\X10RT.STANDALONE.%d", mutexCounter++);
	HANDLE h = CreateMutex(attr, false, myName);
	if (h == NULL) winError("Unable to create mutex");
	#ifdef DEBUG
		printf("X10rt.Standalone: created mutex %s\n", myName);
		fflush(stdout);
	#endif
	*pthrd_mutex = h;
	return 0;
}

int pthread_mutex_lock(pthread_mutex_t *pthrd_mutex)
{
	if (WaitForSingleObject(*pthrd_mutex, INFINITE) == WAIT_FAILED)
		winError("Unable to lock mutex");
	return 0;
}

int pthread_mutex_unlock(pthread_mutex_t *pthrd_mutex)
{
	if (ReleaseMutex(*pthrd_mutex) == 0)
		winError("Unable to release mutex");
	return 0;
}

int pthread_mutex_destroy(pthread_mutex_t *pthrd_mutex)
{
	CloseHandle(*pthrd_mutex);
	return 0;
}


// barrier stuff (uses mutexes)
int barrier_init(pthread_barrier_t *barrier, int numPlaces)
{
    barrier->numPlaces = numPlaces;
    barrier->numWaiting = 0;

    SECURITY_ATTRIBUTES attr;
    attr.nLength = sizeof(SECURITY_ATTRIBUTES);
    attr.lpSecurityDescriptor = NULL;
    attr.bInheritHandle = true;

    barrier->mutex = CreateMutex(&attr, false, "Local\\X10RT.STANDALONE.BARRIER_MUTEX");
    if (barrier->mutex == NULL) winError("Unable to create mutex");

    barrier->barrierReleaseEvent = CreateEvent(&attr, false, false, "Local\\X10RT.STANDALONE.BARRIER");
    if (barrier->barrierReleaseEvent == NULL) winError("Unable to initialize barrier");

	#ifdef DEBUG
		printf("X10rt.Standalone: barrier initialized with %i places\n", numPlaces);
		fflush(stdout);
	#endif
    return 0;
}

int barrier_destroy(pthread_barrier_t *barrier)
{
	if (CloseHandle(barrier->mutex) == 0) winError("Unable to close barrier mutex");
    if (CloseHandle(barrier->barrierReleaseEvent) == 0) winError("Unable to close barrier event");
    return 0;
}

int barrier_wait(pthread_barrier_t *barrier)
{
	if (WaitForSingleObject(barrier->mutex, INFINITE) == WAIT_FAILED)
		winError("Unable to lock barrier mutex");
	barrier->numWaiting++;
	if (barrier->numWaiting == barrier->numPlaces)
	{
		// everyone is here.  Signal the other places to go, and release the mutex
		barrier->numWaiting = 0;
		for (int i=1; i<barrier->numPlaces; i++)
		{
			if (SetEvent(barrier->barrierReleaseEvent) == 0)
				winError("Unable to release a thread from the barrier");
		}
		if (ReleaseMutex(barrier->mutex) == 0)
			winError("Unable to unlock barrier mutex");
	}
	else // release the mutex and wait for the barrier event
		if (SignalObjectAndWait(barrier->mutex, barrier->barrierReleaseEvent, INFINITE, FALSE) == WAIT_FAILED) winError("Unable to block on the barrier");

	return 0;
}
#endif // end platform-specific overrides of standard pthread stuff


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
	pthread_mutex_t messageQueueLock; // lock is per place, so we have parallelism for all but the creation/destruction of the buffer entries
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
void insertNewMessage(MSGTYPE mt, x10rt_msg_params *p, void *dataPtr, x10rt_copy_sz dataLen, void* remoteDataPtr)
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
		printf("Unable to send a message of size %lu through a buffer of size %d!!!!  ABORT\n",
               (unsigned long)entrySize, (int)X10RT_DATABUFFERSIZE);
		abort();
	}

	int detectDeadlock = 0;
	// find a free slot to put the message into
	while (entry == NULL)
	{
		#ifdef DEBUG
			printf("X10rt.Standalone: Place %lu thread %lu locking place %lu's buffer.\n", state.myPlaceId, pthread_self(), p->dest_place);
			fflush(stdout);
		#endif
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
			//#ifdef DEBUG
			if (detectDeadlock == 100) // warning limit
			{
				printf("X10rt.Standalone: place %lu's buffer is full!  Head=%u, Tail=%u\n", (unsigned long)p->dest_place, dest->messageQueueHead, dest->messageQueueTail);
				fflush(stdout);
			}
			detectDeadlock++;
			//#endif
			if (pthread_mutex_unlock(&dest->messageQueueLock) != 0) error("Unable to unlock the message queue after inserting a message");
			if (detectDeadlock < 1000) // failure limit
				sched_yield();
			else
			{
				if (state.myPlaceId == 0)
					fprintf(stderr, "The buffers appear to be stuck in a deadlock state.  Your program is sending too much data at once.  Try the sockets backend instead of standalone.\n");
				abort();
			}
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
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len, &remoteDataPtr, sizeof(void *));
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len+sizeof(void *), dataPtr, dataLen);
	}
	else if (dataLen > 0)
		memcpy(dest->dataBuffer+dest->messageQueueTail+sizeof(struct x10StandaloneMessageQueueEntry)+p->len, dataPtr, dataLen);

	// update our queue positions
	dest->messageQueueTail += entrySize;
	if (dest->messageQueueHead == X10RT_DATABUFFERSIZE)
		dest->messageQueueHead = 0;

	#ifdef DEBUG
		printf("X10rt.Standalone: Place %lu thread %lu added a message of length %lu to place %lu's buffer.  Head=%u, Tail=%u\n", state.myPlaceId, pthread_self(), entrySize, p->dest_place,
				dest->messageQueueHead, dest->messageQueueTail);
		fflush(stdout);
	#endif


	// unlock destination
	if (pthread_mutex_unlock(&dest->messageQueueLock) != 0) error("Unable to unlock the message queue after inserting a message");
	#ifdef DEBUG
		printf("X10rt.Standalone: Place %lu thread %lu unlocked place %lu's buffer.\n", state.myPlaceId, pthread_self(), p->dest_place);
		fflush(stdout);
	#endif
}

/******************************************************
 *  Main API calls.  See x10rt_net.h for documentation
*******************************************************/
x10rt_error x10rt_net_preinit(char* connInfoBuffer, int connInfoBufferSize) {
	return X10RT_ERR_UNSUPPORTED;
}

x10rt_error x10rt_net_init (int *argc, char ***argv, x10rt_msg_type *counter)
{
	// determine the number of places (processes) to create, using an environment variable
	char* NPROCS = getenv(X10_NPLACES);
	if (NPROCS == NULL) 
	{
//		fprintf(stderr, "Warning: "X10_NPLACES" not set.  Assuming 1 place\n");
		state.numPlaces = 1;
	}
	else
		state.numPlaces = atol(NPROCS);

	// allocate the shared memory regions which hold the barrier and pointers to buffers between processes
//	int sharedStateHandle = open("/dev/zero", O_RDWR|O_EXCL, S_IRWXU);
//	if (sharedStateHandle < 0) error("Unable to open the initial shared memory region");
//	if (ftruncate(sharedStateHandle, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces)) < 0) error("Unable to truncate shared memory region"); // set the shared memory size
//	state.barrier = (pthread_barrier_t*)mmap(NULL, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces), PROT_READ|PROT_WRITE, MAP_SHARED, sharedStateHandle, 0);
	state.barrier = (pthread_barrier_t*)mmap(NULL, sizeof(pthread_barrier_t) + (sizeof(struct x10StandalonePlaceState)*state.numPlaces), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);
	if (state.barrier == MAP_FAILED) error("Unable to mmap the initial shared memory region");
	state.perPlaceState = (x10StandalonePlaceState **)state.barrier+sizeof(pthread_barrier_t);
//	if (close(sharedStateHandle) < 0) error("Unable to close the initial shared memory handle");

	// initialize our barrier to the number of places
	pthread_barrierattr_t barrier_attr;
	if (pthread_barrierattr_init(&barrier_attr) != 0) error("Unable to initialize the synchronization barrier attributes");
	if (pthread_barrierattr_setpshared(&barrier_attr, PTHREAD_PROCESS_SHARED) != 0) error("Unable to set the synchronization barrier to shared");
	if (pthread_barrier_init(state.barrier, &barrier_attr, state.numPlaces) != 0) error("Unable to initialize the synchronization barrier");
	if (pthread_barrierattr_destroy(&barrier_attr) != 0) error("Unable to initialize the synchronization barrier attributes");

	pthread_mutexattr_t mta;
	if (pthread_mutexattr_init(&mta) != 0) error("Unable to initialize the mutex attributes");
	if (pthread_mutexattr_setpshared(&mta, PTHREAD_PROCESS_SHARED) != 0) error("Unable to initialize the mutex attributes to shared");
	// initialize structures for each individual place
	for (x10rt_place i=0; i<state.numPlaces; i++)
	{
		// allocate the shared memory data buffer and associates structures
//		int placeStateHandle = open("/dev/zero", O_RDWR|O_EXCL, S_IRWXU);
//		if (placeStateHandle < 0) error("Unable to open the place-specific buffer");
//		if (ftruncate(placeStateHandle, sizeof(struct x10StandalonePlaceState))) error("Unable to truncate a place-specific buffer"); // set the shared memory size
//		state.perPlaceState[i] = (x10StandalonePlaceState *)mmap(NULL, sizeof(struct x10StandalonePlaceState), PROT_READ|PROT_WRITE, MAP_SHARED, placeStateHandle, 0);
		state.perPlaceState[i] = (x10StandalonePlaceState *)mmap(NULL, sizeof(struct x10StandalonePlaceState), PROT_READ|PROT_WRITE, MAP_SHARED|MAP_ANON, -1, 0);
		if (state.perPlaceState[i] == MAP_FAILED) error("Unable to mmap the place-specific buffer");
//		if (close(placeStateHandle) < 0) error("Unable to close the place-specific buffer handle");

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

    return X10RT_ERR_OK;
}

void x10rt_net_register_msg_receiver (x10rt_msg_type msg_type, x10rt_handler *cb)
{
	// register a pointer to methods that will handle specific message types.
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
		printf("X10rt.Standalone: place %lu registered standard message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_put_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
{
	// register a pointer to methods that will handle specific message types.
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
		printf("X10rt.Standalone: place %lu registered put message %u\n", state.myPlaceId, msg_type);
	#endif
}

void x10rt_net_register_get_receiver (x10rt_msg_type msg_type, x10rt_finder *cb1, x10rt_notifier *cb2)
{ 
	// register a pointer to methods that will handle specific message types.
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
		printf("X10rt.Standalone: place %lu registered get message %u\n", state.myPlaceId, msg_type);
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

void x10rt_net_send_msg (x10rt_msg_params *p)
{
    x10rt_lgl_stats.msg.messages_sent++ ;
    x10rt_lgl_stats.msg.bytes_sent += p->len;
	// originating place calls this method, to send something? to a remote place.  It returns once the data transfer is complete.
	// There is not really anything to do here except put the pointer to the message into the receivers buffer
	insertNewMessage(STANDARD, p, NULL, 0, NULL);
}

void x10rt_net_send_get (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{
    x10rt_lgl_stats.get.messages_sent++ ;
    x10rt_lgl_stats.get.bytes_sent += p->len;
	// The local place uses this method to bring in data from a remote place
	insertNewMessage(GET, p, buf, len, NULL);
}

void x10rt_net_remote_op (x10rt_place place, x10rt_remote_ptr victim,
                          x10rt_op_type type, unsigned long long value)
{
    // assume remote ops will be handled by regular x10rt_send_msg
    abort();
}

void x10rt_net_remote_ops (x10rt_remote_op_params *ops, size_t numOps)
{
	// assume remote ops will be handled by regular x10rt_send_msg
	abort();
}

void x10rt_net_register_mem (void *ptr, size_t)
{
    // assume remote ops will be handled by regular x10rt_send_msg so
    // no special work to do here
}

void x10rt_net_send_put (x10rt_msg_params *p, void *buf, x10rt_copy_sz len)
{ 
    x10rt_lgl_stats.put.messages_sent++ ;
    x10rt_lgl_stats.put.bytes_sent += p->len;
    x10rt_lgl_stats.put_copied_bytes_sent += len;
	// originating place calls this method, to transfer data to a remote place.  It returns once the data transfer is complete.
	insertNewMessage(PUT, p, buf, len, NULL);
}

x10rt_error x10rt_net_probe (void)
{
	// the receiving side calls this regularly, to see if messages have come in to be processed.  This is
	// a thread that's part of the receiving end, and this is the thread that will begin execution of the function registered

	x10StandalonePlaceState *myPlace = state.perPlaceState[state.myPlaceId]; // pointer to make this more readable

	#ifdef DEBUG
		printf("X10rt.Standalone: Place %lu thread %lu locking local buffer.\n", state.myPlaceId, pthread_self());
		fflush(stdout);
	#endif
	if (pthread_mutex_lock(&myPlace->messageQueueLock) != 0) error("Unable to lock the message queue to get a message");

	while(true) // loop as long as we have incoming messages to process in the buffer
	{
		// check the buffer to see if we have a message in it
		if (myPlace->messageQueueHead == X10RT_DATABUFFERSIZE) // empty queue
		{
			#ifdef DEBUG
				printf("X10rt.Standalone: Place %lu thread %lu unlocking local buffer (empty).\n", state.myPlaceId, pthread_self());
				fflush(stdout);
			#endif

			if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
            return X10RT_ERR_OK;
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
				#ifdef DEBUG
					printf("X10rt.Standalone: Place %lu thread %lu unlocking local buffer (at tail).\n", state.myPlaceId, pthread_self());
					fflush(stdout);
				#endif

				if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
                return X10RT_ERR_OK;
			}

			entrySize = getTotalLength(entry);
		}

		unsigned int origPosition = myPlace->messageQueueHead + skippedMsgs;

		// mark the message as in-process
		entry->status = INPROCESS;

		#ifdef DEBUG
			printf("X10rt.Standalone: place %lu thread %lu picked up a message from place %lu with type=%d len=%lu and payloadLen=%lu, position=%u Head=%u, Tail=%u\n", state.myPlaceId, pthread_self(), entry->from, entry->standaloneMessageType, entrySize, entry->payloadLen, myPlace->messageQueueHead + skippedMsgs, myPlace->messageQueueHead, myPlace->messageQueueTail);
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
                x10rt_lgl_stats.msg.messages_received++;
                x10rt_lgl_stats.msg.bytes_received += mp.len;
				hcb(&mp);
			}
			break;
			case PUT:
			{
				finderCallback fcb = state.callBackTable[mp.type].finder;
                x10rt_lgl_stats.put.messages_received++;
                x10rt_lgl_stats.put.bytes_received += mp.len;
				void* dest = fcb(&mp, entry->payloadLen); // get the pointer to the destination location
				memcpy(dest, (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen, entry->payloadLen); // copy the data to the destination
				notifierCallback ncb = state.callBackTable[mp.type].notifier;
				ncb(&mp, entry->payloadLen);
                x10rt_lgl_stats.put_copied_bytes_received += entry->payloadLen;
			}
			break;
			case GET:
			{
				// this is the request for data.
				finderCallback fcb = state.callBackTable[mp.type].finder;
                x10rt_lgl_stats.get.messages_received++;
                x10rt_lgl_stats.get.bytes_received += mp.len;
				void* src = fcb(&mp, entry->payloadLen);
                x10rt_lgl_stats.get_copied_bytes_received += entry->payloadLen;

				// send the data to the other side
				mp.dest_place = entry->from;
				void* dest;
				memcpy(&dest, ((char*)entry)+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen, sizeof(void*));
				insertNewMessage(GET_COMPLETED, &mp, src, entry->payloadLen, dest);
			}
			break;
			case GET_COMPLETED:
			{
				// copy over the contents of the shared memory
				void * dest;
				memcpy(&dest, (char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen, sizeof(void*));
				memcpy(dest, (void*)((char*)entry+sizeof(struct x10StandaloneMessageQueueEntry)+entry->msgLen+sizeof(void *)), entry->payloadLen);

				notifierCallback ncb = state.callBackTable[mp.type].notifier;
				ncb(&mp, entry->payloadLen);
                x10rt_lgl_stats.get_copied_bytes_sent += entry->payloadLen;
			}
			break;
			default: // this should never happen
				error("Unknown message type found");
			break;
		}

		#ifdef DEBUG
			printf("X10rt.Standalone: Place %lu thread %lu locking local buffer to remove processed message.\n", state.myPlaceId, pthread_self());
			fflush(stdout);
		#endif
		if (pthread_mutex_lock(&myPlace->messageQueueLock) != 0) error("Unable to lock the message queue after processing a message");

		entry->status = COMPLETED;

		// move the head pointer up in the array, to clear out this message for the next insert
		if (origPosition != myPlace->messageQueueHead)
		{
			#ifdef DEBUG
				printf("X10rt.Standalone: Place %lu thread %lu finished processing a message of size %lu. Not moving head. Head=%u, Tail=%u\n", state.myPlaceId, pthread_self(), entrySize, myPlace->messageQueueHead, myPlace->messageQueueTail);
				fflush(stdout);
			#endif

			// this message was not the previous head.  We're done.
			if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after processing a message");
            return X10RT_ERR_OK;
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
					printf("X10rt.Standalone: Place %lu thread %lu finished processing a message of size %lu. Buffer empty. Head=%u, Tail=%u\n", state.myPlaceId, pthread_self(), entrySize,
							myPlace->messageQueueHead, myPlace->messageQueueTail);
					fflush(stdout);
				#endif
				if (pthread_mutex_unlock(&myPlace->messageQueueLock) != 0) error("Unable to unlock the message queue after finding it empty");
                return X10RT_ERR_OK;
			}

			entry = (x10StandaloneMessageQueueEntry *)(myPlace->dataBuffer + myPlace->messageQueueHead);

			if (entry->status == WRAPPED)
			{
				myPlace->messageQueueHead = 0;
				entry = (x10StandaloneMessageQueueEntry *)myPlace->dataBuffer;
			}
		}

		#ifdef DEBUG
			printf("X10rt.Standalone: Place %lu thread %lu finished processing a message of size %lu. Head=%u, Tail=%u\n", state.myPlaceId, pthread_self(), entrySize,
					myPlace->messageQueueHead, myPlace->messageQueueTail);
			fflush(stdout);
		#endif

		// we still have the messageQueueLock locked here, for our loop back around
	}

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

x10rt_coll_type x10rt_net_coll_support () {
	return X10RT_COLL_NOCOLLECTIVES;
}

bool x10rt_net_remoteop_support () {
	return false;
}

void x10rt_net_team_new (x10rt_place placec, x10rt_place *placev,
                         x10rt_completion_handler2 *ch, void *arg)
{
    abort();
}

void x10rt_net_team_del (x10rt_team team, x10rt_place role,
                         x10rt_completion_handler *ch, void *arg)
{
    abort();
}

x10rt_place x10rt_net_team_sz (x10rt_team team)
{
    abort();
}

void x10rt_net_team_split (x10rt_team parent, x10rt_place parent_role,
                           x10rt_place color, x10rt_place new_role,
                           x10rt_completion_handler2 *ch, void *arg)
{
    abort();
}

void x10rt_net_barrier (x10rt_team team, x10rt_place role,
                        x10rt_completion_handler *ch, void *arg)
{
    abort();
}

void x10rt_net_bcast (x10rt_team team, x10rt_place role,
                      x10rt_place root, const void *sbuf, void *dbuf,
                      size_t el, size_t count,
                      x10rt_completion_handler *ch, void *arg)
{
    abort();
}

void x10rt_net_scatter (x10rt_team team, x10rt_place role,
                        x10rt_place root, const void *sbuf, void *dbuf,
                        size_t el, size_t count,
                        x10rt_completion_handler *ch, void *arg)
{
    abort();
}

void x10rt_net_alltoall (x10rt_team team, x10rt_place role,
                         const void *sbuf, void *dbuf,
                         size_t el, size_t count,
                         x10rt_completion_handler *ch, void *arg)
{
    abort();
}

void x10rt_net_reduce (x10rt_team team, x10rt_place role,
                        x10rt_place root, const void *sbuf, void *dbuf,
                        x10rt_red_op_type op, 
                        x10rt_red_type dtype,
                        size_t count,
                        x10rt_completion_handler *ch, void *arg)
{
	abort();
}

void x10rt_net_allreduce (x10rt_team team, x10rt_place role,
                          const void *sbuf, void *dbuf,
                          x10rt_red_op_type op, 
                          x10rt_red_type dtype,
                          size_t count,
                          x10rt_completion_handler *ch, void *arg)
{
    abort();
}

const char *x10rt_net_error_msg (void) { return NULL; }
