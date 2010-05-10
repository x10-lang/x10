/********************************************************************************************
 * (c) Copyright IBM Corporation 2010
 * Written be Ben Herta for IBM, bherta@us.ibm.com, April 2010
 * This file contains all of the windows-native mappings that are needed
 * because of inadequate implementation in cygwin.  This file is ONLY 
 * needed when compiling in cygwin.
 ********************************************************************************************/

typedef struct barrier_t pthread_barrier_t;
typedef char pthread_barrierattr_t;
#define pthread_barrierattr_init(b) doNothing()
#define pthread_barrierattr_setpshared(b,a) doNothing()
#define pthread_barrierattr_destroy(b) doNothing()
#define pthread_barrier_init(b,a,n) barrier_init(b,n)
#define pthread_barrier_destroy(b) barrier_destroy(b)
#define pthread_barrier_wait(b) barrier_wait(b)
#define PTHREAD_BARRIER_SERIAL_THREAD 1

struct barrier_t
{
    int numPlaces;
    int numWaiting;
    pthread_mutex_t mutex;
    pthread_cond_t signal;
};

void macError(const char* message)
{
	printf("Fatal MacOS Error: %s: %s\n", message, strerror(errno));
	abort();
}

int doNothing(){return 0;}

int barrier_init(pthread_barrier_t *barrier, int numPlaces)
{
    barrier->numPlaces = numPlaces;
    barrier->numWaiting = 0;
    
    pthread_mutexattr_t ma;
    if (pthread_mutexattr_init(&ma) != 0) macError("Unable to initialize barrier mutex attributes");
    if (pthread_mutexattr_setpshared(&ma, PTHREAD_PROCESS_SHARED) != 0) macError("Unable to set barrier mutex attributes to shared");
    if (pthread_mutex_init(&barrier->mutex, &ma) != 0) macError("Unable to initialize barrier mutex");
    pthread_mutexattr_destroy(&ma);

    pthread_condattr_t ca;
    if (pthread_condattr_init(&ca) != 0) macError("Unable to initialize barrier cond attributes");
    if (pthread_condattr_setpshared(&ca, PTHREAD_PROCESS_SHARED) != 0) macError("Unable to set barrier cond attributes to shared");
    if (pthread_cond_init(&barrier->signal, &ca) != 0) macError("Unable to initialize barrier cond");
    pthread_condattr_destroy(&ca);

    return 0;
}

int barrier_destroy(pthread_barrier_t *barrier)
{
	pthread_mutex_destroy(&barrier->mutex);
	pthread_cond_destroy(&barrier->signal);
	return 0;
}

int barrier_wait(pthread_barrier_t *barrier)
{

	if (pthread_mutex_lock(&barrier->mutex) != 0) macError("Unable to lock barrier mutex");
	barrier->numWaiting++;
	if (barrier->numWaiting == barrier->numPlaces)
	{
		// everyone is here.  Signal the other places to go, and release the mutex
		barrier->numWaiting = 0;
		pthread_cond_broadcast(&barrier->signal);
		pthread_mutex_unlock(&barrier->mutex);
	}
	else // release the mutex and wait for the barrier event
		pthread_cond_wait(&barrier->signal, &barrier->mutex);

	return 0;
}
