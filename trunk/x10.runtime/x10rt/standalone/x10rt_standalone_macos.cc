/********************************************************************************************
 * (c) Copyright IBM Corporation 2010
 * Written be Ben Herta for IBM, bherta@us.ibm.com, April 2010
 * This file contains all of the windows-native mappings that are needed
 * because of inadequate implementation in cygwin.  This file is ONLY 
 * needed when compiling in cygwin.
 ********************************************************************************************/

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
