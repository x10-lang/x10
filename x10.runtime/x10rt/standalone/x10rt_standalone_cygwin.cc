/********************************************************************************************
 * (c) Copyright IBM Corporation 2010
 * Written be Ben Herta for IBM, bherta@us.ibm.com, April 2010
 * This file contains all of the windows-native mappings that are needed
 * because of inadequate implementation in cygwin.  This file is ONLY 
 * needed when compiling in cygwin.
 ********************************************************************************************/

#include <windows.h>

typedef SECURITY_ATTRIBUTES pthread_mutexattr;
typedef HANDLE pthread_mutex;
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
	printf("Fatal WIN32 Error: %s: %s\n", message, lpMsgBuf);
	LocalFree(lpMsgBuf);
	ExitProcess(dw); 
}

// mutex stuff
int pthread_mutexattr_init(pthread_mutexattr *attr)
{
	attr->nLength = sizeof(SECURITY_ATTRIBUTES);
	attr->lpSecurityDescriptor = NULL;
	attr->bInheritHandle = true;
	return 0;
}

int pthread_mutexattr_setpshared(pthread_mutexattr *attr, int b)
{
//	if (b == PTHREAD_PROCESS_SHARED)
//		attr->bInheritHandle = true;
	return 0;
}

int pthread_mutexattr_destroy(pthread_mutexattr *attr)
{
	return 0;
}

int pthread_mutex_init(pthread_mutex *pthrd_mutex, pthread_mutexattr *attr)
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

int pthread_mutex_lock(pthread_mutex *pthrd_mutex)
{
	if (WaitForSingleObject(*pthrd_mutex, INFINITE) == WAIT_FAILED)
		winError("Unable to lock mutex");
	return 0;
}

int pthread_mutex_unlock(pthread_mutex *pthrd_mutex)
{
	if (ReleaseMutex(*pthrd_mutex) == 0)
		winError("Unable to release mutex");
	return 0;
}

int pthread_mutex_destroy(pthread_mutex *pthrd_mutex)
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
